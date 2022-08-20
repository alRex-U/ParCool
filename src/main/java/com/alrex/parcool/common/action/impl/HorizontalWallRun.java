package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.HorizontalWallRunAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
	public void onTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (wallRunning) {
			wallRunningTick++;
			Vec3 movement = player.getDeltaMovement();
			player.setDeltaMovement(movement.x(), 0, movement.z());
		} else {
			wallRunningTick = 0;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onClientTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer()) {
			if (coolTime > 0) coolTime--;
			boolean oldRunning = wallRunning;
			wallRunning = false;
			if (
					(!oldRunning
							&& parkourability.getPermission().canHorizontalWallRun()
							&& parkourability.getFastRun().canActWithRunning(player)
							&& !parkourability.getWallJump().justJumped()
							&& !parkourability.getCrawl().isCrawling()
							&& KeyBindings.getKeyHorizontalWallRun().isDown()
							&& Math.abs(player.getDeltaMovement().y()) < 0.3
							&& coolTime == 0
							&& !player.isOnGround()
							&& parkourability.getAdditionalProperties().getNotLandingTick() > 5
							&& !stamina.isExhausted()
					) || (oldRunning
							&& parkourability.getPermission().canHorizontalWallRun()
							&& !parkourability.getWallJump().justJumped()
							&& !parkourability.getCrawl().isCrawling()
							&& KeyBindings.getKeyHorizontalWallRun().isDown()
							&& !player.isOnGround()
					)
			) {
				Vec3 wallDirection = WorldUtil.getWall(player);
				Vec3 direction = VectorUtil.fromYawDegree(player.yBodyRot);
				direction = new Vec3(direction.x(), 0, direction.z()).normalize();
				if (wallDirection != null) {
					wallDirection = wallDirection.normalize();
					//doing "wallDirection/direction" as complex number(x + z i) to calculate difference of player's direction to steps
					Vec3 dividedVec =
							new Vec3(
									wallDirection.x() * direction.x() + wallDirection.z() * direction.z(), 0,
									-wallDirection.x() * direction.z() + wallDirection.z() * direction.x()
							).normalize();
					if (Math.abs(dividedVec.z()) > 0.9) {
						wallIsRightward = dividedVec.z() > 0;
						wallRunning = true;
					}
				}
			}
			if (oldRunning != wallRunning && !wallRunning) {
				coolTime = 10;
			}
			if (wallRunningTick > Max_Running_Tick) {
				wallRunning = false;
				coolTime = 10;
			}
		}
		if (wallRunning && wallRunningTick <= 3) {
			Animation animation = Animation.get(player);
			if (animation != null) {
				animation.setAnimator(new HorizontalWallRunAnimator());
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {

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
