package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.network.ResetFallDistanceMessage;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class WallJump extends Action {

	private boolean jump = false;

	public boolean justJumped() {
		return jump;
	}

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		jump = false;
	}


	@OnlyIn(Dist.CLIENT)
	@Nullable
	private Vector3d getJumpDirection(PlayerEntity player) {
		Vector3d wall = WorldUtil.getWall(player);
		if (wall == null) return null;

		Vector3d lookVec = player.getLookAngle();
		Vector3d vec = new Vector3d(lookVec.x(), 0, lookVec.z()).normalize();

		Vector3d value;

		if (wall.dot(vec) > 0) {//To Wall
			if (ParCoolConfig.CONFIG_CLIENT.disableWallJumpTowardWall.get()) return null;
			double dot = vec.reverse().dot(wall);
			value = vec.add(wall.scale(2 * dot / wall.length())); // Perfect.
		} else {//back on Wall
			value = vec;
		}

		return value.normalize().add(wall.scale(-0.7));
	}

	@OnlyIn(Dist.CLIENT)
	private boolean canWallJump(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		return !stamina.isExhausted()
				&& parkourability.getPermission().canWallJump()
				&& !player.isOnGround()
				&& !player.isInWaterOrBubble()
				&& !player.isFallFlying()
				&& !player.abilities.flying
				&& parkourability.getAdditionalProperties().getNotCreativeFlyingTick() > 10
				&& !parkourability.getClingToCliff().isCling()
				&& parkourability.getClingToCliff().getNotClingTick() > 3
				&& KeyRecorder.keyWallJump.isPressed()
				&& !parkourability.getCrawl().isCrawling()
				&& parkourability.getAdditionalProperties().getNotLandingTick() > 5
				&& WorldUtil.getWall(player) != null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(PlayerEntity player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer() && canWallJump(player, parkourability, stamina)) {
			Vector3d jumpDirection = getJumpDirection(player);
			if (jumpDirection == null) return;
			if (ParCoolConfig.CONFIG_CLIENT.autoTurningWallJump.get()) {
				player.yRot = (float) VectorUtil.toYawDegree(jumpDirection);
			}

			Vector3d direction = new Vector3d(jumpDirection.x(), 1.4, jumpDirection.z()).scale(0.3);
			Vector3d motion = player.getDeltaMovement();

			stamina.consume(parkourability.getActionInfo().getStaminaConsumptionWallJump(), player);
			player.setDeltaMovement(
					motion.x() + direction.x(),
					motion.y() > direction.y() ? motion.y + direction.y() : direction.y(),
					motion.z() + direction.z()
			);
			jump = true;
			ResetFallDistanceMessage.sync(player);
		}
	}

	@Override
	public void onRender(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {

	}

	@Override
	public void saveState(ByteBuffer buffer) {

	}

	@Override
	public void restoreState(ByteBuffer buffer) {

	}
}
