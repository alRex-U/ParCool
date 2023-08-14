package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.DodgeAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;


public class Dodge extends Action {
	public static final int MAX_COOL_DOWN_TICK = 10;
	public static final int MAX_TICK = 11;
	public enum DodgeDirection {
		Front, Back, Left, Right;
	}

	private DodgeDirection dodgeDirection = null;
	private int coolTime = 0;
	private int successivelyCount = 0;
	private int successivelyCoolTick = 0;

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		if (coolTime > 0) coolTime--;
		if (successivelyCoolTick > 0) {
			successivelyCoolTick--;
		} else {
			successivelyCount = 0;
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		boolean enabledDoubleTap = ParCoolConfig.Client.Booleans.EnableDoubleTappingForDodge.get();
		DodgeDirection direction = null;
		if (enabledDoubleTap) {
			if (KeyRecorder.keyBack.isDoubleTapped()) direction = DodgeDirection.Back;
			if (KeyRecorder.keyLeft.isDoubleTapped()) direction = DodgeDirection.Left;
			if (KeyRecorder.keyRight.isDoubleTapped()) direction = DodgeDirection.Right;
		}
		if (direction == null && KeyRecorder.keyDodge.isPressed()) {
			if (KeyBindings.getKeyBack().isDown()) direction = DodgeDirection.Back;
			if (KeyBindings.getKeyForward().isDown()) direction = DodgeDirection.Front;
			if (KeyBindings.getKeyLeft().isDown()) direction = DodgeDirection.Left;
			if (KeyBindings.getKeyRight().isDown()) direction = DodgeDirection.Right;
		}
		if (direction == null) return false;
		startInfo.putInt(direction.ordinal());
		return (parkourability.getActionInfo().can(Dodge.class)
				&& !isInSuccessiveCoolDown()
				&& coolTime <= 0
				&& player.isOnGround()
				&& !player.isShiftKeyDown()
				&& !stamina.isExhausted()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return !(parkourability.get(Roll.class).isDoing()
				|| parkourability.get(ClingToCliff.class).isDoing()
				|| !player.isOnGround()
				|| getDoingTick() >= MAX_TICK
				|| player.isInWaterOrBubble()
				|| player.isFallFlying()
				|| player.abilities.flying
				|| !parkourability.getActionInfo().can(Dodge.class)
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		dodgeDirection = DodgeDirection.values()[startData.getInt()];
		coolTime = MAX_COOL_DOWN_TICK;
		if (successivelyCount < 3) {
			successivelyCount++;
		}
		successivelyCoolTick = MAX_COOL_DOWN_TICK * 3;
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new DodgeAnimator(dodgeDirection));
	}

	@Override
	public void onWorkingTickInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		if (!player.isOnGround()) return;
		Vector3d lookVec = VectorUtil.fromYawDegree(player.getYHeadRot());
		Vector3d dodgeVec = Vector3d.ZERO;
		switch (dodgeDirection) {
			case Front:
				dodgeVec = lookVec;
				break;
			case Back:
				dodgeVec = lookVec.reverse();
				break;
			case Right:
				dodgeVec = lookVec.yRot((float) Math.PI / -2);
				break;
			case Left:
				dodgeVec = lookVec.yRot((float) Math.PI / 2);
				break;
		}
		dodgeVec = dodgeVec.scale(.9 * ParCoolConfig.Client.Doubles.DodgeSpeedModifier.get());
		player.setDeltaMovement(dodgeVec);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		dodgeDirection = DodgeDirection.values()[startData.getInt()];
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new DodgeAnimator(dodgeDirection));
	}

	public int getCoolTime() {
		return coolTime;
	}

	public int getSuccessivelyCoolTick() {
		return successivelyCoolTick;
	}

	public boolean isInSuccessiveCoolDown() {
		return successivelyCount >= 3;
	}

	public float getCoolDownPhase() {
		return Math.min(
				(float) (Dodge.MAX_COOL_DOWN_TICK - getCoolTime()) / Dodge.MAX_COOL_DOWN_TICK,
				isInSuccessiveCoolDown() ? (float) (Dodge.MAX_COOL_DOWN_TICK * 3 - getSuccessivelyCoolTick()) / (Dodge.MAX_COOL_DOWN_TICK * 3.0f) : 1
		);
	}
}
