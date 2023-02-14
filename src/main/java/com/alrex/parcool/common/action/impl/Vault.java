package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.KongVaultAnimator;
import com.alrex.parcool.client.animation.impl.SpeedVaultAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class Vault extends Action {
	public enum TypeSelectionMode {
		SpeedVault, KongVault, Dynamic
	}

	public enum AnimationType {
		SpeedVault((byte) 0), KongVault((byte) 1);
		private final byte code;

		AnimationType(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return code;
		}

		@Nullable
		public static AnimationType fromCode(byte code) {
			switch (code) {
				case 0:
					return SpeedVault;
				case 1:
					return KongVault;
			}
			return null;
		}
	}

	//only in client
	private double stepHeight = 0;
	private Vector3d stepDirection = null;

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startInfo) {
		Vector3d lookVec = player.getLookAngle();
		lookVec = new Vector3d(lookVec.x(), 0, lookVec.z()).normalize();
		Vector3d step = WorldUtil.getVaultableStep(player);
		if (step == null) return false;
		step = step.normalize();
		//doing "vec/stepDirection" as complex number(x + z i) to calculate difference of player's direction to steps
		Vector3d dividedVec =
				new Vector3d(
						lookVec.x() * step.x() + lookVec.z() * step.z(), 0,
						-lookVec.x() * step.z() + lookVec.z() * step.x()
				).normalize();
		if (dividedVec.x() < 0.707106) {
			return false;
		}
		AnimationType animationType = null;
		SpeedVaultAnimator.Type type = SpeedVaultAnimator.Type.Right;
		switch (ParCoolConfig.CONFIG_CLIENT.vaultAnimationMode.get()) {
			case KongVault:
				animationType = AnimationType.KongVault;
				break;
			case SpeedVault:
				animationType = AnimationType.SpeedVault;
				type = dividedVec.z() > 0 ? SpeedVaultAnimator.Type.Right : SpeedVaultAnimator.Type.Left;
				break;
			default:
				if (dividedVec.x() > 0.99) {
					animationType = AnimationType.KongVault;
				} else {
					animationType = AnimationType.SpeedVault;
					type = dividedVec.z() > 0 ? SpeedVaultAnimator.Type.Right : SpeedVaultAnimator.Type.Left;
				}
				break;
		}
		double wallHeight = WorldUtil.getWallHeight(player);
		startInfo.put(animationType.getCode());
		BufferUtil.wrap(startInfo).putBoolean(type == SpeedVaultAnimator.Type.Right);
		startInfo
				.putDouble(step.x())
				.putDouble(step.y())
				.putDouble(step.z())
				.putDouble(wallHeight);

		return (parkourability.getPermission().canVault()
				&& !(ParCoolConfig.CONFIG_CLIENT.vaultNeedKeyPressed.get() && !KeyBindings.getKeyVault().isDown())
				&& parkourability.get(FastRun.class).canActWithRunning(player)
				&& !stamina.isExhausted()
				&& (player.isOnGround() || !ParCoolConfig.CONFIG_CLIENT.disableVaultInAir.get())
				&& wallHeight > player.getBbHeight() * 0.44 /*about 0.8*/
		);
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return getDoingTick() < getVaultAnimateTime();
	}

	private int getVaultAnimateTime() {
		return 2;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startData) {
		stamina.consume(parkourability.getActionInfo().getStaminaConsumptionVault(), player);

		AnimationType animationType = AnimationType.fromCode(startData.get());
		SpeedVaultAnimator.Type speedVaultType = BufferUtil.getBoolean(startData) ?
				SpeedVaultAnimator.Type.Right : SpeedVaultAnimator.Type.Left;
		stepDirection = new Vector3d(startData.getDouble(), startData.getDouble(), startData.getDouble());
		stepHeight = startData.getDouble();
		Animation animation = Animation.get(player);
		if (animation != null && animationType != null) {
			switch (animationType) {
				case SpeedVault:
					animation.setAnimator(new SpeedVaultAnimator(speedVaultType));
					break;
				case KongVault:
					animation.setAnimator(new KongVaultAnimator());
					break;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		AnimationType animationType = AnimationType.fromCode(startData.get());
		SpeedVaultAnimator.Type speedVaultType = BufferUtil.getBoolean(startData) ?
				SpeedVaultAnimator.Type.Right : SpeedVaultAnimator.Type.Left;
		Animation animation = Animation.get(player);
		if (animation != null && animationType != null) {
			switch (animationType) {
				case SpeedVault:
					animation.setAnimator(new SpeedVaultAnimator(speedVaultType));
					break;
				case KongVault:
					animation.setAnimator(new KongVaultAnimator());
					break;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onWorkingTickInClient(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		player.setDeltaMovement(
				stepDirection.x() / 10,
				(stepHeight + 0.02) / this.getVaultAnimateTime(),
				stepDirection.z() / 10
		);
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStopInLocalClient(PlayerEntity player) {
		stepDirection = stepDirection.normalize();
		player.setDeltaMovement(
				stepDirection.x() * 0.45,
				0.075,
				stepDirection.z() * 0.45
		);
	}
}
