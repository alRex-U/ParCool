package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.DodgeAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.EntityUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;


public class Dodge extends Action {
	public static final int MAX_COOL_DOWN_TICK = 10;

	public enum DodgeDirection {
		Front, Back, Left, Right;

		int getCode() {
			switch (this) {
				case Front:
					return 0;
				case Back:
					return 1;
				case Left:
					return 2;
				case Right:
					return 3;
			}
			return -1;
		}

		public static DodgeDirection getFromCode(int code) {
			switch (code) {
				case 1:
					return Back;
				case 2:
					return Left;
				case 3:
					return Right;
				default:
					return Front;
			}
		}
	}

	private DodgeDirection dodgeDirection = null;
	private int coolTime = 0;
	private int successivelyCount = 0;
	private int successivelyCoolTick = 0;

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(Player player, Parkourability parkourability, IStamina stamina) {
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
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		boolean enabledDoubleTap = !ParCoolConfig.CONFIG_CLIENT.disableDoubleTappingForDodge.get();
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
		startInfo.putInt(direction.getCode());
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
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
		return !(parkourability.get(Roll.class).isDoing()
				|| parkourability.get(ClingToCliff.class).isDoing()
				|| player.isOnGround()
				|| player.isInWaterOrBubble()
				|| player.isFallFlying()
				|| player.getAbilities().flying
				|| !parkourability.getActionInfo().can(Dodge.class)
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		dodgeDirection = DodgeDirection.getFromCode(startData.getInt());
		Vec3 lookVec = player.getLookAngle();
		lookVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();
		final double jump = 0.3;
		Vec3 dodgeVec = Vec3.ZERO;
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
		coolTime = MAX_COOL_DOWN_TICK;
		if (successivelyCount < 3) {
			successivelyCount++;
		}
		successivelyCoolTick = MAX_COOL_DOWN_TICK * 3;
		dodgeVec = dodgeVec.scale(0.6 * ParCoolConfig.CONFIG_CLIENT.dodgeSpeedModifier.get());
		EntityUtil.addVelocity(player, new Vec3(dodgeVec.x, jump, dodgeVec.z));
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new DodgeAnimator());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		dodgeDirection = DodgeDirection.getFromCode(startData.getInt());
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new DodgeAnimator());
	}

	public DodgeDirection getDodgeDirection() {
		return dodgeDirection;
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
