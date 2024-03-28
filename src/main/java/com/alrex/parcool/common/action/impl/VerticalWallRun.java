package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.VerticalWallRunAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class VerticalWallRun extends Action {
	private double playerYSpeed = 0;
	private Vector3d wallDirection = null;

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		playerYSpeed = player.getDeltaMovement().y();
	}

	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		int tickAfterJump = parkourability.getAdditionalProperties().getTickAfterLastJump();
		Vector3d lookVec = player.getLookAngle();
		boolean able = !stamina.isExhausted()
				&& (Math.abs(player.getDeltaMovement().y()) <= player.getBbHeight() / 5)
				&& (4 < tickAfterJump && tickAfterJump < 13)
				&& getNotDoingTick() > 15
				&& !player.isFallFlying()
				&& KeyBindings.getKeyJump().isDown()
				&& !parkourability.get(ClingToCliff.class).isDoing()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !parkourability.get(CatLeap.class).isDoing()
				&& !parkourability.get(WallSlide.class).isDoing()
				&& !parkourability.get(HorizontalWallRun.class).isDoing()
				&& !parkourability.get(Vault.class).isDoing()
				&& !parkourability.get(Flipping.class).isDoing()
				&& parkourability.get(FastRun.class).getNotDashTick(parkourability.getAdditionalProperties()) < 8
				&& parkourability.getAdditionalProperties().getLastSprintingTick() > 12
				&& lookVec.y() > 0;
		if (able) {
			Vector3d wall = WorldUtil.getWall(player);
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
					float slipperiness = player.level.getBlockState(targetBlock).getSlipperiness(player.level, targetBlock, player);
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
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		Vector3d wall = WorldUtil.getWall(player);
		if (wall == null) return false;
		wall = wall.normalize();
		return (wall.dot(VectorUtil.fromYawDegree(player.getYHeadRot())) > 0.93
				&& playerYSpeed > 0)
				|| getDoingTick() > 30;
	}

	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		double height = startData.getDouble();
		float slipperiness = startData.getFloat();
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.VERTICAL_WALL_RUN.get(), 1f, 1f);
		player.setDeltaMovement(player
				.getDeltaMovement()
				.multiply(1, 0, 1)
				.add(0, (slipperiness <= 0.8f ? 0.32 : 0.16) * Math.sqrt(height), 0)
		);
		onStartInOtherClient(player, parkourability, startData);
	}

	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		startData.position(8 + 4); // skip (double * 1) and (float * 1)
		wallDirection = new Vector3d(startData.getDouble(), startData.getDouble(), startData.getDouble());
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new VerticalWallRunAnimator());
		}
	}

	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
		if (wallDirection != null && isDoing()) {
			player.setYHeadRot((float) VectorUtil.toYawDegree(wallDirection));
            player.yBodyRotO = player.yBodyRot = player.getYHeadRot();
		}
	}

	@Override
	public void onWorkingTickInClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		spawnRunningParticle(player);
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	@OnlyIn(Dist.CLIENT)
	public void spawnRunningParticle(PlayerEntity player) {
		if (wallDirection == null) return;
		World level = player.level;
		Vector3d pos = player.position();
		BlockPos leanedBlock = new BlockPos(
				pos.add(wallDirection.x(), player.getBbHeight() * 0.25, wallDirection.z())
		);
		if (!level.isLoaded(leanedBlock)) return;
		float width = player.getBbWidth();
		BlockState blockstate = level.getBlockState(leanedBlock);

		Vector3d normalizedWallVec = wallDirection.normalize();
		Vector3d orthogonalToWallVec = normalizedWallVec.yRot((float) (Math.PI / 2));
		if (blockstate.getRenderShape() != BlockRenderType.INVISIBLE) {
			Vector3d particlePos = new Vector3d(
					pos.x() + (normalizedWallVec.x() * 0.4 + orthogonalToWallVec.x() * (player.getRandom().nextDouble() - 0.5D)) * width,
					pos.y() + 0.1D + 0.3 * player.getRandom().nextDouble(),
					pos.z() + (normalizedWallVec.z() * 0.4 + orthogonalToWallVec.z() * (player.getRandom().nextDouble() - 0.5D)) * width
			);
			Vector3d particleSpeed = normalizedWallVec
					.reverse()
					.yRot((float) (Math.PI * 0.2 * (player.getRandom().nextDouble() - 0.5)))
					.scale(2 + 4 * player.getRandom().nextDouble())
					.add(0, 0.5, 0);
			level.addParticle(
					new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(leanedBlock),
					particlePos.x(),
					particlePos.y(),
					particlePos.z(),
					particleSpeed.x(),
					particleSpeed.y(),
					particleSpeed.z()
			);
		}
	}
}
