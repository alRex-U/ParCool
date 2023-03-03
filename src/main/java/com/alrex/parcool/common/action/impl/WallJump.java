package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.BackwardWallJumpAnimator;
import com.alrex.parcool.client.animation.impl.WallJumpAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.common.capability.impl.Parkourability;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

;

public class WallJump extends Action {

	private boolean jump = false;

	public boolean justJumped() {
		return jump;
	}

	@Override
	public void onTick(Player player, Parkourability parkourability, IStamina stamina) {
		jump = false;
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}


	@OnlyIn(Dist.CLIENT)
	@Nullable
	private Vec3 getJumpDirection(Player player, Vec3 wall) {
		if (wall == null) return null;

		Vec3 lookVec = player.getLookAngle();
		Vec3 vec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

		if (wall.dot(vec) > 0) {//To Wall
			return null;
		} else {/*back on Wall*/}

		return vec.normalize().add(wall.scale(-0.7));
	}

	@Override
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Vec3 wallDirection = WorldUtil.getWall(player);
		Vec3 jumpDirection = getJumpDirection(player, wallDirection);
		if (jumpDirection == null) return false;
		ClingToCliff cling = parkourability.get(ClingToCliff.class);

		boolean value = (!stamina.isExhausted()
				&& parkourability.getActionInfo().can(WallJump.class)
				&& !player.isOnGround()
				&& !player.isInWaterOrBubble()
				&& !player.isFallFlying()
				&& !player.getAbilities().flying
				&& parkourability.getAdditionalProperties().getNotCreativeFlyingTick() > 10
				&& ((!cling.isDoing() && cling.getNotDoingTick() > 3)
				|| (cling.isDoing() && cling.getFacingDirection() != ClingToCliff.FacingDirection.ToWall))
				&& KeyRecorder.keyWallJump.isPressed()
				&& !parkourability.get(Crawl.class).isDoing()
				&& parkourability.getAdditionalProperties().getNotLandingTick() > 5
				&& WorldUtil.getWall(player) != null
		);
		if (!value) return false;

		//doing "wallDirection/jumpDirection" as complex number(x + z i) to calculate difference of player's direction to wall
		Vec3 dividedVec =
				new Vec3(
						wallDirection.x * jumpDirection.x + wallDirection.z * jumpDirection.z, 0,
						-wallDirection.x * jumpDirection.z + wallDirection.z * jumpDirection.x
				).normalize();
		Vec3 lookVec = player.getLookAngle().multiply(1, 0, 1).normalize();
		Vec3 lookDividedVec =
				new Vec3(
						lookVec.x * wallDirection.x + lookVec.z * wallDirection.z, 0,
						-lookVec.x * wallDirection.z + lookVec.z * wallDirection.x
				).normalize();

		WallJumpAnimationType type;
		if (lookDividedVec.x > 0.5) {
			type = WallJumpAnimationType.Back;
		} else if (dividedVec.z > 0) {
			type = WallJumpAnimationType.SwingRightArm;
		} else {
			type = WallJumpAnimationType.SwingLeftArm;
		}
		startInfo
				.putDouble(jumpDirection.x)
				.putDouble(jumpDirection.z)
				.putDouble(wallDirection.x)
				.putDouble(wallDirection.z)
				.put(type.getCode());
		return true;
	}

	@Override
	public boolean canContinue(Player player, Parkourability parkourability, IStamina stamina) {
		return false;
	}

	@Override
	public void onStart(Player player, Parkourability parkourability) {
		jump = true;
		player.fallDistance = 0;
	}

	@Override
	public void onStartInLocalClient(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		double speedScale = player.getBbWidth() / 0.6;
		Vec3 jumpDirection = new Vec3(startData.getDouble(), 0, startData.getDouble()).scale(speedScale);
		Vec3 direction = new Vec3(jumpDirection.x, player.getBbHeight() * 0.84, jumpDirection.z).scale(player.getBbWidth() / 2);
		Vec3 wallDirection = new Vec3(startData.getDouble(), 0, startData.getDouble());
		Vec3 motion = player.getDeltaMovement();

		BlockPos leanedBlock = new BlockPos(
				player.getX() + wallDirection.x,
				player.getBoundingBox().minY + player.getBbHeight() * 0.25,
				player.getZ() + wallDirection.z
		);
		float slipperiness = player.level.isLoaded(leanedBlock) ?
				player.level.getBlockState(leanedBlock).getFriction(player.level, leanedBlock, player)
				: 0.6f;

		double ySpeed;
		if (slipperiness > 0.9) {// icy blocks
			ySpeed = motion.y;
		} else {
			ySpeed = motion.y > direction.y ? motion.y + direction.y : direction.y;
		}
		player.setDeltaMovement(
				motion.x + direction.x,
				ySpeed,
				motion.z + direction.z
		);

		WallJumpAnimationType type = WallJumpAnimationType.fromCode(startData.get());
		Animation animation = Animation.get(player);
		if (animation != null) {
			switch (type) {
				case Back:
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
	public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
		startData.position(32);
		WallJumpAnimationType type = WallJumpAnimationType.fromCode(startData.get());
		Animation animation = Animation.get(player);
		if (animation != null) {
			switch (type) {
				case Back:
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

	private enum WallJumpAnimationType {
		Back((byte) 0), SwingRightArm((byte) 1), SwingLeftArm((byte) 2);
		private byte code;

		WallJumpAnimationType(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return code;
		}

		public static WallJumpAnimationType fromCode(byte code) {
			switch (code) {
				case 1:
					return SwingRightArm;
				case 2:
					return SwingLeftArm;
				default:
					return Back;
			}
		}
	}
}
