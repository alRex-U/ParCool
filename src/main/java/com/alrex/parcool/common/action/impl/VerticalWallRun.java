package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.VerticalWallRunAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

import java.nio.ByteBuffer;

public class VerticalWallRun extends Action {
	private double playerYSpeed = 0;
	private Vec3 wallDirection = null;

	@Override
    public void onTick(Player player, Parkourability parkourability) {
		playerYSpeed = player.getDeltaMovement().y();
	}

	@Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
		int tickAfterJump = parkourability.getAdditionalProperties().getTickAfterLastJump();
		Vec3 lookVec = player.getLookAngle();
        boolean able = (Math.abs(player.getDeltaMovement().y()) <= player.getBbHeight() / 5)
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
			Vec3 wall = WorldUtil.getWall(player);
			if (wall == null) return false;
			wall = wall.normalize();
			if (wall.dot(VectorUtil.fromYawDegree(player.getYHeadRot())) > 0.93) {
				double height = WorldUtil.getWallHeight(player, wall, player.getBbHeight() * 2.2, 0.2);
                if (height > player.getBbHeight() * 1.3) {
					BlockPos targetBlock = new BlockPos(
							(int) (player.getX() + wall.x()),
							(int) (player.getBoundingBox().minY + player.getBbHeight() * 0.5),
							(int) (player.getZ() + wall.z())
					);
					if (!player.getCommandSenderWorld().isLoaded(targetBlock)) return false;
					float slipperiness = player.getCommandSenderWorld().getBlockState(targetBlock).getFriction(player.getCommandSenderWorld(), targetBlock, player);
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
    public boolean canContinue(Player player, Parkourability parkourability) {
		Vec3 wall = WorldUtil.getWall(player);
		if (wall == null) return false;
		wall = wall.normalize();
		return (wall.dot(VectorUtil.fromYawDegree(player.getYHeadRot())) > 0.93
				&& playerYSpeed > 0)
				|| getDoingTick() > 30;
	}

	@Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
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
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        startData.position(8 + 4); // skip (double * 1) and (float * 1)
		wallDirection = new Vec3(startData.getDouble(), startData.getDouble(), startData.getDouble());
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.VERTICAL_WALL_RUN.get(), 1f, 1f);
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new VerticalWallRunAnimator());
		}
	}

	@Override
    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
		if (wallDirection != null && isDoing()) {
			player.setYHeadRot((float) VectorUtil.toYawDegree(wallDirection));
            player.yBodyRotO = player.yBodyRot = player.getYHeadRot();
		}
	}

    @Override
    public void onWorkingTickInClient(Player player, Parkourability parkourability) {
        spawnRunningParticle(player);
    }

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}

	@OnlyIn(Dist.CLIENT)
	public void spawnRunningParticle(Player player) {
		if (!ParCoolConfig.Client.Booleans.EnableActionParticles.get()) return;
		if (wallDirection == null) return;
		Level level = player.level();
		Vec3 pos = player.position();
        BlockPos leanedBlock = new BlockPos(
                (int) Math.floor(pos.x() + wallDirection.x()),
                (int) Math.floor(pos.y() + player.getBbHeight() * 0.25),
                (int) Math.floor(pos.z() + wallDirection.z())
        );
		if (!level.isLoaded(leanedBlock)) return;
		float width = player.getBbWidth();
		BlockState blockstate = level.getBlockState(leanedBlock);

        Vec3 normalizedWallVec = wallDirection.normalize();
        Vec3 orthogonalToWallVec = normalizedWallVec.yRot((float) (Math.PI / 2));
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            Vec3 particlePos = new Vec3(
                    pos.x() + (normalizedWallVec.x() * 0.4 + orthogonalToWallVec.x() * (player.getRandom().nextDouble() - 0.5D)) * width,
                    pos.y() + 0.1D + 0.3 * player.getRandom().nextDouble(),
                    pos.z() + (normalizedWallVec.z() * 0.4 + orthogonalToWallVec.z() * (player.getRandom().nextDouble() - 0.5D)) * width
            );
            Vec3 particleSpeed = normalizedWallVec
                    .reverse()
                    .yRot((float) (Math.PI * 0.2 * (player.getRandom().nextDouble() - 0.5)))
                    .scale(2 + 4 * player.getRandom().nextDouble())
                    .add(0, 0.5, 0);
            level.addParticle(
					new BlockParticleOption(ParticleTypes.BLOCK, blockstate),
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
