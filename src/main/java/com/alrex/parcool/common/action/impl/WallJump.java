package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.common.capability.impl.Stamina;
import com.alrex.parcool.common.network.ResetFallDistanceMessage;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
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
	public void onTick(Player player, Parkourability parkourability, Stamina stamina) {
		jump = false;
	}


	@OnlyIn(Dist.CLIENT)
	@Nullable
	private Vec3 getJumpDirection(Player player) {
		Vec3 wall = WorldUtil.getWall(player);
		if (wall == null) return null;

		Vec3 lookVec = player.getLookAngle();
		Vec3 vec = new Vec3(lookVec.x(), 0, lookVec.z()).normalize();

		Vec3 value;

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
	private boolean canWallJump(Player player, Parkourability parkourability, Stamina stamina) {
		return !stamina.isExhausted()
				&& parkourability.getPermission().canWallJump()
				&& !player.isOnGround()
				&& !player.isInWaterOrBubble()
				&& !player.isFallFlying()
				&& !player.getAbilities().flying
				&& !parkourability.getClingToCliff().isCling()
				&& parkourability.getClingToCliff().getNotClingTick() > 3
				&& KeyRecorder.keyJumpState.isPressed()
				&& !parkourability.getCrawl().isCrawling()
				&& parkourability.getAdditionalProperties().getNotLandingTick() > 5
				&& WorldUtil.getWall(player) != null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void onClientTick(Player player, Parkourability parkourability, Stamina stamina) {
		if (player.isLocalPlayer() && canWallJump(player, parkourability, stamina)) {
			Vec3 jumpDirection = getJumpDirection(player);
			if (jumpDirection == null) return;
			if (ParCoolConfig.CONFIG_CLIENT.autoTurningWallJump.get()) {
				player.setYRot((float) VectorUtil.toYawDegree(jumpDirection));
			}

			Vec3 direction = new Vec3(jumpDirection.x(), 1.4, jumpDirection.z()).scale(0.3);
			Vec3 motion = player.getDeltaMovement();

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
	public void onRender(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {

	}

	@Override
	public void saveState(ByteBuffer buffer) {

	}

	@Override
	public void restoreState(ByteBuffer buffer) {

	}
}
