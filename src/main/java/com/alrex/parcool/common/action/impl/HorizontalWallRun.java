package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.impl.HorizontalWallRunAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

;

public class HorizontalWallRun extends Action {
	private int coolTime = 0;
	private float bodyYaw = 0;
	private static final int Max_Running_Tick = ParCoolConfig.CONFIG_CLIENT.wallRunContinuableTick.get();
	private boolean wallIsRightward = false;
	private Vec3 runningWallDirection = null;

	@Override
	public void onClientTick(Player player, Parkourability parkourability, IStamina stamina) {
		if (coolTime > 0) coolTime--;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onWorkingTickInLocalClient(Player player, Parkourability parkourability, IStamina stamina) {
		Vec3 wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth());
		if (wallDirection == null) return;
		Vec3 targetVec = wallDirection.yRot((wallIsRightward ? 1 : -1) * (float) Math.PI / 2);
		Vec3 lookVec = player.getLookAngle();
		double differenceAngle = Math.asin(
				new Vec3(
						lookVec.x * targetVec.x + lookVec.z * targetVec.z, 0,
						-lookVec.x * targetVec.z + lookVec.z * targetVec.x
				).normalize().z
		);
		bodyYaw = (float) VectorUtil.toYawDegree(targetVec.yRot((float) (differenceAngle / 10)));
		if (runningWallDirection == null) return;
		Vec3 movement = player.getDeltaMovement();
		BlockPos leanedBlock = new BlockPos(
				player.getX() + runningWallDirection.x,
				player.getY() + player.getBbHeight() * 0.5,
				player.getZ() + runningWallDirection.z
		);
		float slipperiness = player.level.getBlockState(leanedBlock).getFriction(player.level, leanedBlock, player);
		if (slipperiness <= 0.8) {
			player.setDeltaMovement(
					movement.x,
					movement.y * (slipperiness - 0.1) * ((double) getDoingTick()) / Max_Running_Tick,
					movement.z
			);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Vec3 wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth());
		if (wallDirection == null) return false;
		Vec3 wallVec = wallDirection.normalize();
		Vec3 direction = VectorUtil.fromYawDegree(player.yBodyRot);
		direction = new Vec3(direction.x, 0, direction.z).normalize();
		//doing "wallDirection/direction" as complex number(x + z i) to calculate difference of player's direction to steps
		Vec3 dividedVec =
				new Vec3(
						wallVec.x * direction.x + wallVec.z * direction.z, 0,
						-wallVec.x * direction.z + wallVec.z * direction.x
				).normalize();
		if (Math.abs(dividedVec.z) < 0.9) {
			return false;
		}
		BufferUtil.wrap(startInfo).putBoolean(dividedVec.z > 0/*if true, wall is in right side*/);
		startInfo.putDouble(wallDirection.x)
				.putDouble(wallDirection.z);

		return (parkourability.getActionInfo().can(HorizontalWallRun.class)
				&& !parkourability.get(WallJump.class).justJumped()
				&& !parkourability.get(Crawl.class).isDoing()
				&& KeyBindings.getKeyHorizontalWallRun().isDown()
				&& Math.abs(player.getDeltaMovement().y) < 0.3
				&& coolTime == 0
				&& !player.isOnGround()
				&& parkourability.getAdditionalProperties().getNotLandingTick() > 5
				&& (parkourability.get(FastRun.class).canActWithRunning(player)
				|| parkourability.get(FastRun.class).getNotDashTick(parkourability.getAdditionalProperties()) < 10
		)
				&& !stamina.isExhausted()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
		Vec3 wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth());
		if (wallDirection == null) return false;
		return (getDoingTick() < Max_Running_Tick
				&& !stamina.isExhausted()
				&& parkourability.getActionInfo().can(HorizontalWallRun.class)
				&& !parkourability.get(WallJump.class).justJumped()
				&& !parkourability.get(Crawl.class).isDoing()
				&& KeyBindings.getKeyHorizontalWallRun().isDown()
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
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new HorizontalWallRunAnimator(wallIsRightward));
		}
	}

	@Override
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		wallIsRightward = BufferUtil.getBoolean(startData);
		runningWallDirection = new Vec3(startData.getDouble(), 0, startData.getDouble());
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new HorizontalWallRunAnimator(wallIsRightward));
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {
		if (isDoing()) {
			player.setYRot(bodyYaw);
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
