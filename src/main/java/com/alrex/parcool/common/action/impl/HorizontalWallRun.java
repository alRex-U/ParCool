package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.api.compatibility.LevelWrapper;
import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.api.compatibility.Vec3Wrapper;
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
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
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
	private Vec3Wrapper runningWallDirection = null;
	private Vec3Wrapper runningDirection = null;

	@Override
	public void onClientTick(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		if (coolTime > 0) coolTime--;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onWorkingTickInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		Vec3Wrapper wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth() * 0.65f);
		if (wallDirection == null) return;
		if (runningWallDirection == null) return;
		if (runningDirection == null) return;
		Vec3Wrapper lookVec = VectorUtil.fromYawDegree(player.getYBodyRot());
		double differenceAngle = Math.asin(
				new Vec3Wrapper(
						lookVec.x() * runningDirection.x() + lookVec.z() * runningDirection.z(), 0,
						-lookVec.x() * runningDirection.z() + lookVec.z() * runningDirection.x()
				).normalize().z()
		);
		bodyYaw = (float) VectorUtil.toYawDegree(lookVec.yRot((float) (differenceAngle / 10)));
		Vec3Wrapper movement = player.getDeltaMovement();
		BlockPos leanedBlock = new BlockPos(
				player.getX() + runningWallDirection.x(),
				player.getBoundingBox().minY + player.getBbHeight() * 0.5,
				player.getZ() + runningWallDirection.z()
		);
		if (!player.isEveryLoaded(leanedBlock)) return;
		float slipperiness = player.getSlipperiness(leanedBlock);
		if (slipperiness <= 0.8) {
			double speedScale = 0.2;
			ModifiableAttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
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
	public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Vec3Wrapper wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth() * 0.65f);
		if (wallDirection == null) return false;
		Vec3Wrapper wallVec = wallDirection.normalize();
		Vec3Wrapper lookDirection = VectorUtil.fromYawDegree(player.getYBodyRot());
		lookDirection = new Vec3Wrapper(lookDirection.x(), 0, lookDirection.z()).normalize();
		//doing "wallDirection/direction" as complex number(x + z i) to calculate difference of player's direction to steps
		Vec3Wrapper dividedVec =
				new Vec3Wrapper(
						wallVec.x() * lookDirection.x() + wallVec.z() * lookDirection.z(), 0,
						-wallVec.x() * lookDirection.z() + wallVec.z() * lookDirection.x()
				).normalize();
		if (Math.abs(dividedVec.z()) < 0.9) {
			return false;
		}
		BufferUtil.wrap(startInfo).putBoolean(dividedVec.z() > 0/*if true, wall is in right side*/);
		Vec3Wrapper runDirection = wallVec.yRot((float) (Math.PI / 2));
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
				&& !parkourability.get(ClingToCliff.class).isDoing()
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
	public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		Vec3Wrapper wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth() * 0.65f);
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
	public void onStop(PlayerWrapper player) {
		coolTime = 10;
	}

	@Override
	public void onStartInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		wallIsRightward = BufferUtil.getBoolean(startData);
		runningWallDirection = new Vec3Wrapper(startData.getDouble(), 0, startData.getDouble());
		runningDirection = new Vec3Wrapper(startData.getDouble(), 0, startData.getDouble());
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.HORIZONTAL_WALL_RUN.get(), 1f, 1f);
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new HorizontalWallRunAnimator(wallIsRightward));
		}
	}

	@Override
	public void onStartInOtherClient(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
		wallIsRightward = BufferUtil.getBoolean(startData);
		runningWallDirection = new Vec3Wrapper(startData.getDouble(), 0, startData.getDouble());
		runningDirection = new Vec3Wrapper(startData.getDouble(), 0, startData.getDouble());
		Animation animation = Animation.get(player);
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.HORIZONTAL_WALL_RUN.get(), 1f, 1f);
		if (animation != null) {
			animation.setAnimator(new HorizontalWallRunAnimator(wallIsRightward));
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, PlayerWrapper player, Parkourability parkourability) {
		if (isDoing()) {
			if (runningDirection == null) return;
			Vec3Wrapper lookVec = VectorUtil.fromYawDegree(player.getYHeadRot());
			double differenceAngle = Math.asin(
					new Vec3Wrapper(
							lookVec.x() * runningDirection.x() + lookVec.z() * runningDirection.z(), 0,
							-lookVec.x() * runningDirection.z() + lookVec.z() * runningDirection.x()
					).normalize().z()
			);
			if (Math.abs(differenceAngle) > Math.PI / 4) {
				player.setYRot((float) VectorUtil.toYawDegree(
						runningDirection.yRot((float) (-Math.signum(differenceAngle) * Math.PI / 4))
				));
			}
			player.setAllYBodyRot(bodyYaw);
		}
	}

	@Override
	public void onWorkingTickInClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
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
	public void spawnRunningParticle(PlayerWrapper player) {
		if (!ParCoolConfig.Client.Booleans.EnableActionParticles.get()) return;
		if (runningDirection == null || runningWallDirection == null) return;
		LevelWrapper level = player.getLevel();
		Vec3Wrapper pos = player.position();
		BlockPos leanedBlock = new BlockPos(
				pos.add(runningWallDirection.x(), player.getBbHeight() * 0.25, runningWallDirection.z())
		);
		if (!level.isLoaded(leanedBlock)) return;
		float width = player.getBbWidth();
		BlockState blockstate = level.getBlockState(leanedBlock);

		Vec3Wrapper wallDirection = runningWallDirection.normalize();
		Vec3Wrapper orthogonalToWallVec = wallDirection.yRot((float) (Math.PI / 2));
		Vec3Wrapper particleBaseDirection = runningDirection.subtract(wallDirection);
		if (blockstate.getRenderShape() != BlockRenderType.INVISIBLE) {
			Vec3Wrapper particlePos = new Vec3Wrapper(
					pos.x() + (wallDirection.x() * 0.4 + orthogonalToWallVec.x() * (player.getRandom().nextDouble() - 0.5D)) * width,
					pos.y() + 0.1D + 0.3 * player.getRandom().nextDouble(),
					pos.z() + (wallDirection.z() * 0.4 + orthogonalToWallVec.z() * (player.getRandom().nextDouble() - 0.5D)) * width
			);
			Vec3Wrapper particleSpeed = particleBaseDirection
					.yRot((float) (Math.PI * 0.2 * (player.getRandom().nextDouble() - 0.5)))
					.scale(3 + 6 * player.getRandom().nextDouble())
					.add(0, 1.5, 0);
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
