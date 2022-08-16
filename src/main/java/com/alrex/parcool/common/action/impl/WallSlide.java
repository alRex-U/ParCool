package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.WallSlideAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class WallSlide extends Action {
	private boolean sliding = false;
	private int slidingTick = 0;

	public boolean isSliding() {
		return sliding;
	}

	private Vector3d leanedWallDirection = null;

	@Nullable
	public Vector3d getLeanedWallDirection() {
		return leanedWallDirection;
	}

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (sliding) {
			slidingTick++;

			leanedWallDirection = WorldUtil.getWall(player);
			if (leanedWallDirection != null) {
				BlockPos leanedBlock = new BlockPos(
						player.getX() + leanedWallDirection.x(),
						player.getBoundingBox().minY + player.getBbHeight() * 0.75,
						player.getZ() + leanedWallDirection.z()
				);
				float slipperiness = player.level.getBlockState(leanedBlock).getSlipperiness(player.level, leanedBlock, player);
				slipperiness = (float) Math.sqrt(slipperiness);
				player.fallDistance *= slipperiness;
				player.setDeltaMovement(player.getDeltaMovement().multiply(1, slipperiness, 1));
			}
		} else {
			slidingTick = 0;
		}
	}

	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			Vector3d wall = WorldUtil.getWall(player);
			sliding = !player.isOnGround() &&
					parkourability.getPermission().canWallSlide() &&
					!parkourability.getDodge().isDodging() &&
					!player.abilities.flying &&
					wall != null &&
					KeyBindings.getKeyWallSlide().isDown() &&
					!stamina.isExhausted() &&
					!parkourability.getClingToCliff().isCling() &&
					parkourability.getClingToCliff().getNotClingTick() > 12;
		}
		if (sliding) {
			Animation animation = Animation.get(player);
			if (animation != null && !animation.hasAnimator()) {
				animation.setAnimator(new WallSlideAnimator());
			}
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {

	}

	@Override
	public void restoreState(ByteBuffer buffer) {
		sliding = BufferUtil.getBoolean(buffer);
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer).putBoolean(sliding);
	}
}
