package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.WallSlideAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class WallSlide extends Action {
	private Vector3d leanedWallDirection = null;

	@Nullable
	public Vector3d getLeanedWallDirection() {
		return leanedWallDirection;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		return canContinue(player, parkourability, stamina);
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		Vector3d wall = WorldUtil.getWall(player);
		return (wall != null
				&& !player.isOnGround()
				&& parkourability.getActionInfo().can(WallSlide.class)
				&& !parkourability.get(FastRun.class).isDoing()
				&& !parkourability.get(Dodge.class).isDoing()
				&& !player.abilities.flying
				&& player.getDeltaMovement().y() <= 0
				&& KeyBindings.getKeyWallSlide().isDown()
				&& !stamina.isExhausted()
				&& !parkourability.get(ClingToCliff.class).isDoing()
				&& parkourability.get(ClingToCliff.class).getNotDoingTick() > 12
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onWorkingTickInClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		Animation animation = Animation.get(player);
		if (animation != null && !animation.hasAnimator()) {
			animation.setAnimator(new WallSlideAnimator());
		}
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnWorking;
	}

	@Override
	public void onWorkingTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
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
			player.setDeltaMovement(player.getDeltaMovement().multiply(0.8, slipperiness, 0.8));
		}
	}
}
