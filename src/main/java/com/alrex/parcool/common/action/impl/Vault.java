package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.impl.KongVaultAnimator;
import com.alrex.parcool.client.animation.impl.SpeedVaultAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.network.StartVaultMessage;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class Vault extends Action {
	public enum TypeSelectionMode {
		SpeedVault, KongVault, Dynamic
	}

	public enum AnimationType {
		SpeedVault(0), KongVault(1);
		private final int code;

		AnimationType(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		@Nullable
		public static AnimationType fromCode(int code) {
			switch (code) {
				case 0:
					return SpeedVault;
				case 1:
					return KongVault;
			}
			return null;
		}
	}

	private boolean vaulting = false;
	private int vaultingTick = 0;

	//only in client
	private double stepHeight = 0;
	private Vector3d stepDirection = null;

	//for not Local Player
	private boolean start = false;
	private AnimationType startWith = null;

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (vaulting) {
			vaultingTick++;
		} else {
			vaultingTick = 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	private boolean canVault(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		Vector3d lookVec = player.getLookAngle();
		lookVec = new Vector3d(lookVec.x(), 0, lookVec.z()).normalize();
		Vector3d wall = WorldUtil.getWall(player);
		if (wall == null) return false;
		return !this.vaulting &&
				parkourability.getPermission().canVault() &&
				!(ParCoolConfig.CONFIG_CLIENT.vaultNeedKeyPressed.get() && !KeyBindings.getKeyVault().isDown()) &&
				parkourability.getFastRun().canActWithRunning(player) &&
				!stamina.isExhausted() &&
				(player.isOnGround() || !ParCoolConfig.CONFIG_CLIENT.disableVaultInAir.get()) &&
				(wall.dot(lookVec) / wall.length() / lookVec.length()) > 0.707106 /*check facing wall*/ &&
				WorldUtil.getVaultableStep(player) != null &&
				WorldUtil.getWallHeight(player) > 0.8;
	}

	private int getVaultAnimateTime() {
		return 2;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (start) {
			start = false;
			Animation animation = Animation.get(player);
			if (animation != null) {
				switch (startWith) {
					case KongVault: {
						animation.setAnimator(new KongVaultAnimator());
						break;
					}
					case SpeedVault: {
						animation.setAnimator(new SpeedVaultAnimator(SpeedVaultAnimator.Type.Right));
						break;
					}
				}
				startWith = null;
			}
		}
		if (player.isLocalPlayer()) {
			if (!this.isVaulting() && this.canVault(player, parkourability, stamina)) {
				vaulting = true;
				vaultingTick = 0;
				stepDirection = WorldUtil.getVaultableStep(player);
				stepHeight = WorldUtil.getWallHeight(player);

				Vector3d lookVec = player.getLookAngle();
				Vector3d vec = new Vector3d(lookVec.x(), 0, lookVec.z()).normalize();
				Vector3d s = stepDirection;

				//doing "vec/stepDirection" as complex number(x + z i) to calculate difference of player's direction to steps
				Vector3d dividedVec =
						new Vector3d(
								vec.x() * s.x() + vec.z() * s.z(), 0,
								-vec.x() * s.z() + vec.z() * s.x()
						).normalize();
				stamina.consume(parkourability.getActionInfo().getStaminaConsumptionVault(), player);
				Animation animation = Animation.get(player);
				AnimationType animationType = AnimationType.SpeedVault;
				if (animation != null) {
					Animator animator = null;
					switch (ParCoolConfig.CONFIG_CLIENT.vaultAnimationMode.get()) {
						case Dynamic: {
							if (dividedVec.x() > 0.99) {
								animationType = AnimationType.KongVault;
								animator = new KongVaultAnimator();
							} else {
								animationType = AnimationType.SpeedVault;
								SpeedVaultAnimator.Type type = dividedVec.z() > 0 ? SpeedVaultAnimator.Type.Right : SpeedVaultAnimator.Type.Left;
								animator = new SpeedVaultAnimator(type);
							}
							break;
						}
						case KongVault: {
							animationType = AnimationType.KongVault;
							animator = new KongVaultAnimator();
							break;
						}
						case SpeedVault: {
							animationType = AnimationType.SpeedVault;
							SpeedVaultAnimator.Type type = dividedVec.z() > 0 ? SpeedVaultAnimator.Type.Right : SpeedVaultAnimator.Type.Left;
							animator = new SpeedVaultAnimator(type);
							break;
						}
					}
					if (animator != null) animation.setAnimator(animator);
				}
				StartVaultMessage.send(player, animationType);
			}

			if (vaulting) {
				player.setDeltaMovement(
						stepDirection.x() / 10,
						(stepHeight + 0.05) / this.getVaultAnimateTime(),
						stepDirection.z() / 10
				);
			}

			if (vaultingTick >= this.getVaultAnimateTime()) {
				vaulting = false;
				stepDirection = stepDirection.normalize();
				player.setDeltaMovement(
						stepDirection.x() * 0.45,
						0.15,
						stepDirection.z() * 0.45
				);
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
	}

	@Override
	public void restoreState(ByteBuffer buffer) {

	}

	public void receiveStartVault(StartVaultMessage message) {
		start = true;
		startWith = message.getType();
	}

	@Override
	public void saveState(ByteBuffer buffer) {

	}

	public boolean isVaulting() {
		return vaulting;
	}

	public int getVaultingTick() {
		return vaultingTick;
	}
}
