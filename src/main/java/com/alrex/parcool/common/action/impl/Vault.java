package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.KongVaultAnimator;
import com.alrex.parcool.client.animation.impl.SpeedVaultAnimator;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.common.network.StartVaultMessage;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class Vault extends Action {
	private boolean vauting = false;
	private int vaultingTick = 0;

	//only in client
	private double stepHeight = 0;
	private Vec3 stepDirection = null;

	//for not Local Player
	private boolean start = false;
	private Type startType = null;

	@Override
	public void onTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (vauting) {
			vaultingTick++;
		} else {
			vaultingTick = 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	private boolean canVault(Player player, Parkourability parkourability, Stamina stamina) {
		Vec3 lookVec = player.getLookAngle();
		lookVec = new Vec3(lookVec.x(), 0, lookVec.z()).normalize();
		Vec3 wall = WorldUtil.getWall(player);
		if (wall == null) return false;
		return !this.vauting &&
				parkourability.getPermission().canVault() &&
				parkourability.getFastRun().canActWithRunning(player) &&
				player.isOnGround() &&
				(wall.dot(lookVec) / wall.length() / lookVec.length()) > 0.707106 /*check facing wall*/ &&
				WorldUtil.getVaultableStep(player) != null &&
				WorldUtil.getWallHeight(player) > 0.8;
	}

	private int getVaultAnimateTime() {
		return 2;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (start) {
			start = false;
			Animation animation = Animation.get(player);
			if (animation != null) {
				if (startType == Type.Right || startType == Type.Left) {
					animation.setAnimator(new SpeedVaultAnimator(startType));
				} else if (startType == Type.Kong) {
					animation.setAnimator(new KongVaultAnimator());
				}
			}
			startType = null;
		}
		if (player.isLocalPlayer()) {
			if (!this.isVaulting() && this.canVault(player, parkourability, stamina)) {
				vauting = true;
				vaultingTick = 0;
				stepDirection = WorldUtil.getVaultableStep(player);
				stepHeight = WorldUtil.getWallHeight(player);

				Vec3 lookVec = player.getLookAngle();
				Vec3 vec = new Vec3(lookVec.x(), 0, lookVec.z()).normalize();
				Vec3 s = stepDirection.normalize();

				//doing "vec/stepDirection" as complex number(x + z i) to calculate difference of player's direction to steps
				Vec3 dividedVec =
						new Vec3(
								vec.x() * s.x() + vec.z() * s.z(), 0,
								-vec.x() * s.z() + vec.z() * s.x()
						).normalize();
				Animation animation = Animation.get(player);

				Type type;
				if (!ParCoolConfig.CONFIG_CLIENT.disableKongVault.get() && -0.09 < dividedVec.z() && dividedVec.z() < 0.09) {
					type = Type.Kong;
				} else type = dividedVec.z() > 0 ? Type.Right : Type.Left;

				if (animation != null) {
					animation.setAnimator(
							type == Type.Kong ?
									new KongVaultAnimator() :
									new SpeedVaultAnimator(type)
					);
				}
				StartVaultMessage.send(player, type);
			}

			if (vauting) {
				player.setDeltaMovement(
						stepDirection.x() / 10,
						(stepHeight + 0.05) / this.getVaultAnimateTime(),
						stepDirection.z() / 10
				);
			}

			if (vaultingTick >= this.getVaultAnimateTime()) {
				vauting = false;
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
	public void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {
	}

	@Override
	public boolean needSynchronization(ByteBuffer savedInstanceState) {
		return false;
	}

	@Override
	public void sendSynchronization(Player player) {

	}


	@Override
	public void synchronize(Object message) {
		if (message instanceof StartVaultMessage) {
			start = true;
			startType = ((StartVaultMessage) message).getType();
		}
	}

	@Override
	public void saveState(ByteBuffer buffer) {

	}

	public boolean isVaulting() {
		return vauting;
	}

	public int getVaultingTick() {
		return vaultingTick;
	}

	public enum Type {
		Right(0),
		Left(1),
		Kong(2);

		final int code;

		Type(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		@Nullable
		public static Type get(int code) {
			return switch (code) {
				case 0 -> Right;
				case 1 -> Left;
				case 2 -> Kong;
				default -> null;
			};
		}
	}
}
