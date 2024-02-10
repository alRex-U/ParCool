package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.BackwardWallJumpAnimator;
import com.alrex.parcool.client.animation.impl.WallJumpAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.client.sound.SoundEvents;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
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

		return value.normalize().add(wall.scale(-0.7)).normalize();
	}

	public float getCoolDownPhase() {
		float phase = getNotDoingTick() / MAX_COOL_DOWN_TICK;
		if (phase > 1) return 1;
		else return phase;
	}

	@Override
	public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Vector3d wallDirection = WorldUtil.getWall(player);
		Vector3d jumpDirection = getJumpDirection(player, wallDirection);
		if (jumpDirection == null) return false;
		ClingToCliff cling = parkourability.get(ClingToCliff.class);

		boolean value = (!stamina.isExhausted()
				&& getNotDoingTick() > MAX_COOL_DOWN_TICK
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
				&& parkourability.getAdditionalProperties().getNotLandingTick() > 4
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

		double lookAngleY = player.getLookAngle().normalize().y();
		if (lookAngleY > 0.5) { // Looking upward
			jumpDirection = jumpDirection.add(0, lookAngleY * 2, 0).normalize();
		} else {
			jumpDirection = jumpDirection.add(0, 1, 0).normalize();
		}
		startInfo
				.putDouble(jumpDirection.x())
				.putDouble(jumpDirection.y())
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
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.WALL_JUMP, 1f, 1f);
		Vector3d jumpDirection = new Vector3d(startData.getDouble(), startData.getDouble(), startData.getDouble());
		Vector3d jumpMotion = jumpDirection.scale(0.59);
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
			ySpeed = motion.y() > jumpMotion.y() ? motion.y + jumpMotion.y() : jumpMotion.y();
			spawnJumpParticles(player, wallDirection, jumpDirection);
		}
		player.setDeltaMovement(
				motion.x() + jumpMotion.x(),
				ySpeed,
				motion.z() + jumpMotion.z()
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
	public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
		Vector3d jumpDirection = new Vector3d(startData.getDouble(), startData.getDouble(), startData.getDouble());
		Vector3d wallDirection = new Vector3d(startData.getDouble(), 0, startData.getDouble());
		BlockPos leanedBlock = new BlockPos(
				player.getX() + wallDirection.x(),
				player.getBoundingBox().minY + player.getBbHeight() * 0.25,
				player.getZ() + wallDirection.z()
		);
		float slipperiness = player.level.isLoaded(leanedBlock) ?
				player.level.getBlockState(leanedBlock).getSlipperiness(player.level, leanedBlock, player)
				: 1f;
		if (slipperiness <= 0.9) {// icy blocks
			spawnJumpParticles(player, wallDirection, jumpDirection);
		}

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
	public void onWorkingTickInClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
		super.onWorkingTickInClient(player, parkourability, stamina);
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnJumpParticles(PlayerEntity player, Vector3d wallDirection, Vector3d jumpDirection) {
		World level = player.level;
		Vector3d pos = player.position();
		BlockPos leanedBlock = new BlockPos(
				pos.add(wallDirection.x(), player.getBbHeight() * 0.25, wallDirection.z())
		);
		if (!level.isLoaded(leanedBlock)) return;
		float width = player.getBbWidth();
		BlockState blockstate = level.getBlockState(leanedBlock);

		Vector3d horizontalJumpDirection = jumpDirection.multiply(1, 0, 1).normalize();

		wallDirection = wallDirection.normalize();
		Vector3d orthogonalToWallVec = wallDirection.yRot((float) (Math.PI / 2)).normalize();

		//doing "Conjugate of (horizontalJumpDirection/-wallDirection)" as complex number(x + z i)
		Vector3d differenceVec =
				new Vector3d(
						-wallDirection.x() * horizontalJumpDirection.x() - wallDirection.z() * horizontalJumpDirection.z(), 0,
						wallDirection.z() * horizontalJumpDirection.x() - wallDirection.x() * horizontalJumpDirection.z()
				).multiply(1, 0, -1).normalize();
		Vector3d particleBaseDirection =
				new Vector3d(
						-wallDirection.x() * differenceVec.x() + wallDirection.z() * differenceVec.z(), 0,
						-wallDirection.x() * differenceVec.z() - wallDirection.z() * differenceVec.x()
				);
		if (blockstate.getRenderShape() != BlockRenderType.INVISIBLE) {
			for (int i = 0; i < 10; i++) {
				Vector3d particlePos = new Vector3d(
						pos.x() + (wallDirection.x() * 0.4 + orthogonalToWallVec.x() * (player.getRandom().nextDouble() - 0.5D)) * width,
						pos.y() + 0.1D + 0.3 * player.getRandom().nextDouble(),
						pos.z() + (wallDirection.z() * 0.4 + orthogonalToWallVec.z() * (player.getRandom().nextDouble() - 0.5D)) * width
				);
				Vector3d particleSpeed = particleBaseDirection
						.yRot((float) (Math.PI * 0.2 * (player.getRandom().nextDouble() - 0.5)))
						.scale(3 + 9 * player.getRandom().nextDouble())
						.add(0, -jumpDirection.y() * 3 * player.getRandom().nextDouble(), 0);
				level.addParticle(
						new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(leanedBlock),
						particlePos.x(),
						particlePos.y(),
						particlePos.z(),
						particleSpeed.x(),
						particleSpeed.y(),
						particleSpeed.z()
				);

			}
		}
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
