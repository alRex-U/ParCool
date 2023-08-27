package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.CatLeapAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class CatLeap extends Action {
	private int coolTimeTick = 0;
	private boolean ready = false;
	private int readyTick = 0;
	private final int MAX_COOL_TIME_TICK = 30;

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
		return (parkourability.getActionInfo().can(CatLeap.class)
				&& player.isOnGround()
				&& !stamina.isExhausted()
				&& coolTimeTick <= 0
				&& readyTick > 0
				&& KeyRecorder.keySneak.isReleased()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return !((getDoingTick() > 1 && player.isOnGround())
				|| player.isFallFlying()
				|| player.isInWaterOrBubble()
				|| player.isInLava()
		);
	}

	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		final double catLeapYSpeed = 0.49;
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.8f, 0.7f);
		Vec3 motionVec = player.getDeltaMovement();
		Vec3 vec = new Vec3(motionVec.x, 0, motionVec.z).normalize();
		coolTimeTick = MAX_COOL_TIME_TICK;
		player.setDeltaMovement(vec.x, catLeapYSpeed, vec.z);
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new CatLeapAnimator());
	}

	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new CatLeapAnimator());
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	public float getCoolDownPhase() {
		return ((float) MAX_COOL_TIME_TICK - coolTimeTick) / MAX_COOL_TIME_TICK;
	}
}
