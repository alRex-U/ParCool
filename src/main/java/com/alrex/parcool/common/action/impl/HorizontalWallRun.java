package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.HorizontalWallRunAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class HorizontalWallRun extends Action {
	public enum ControlType {
		PressKey, Auto
	}
	private int coolTime = 0;
	private float bodyYaw = 0;

	private int getMaxRunningTick(ActionInfo info) {
        Integer value = info.getClientSetting().get(ParCoolConfig.Client.Integers.WallRunContinuableTick);
        if (value == null) value = ParCoolConfig.Client.Integers.WallRunContinuableTick.DefaultValue;
        return Math.min(value, info.getServerLimitation().get(ParCoolConfig.Server.Integers.MaxWallRunContinuableTick));
	}

	private boolean wallIsRightward = false;
	private Vec3 runningWallDirection = null;
	private Vec3 runningDirection = null;

	@Override
	public void onClientTick(Player player, Parkourability parkourability, IStamina stamina) {
		if (coolTime > 0) coolTime--;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onWorkingTickInLocalClient(Player player, Parkourability parkourability, IStamina stamina) {
        Vec3 wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth() / 2);
		if (wallDirection == null) return;
		if (runningWallDirection == null) return;
		if (runningDirection == null) return;
		Vec3 lookVec = VectorUtil.fromYawDegree(player.yBodyRot);
		double differenceAngle = Math.asin(
				new Vec3(
						lookVec.x() * runningDirection.x() + lookVec.z() * runningDirection.z(), 0,
						-lookVec.x() * runningDirection.z() + lookVec.z() * runningDirection.x()
				).normalize().z()
		);
		bodyYaw = (float) VectorUtil.toYawDegree(lookVec.yRot((float) (differenceAngle / 10)));
		Vec3 movement = player.getDeltaMovement();
		BlockPos leanedBlock = new BlockPos(
				player.getX() + runningWallDirection.x(),
				player.getBoundingBox().minY + player.getBbHeight() * 0.5,
				player.getZ() + runningWallDirection.z()
		);
		if (!player.level.isLoaded(leanedBlock)) return;
		float slipperiness = player.level.getBlockState(leanedBlock).getFriction(player.level, leanedBlock, player);
		if (slipperiness <= 0.8) {
			double speedScale = 0.2;
			var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
			if (attr != null) {
				speedScale *= attr.getValue() / attr.getBaseValue();
			}
			player.setDeltaMovement(
					runningDirection.x() * speedScale,
					movement.y() * (slipperiness - 0.1) * ((double) getDoingTick()) / getMaxRunningTick(parkourability.getActionInfo()),
					runningDirection.z() * speedScale
			);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
        Vec3 wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth() / 2);
		if (wallDirection == null) return false;
		Vec3 wallVec = wallDirection.normalize();
		Vec3 lookDirection = VectorUtil.fromYawDegree(player.yBodyRot);
		lookDirection = new Vec3(lookDirection.x(), 0, lookDirection.z()).normalize();
		//doing "wallDirection/direction" as complex number(x + z i) to calculate difference of player's direction to steps
		Vec3 dividedVec =
				new Vec3(
						wallVec.x() * lookDirection.x() + wallVec.z() * lookDirection.z(), 0,
						-wallVec.x() * lookDirection.z() + wallVec.z() * lookDirection.x()
				).normalize();
		if (Math.abs(dividedVec.z()) < 0.9) {
			return false;
		}
		BufferUtil.wrap(startInfo).putBoolean(dividedVec.z() > 0/*if true, wall is in right side*/);
		Vec3 runDirection = wallVec.yRot((float) (Math.PI / 2));
		if (runDirection.dot(lookDirection) < 0) {
			runDirection = runDirection.reverse();
		}
		startInfo.putDouble(wallDirection.x())
				.putDouble(wallDirection.z())
				.putDouble(runDirection.x())
				.putDouble(runDirection.z());

		return (!parkourability.get(WallJump.class).justJumped()
				&& (
				(ParCoolConfig.Client.HWallRunControl.get() == ControlType.PressKey && KeyBindings.getKeyHorizontalWallRun().isDown())
						|| ParCoolConfig.Client.HWallRunControl.get() == ControlType.Auto
		)
				&& !parkourability.get(Crawl.class).isDoing()
				&& !parkourability.get(Dodge.class).isDoing()
				&& !parkourability.get(Vault.class).isDoing()
				&& !player.isInWaterOrBubble()
				&& Math.abs(player.getDeltaMovement().y()) < 0.5
				&& coolTime == 0
				&& !player.isOnGround()
				&& parkourability.getAdditionalProperties().getNotLandingTick() > 5
                && (
                parkourability.get(FastRun.class).canActWithRunning(player)
                        || parkourability.get(FastRun.class).getNotDashTick(parkourability.getAdditionalProperties()) < 10
		)
				&& !stamina.isExhausted()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
        Vec3 wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth() / 2);
		if (wallDirection == null) return false;
		return (getDoingTick() < getMaxRunningTick(parkourability.getActionInfo())
				&& !stamina.isExhausted()
				&& !parkourability.get(WallJump.class).justJumped()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !parkourability.get(Dodge.class).isDoing()
				&& !parkourability.get(Vault.class).isDoing()
				&& (
				(ParCoolConfig.Client.HWallRunControl.get() == ControlType.PressKey && KeyBindings.getKeyHorizontalWallRun().isDown())
						|| ParCoolConfig.Client.HWallRunControl.get() == ControlType.Auto
		)
				&& !player.isOnGround()
		);
	}

	@Override
	public void onStop(Player player) {
		coolTime = 10;
	}

	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		wallIsRightward = BufferUtil.getBoolean(startData);
		runningWallDirection = new Vec3(startData.getDouble(), 0, startData.getDouble());
		runningDirection = new Vec3(startData.getDouble(), 0, startData.getDouble());
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.HORIZONTAL_WALL_RUN.get(), 1f, 1f);
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new HorizontalWallRunAnimator(wallIsRightward));
		}
	}

	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		wallIsRightward = BufferUtil.getBoolean(startData);
		runningWallDirection = new Vec3(startData.getDouble(), 0, startData.getDouble());
		runningDirection = new Vec3(startData.getDouble(), 0, startData.getDouble());
		Animation animation = Animation.get(player);
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.HORIZONTAL_WALL_RUN.get(), 1f, 1f);
		if (animation != null) {
			animation.setAnimator(new HorizontalWallRunAnimator(wallIsRightward));
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {
		if (isDoing()) {
			if (runningDirection == null) return;
			Vec3 lookVec = VectorUtil.fromYawDegree(player.getYHeadRot());
			double differenceAngle = Math.asin(
					new Vec3(
							lookVec.x() * runningDirection.x() + lookVec.z() * runningDirection.z(), 0,
							-lookVec.x() * runningDirection.z() + lookVec.z() * runningDirection.x()
					).normalize().z()
			);
			if (Math.abs(differenceAngle) > Math.PI / 4) {
				player.setYRot((float) VectorUtil.toYawDegree(
						runningDirection.yRot((float) (-Math.signum(differenceAngle) * Math.PI / 4))
				));
			}
            player.yBodyRotO = player.yBodyRot = bodyYaw;
		}
	}

    @Override
    public void onWorkingTickInClient(Player player, Parkourability parkourability, IStamina stamina) {
        spawnRunningParticle(player);
    }

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
		buffer.putFloat(bodyYaw);
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
		bodyYaw = buffer.getFloat();
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnWorking;
	}

    @OnlyIn(Dist.CLIENT)
    public void spawnRunningParticle(Player player) {
        if (runningDirection == null || runningWallDirection == null) return;
        Level level = player.level;
        Vec3 pos = player.position();
        BlockPos leanedBlock = new BlockPos(
                pos.add(runningWallDirection.x(), player.getBbHeight() * 0.25, runningWallDirection.z())
        );
        if (!level.isLoaded(leanedBlock)) return;
        float width = player.getBbWidth();
        BlockState blockstate = level.getBlockState(leanedBlock);

        Vec3 wallDirection = runningWallDirection.normalize();
        Vec3 orthogonalToWallVec = wallDirection.yRot((float) (Math.PI / 2));
        Vec3 particleBaseDirection = runningDirection.subtract(wallDirection);
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            Vec3 particlePos = new Vec3(
                    pos.x() + (wallDirection.x() * 0.4 + orthogonalToWallVec.x() * (player.getRandom().nextDouble() - 0.5D)) * width,
                    pos.y() + 0.1D + 0.3 * player.getRandom().nextDouble(),
                    pos.z() + (wallDirection.z() * 0.4 + orthogonalToWallVec.z() * (player.getRandom().nextDouble() - 0.5D)) * width
            );
            Vec3 particleSpeed = particleBaseDirection
                    .yRot((float) (Math.PI * 0.2 * (player.getRandom().nextDouble() - 0.5)))
                    .scale(3 + 6 * player.getRandom().nextDouble())
                    .add(0, 1.5, 0);
            level.addParticle(
                    new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(leanedBlock),
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
