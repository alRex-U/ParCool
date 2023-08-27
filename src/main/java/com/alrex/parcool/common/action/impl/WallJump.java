package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.BackwardWallJumpAnimator;
import com.alrex.parcool.client.animation.impl.WallJumpAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.impl.Animation;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class WallJump extends Action {

	private boolean jump = false;

	public boolean justJumped() {
		return jump;
	}

	private final float MAX_COOL_DOWN_TICK = 8;

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
		wall = wall.normalize();
		Vec3 lookVec = player.getLookAngle();
		Vec3 vec = new Vec3(lookVec.x(), 0, lookVec.z()).normalize();
		Vec3 value;
		double dotProduct = wall.dot(vec);

		if (dotProduct > 0.35) {
			if (!ParCoolConfig.Client.Booleans.EnableWallJumpBackward.get()) return null;
		}
		if (dotProduct > 0) {//To Wall
			double dot = vec.reverse().dot(wall);
			value = vec.add(wall.scale(2 * dot / wall.length()));
		} else {//back on Wall
			value = vec;
		}

		return value.normalize().add(wall.scale(-0.7)).scale(0.85);
	}

	public float getCoolDownPhase() {
		float phase = getNotDoingTick() / MAX_COOL_DOWN_TICK;
		if (phase > 1) return 1;
		else return phase;
	}

	@Override
	public boolean canStart(Player player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Vec3 wallDirection = WorldUtil.getWall(player);
		Vec3 jumpDirection = getJumpDirection(player, wallDirection);
		if (jumpDirection == null) return false;
		ClingToCliff cling = parkourability.get(ClingToCliff.class);

		boolean value = (!stamina.isExhausted()
				&& getNotDoingTick() > MAX_COOL_DOWN_TICK
				&& !player.isOnGround()
				&& !player.isInWaterOrBubble()
				&& !player.isFallFlying()
				&& !player.getAbilities().flying
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
		Vec3 dividedVec =
				new Vec3(
						wallDirection.x() * jumpDirection.x() + wallDirection.z() * jumpDirection.z(), 0,
						-wallDirection.x() * jumpDirection.z() + wallDirection.z() * jumpDirection.x()
				).normalize();
		Vec3 lookVec = player.getLookAngle().multiply(1, 0, 1).normalize();
		Vec3 lookDividedVec =
				new Vec3(
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
		double speedScale = 1;
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1f, 0.7f);
		Vec3 jumpDirection = new Vec3(startData.getDouble(), 0, startData.getDouble()).scale(speedScale);
		Vec3 direction = new Vec3(jumpDirection.x(), 1.512, jumpDirection.z()).scale(.3);
		Vec3 wallDirection = new Vec3(startData.getDouble(), 0, startData.getDouble());
		Vec3 motion = player.getDeltaMovement();

		BlockPos leanedBlock = new BlockPos(
				player.getX() + wallDirection.x(),
				player.getBoundingBox().minY + player.getBbHeight() * 0.25,
				player.getZ() + wallDirection.z()
		);
		float slipperiness = player.level.isLoaded(leanedBlock) ?
				player.level.getBlockState(leanedBlock).getFriction(player.level, leanedBlock, player)
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
		startData.position(16);
		Vec3 wallDirection = new Vec3(startData.getDouble(), 0, startData.getDouble());
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
	public void onWorkingTickInClient(Player player, Parkourability parkourability, IStamina stamina) {
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
