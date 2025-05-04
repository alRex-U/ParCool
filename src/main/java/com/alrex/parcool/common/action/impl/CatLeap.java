package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.CatLeapAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class CatLeap extends Action {
	private int coolTimeTick = 0;
	private boolean ready = false;
	private int readyTick = 0;
    private static final int MAX_COOL_TIME_TICK = 30;

	@Override
	public void onTick(Player player, Parkourability parkourability, IStamina stamina) {
		if (coolTimeTick > 0) {
			coolTimeTick--;
		}
	}

	@Override
	public void onClientTick(Player player, Parkourability parkourability, IStamina stamina) {
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
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
        Vec3 movement = player.getDeltaMovement();
        if (movement.lengthSqr() < 0.001) return false;
        movement = movement.multiply(1, 0, 1).normalize();
        startInfo.putDouble(movement.x()).putDouble(movement.z());
		return (player.onGround()
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
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
		return !((getDoingTick() > 1 && player.onGround())
				|| player.isFallFlying()
				|| player.isInWaterOrBubble()
				|| player.isInLava()
		);
	}

	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
        Vec3 jumpDirection = new Vec3(startData.getDouble(), 0, startData.getDouble());
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.CATLEAP.get(), 1, 1);
		coolTimeTick = MAX_COOL_TIME_TICK;
        spawnJumpEffect(player, jumpDirection);
        player.jumpFromGround();
        Vec3 motionVec = player.getDeltaMovement();
        player.setDeltaMovement(jumpDirection.x(), motionVec.y() * 1.16667, jumpDirection.z());
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new CatLeapAnimator());
	}

	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		Vec3 jumpDirection = new Vec3(startData.getDouble(), 0, startData.getDouble());
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.CATLEAP.get(), 1, 1);
		spawnJumpEffect(player, jumpDirection);
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new CatLeapAnimator());
	}

	@Override
	public boolean wantsToShowStatusBar(LocalPlayer player, Parkourability parkourability) {
		return coolTimeTick > 0;
	}

	@Override
	public float getStatusValue(LocalPlayer player, Parkourability parkourability) {
		return coolTimeTick / (float) MAX_COOL_TIME_TICK;
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnJumpEffect(Player player, Vec3 jumpDirection) {
		if (!ParCoolConfig.Client.Booleans.EnableActionParticles.get()) return;
		Level level = player.level();
		Vec3 pos = player.position();
		var blockPosVec = pos.add(0, -0.2, 0);
		BlockPos blockpos = new BlockPos(
				(int) Math.floor(blockPosVec.x()),
				(int) Math.floor(blockPosVec.y()),
				(int) Math.floor(blockPosVec.z())
		);
		if (!level.isLoaded(blockpos)) return;
		float width = player.getBbWidth();
		BlockState blockstate = level.getBlockState(blockpos);
		if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
			for (int i = 0; i < 20; i++) {
				Vec3 particlePos = new Vec3(
						pos.x() + (jumpDirection.x() * -0.5 + player.getRandom().nextDouble() - 0.5D) * width,
						pos.y() + 0.1D,
						pos.z() + (jumpDirection.z() * -0.5 + player.getRandom().nextDouble() - 0.5D) * width
				);
				Vec3 particleSpeed = particlePos.subtract(pos).normalize().scale(2.5 + 8 * player.getRandom().nextDouble()).add(0, 1.5, 0);
				level.addParticle(
						new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(blockpos),
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
