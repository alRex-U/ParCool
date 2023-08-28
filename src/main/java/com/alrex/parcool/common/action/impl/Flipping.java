package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.FlippingAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.impl.Animation;
import net.minecraft.world.entity.player.Player;

import java.nio.ByteBuffer;

public class Flipping extends Action {

	public enum FlippingDirection {
		Front, Back;

		public int getCode() {
			switch (this) {
				case Front:
					return 0;
				case Back:
					return 1;
			}
			return -1;
		}

		public static FlippingDirection getFromCode(int code) {
			switch (code) {
				case 0:
					return Front;
				case 1:
					return Back;
			}
			return null;
		}
	}

	@Override
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		FlippingDirection fDirection;
		if (KeyBindings.getKeyBack().isDown()) {
			fDirection = FlippingDirection.Back;
		} else {
			fDirection = FlippingDirection.Front;
		}
		startInfo.putInt(fDirection.getCode());
		return (parkourability.getActionInfo().can(Flipping.class)
				&& !stamina.isExhausted()
				&& parkourability.getAdditionalProperties().getNotLandingTick() <= 1
				&& (
				(KeyBindings.getKeyRight().isDown()
						&& KeyRecorder.keyRight.getTickKeyDown() < 3
						&& KeyBindings.getKeyLeft().isDown()
						&& KeyRecorder.keyLeft.getTickKeyDown() < 3
				) || KeyRecorder.keyFlipping.isPressed()
		)
		);
	}

	@Override
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
		return !player.onGround() || getDoingTick() <= 10;
	}

	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		player.jumpFromGround();
		stamina.consume(parkourability.getActionInfo().getStaminaConsumptionOf(Flipping.class));
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new FlippingAnimator(
					FlippingDirection.getFromCode(startData.getInt())
			));
		}
	}

	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new FlippingAnimator(
					FlippingDirection.getFromCode(startData.getInt())
			));
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}
}
