package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.FlippingAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

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
	public void onTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (flipping) {
			flippingTick++;
		} else {
			flippingTick = 0;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onClientTick(Player player, Parkourability parkourability, Stamina stamina) {
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
				stopFlipping(player);
			}
		}
		if (flipping && flippingTick <= 1) {
			Animation animation = Animation.get(player);
			if (animation != null) {
				animation.setAnimator(new FlippingAnimator(player.getXRot()));
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void startFlipping(Player player, Parkourability parkourability, Stamina stamina) {
		flipping = true;
		flippingTick = 0;
		if (KeyBindings.getKeyBack().isDown()) {
			direction = FlippingDirection.Back;
		} else {
			direction = FlippingDirection.Front;
		}
		synchronizeExplicitly(player);
		stamina.consume(parkourability.getActionInfo().getStaminaConsumptionFlipping(), player);
	}

	private void stopFlipping(Player player) {
		synchronizeExplicitly(player);
		flipping = false;
		flippingTick = 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {

	}

	@Override
	public void restoreState(ByteBuffer buffer) {
		flipping = BufferUtil.getBoolean(buffer);
		direction = FlippingDirection.getFromCode(buffer.getInt());
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer)
				.putBoolean(flipping)
				.unwrap()
				.putInt(direction == null ? -1 : direction.getCode());
	}
}
