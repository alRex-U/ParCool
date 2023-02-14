package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.DodgeAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.EntityUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;


public class Dodge extends Action {
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
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (coolTime > 0) coolTime--;
		if (successivelyCoolTick > 0) {
			successivelyCoolTick--;
		} else {
			successivelyCount = 0;
		}
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startInfo) {
		boolean enabledDoubleTap = !ParCoolConfig.CONFIG_CLIENT.disableDoubleTappingForDodge.get();
		DodgeDirection direction = null;
		if (enabledDoubleTap) {
			if (KeyRecorder.keyBack.isDoubleTapped()) direction = DodgeDirection.Back;
			if (KeyRecorder.keyLeft.isDoubleTapped()) direction = DodgeDirection.Left;
			if (KeyRecorder.keyRight.isDoubleTapped()) direction = DodgeDirection.Right;
			if (ParCoolConfig.CONFIG_CLIENT.canFrontDodgeByDoubleTap.get() && KeyRecorder.keyForward.isDoubleTapped())
				direction = DodgeDirection.Front;
		}
		if (direction == null && KeyRecorder.keyDodge.isPressed()) {
			if (KeyBindings.getKeyBack().isDown()) direction = DodgeDirection.Back;
			if (KeyBindings.getKeyForward().isDown()) direction = DodgeDirection.Front;
			if (KeyBindings.getKeyLeft().isDown()) direction = DodgeDirection.Left;
			if (KeyBindings.getKeyRight().isDown()) direction = DodgeDirection.Right;
		}
		if (direction == null) return false;
		startInfo.putInt(direction.getCode());
		return (parkourability.getPermission().canDodge()
				&& !isInSuccessiveCoolDown()
				&& coolTime <= 0
				&& player.isOnGround()
				&& !player.isShiftKeyDown()
				&& !stamina.isExhausted()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return !(parkourability.get(Roll.class).isDoing()
				|| parkourability.get(ClingToCliff.class).isDoing()
				|| player.isOnGround()
				|| player.isInWaterOrBubble()
				|| player.isFallFlying()
				|| player.abilities.flying
				|| !parkourability.getPermission().canDodge()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startData) {
		dodgeDirection = DodgeDirection.getFromCode(startData.getInt());
		stamina.consume(parkourability.getActionInfo().getStaminaConsumptionDodge(), player);
		Vector3d lookVec = player.getLookAngle();
		lookVec = new Vector3d(lookVec.x(), 0, lookVec.z()).normalize();
		final double jump = 0.3;
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
		coolTime = parkourability.getActionInfo().getMaxDodgeCoolTick();
		if (successivelyCount < 3) {
			successivelyCount++;
		}
		successivelyCoolTick = parkourability.getActionInfo().getMaxDodgeCoolTick() * 3;
		dodgeVec = dodgeVec.scale(ParCoolConfig.CONFIG_CLIENT.dodgeSpeedModifier.get());
		EntityUtil.addVelocity(player, new Vector3d(dodgeVec.x(), jump, dodgeVec.z()));
		Animation animation = Animation.get(player);
		if (animation != null) animation.setAnimator(new DodgeAnimator());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
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
}
