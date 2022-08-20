package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.WallSlideAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class WallSlide extends Action {
	private boolean sliding = false;
	private int slidingTick = 0;

	public boolean isSliding() {
		return sliding;
	}

	private Vec3 leanedWallDirection = null;

	@Nullable
	public Vec3 getLeanedWallDirection() {
		return leanedWallDirection;
	}

	@Override
	public void onTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (sliding) {
			slidingTick++;

			leanedWallDirection = WorldUtil.getWall(player);
			if (leanedWallDirection != null) {
				BlockPos leanedBlock = new BlockPos(
						player.getX() + leanedWallDirection.x(),
						player.getBoundingBox().minY + player.getBbHeight() * 0.75,
						player.getZ() + leanedWallDirection.z()
				);
				float slipperiness = player.level.getBlockState(leanedBlock).getFriction(player.level, leanedBlock, player);
				slipperiness = (float) Math.sqrt(slipperiness);
				player.fallDistance *= slipperiness;
				player.setDeltaMovement(player.getDeltaMovement().multiply(0.8, slipperiness, 0.8));
			}
		} else {
			slidingTick = 0;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onClientTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			Vec3 wall = WorldUtil.getWall(player);
			sliding = !player.isOnGround() &&
					parkourability.getPermission().canWallSlide() &&
					!parkourability.getFastRun().isRunning() &&
					!parkourability.getDodge().isDodging() &&
					!player.getAbilities().flying &&
					player.getDeltaMovement().y() <= 0 &&
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
	@OnlyIn(Dist.CLIENT)
	public void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {

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
