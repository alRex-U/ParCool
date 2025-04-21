package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.Animation;
import com.alrex.parcool.client.animation.impl.DodgeAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.compat.shoulderSurfing.ShoulderSurfingCompat;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.EntityUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Dodge extends Action {
	public static final int MAX_TICK = 11;

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
		Front, Back, Left, Right
	}

	private DodgeDirection dodgeDirection = null;
	private int coolTime = 0;
	private int successivelyCount = 0;
	private int successivelyCoolTick = 0;

	@OnlyIn(Dist.CLIENT)
	@Override
    public void onClientTick(Player player, Parkourability parkourability) {
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
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
		boolean enabledDoubleTap = ParCoolConfig.Client.Booleans.EnableDoubleTappingForDodge.get();
		DodgeDirection direction = null;
		if (enabledDoubleTap) {
			if (KeyRecorder.keyBack.isDoubleTapped()) direction = DodgeDirection.Back;
			if (KeyRecorder.keyLeft.isDoubleTapped()) direction = DodgeDirection.Left;
			if (KeyRecorder.keyRight.isDoubleTapped()) direction = DodgeDirection.Right;
		}
		if (direction == null && KeyRecorder.keyDodge.isPressed()) {
			if (KeyBindings.getKeyForward().isDown() || ShoulderSurfingCompat.isCameraDecoupled()) direction = DodgeDirection.Front;
			else if (KeyBindings.getKeyBack().isDown()) direction = DodgeDirection.Back;
			else if (KeyBindings.getKeyLeft().isDown()) direction = DodgeDirection.Left;
			else if (KeyBindings.getKeyRight().isDown()) direction = DodgeDirection.Right;
		}
		if (direction == null) return false;
		startInfo.putInt(direction.ordinal());
		return (parkourability.getAdditionalProperties().getLandingTick() > 5
				&& !isInSuccessiveCoolDown(parkourability.getActionInfo())
				&& coolTime <= 0
				&& player.onGround()
                && !player.isInWaterOrBubble()
				&& !player.isShiftKeyDown()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !parkourability.get(Roll.class).isDoing()
				&& !parkourability.get(Tap.class).isDoing()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
    public boolean canContinue(Player player, Parkourability parkourability) {
		return !(parkourability.get(Roll.class).isDoing()
				|| parkourability.get(ClingToCliff.class).isDoing()
				|| getDoingTick() >= MAX_TICK
				|| player.isInWaterOrBubble()
				|| player.isFallFlying()
				|| player.getAbilities().flying
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		dodgeDirection = DodgeDirection.values()[startData.getInt()];
		coolTime = getMaxCoolTime(parkourability.getActionInfo());
		if (successivelyCount < getMaxSuccessiveDodge(parkourability.getActionInfo())) {
			successivelyCount++;
		}
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.DODGE.get(), 1f, 1f);
		successivelyCoolTick = getSuccessiveCoolTime(parkourability.getActionInfo());

		if (!player.onGround()) return;
		Vec3 dodgeVec;
		if (ShoulderSurfingCompat.isCameraDecoupled()) {
			dodgeVec = EntityUtil.GetCameraLookAngle();
		} else {
			dodgeVec = VectorUtil.fromYawDegree(player.getYHeadRot());
			dodgeVec = switch (dodgeDirection) {
				case Front -> dodgeVec;
				case Back -> dodgeVec.reverse();
				case Right -> dodgeVec.yRot((float) Math.PI / -2);
				case Left -> dodgeVec.yRot((float) Math.PI / 2);
			};
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
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
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
	public boolean wantsToShowStatusBar(LocalPlayer player, Parkourability parkourability) {
		return coolTime > 0 || isInSuccessiveCoolDown(parkourability.getActionInfo());
	}

	@Override
	public float getStatusValue(LocalPlayer player, Parkourability parkourability) {
		ActionInfo info = parkourability.getActionInfo();
		int maxCoolTime = getMaxCoolTime(info);
		int successiveMaxCoolTime = getSuccessiveCoolTime(info);
		return Math.max(
				(float) getCoolTime() / maxCoolTime,
				isInSuccessiveCoolDown(info) ? (float) (getSuccessivelyCoolTick()) / (successiveMaxCoolTime) : 0
		);
	}
}
