package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.KongVaultAnimator;
import com.alrex.parcool.client.animation.impl.SpeedVaultAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class Vault extends Action {
	public enum TypeSelectionMode {
		SpeedVault, KongVault, Dynamic
	}
	public static final int MAX_TICK = 11;

	public enum AnimationType {
		SPEED_VAULT_RIGHT, SPEED_VAULT_LEFT, KONG_VAULT
	}

	//only in client
	private double stepHeight = 0;
	private Vec3 stepDirection = null;
	@Nullable
	private AnimationType currentAnimation;

	@Nullable
	public AnimationType getCurrentAnimation() {
		if (!isDoing()) return null;
		return currentAnimation;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Vec3 lookVec = player.getLookAngle();
		lookVec = new Vec3(lookVec.x(), 0, lookVec.z()).normalize();
		Vec3 step = WorldUtil.getVaultableStep(player);
		if (step == null) return false;
		step = step.normalize();
		//doing "vec/stepDirection" as complex number(x + z i) to calculate difference of player's direction to steps
		Vec3 dividedVec =
				new Vec3(
						lookVec.x() * step.x() + lookVec.z() * step.z(), 0,
						-lookVec.x() * step.z() + lookVec.z() * step.x()
				).normalize();
		if (dividedVec.x() < 0.707106) {
			return false;
		}
		AnimationType animationType;
		switch (ParCoolConfig.Client.VaultAnimationMode.get()) {
			case KongVault:
				animationType = AnimationType.KONG_VAULT;
				break;
			case SpeedVault:
				animationType = dividedVec.z() > 0 ? AnimationType.SPEED_VAULT_RIGHT : AnimationType.SPEED_VAULT_LEFT;
				break;
			default:
				if (dividedVec.x() > 0.99) {
					animationType = AnimationType.KONG_VAULT;
				} else {
					animationType = dividedVec.z() > 0 ? AnimationType.SPEED_VAULT_RIGHT : AnimationType.SPEED_VAULT_LEFT;
				}
				break;
		}
		double wallHeight = WorldUtil.getWallHeight(player);
		startInfo
				.put((byte) animationType.ordinal())
				.putDouble(step.x())
				.putDouble(step.y())
				.putDouble(step.z())
				.putDouble(wallHeight);

		return (!stamina.isExhausted()
				&& !(ParCoolConfig.Client.Booleans.VaultKeyPressedNeeded.get() && !KeyBindings.getKeyVault().isDown())
				&& parkourability.get(FastRun.class).canActWithRunning(player)
				&& !stamina.isExhausted()
				&& (player.isOnGround() || ParCoolConfig.Client.Booleans.EnableVaultInAir.get())
				&& wallHeight > player.getBbHeight() * 0.44 /*about 0.8*/
		);
	}

	@Override
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
		return getDoingTick() < MAX_TICK;
	}

	private int getVaultAnimateTime() {
		return 2;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		currentAnimation = AnimationType.values()[startData.get()];
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.VAULT.get(), 1f, 1f);
		stepDirection = new Vec3(startData.getDouble(), startData.getDouble(), startData.getDouble());
		stepHeight = startData.getDouble();
		Animation animation = Animation.get(player);
		if (animation != null && currentAnimation != null) {
			switch (currentAnimation) {
				case SPEED_VAULT_RIGHT:
					animation.setAnimator(new SpeedVaultAnimator(SpeedVaultAnimator.Type.Right));
					break;
				case SPEED_VAULT_LEFT:
					animation.setAnimator(new SpeedVaultAnimator(SpeedVaultAnimator.Type.Left));
					break;
				case KONG_VAULT:
					animation.setAnimator(new KongVaultAnimator());
					break;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		currentAnimation = AnimationType.values()[startData.get()];
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.VAULT.get(), 1f, 1f);
		Animation animation = Animation.get(player);
		if (animation != null && currentAnimation != null) {
			switch (currentAnimation) {
				case SPEED_VAULT_RIGHT:
					animation.setAnimator(new SpeedVaultAnimator(SpeedVaultAnimator.Type.Right));
					break;
				case SPEED_VAULT_LEFT:
					animation.setAnimator(new SpeedVaultAnimator(SpeedVaultAnimator.Type.Left));
					break;
				case KONG_VAULT:
					animation.setAnimator(new KongVaultAnimator());
					break;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onWorkingTickInLocalClient(Player player, Parkourability parkourability, IStamina stamina) {
		if (stepDirection == null) return;
		if (getDoingTick() < getVaultAnimateTime()) {
			player.setDeltaMovement(
					stepDirection.x() / 10,
					((stepHeight + 0.02) / this.getVaultAnimateTime()) / (player.getBbHeight() / 1.8),
					stepDirection.z() / 10
			);
		} else if (getDoingTick() == getVaultAnimateTime()) {
			stepDirection = stepDirection.normalize();
			player.setDeltaMovement(
					stepDirection.x() * 0.45,
					0.075 * (player.getBbHeight() / 1.8),
					stepDirection.z() * 0.45
			);
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStopInLocalClient(Player player) {
	}
}

