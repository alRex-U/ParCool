package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.VerticalWallRunAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class VerticalWallRun extends Action {
	private double playerYSpeed = 0;
	private Vec3 wallDirection = null;

	@Override
	public void onTick(Player player, Parkourability parkourability, IStamina stamina) {
		playerYSpeed = player.getDeltaMovement().y();
	}

	@Override
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		int tickAfterJump = parkourability.getAdditionalProperties().getTickAfterLastJump();
		Vec3 lookVec = player.getLookAngle();
		boolean able = !stamina.isExhausted()
				&& (Math.abs(player.getDeltaMovement().y()) <= player.getBbHeight() / 5)
				&& (4 < tickAfterJump && tickAfterJump < 13)
				&& getNotDoingTick() > 15
				&& !player.isFallFlying()
				&& KeyBindings.getKeyJump().isDown()
				&& !parkourability.get(ClingToCliff.class).isDoing()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !parkourability.get(WallSlide.class).isDoing()
				&& !parkourability.get(HorizontalWallRun.class).isDoing()
				&& !parkourability.get(Vault.class).isDoing()
				&& !parkourability.get(Flipping.class).isDoing()
				&& parkourability.get(FastRun.class).getNotDashTick(parkourability.getAdditionalProperties()) < 8
				&& parkourability.get(FastRun.class).getLastDashTick() > 12
				&& lookVec.y() > 0;
		if (able) {
			Vec3 wall = WorldUtil.getWall(player);
			if (wall == null) return false;
			wall = wall.normalize();
			if (wall.dot(VectorUtil.fromYawDegree(player.getYHeadRot())) > 0.93) {
				double height = WorldUtil.getWallHeight(player, wall, player.getBbHeight() * 2.2, 0.2);
				if (height > 2.3) {
					BlockPos targetBlock = new BlockPos(
							player.getX() + wall.x(),
							player.getBoundingBox().minY + player.getBbHeight() * 0.5,
							player.getZ() + wall.z()
					);
					if (!player.level.isLoaded(targetBlock)) return false;
					float slipperiness = player.level.getBlockState(targetBlock).getFriction(player.level, targetBlock, player);
					startInfo.putDouble(height);
					startInfo.putFloat(slipperiness);
					startInfo.putDouble(wall.x());
					startInfo.putDouble(wall.y());
					startInfo.putDouble(wall.z());
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
		Vec3 wall = WorldUtil.getWall(player);
		if (wall == null) return false;
		wall = wall.normalize();
		return (wall.dot(VectorUtil.fromYawDegree(player.getYHeadRot())) > 0.93
				&& playerYSpeed > 0)
				|| getDoingTick() > 30;
	}

	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		double height = startData.getDouble();
		float slipperiness = startData.getFloat();
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1f, 0.7f);
		player.setDeltaMovement(player
				.getDeltaMovement()
				.multiply(1, 0, 1)
				.add(0, (slipperiness <= 0.8f ? 0.32 : 0.16) * Math.sqrt(height), 0)
		);
		onStartInOtherClient(player, parkourability, startData);
	}

	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		startData.position(12);
		wallDirection = new Vec3(startData.getDouble(), startData.getDouble(), startData.getDouble());
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new VerticalWallRunAnimator());
		}
	}

	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {
		if (wallDirection != null && isDoing()) {
			player.setYHeadRot((float) VectorUtil.toYawDegree(wallDirection));
			player.setYBodyRot(player.getYHeadRot());
		}
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}
}
