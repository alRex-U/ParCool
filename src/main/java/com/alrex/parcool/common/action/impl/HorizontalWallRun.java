package com.alrex.parcool.common.action.impl;

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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class HorizontalWallRun extends Action {
	private int coolTime = 0;
	private float bodyYaw = 0;

	private int getMaxRunningTick(ActionInfo info) {
		Integer value = info.getClientInformation().get(ParCoolConfig.Client.Integers.WallRunContinuableTick);
		if (value == null) return ParCoolConfig.Client.Integers.WallRunContinuableTick.DefaultValue;
		if (info.getServerLimitation().isEnabled())
			value = Math.min(value, info.getServerLimitation().get(ParCoolConfig.Server.Integers.MaxWallRunContinuableTick));
		if (info.getIndividualLimitation().isEnabled())
			value = Math.min(value, info.getIndividualLimitation().get(ParCoolConfig.Server.Integers.MaxWallRunContinuableTick));
		return value;
	}

	private boolean wallIsRightward = false;
	private Vector3d runningWallDirection = null;
	private Vector3d runningDirection = null;

	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		if (coolTime > 0) coolTime--;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onWorkingTickInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		Vector3d wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth() / 2);
		if (wallDirection == null) return;
		if (runningWallDirection == null) return;
		if (runningDirection == null) return;
		Vector3d lookVec = VectorUtil.fromYawDegree(player.yBodyRot);
		double differenceAngle = Math.asin(
				new Vector3d(
						lookVec.x() * runningDirection.x() + lookVec.z() * runningDirection.z(), 0,
						-lookVec.x() * runningDirection.z() + lookVec.z() * runningDirection.x()
				).normalize().z()
		);
		bodyYaw = (float) VectorUtil.toYawDegree(lookVec.yRot((float) (differenceAngle / 10)));
		Vector3d movement = player.getDeltaMovement();
		BlockPos leanedBlock = new BlockPos(
				player.getX() + runningWallDirection.x(),
				player.getBoundingBox().minY + player.getBbHeight() * 0.5,
				player.getZ() + runningWallDirection.z()
		);
		if (!player.level.isLoaded(leanedBlock)) return;
		float slipperiness = player.level.getBlockState(leanedBlock).getSlipperiness(player.level, leanedBlock, player);
		if (slipperiness <= 0.8) {
			player.setDeltaMovement(
					runningDirection.x() * 0.3,
					movement.y() * (slipperiness - 0.1) * ((double) getDoingTick()) / getMaxRunningTick(parkourability.getActionInfo()),
					runningDirection.z() * 0.3
			);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Vector3d wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth() / 2);
		if (wallDirection == null) return false;
		Vector3d wallVec = wallDirection.normalize();
		Vector3d lookDirection = VectorUtil.fromYawDegree(player.yBodyRot);
		lookDirection = new Vector3d(lookDirection.x(), 0, lookDirection.z()).normalize();
		//doing "wallDirection/direction" as complex number(x + z i) to calculate difference of player's direction to steps
		Vector3d dividedVec =
				new Vector3d(
						wallVec.x() * lookDirection.x() + wallVec.z() * lookDirection.z(), 0,
						-wallVec.x() * lookDirection.z() + wallVec.z() * lookDirection.x()
				).normalize();
		if (Math.abs(dividedVec.z()) < 0.9) {
			return false;
		}
		BufferUtil.wrap(startInfo).putBoolean(dividedVec.z() > 0/*if true, wall is in right side*/);
		Vector3d runDirection = wallVec.yRot((float) (Math.PI / 2));
		if (runDirection.dot(lookDirection) < 0) {
			runDirection = runDirection.reverse();
		}
		startInfo.putDouble(wallDirection.x())
				.putDouble(wallDirection.z())
				.putDouble(runDirection.x())
				.putDouble(runDirection.z());

		return (!parkourability.get(WallJump.class).justJumped()
				&& KeyBindings.getKeyHorizontalWallRun().isDown()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !parkourability.get(Dodge.class).isDoing()
				&& !parkourability.get(Vault.class).isDoing()
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
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		Vector3d wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth() / 2);
		if (wallDirection == null) return false;
		return (getDoingTick() < getMaxRunningTick(parkourability.getActionInfo())
				&& !stamina.isExhausted()
				&& !parkourability.get(WallJump.class).justJumped()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !parkourability.get(Dodge.class).isDoing()
				&& !parkourability.get(Vault.class).isDoing()
				&& KeyBindings.getKeyHorizontalWallRun().isDown()
				&& !player.isOnGround()
		);
	}

	@Override
	public void onStop(PlayerEntity player) {
		coolTime = 10;
	}

	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		wallIsRightward = BufferUtil.getBoolean(startData);
		runningWallDirection = new Vector3d(startData.getDouble(), 0, startData.getDouble());
		runningDirection = new Vector3d(startData.getDouble(), 0, startData.getDouble());
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1f, 0.7f);
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new HorizontalWallRunAnimator(wallIsRightward));
		}
	}

	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		wallIsRightward = BufferUtil.getBoolean(startData);
		runningWallDirection = new Vector3d(startData.getDouble(), 0, startData.getDouble());
		runningDirection = new Vector3d(startData.getDouble(), 0, startData.getDouble());
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new HorizontalWallRunAnimator(wallIsRightward));
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
		if (isDoing()) {
			if (runningDirection == null) return;
			Vector3d lookVec = VectorUtil.fromYawDegree(player.getYHeadRot());
			double differenceAngle = Math.asin(
					new Vector3d(
							lookVec.x() * runningDirection.x() + lookVec.z() * runningDirection.z(), 0,
							-lookVec.x() * runningDirection.z() + lookVec.z() * runningDirection.x()
					).normalize().z()
			);
			if (Math.abs(differenceAngle) > Math.PI / 4) {
				player.yRot = ((float) VectorUtil.toYawDegree(
						runningDirection.yRot((float) (-Math.signum(differenceAngle) * Math.PI / 4))
				));
			}
			player.setYBodyRot(bodyYaw);
		}
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
}
