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
	private boolean wallRunning = false;
	private int wallRunningTick = 0;
	private int coolTime = 0;
	private float bodyYaw = 0;
	private static final int Max_Running_Tick = 25;
	private boolean wallIsRightward = false;

	public boolean isWallRunning() {
		return wallRunning;
	}

	public boolean isWallRightSide() {
		return wallIsRightward;
	}

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (wallRunning) {
			if (!player.isLocalPlayer()) {
				Vector3d movement = player.getDeltaMovement();
				player.setDeltaMovement(movement.x(), 0, movement.z());
			}
			wallRunningTick++;
		} else {
			wallRunningTick = 0;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			if (coolTime > 0) coolTime--;
			final boolean oldRunning = wallRunning;
			wallRunning = false;
			boolean start = false;
			Vector3d wallDirection = WorldUtil.getRunnableWall(player, player.getBbWidth());
			if (!oldRunning
					&& parkourability.getPermission().canHorizontalWallRun()
					&& !parkourability.getWallJump().justJumped()
					&& !parkourability.getCrawl().isCrawling()
					&& KeyBindings.getKeyHorizontalWallRun().isDown()
					&& Math.abs(player.getDeltaMovement().y()) < 0.3
					&& coolTime == 0
					&& !player.isOnGround()
					&& parkourability.getAdditionalProperties().getNotLandingTick() > 5
					&&
					(parkourability.getFastRun().canActWithRunning(player)
							|| parkourability.getFastRun().getNotDashTick(parkourability.getAdditionalProperties()) < 3
					)
					&& !stamina.isExhausted()
					&& wallDirection != null
			) {
				start = true;
			}
			if (start) {
				Vector3d direction = VectorUtil.fromYawDegree(player.yBodyRot);
				Vector3d wallVec = wallDirection.normalize();
				direction = new Vector3d(direction.x(), 0, direction.z()).normalize();
				//doing "wallDirection/direction" as complex number(x + z i) to calculate difference of player's direction to steps
				Vector3d dividedVec =
						new Vector3d(
								wallVec.x() * direction.x() + wallVec.z() * direction.z(), 0,
								-wallVec.x() * direction.z() + wallVec.z() * direction.x()
						).normalize();
				if (Math.abs(dividedVec.z()) > 0.9) {
					wallIsRightward = dividedVec.z() > 0;
					wallRunning = true;
				}
			} else {
				//When continue wall run
				if (oldRunning
						&& parkourability.getPermission().canHorizontalWallRun()
						&& !parkourability.getWallJump().justJumped()
						&& !parkourability.getCrawl().isCrawling()
						&& KeyBindings.getKeyHorizontalWallRun().isDown()
						&& !player.isOnGround()
						&& wallDirection != null
				) {
					wallRunning = true;
				}
			}
			if (wallRunningTick > Max_Running_Tick) {
				wallRunning = false;
				coolTime = 10;
			}
			if (oldRunning != wallRunning && !wallRunning) {
				coolTime = 10;
			}
			if (wallRunning && wallDirection != null) {
				Vector3d movement = player.getDeltaMovement();
				Vector3d targetVec = wallDirection.yRot((wallIsRightward ? 1 : -1) * (float) Math.PI / 2);
				Vector3d lookVec = player.getLookAngle();
				double differenceAngle = Math.asin(
						new Vector3d(
								lookVec.x() * targetVec.x() + lookVec.z() * targetVec.z(), 0,
								-lookVec.x() * targetVec.z() + lookVec.z() * targetVec.x()
						).normalize().z()
				);
				bodyYaw = (float) VectorUtil.toYawDegree(targetVec.yRot((float) (differenceAngle / 4)));
				player.setDeltaMovement(
						movement.x(),
						movement.y() * ((double) wallRunningTick) / Max_Running_Tick,
						movement.z()
				);
			}
		}
		if (wallRunning && wallRunningTick == 0) {
			Animation animation = Animation.get(player);
			if (animation != null) {
				animation.setAnimator(new HorizontalWallRunAnimator());
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
		if (wallRunning) {
			player.yRot = bodyYaw;
		}
	}

	@Override
	public void restoreState(ByteBuffer buffer) {
		wallRunning = BufferUtil.getBoolean(buffer);
		wallIsRightward = BufferUtil.getBoolean(buffer);
	}

	@Override
	public void saveState(ByteBuffer buffer) {
		BufferUtil.wrap(buffer)
				.putBoolean(wallRunning)
				.putBoolean(wallIsRightward);
	}
}
