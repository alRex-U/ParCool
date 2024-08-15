package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.FlippingAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

public class Flipping extends Action {
	public enum ControlType {
		PressRightAndLeft, TapMovementAndJump, PressFlippingKey;

		@OnlyIn(Dist.CLIENT)
		public boolean isInputDone(boolean justJumped) {
			switch (this) {
				case PressRightAndLeft:
					return KeyBindings.getKeyRight().isDown()
							&& KeyRecorder.keyRight.getTickKeyDown() < 3
							&& KeyBindings.getKeyLeft().isDown()
							&& KeyRecorder.keyLeft.getTickKeyDown() < 3;
				case PressFlippingKey:
					return KeyRecorder.keyFlipping.isPressed();
				case TapMovementAndJump:
					return justJumped && (
							(KeyBindings.getKeyForward().isDown() && KeyRecorder.keyForward.getTickKeyDown() < 4)
									|| (KeyBindings.getKeyBack().isDown() && KeyRecorder.keyBack.getTickKeyDown() < 4)
					);
			}
			return false;
		}
	}

	public enum Direction {
		Front, Back;
	}

	private boolean justJumped = false;

	public void onJump(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		justJumped = true;
	}
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Direction fDirection;
		if (KeyBindings.getKeyBack().isDown()) {
			fDirection = Direction.Back;
		} else {
			fDirection = Direction.Front;
		}
		ControlType control = ParCoolConfig.Client.FlipControl.get();
		startInfo
				.putInt(control.ordinal())
				.putInt(fDirection.ordinal());
		boolean input = control.isInputDone(justJumped);
		justJumped = false;
		return (input
                && !player.isShiftKeyDown()
				&& parkourability.getActionInfo().can(Flipping.class)
				&& !parkourability.get(Dive.class).isDoing()
				&& !parkourability.get(ChargeJump.class).isDoing()
				&& !parkourability.getCancelMarks().cancelJump()
				&& !stamina.isExhausted()
				&& parkourability.getAdditionalProperties().getNotLandingTick() <= 1
		);
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return !player.isOnGround() || getDoingTick() <= 10;
	}

	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		ControlType control = ControlType.values()[startData.getInt()];
		if (control != ControlType.TapMovementAndJump) player.jumpFromGround();
		stamina.consume(parkourability.getActionInfo().getStaminaConsumptionOf(Flipping.class));
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new FlippingAnimator(
					Direction.values()[startData.getInt()]
			));
		}
	}

	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		startData.position(4); // skip (int * 1)
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new FlippingAnimator(
					Direction.values()[startData.getInt()]
			));
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}
}
