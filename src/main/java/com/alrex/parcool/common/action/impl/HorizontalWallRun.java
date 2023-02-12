package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.HorizontalWallRunAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class HorizontalWallRun extends Action {
	private int coolTime = 0;
	private float bodyYaw = 0;
	private static final int Max_Running_Tick = 25;
	private boolean wallIsRightward = false;

	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (coolTime > 0) coolTime--;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onWorkingTickInLocalClient(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		Vector3d wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth());
		if (wallDirection == null) return;
		Vector3d targetVec = wallDirection.yRot((wallIsRightward ? 1 : -1) * (float) Math.PI / 2);
		Vector3d lookVec = player.getLookAngle();
		double differenceAngle = Math.asin(
				new Vector3d(
						lookVec.x() * targetVec.x() + lookVec.z() * targetVec.z(), 0,
						-lookVec.x() * targetVec.z() + lookVec.z() * targetVec.x()
				).normalize().z()
		);
		bodyYaw = (float) VectorUtil.toYawDegree(targetVec.yRot((float) (differenceAngle / 4)));
		Vector3d movement = player.getDeltaMovement();
		player.setDeltaMovement(
				movement.x(),
				movement.y() * ((double) getDoingTick()) / Max_Running_Tick,
				movement.z()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startInfo) {
		Vector3d wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth());
		if (wallDirection == null) return false;
		Vector3d wallVec = wallDirection.normalize();
		Vector3d direction = VectorUtil.fromYawDegree(player.yBodyRot);
		direction = new Vector3d(direction.x(), 0, direction.z()).normalize();
		//doing "wallDirection/direction" as complex number(x + z i) to calculate difference of player's direction to steps
		Vector3d dividedVec =
				new Vector3d(
						wallVec.x() * direction.x() + wallVec.z() * direction.z(), 0,
						-wallVec.x() * direction.z() + wallVec.z() * direction.x()
				).normalize();
		if (Math.abs(dividedVec.z()) < 0.9) {
			return false;
		}
		BufferUtil.wrap(startInfo).putBoolean(dividedVec.z() > 0/*if true, wall is in right side*/);

		return (parkourability.getPermission().canHorizontalWallRun()
				&& !parkourability.getWallJump().justJumped()
				&& !parkourability.getCrawl().isDoing()
				&& KeyBindings.getKeyHorizontalWallRun().isDown()
				&& Math.abs(player.getDeltaMovement().y()) < 0.3
				&& coolTime == 0
				&& !player.isOnGround()
				&& parkourability.getAdditionalProperties().getNotLandingTick() > 5
				&& (parkourability.getFastRun().canActWithRunning(player)
				|| parkourability.getFastRun().getNotDashTick(parkourability.getAdditionalProperties()) < 3
		)
				&& !stamina.isExhausted()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		Vector3d wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth());
		if (wallDirection == null) return false;
		return (getDoingTick() < Max_Running_Tick &&
				parkourability.getPermission().canHorizontalWallRun() &&
				!parkourability.getWallJump().justJumped() &&
				!parkourability.getCrawl().isDoing() &&
				KeyBindings.getKeyHorizontalWallRun().isDown() &&
				!player.isOnGround()
		);
	}

	@Override
	public void onStop(PlayerEntity player) {
		coolTime = 10;
	}

	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, Stamina stamina, ByteBuffer startData) {
		wallIsRightward = BufferUtil.getBoolean(startData);
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new HorizontalWallRunAnimator(wallIsRightward));
		}
	}

	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		wallIsRightward = BufferUtil.getBoolean(startData);
		Animation animation = Animation.get(player);
		if (animation != null) {
			animation.setAnimator(new HorizontalWallRunAnimator(wallIsRightward));
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
		if (isDoing()) {
			player.yRot = bodyYaw;
		}
	}

	@Override
	public void restoreSynchronizedState(ByteBuffer buffer) {
	}

	@Override
	public void saveSynchronizedState(ByteBuffer buffer) {
	}
}
