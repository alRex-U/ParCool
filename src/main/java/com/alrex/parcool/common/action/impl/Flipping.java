package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.FlippingAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class Flipping extends Action {

	public enum FlippingDirection {
		Front, Back
	}

	public boolean isFlipping() {
		return flipping;
	}

	public FlippingDirection getDirection() {
		return direction;
	}

	private FlippingDirection direction = null;
	private int flippingTick = 0;
	private boolean flipping = false;

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (flipping) {
			flippingTick++;
		} else {
			flippingTick = 0;
		}
	}

	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			if (
					!flipping &&
							parkourability.getPermission().canFlipping() &&
							parkourability.getAdditionalProperties().getNotLandingTick() <= 1 &&
							KeyBindings.getKeyRight().isDown() && KeyRecorder.keyRight.getTickKeyDown() < 3 &&
							KeyBindings.getKeyLeft().isDown() && KeyRecorder.keyLeft.getTickKeyDown() < 3
			) {
				player.jumpFromGround();
				startFlipping(player, parkourability, stamina);
			}
			if (player.isOnGround() && flippingTick > 2) {
				stopFlipping();
			}
		}
	}

	private void startFlipping(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		flipping = true;
		flippingTick = 0;
		if (KeyBindings.getKeyBack().isDown()) {
			direction = FlippingDirection.Back;
		} else {
			direction = FlippingDirection.Front;
		}
		Animation animation = Animation.get(player);
		if (animation == null) return;
		animation.setAnimator(new FlippingAnimator(player.xRot));
	}

	private void stopFlipping() {
		flipping = false;
		flippingTick = 0;
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {

	}

	@Override
	public void restoreState(ByteBuffer buffer) {

	}

	@Override
	public void saveState(ByteBuffer buffer) {

	}
}
