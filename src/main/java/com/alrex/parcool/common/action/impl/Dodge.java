package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.DodgeAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.Dodge.DodgeDirection;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.utilities.CameraUtil;
import com.alrex.parcool.utilities.EntityUtil;
import com.alrex.parcool.utilities.VectorUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
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
		return Math.min(
				info.getClientSetting().get(ParCoolConfig.Client.Doubles.DodgeSpeedModifier),
				info.getServerLimitation().get(ParCoolConfig.Server.Doubles.MaxDodgeSpeedModifier)
		);
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
			if (KeyBindings.isKeyBackDown()) direction = DodgeDirection.Back;
			if (KeyBindings.isKeyForwardDown()) direction = DodgeDirection.Front;
			if (KeyBindings.isKeyLeftDown()) direction = DodgeDirection.Left;
			if (KeyBindings.isKeyRightDown()) direction = DodgeDirection.Right;
		}
		if (direction == null) return false;
		Vector3d dodgeVec = KeyRecorder.getLastMoveVector();
		if (dodgeVec == null) return false;
		direction = AdditionalMods.betterThirdPerson().handleCustomCameraRotationForDodge(direction);
		direction = AdditionalMods.shoulderSurfingManager().handleCustomCameraRotationForDodge(direction);
		startInfo.putInt(direction.ordinal());
		startInfo.putDouble(dodgeVec.x());
		startInfo.putDouble(dodgeVec.z());
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
		Vector3d dodgeVec = new Vector3d(startData.getDouble(), 0, startData.getDouble());
		if (successivelyCount < getMaxSuccessiveDodge(parkourability.getActionInfo())) {
			successivelyCount++;
		}
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get()) {
			player.playSound(SoundEvents.DODGE.get(), 1f, 1f);
		}
		successivelyCoolTick = getSuccessiveCoolTime(parkourability.getActionInfo());

		if (!player.isOnGround()) return;
		dodgeVec = dodgeVec.scale(0.9 * getSpeedModifier(parkourability.getActionInfo()));
		if (CameraUtil.isCameraDecoupled()) {
			EntityUtil.setYRot(player, VectorUtil.toYaw(dodgeVec));
		}
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
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
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
	public boolean wantsToShowStatusBar(ClientPlayerEntity player, Parkourability parkourability) {
		return coolTime > 0 || isInSuccessiveCoolDown(parkourability.getActionInfo());
	}

	@Override
	public float getStatusValue(ClientPlayerEntity player, Parkourability parkourability) {
		ActionInfo info = parkourability.getActionInfo();
		int maxCoolTime = getMaxCoolTime(info);
		int successiveMaxCoolTime = getSuccessiveCoolTime(info);
		return Math.max(
				(float) getCoolTime() / maxCoolTime,
				isInSuccessiveCoolDown(info) ? (float) (getSuccessivelyCoolTick()) / (successiveMaxCoolTime) : 0
		);
	}
}
