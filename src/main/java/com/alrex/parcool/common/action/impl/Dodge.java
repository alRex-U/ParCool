package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.api.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.api.compatibility.Vec3Wrapper;
import com.alrex.parcool.client.animation.impl.DodgeAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Dodge extends Action {
	public static final int MAX_TICK = 11;
	private static final BehaviorEnforcer.ID ID_JUMP_CANCEL = BehaviorEnforcer.newID();
	private static final BehaviorEnforcer.ID ID_DESCEND_EDGE = BehaviorEnforcer.newID();

	private static int getMaxCoolTime(ActionInfo info) {
		return Math.max(
				info.getClientSetting().get(ParCoolConfig.Client.Integers.DodgeCoolTime),
				info.getServerLimitation().get(ParCoolConfig.Server.Integers.DodgeCoolTime)
		);
	}

	private static int getMaxSuccessiveDodge(ActionInfo info) {
		return Math.min(
				info.getClientSetting().get(ParCoolConfig.Client.Integers.MaxSuccessiveDodgeCount),
				info.getServerLimitation().get(ParCoolConfig.Server.Integers.MaxSuccessiveDodgeCount)
		);
	}

	private static int getSuccessiveCoolTime(ActionInfo info) {
		return Math.max(
				info.getClientSetting().get(ParCoolConfig.Client.Integers.SuccessiveDodgeCoolTime),
				info.getServerLimitation().get(ParCoolConfig.Server.Integers.SuccessiveDodgeCoolTime)
		);
	}

	public enum DodgeDirection {
		Front, Back, Left, Right;

		public DodgeDirection inverse() {
			switch (this) {
				case Front:
					return Back;
				case Back:
					return Front;
				case Left:
					return Right;
				case Right:
					return Left;
			}
			return Front;
		}

		public DodgeDirection right() {
			switch (this) {
				case Front:
					return Right;
				case Right:
					return Back;
				case Back:
					return Left;
				case Left:
					return Front;
			}
			return Front;
		}

		public DodgeDirection left() {
			switch (this) {
				case Front:
					return Left;
				case Left:
					return Back;
				case Back:
					return Right;
				case Right:
					return Front;
			}
			return Front;
		}
	}

	private DodgeDirection dodgeDirection = null;
	private int coolTime = 0;
	private int successivelyCount = 0;
	private int successivelyCoolTick = 0;

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
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
		return Math.min(
				info.getClientSetting().get(ParCoolConfig.Client.Doubles.DodgeSpeedModifier),
				info.getServerLimitation().get(ParCoolConfig.Server.Doubles.MaxDodgeSpeedModifier)
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		boolean enabledDoubleTap = ParCoolConfig.Client.Booleans.EnableDoubleTappingForDodge.get();
		DodgeDirection direction = null;
		if (enabledDoubleTap) {
			if (KeyRecorder.keyBack.isDoubleTapped()) direction = DodgeDirection.Back;
			if (KeyRecorder.keyLeft.isDoubleTapped()) direction = DodgeDirection.Left;
			if (KeyRecorder.keyRight.isDoubleTapped()) direction = DodgeDirection.Right;
		}
		if (direction == null && KeyRecorder.keyDodge.isPressed()) {
			if (KeyBindings.isKeyBackDown()) direction = DodgeDirection.Back;
			if (KeyBindings.isKeyForwardDown()) direction = DodgeDirection.Front;
			if (KeyBindings.isKeyLeftDown()) direction = DodgeDirection.Left;
			if (KeyBindings.isKeyRightDown()) direction = DodgeDirection.Right;
		}
		if (direction == null) return false;
		direction = AdditionalMods.betterThirdPerson().handleCustomCameraRotationForDodge(direction);
		direction = AdditionalMods.shoulderSurfingManager().handleCustomCameraRotationForDodge(direction);
		startInfo.putInt(direction.ordinal());
		return ((parkourability.getAdditionalProperties().getLandingTick() > 5 || parkourability.getAdditionalProperties().getPreviousNotLandingTick() < 2)
				&& player.isOnGround()
				&& !isInSuccessiveCoolDown(parkourability.getActionInfo())
				&& coolTime <= 0
				&& !player.isInWaterOrBubble()
				&& !player.isShiftKeyDown()
				&& !stamina.isExhausted()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !parkourability.get(Roll.class).isDoing()
				&& !parkourability.get(Tap.class).isDoing()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		return !(parkourability.get(Roll.class).isDoing()
				|| parkourability.get(ClingToCliff.class).isDoing()
				|| getDoingTick() >= MAX_TICK
				|| player.isInWaterOrBubble()
				|| player.isFallFlying()
				|| player.isFlying()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		dodgeDirection = DodgeDirection.values()[startData.getInt()];
		coolTime = getMaxCoolTime(parkourability.getActionInfo());
		if (successivelyCount < getMaxSuccessiveDodge(parkourability.getActionInfo())) {
			successivelyCount++;
		}
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.DODGE.get(), 1f, 1f);
		successivelyCoolTick = getSuccessiveCoolTime(parkourability.getActionInfo());

		if (!player.isOnGround()) return;
		Vec3Wrapper lookVec = VectorUtil.fromYawDegree(player.getYHeadRot());
		Vec3Wrapper dodgeVec = Vec3Wrapper.ZERO;
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
		parkourability.getBehaviorEnforcer().addMarkerCancellingJump(ID_JUMP_CANCEL, this::isDoing);
		if (!parkourability.getClientInfo().get(ParCoolConfig.Client.Booleans.CanGetOffStepsWhileDodge)) {
			parkourability.getBehaviorEnforcer().addMarkerCancellingDescendFromEdge(ID_DESCEND_EDGE, this::isDoing);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInOtherClient(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
		dodgeDirection = DodgeDirection.values()[startData.getInt()];
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.DODGE.get(), 1f, 1f);
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

	@Override
	public boolean wantsToShowStatusBar(ClientPlayerWrapper player, Parkourability parkourability) {
		return coolTime > 0 || isInSuccessiveCoolDown(parkourability.getActionInfo());
	}

	@Override
	public float getStatusValue(ClientPlayerWrapper player, Parkourability parkourability) {
		ActionInfo info = parkourability.getActionInfo();
		int maxCoolTime = getMaxCoolTime(info);
		int successiveMaxCoolTime = getSuccessiveCoolTime(info);
		return Math.max(
				(float) getCoolTime() / maxCoolTime,
				isInSuccessiveCoolDown(info) ? (float) (getSuccessivelyCoolTick()) / (successiveMaxCoolTime) : 0
		);
	}
}
