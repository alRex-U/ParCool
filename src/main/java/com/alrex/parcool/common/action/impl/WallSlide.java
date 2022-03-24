package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.WallSlideAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.common.network.SyncWallSlideMessage;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class WallSlide extends Action {
	private boolean sliding = false;
	private int slidingTick = 0;

	public boolean isSliding() {
		return sliding;
	}

	@Override
	public void onTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (sliding) {
			slidingTick++;
			if (parkourability.getAdditionalProperties().getNotLandingTick() > 10 &&
					parkourability.getClingToCliff().getNotClingTick() > 10 &&
					slidingTick > 10
			) {
				player.fallDistance = 2;
			}
		} else {
			slidingTick = 0;
		}
	}

	@Override
	public void onClientTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			sliding = !player.isOnGround() &&
					parkourability.getPermission().canWallSlide() &&
					!parkourability.getDodge().isDodging() &&
					WorldUtil.getWall(player) != null &&
					KeyBindings.getKeyBindWallSlide().isDown() &&
					!stamina.isExhausted() &&
					!parkourability.getClingToCliff().isCling() &&
					parkourability.getClingToCliff().getNotClingTick() > 12;
		}
		if (sliding) {
			stamina.consume(parkourability.getActionInfo().getStaminaConsumeWallSlide(), parkourability.getActionInfo());
			player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0.87, 1));

			Animation animation = Animation.get(player);
			if (animation != null) {
				animation.setAnimator(new WallSlideAnimator());
			}
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {

	}

	@Override
	public boolean needSynchronization(ByteBuffer savedInstanceState) {
		return BufferUtil.getBoolean(savedInstanceState) != sliding;
	}

	@Override
	public void sendSynchronization(Player player) {
		SyncWallSlideMessage.sync(player, this);
	}

	@Override
	public void synchronize(Object message) {
		if (message instanceof SyncWallSlideMessage) {
			this.sliding = ((SyncWallSlideMessage) message).isSliding();
		}

	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer).putBoolean(sliding).unwrap();
	}
}
