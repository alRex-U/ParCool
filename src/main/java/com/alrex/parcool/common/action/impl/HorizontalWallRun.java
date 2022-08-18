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
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;

public class HorizontalWallRun extends Action {
	private boolean wallRunning = false;
	private int wallRunningTick = 0;
	private int coolTime = 0;
	private static final int Max_Running_Tick = 14;
	private boolean wallIsRightward = false;

	public boolean isWallRunning() {
		return wallRunning;
	}

	public boolean isWallRightSide() {
		return wallIsRightward;
	}

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (wallRunningTick > Max_Running_Tick) {
			wallRunning = false;
			coolTime = 10;
		}
		if (wallRunning) {
			wallRunningTick++;
			Vector3d movement = player.getDeltaMovement();
			player.setDeltaMovement(movement.x(), 0, movement.z());
		} else {
			wallRunningTick = 0;
		}
	}

	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			if (coolTime > 0) coolTime--;
			boolean oldRunning = wallRunning;
			wallRunning = false;
			if (
					parkourability.getPermission().canHorizontalWallRun()
							&& parkourability.getFastRun().canActWithRunning(player)
							&& !parkourability.getWallJump().justJumped()
							&& !parkourability.getCrawl().isCrawling()
							&& KeyBindings.getKeyHorizontalWallRun().isDown()
							&& coolTime == 0
							&& !player.isOnGround()
							&& parkourability.getAdditionalProperties().getNotLandingTick() > 5
							&& !stamina.isExhausted()
			) {
				Vector3d wallDirection = WorldUtil.getWall(player);
				Vector3d direction = VectorUtil.fromYawDegree(player.yBodyRot);
				direction = new Vector3d(direction.x(), 0, direction.z()).normalize();
				if (wallDirection != null) {
					wallDirection = wallDirection.normalize();
					//doing "wallDirection/direction" as complex number(x + z i) to calculate difference of player's direction to steps
					Vector3d dividedVec =
							new Vector3d(
									wallDirection.x() * direction.x() + wallDirection.z() * direction.z(), 0,
									-wallDirection.x() * direction.z() + wallDirection.z() * direction.x()
							).normalize();
					if (Math.abs(dividedVec.z()) > 0.966) {
						wallIsRightward = dividedVec.z() > 0;
						wallRunning = true;
					}
				}
			}
			if (oldRunning != wallRunning && !wallRunning) {
				coolTime = 10;
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
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {

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
