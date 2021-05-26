package com.alrex.parcool.common.capability.impl;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.capability.IGrabCliff;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.IWallJump;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class WallJump implements IWallJump {
	@Override
	public double getJumpPower() {
		return 0.3;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean canWallJump(PlayerEntity player) {
		IStamina stamina = IStamina.get(player);
		IGrabCliff grabCliff = IGrabCliff.get(player);
		if (stamina == null || grabCliff == null) return false;

		return !stamina.isExhausted() && ParCoolConfig.CONFIG_CLIENT.canWallJump.get() && !player.collidedVertically && !player.isInWaterOrBubbleColumn() && !player.isElytraFlying() && !player.abilities.isFlying && !grabCliff.isGrabbing() && KeyRecorder.keyJumpState.isPressed() && WorldUtil.getWall(player) != null;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	@Nullable
	public Vector3d getJumpDirection(PlayerEntity player) {
		Vector3d wall = WorldUtil.getWall(player);
		if (wall == null) return null;

		Vector3d lookVec = player.getLookVec();
		Vector3d vec = new Vector3d(lookVec.getX(), 0, lookVec.getZ()).normalize();

		Vector3d value;

		if (wall.dotProduct(vec) > 0) {//To Wall
			double dot = vec.inverse().dotProduct(wall);
			value = vec.add(wall.scale(2 * dot / wall.length())); // Perfect.
		} else {//back on Wall
			value = vec;
		}

		return value.normalize().add(wall.scale(-0.7));
	}

	@Override
	public int getStaminaConsumption() {
		return 200;
	}
}
