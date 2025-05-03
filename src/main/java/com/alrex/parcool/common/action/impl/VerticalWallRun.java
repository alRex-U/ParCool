package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.VerticalWallRunAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.BlockStateWrapper;
import com.alrex.parcool.compatibility.LevelWrapper;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class VerticalWallRun extends Action {
	private double playerYSpeed = 0;
	private Vec3Wrapper wallDirection = null;

	@Override
	public void onTick(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		playerYSpeed = player.getDeltaMovement().y();
	}

	@Override
	public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		int tickAfterJump = parkourability.getAdditionalProperties().getTickAfterLastJump();
		Vec3Wrapper lookVec = player.getLookAngle();
		boolean able = !stamina.isExhausted()
				&& (Math.abs(player.getDeltaMovement().y()) <= player.getBbHeight() / 5)
				&& (4 < tickAfterJump && tickAfterJump < 13)
				&& getNotDoingTick() > 15
				&& !player.isFallFlying()
				&& KeyBindings.isKeyJumpDown()
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
			Vec3Wrapper wall = WorldUtil.getWall(player);
			if (wall == null) return false;
			wall = wall.normalize();
			if (wall.dot(VectorUtil.fromYawDegree(player.getYHeadRot())) > 0.93) {
				double height = WorldUtil.getWallHeight(player, wall, player.getBbHeight() * 2.2, 0.2);
				if (height > player.getBbHeight() * 1.3) {
					BlockPos targetBlock = new BlockPos(
							player.getX() + wall.x(),
							player.getBoundingBox().minY + player.getBbHeight() * 0.5,
							player.getZ() + wall.z()
					);
					if (!player.isEveryLoaded(targetBlock)) return false;
					float slipperiness = player.getSlipperiness(targetBlock);
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
	public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		Vec3Wrapper wall = WorldUtil.getWall(player);
		if (wall == null) return false;
		wall = wall.normalize();
		return (wall.dot(VectorUtil.fromYawDegree(player.getYHeadRot())) > 0.93
				&& playerYSpeed > 0)
				|| getDoingTick() > 30;
	}

	@Override
	public void onStartInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
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
	public void onStartInOtherClient(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
		startData.position(8 + 4); // skip (double * 1) and (float * 1)
		wallDirection = new Vec3Wrapper(startData.getDouble(), startData.getDouble(), startData.getDouble());
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.VERTICAL_WALL_RUN.get(), 1f, 1f);
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new VerticalWallRunAnimator());
		}
	}

	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, PlayerWrapper player, Parkourability parkourability) {
		if (wallDirection != null && isDoing()) {
			player.setYHeadRot((float)VectorUtil.toYawDegree(wallDirection));
            player.setAllYBodyRot(player.getYHeadRot());
		}
	}

	@Override
	public void onWorkingTickInClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		spawnRunningParticle(player);
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	@OnlyIn(Dist.CLIENT)
	public void spawnRunningParticle(PlayerWrapper player) {
		if (!ParCoolConfig.Client.Booleans.EnableActionParticles.get()) return;
		if (wallDirection == null) return;
		LevelWrapper level = player.getLevel();
		Vec3Wrapper pos = player.position();
		BlockPos leanedBlock = new BlockPos(
				pos.add(wallDirection.x(), player.getBbHeight() * 0.25, wallDirection.z())
		);
		if (!level.isLoaded(leanedBlock)) return;
		float width = player.getBbWidth();
		BlockStateWrapper blockstate = level.getBlockState(leanedBlock);

		Vec3Wrapper normalizedWallVec = wallDirection.normalize();
		Vec3Wrapper orthogonalToWallVec = normalizedWallVec.yRot((float) (Math.PI / 2));
		if (blockstate.getRenderShape() != BlockRenderType.INVISIBLE) {
			Vec3Wrapper particlePos = new Vec3Wrapper(
					pos.x() + (normalizedWallVec.x() * 0.4 + orthogonalToWallVec.x() * (player.getRandom().nextDouble() - 0.5D)) * width,
					pos.y() + 0.1D + 0.3 * player.getRandom().nextDouble(),
					pos.z() + (normalizedWallVec.z() * 0.4 + orthogonalToWallVec.z() * (player.getRandom().nextDouble() - 0.5D)) * width
			);
			Vec3Wrapper particleSpeed = normalizedWallVec
					.reverse()
					.yRot((float) (Math.PI * 0.2 * (player.getRandom().nextDouble() - 0.5)))
					.scale(2 + 4 * player.getRandom().nextDouble())
					.add(0, 0.5, 0);
			level.addParticle(
					blockstate.getBlockParticleData(ParticleTypes.BLOCK, leanedBlock),
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
