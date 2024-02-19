package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.DodgeAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Dodge extends Action {
	public static final int MAX_TICK = 11;

	private static int getMaxCoolTime(ActionInfo info) {
		int value = info.getClientInformation().get(ParCoolConfig.Client.Integers.DodgeCoolTime);
		if (info.getIndividualLimitation().isEnabled())
			value = Math.max(value, info.getIndividualLimitation().get(ParCoolConfig.Server.Integers.DodgeCoolTime));
		if (info.getServerLimitation().isEnabled())
			value = Math.max(value, info.getServerLimitation().get(ParCoolConfig.Server.Integers.DodgeCoolTime));
		return value;
	}

	private static int getMaxSuccessiveDodge(ActionInfo info) {
		int value = info.getClientInformation().get(ParCoolConfig.Client.Integers.MaxSuccessiveDodgeCount);
		if (info.getIndividualLimitation().isEnabled())
			value = Math.min(value, info.getIndividualLimitation().get(ParCoolConfig.Server.Integers.MaxSuccessiveDodgeCount));
		if (info.getServerLimitation().isEnabled())
			value = Math.min(value, info.getServerLimitation().get(ParCoolConfig.Server.Integers.MaxSuccessiveDodgeCount));
		return value;
	}

	private static int getSuccessiveCoolTime(ActionInfo info) {
		int value = info.getClientInformation().get(ParCoolConfig.Client.Integers.SuccessiveDodgeCoolTime);
		if (info.getIndividualLimitation().isEnabled())
			value = Math.max(value, info.getIndividualLimitation().get(ParCoolConfig.Server.Integers.SuccessiveDodgeCoolTime));
		if (info.getServerLimitation().isEnabled())
			value = Math.max(value, info.getServerLimitation().get(ParCoolConfig.Server.Integers.SuccessiveDodgeCoolTime));
		return value;
	}

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

	public double getSpeedModifier(ActionInfo info) {
		double value = info.getClientInformation().get(ParCoolConfig.Client.Doubles.DodgeSpeedModifier);
		if (info.getServerLimitation().isEnabled())
			value = Math.min(value, info.getServerLimitation().get(ParCoolConfig.Server.Doubles.MaxDodgeSpeedModifier));
		if (info.getIndividualLimitation().isEnabled())
			value = Math.min(value, info.getIndividualLimitation().get(ParCoolConfig.Server.Doubles.MaxDodgeSpeedModifier));
		return value;
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
		return (parkourability.getAdditionalProperties().getLandingTick() > 5
				&& !isInSuccessiveCoolDown(parkourability.getActionInfo())
				&& coolTime <= 0
				&& player.isOnGround()
				&& !player.isInWaterOrBubble()
				&& !player.isShiftKeyDown()
				&& !stamina.isExhausted()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return !(parkourability.get(Roll.class).isDoing()
				|| parkourability.get(ClingToCliff.class).isDoing()
				|| getDoingTick() >= MAX_TICK
				|| player.isInWaterOrBubble()
				|| player.isFallFlying()
				|| player.abilities.flying
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		dodgeDirection = DodgeDirection.values()[startData.getInt()];
		coolTime = getMaxCoolTime(parkourability.getActionInfo());
		if (successivelyCount < getMaxSuccessiveDodge(parkourability.getActionInfo())) {
			successivelyCount++;
		}
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.DODGE.get(), 1f, 1f);
		successivelyCoolTick = getSuccessiveCoolTime(parkourability.getActionInfo());

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
		dodgeVec = dodgeVec.scale(.9 * getSpeedModifier(parkourability.getActionInfo()));
		player.setDeltaMovement(dodgeVec);

		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new DodgeAnimator(dodgeDirection));
		parkourability.getCancelMarks().addMarkerCancellingJump(this::isDoing);
		if (!parkourability.getClientInfo().get(ParCoolConfig.Client.Booleans.CanGetOffStepsWhileDodge)) {
			parkourability.getCancelMarks().addMarkerCancellingDescendFromEdge(this::isDoing);
		}
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

	public boolean isInSuccessiveCoolDown(ActionInfo info) {
		return successivelyCount >= getMaxSuccessiveDodge(info);
	}

	public float getCoolDownPhase(ActionInfo info) {
		int maxCoolTime = getMaxCoolTime(info);
		int successiveMaxCoolTime = getSuccessiveCoolTime(info);
		return Math.min(
				(float) (maxCoolTime - getCoolTime()) / maxCoolTime,
				isInSuccessiveCoolDown(info) ? (float) (successiveMaxCoolTime - getSuccessivelyCoolTick()) / (successiveMaxCoolTime) : 1
		);
	}
}
