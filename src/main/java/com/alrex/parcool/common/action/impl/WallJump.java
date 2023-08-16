package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.BackwardWallJumpAnimator;
import com.alrex.parcool.client.animation.impl.WallJumpAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class WallJump extends Action {

	private boolean jump = false;
	private float forceBodyAngle = 0;

	public boolean justJumped() {
		return jump;
	}

	@Override
	public void onTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		jump = false;
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}


	@OnlyIn(Dist.CLIENT)
	@Nullable
	private Vector3d getJumpDirection(PlayerEntity player, Vector3d wall) {
		if (wall == null) return null;
		wall = wall.normalize();
		Vector3d lookVec = player.getLookAngle();
		Vector3d vec = new Vector3d(lookVec.x(), 0, lookVec.z()).normalize();
		Vector3d value;

		if (wall.dot(vec) > 0) {//To Wall
			if (!ParCoolConfig.Client.Booleans.EnableWallJumpBackward.get()) return null;
			double dot = vec.reverse().dot(wall);
			value = vec.add(wall.scale(2 * dot / wall.length()));
		} else {//back on Wall
			value = vec;
		}

		return value.normalize().add(wall.scale(-0.7)).scale(0.85);
	}

	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Vector3d wallDirection = WorldUtil.getWall(player);
		Vector3d jumpDirection = getJumpDirection(player, wallDirection);
		if (jumpDirection == null) return false;
		ClingToCliff cling = parkourability.get(ClingToCliff.class);

		boolean value = (!stamina.isExhausted()
				&& parkourability.getActionInfo().can(WallJump.class)
				&& !player.isOnGround()
				&& !player.isInWaterOrBubble()
				&& !player.isFallFlying()
				&& !player.abilities.flying
				&& parkourability.getAdditionalProperties().getNotCreativeFlyingTick() > 10
				&& ((!cling.isDoing() && cling.getNotDoingTick() > 3)
				|| (cling.isDoing() && cling.getFacingDirection() != ClingToCliff.FacingDirection.ToWall))
				&& KeyRecorder.keyWallJump.isPressed()
				&& !parkourability.get(Crawl.class).isDoing()
				&& !parkourability.get(VerticalWallRun.class).isDoing()
				&& parkourability.getAdditionalProperties().getNotLandingTick() > 5
				&& WorldUtil.getWall(player) != null
		);
		if (!value) return false;

		//doing "wallDirection/jumpDirection" as complex number(x + z i) to calculate difference of player's direction to wall
		Vector3d dividedVec =
				new Vector3d(
						wallDirection.x() * jumpDirection.x() + wallDirection.z() * jumpDirection.z(), 0,
						-wallDirection.x() * jumpDirection.z() + wallDirection.z() * jumpDirection.x()
				).normalize();
		Vector3d lookVec = player.getLookAngle().multiply(1, 0, 1).normalize();
		Vector3d lookDividedVec =
				new Vector3d(
						lookVec.x() * wallDirection.x() + lookVec.z() * wallDirection.z(), 0,
						-lookVec.x() * wallDirection.z() + lookVec.z() * wallDirection.x()
				).normalize();

		WallJumpAnimationType type;
		if (lookDividedVec.x() > 0.707) {
			type = WallJumpAnimationType.Back;
		} else if (dividedVec.z() > 0) {
			type = WallJumpAnimationType.SwingRightArm;
		} else {
			type = WallJumpAnimationType.SwingLeftArm;
		}
		startInfo
				.putDouble(jumpDirection.x())
				.putDouble(jumpDirection.z())
				.putDouble(wallDirection.x())
				.putDouble(wallDirection.z())
				.put(type.getCode());
		return true;
	}

	@Override
	public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		return false;
	}

	@Override
	public void onStart(PlayerEntity player, Parkourability parkourability) {
		jump = true;
		player.fallDistance = 0;
	}

	@Override
	public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		double speedScale = 1;
		Vector3d jumpDirection = new Vector3d(startData.getDouble(), 0, startData.getDouble()).scale(speedScale);
		Vector3d direction = new Vector3d(jumpDirection.x(), 1.512, jumpDirection.z()).scale(.3);
		Vector3d wallDirection = new Vector3d(startData.getDouble(), 0, startData.getDouble());
		Vector3d motion = player.getDeltaMovement();

		BlockPos leanedBlock = new BlockPos(
				player.getX() + wallDirection.x(),
				player.getBoundingBox().minY + player.getBbHeight() * 0.25,
				player.getZ() + wallDirection.z()
		);
		float slipperiness = player.level.isLoaded(leanedBlock) ?
				player.level.getBlockState(leanedBlock).getSlipperiness(player.level, leanedBlock, player)
				: 0.6f;

		double ySpeed;
		if (slipperiness > 0.9) {// icy blocks
			ySpeed = motion.y();
		} else {
			ySpeed = motion.y() > direction.y() ? motion.y + direction.y() : direction.y();
		}
		player.setDeltaMovement(
				motion.x() + direction.x(),
				ySpeed,
				motion.z() + direction.z()
		);

		
		
		WallJumpAnimationType type = WallJumpAnimationType.fromCode(startData.get());
		Animation animation = Animation.get(player);
		if (animation != null) {
			switch (type) {
				case Back:
					forceBodyAngle = (float) VectorUtil.toYawDegree(wallDirection);
					animation.setAnimator(new BackwardWallJumpAnimator());
					break;
				case SwingLeftArm:
					animation.setAnimator(new WallJumpAnimator(false));
					break;
				case SwingRightArm:
					animation.setAnimator(new WallJumpAnimator(true));
			}
		}
	}

	@Override
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		startData.position(16);
		Vector3d wallDirection = new Vector3d(startData.getDouble(), 0, startData.getDouble());
		WallJumpAnimationType type = WallJumpAnimationType.fromCode(startData.get());
		Animation animation = Animation.get(player);
		if (animation != null) {
			switch (type) {
				case Back:
					forceBodyAngle = (float) VectorUtil.toYawDegree(wallDirection);
					animation.setAnimator(new BackwardWallJumpAnimator());
					break;
				case SwingLeftArm:
					animation.setAnimator(new WallJumpAnimator(false));
					break;
				case SwingRightArm:
					animation.setAnimator(new WallJumpAnimator(true));
			}
		}
	}

	@Override
	public void onWorkingTickInClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		super.onWorkingTickInClient(player, parkourability, stamina);
	}

	private enum WallJumpAnimationType {
		Back, SwingRightArm, SwingLeftArm;

		public byte getCode() {
			return (byte) this.ordinal();
		}

		public static WallJumpAnimationType fromCode(byte code) {
			return values()[code];
		}
	}
}
