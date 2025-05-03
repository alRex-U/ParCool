package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.CatLeapAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.BlockStateWrapper;
import com.alrex.parcool.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.compatibility.LevelWrapper;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.block.BlockRenderType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class CatLeap extends Action {
	private int coolTimeTick = 0;
	private boolean ready = false;
	private int readyTick = 0;
    private static final int MAX_COOL_TIME_TICK = 30;

	@Override
	public void onTick(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		if (coolTimeTick > 0) {
			coolTimeTick--;
		}
	}

	@Override
	public void onClientTick(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		if (player.isLocalPlayer()) {
			if (KeyRecorder.keySneak.isPressed() && parkourability.get(FastRun.class).getNotDashTick(parkourability.getAdditionalProperties()) < 10) {
				ready = true;
			}
			if (ready) {
				readyTick++;
			}
			if (readyTick > 10) {
				ready = false;
				readyTick = 0;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Vec3Wrapper movement = player.getDeltaMovement();
		if (movement.lengthSqr() < 0.001) return false;
		movement = movement.multiply(1, 0, 1).normalize();
		startInfo.putDouble(movement.x()).putDouble(movement.z());
		return (player.isOnGround()
				&& !stamina.isExhausted()
				&& coolTimeTick <= 0
				&& readyTick > 0
				&& parkourability.get(ChargeJump.class).getChargingTick() < ChargeJump.JUMP_MAX_CHARGE_TICK / 2
				&& !parkourability.get(HideInBlock.class).isDoing()
				&& !parkourability.get(Roll.class).isDoing()
				&& !parkourability.get(Tap.class).isDoing()
				&& KeyRecorder.keySneak.isReleased()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		return !((getDoingTick() > 1 && player.isOnGround())
				|| player.isFallFlying()
				|| player.isInWaterOrBubble()
				|| player.isInLava()
		);
	}

	@Override
	public void onStartInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		Vec3Wrapper jumpDirection = new Vec3Wrapper(startData.getDouble(), 0, startData.getDouble());
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.CATLEAP.get(), 1, 1);
		coolTimeTick = MAX_COOL_TIME_TICK;
		spawnJumpEffect(player, jumpDirection);
		player.jumpFromGround();
		Vec3Wrapper motionVec = player.getDeltaMovement();
		player.setDeltaMovement(jumpDirection.x(), motionVec.y() * 1.16667, jumpDirection.z());
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new CatLeapAnimator());
	}

	@Override
	public void onStartInOtherClient(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
		Vec3Wrapper jumpDirection = new Vec3Wrapper(startData.getDouble(), 0, startData.getDouble());
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.CATLEAP.get(), 1, 1);
		spawnJumpEffect(player, jumpDirection);
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new CatLeapAnimator());
	}

	@Override
	public boolean wantsToShowStatusBar(ClientPlayerWrapper player, Parkourability parkourability) {
		return coolTimeTick > 0;
	}

	@Override
	public float getStatusValue(ClientPlayerWrapper player, Parkourability parkourability) {
		return coolTimeTick / (float) MAX_COOL_TIME_TICK;
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnJumpEffect(PlayerWrapper player, Vec3Wrapper jumpDirection) {
		if (!ParCoolConfig.Client.Booleans.EnableActionParticles.get()) return;
		LevelWrapper level = player.getLevel();
		Vec3Wrapper pos = player.position();
		BlockPos blockpos = new BlockPos(pos.add(0, -0.2, 0));
		if (!level.isLoaded(blockpos)) return;
		float width = player.getBbWidth();
		BlockStateWrapper blockState = level.getBlockState(blockpos);
		if (blockState.getRenderShape() != BlockRenderType.INVISIBLE) {
			for (int i = 0; i < 20; i++) {
				Vec3Wrapper particlePos = new Vec3Wrapper(
						pos.x() + (jumpDirection.x() * -0.5 + player.getRandom().nextDouble() - 0.5D) * width,
						pos.y() + 0.1D,
						pos.z() + (jumpDirection.z() * -0.5 + player.getRandom().nextDouble() - 0.5D) * width
				);
				Vec3Wrapper particleSpeed = particlePos.subtract(pos).normalize().scale(2.5 + 8 * player.getRandom().nextDouble()).add(0, 1.5, 0);
				level.addParticle(
						blockState.getBlockParticleData(ParticleTypes.BLOCK, blockpos),
						particlePos.x(),
						particlePos.y(),
						particlePos.z(),
						particleSpeed.x(),
						particleSpeed.y(),
						particleSpeed.z()
				);

			}
		}
	}
}
