package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.api.compatibility.LevelWrapper;
import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.api.compatibility.Vec3Wrapper;
import com.alrex.parcool.client.animation.impl.BackwardWallJumpAnimator;
import com.alrex.parcool.client.animation.impl.WallJumpAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class WallJump extends Action {
	public enum ControlType {
		PressKey, ReleaseKey
	}

	private boolean jump = false;

	public boolean justJumped() {
		return jump;
	}

	private static final float MAX_COOL_DOWN_TICK = 8;

	private boolean isInCooldown(Parkourability parkourability) {
		return (parkourability.getClientInfo().get(ParCoolConfig.Client.Booleans.EnableWallJumpCooldown)
				|| !parkourability.getServerLimitation().get(ParCoolConfig.Server.Booleans.AllowDisableWallJumpCooldown))
				&& getNotDoingTick() <= MAX_COOL_DOWN_TICK;
	}
	@Override
	public void onTick(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		jump = false;
	}

	@Override
	public StaminaConsumeTiming getStaminaConsumeTiming() {
		return StaminaConsumeTiming.OnStart;
	}


	@OnlyIn(Dist.CLIENT)
	@Nullable
	private Vec3Wrapper getJumpDirection(PlayerWrapper player, Vec3Wrapper wall) {
		if (wall == null) return null;
		wall = wall.normalize();
		Vec3Wrapper lookVec = player.getLookAngle();
		Vec3Wrapper vec = new Vec3Wrapper(lookVec.x(), 0, lookVec.z()).normalize();
		Vec3Wrapper value;
		double dotProduct = wall.dot(vec);

		if (dotProduct > -Math.cos(Math.toRadians(ParCoolConfig.Client.Integers.AcceptableAngleOfWallJump.get()))) {
			return null;
		}
		if (dotProduct > 0) {//To Wall
			double dot = vec.reverse().dot(wall);
			value = vec.add(wall.scale(2 * dot / wall.length()));
		} else {//back on Wall
			value = vec;
		}

		return value.normalize().add(wall.scale(-0.7)).normalize();
	}

	@Override
	public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
		Vec3Wrapper wallDirection = WorldUtil.getWall(player, player.getBbWidth() * 0.65);
		Vec3Wrapper jumpDirection = getJumpDirection(player, wallDirection);
		if (jumpDirection == null) return false;
		ClingToCliff cling = parkourability.get(ClingToCliff.class);
		ControlType control = ParCoolConfig.Client.WallJumpControl.get();

		boolean value = (!stamina.isExhausted()
				&& !player.isOnGround()
				&& !player.isInWaterOrBubble()
				&& !player.isFallFlying()
				&& !player.isFlying()
				&& parkourability.getAdditionalProperties().getNotCreativeFlyingTick() > 10
				&& ((!cling.isDoing() && cling.getNotDoingTick() > 3)
				|| (cling.isDoing() && cling.getFacingDirection() != ClingToCliff.FacingDirection.ToWall))
				&& ((control == ControlType.PressKey && KeyRecorder.keyWallJump.isPressed()) || (control == ControlType.ReleaseKey && KeyRecorder.keyWallJump.isReleased()))
				&& !parkourability.get(Crawl.class).isDoing()
				&& !parkourability.get(VerticalWallRun.class).isDoing()
				&& parkourability.getAdditionalProperties().getNotLandingTick() > 4
				&& !isInCooldown(parkourability)
		);
		if (!value) return false;

		//doing "wallDirection/jumpDirection" as complex number(x + z i) to calculate difference of player's direction to wall
		Vec3Wrapper dividedVec =
				new Vec3Wrapper(
						wallDirection.x() * jumpDirection.x() + wallDirection.z() * jumpDirection.z(), 0,
						-wallDirection.x() * jumpDirection.z() + wallDirection.z() * jumpDirection.x()
				).normalize();
		Vec3Wrapper lookVec = player.getLookAngle().multiply(1, 0, 1).normalize();
		Vec3Wrapper lookDividedVec =
				new Vec3Wrapper(
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
	public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		return false;
	}

	@Override
    public void onStart(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
		jump = true;
		player.resetFallDistance();
	}

	@Override
	public void onStartInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.WALL_JUMP.get(), 1f, 1f);
		Vec3Wrapper jumpDirection = new Vec3Wrapper(startData.getDouble(), startData.getDouble(), startData.getDouble());
		Vec3Wrapper jumpMotion = jumpDirection.scale(0.59);
		Vec3Wrapper wallDirection = new Vec3Wrapper(startData.getDouble(), 0, startData.getDouble());
		Vec3Wrapper motion = player.getDeltaMovement();

		BlockPos leanedBlock = new BlockPos(
				player.getX() + wallDirection.x(),
				player.getBoundingBox().minY + player.getBbHeight() * 0.25,
				player.getZ() + wallDirection.z()
		);
		float slipperiness = player.isEveryLoaded(leanedBlock) ?
				player.getSlipperiness(leanedBlock)
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
	public void onStartInOtherClient(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
		if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
			player.playSound(SoundEvents.WALL_JUMP.get(), 1f, 1f);
		Vec3Wrapper jumpDirection = new Vec3Wrapper(startData.getDouble(), startData.getDouble(), startData.getDouble());
		Vec3Wrapper wallDirection = new Vec3Wrapper(startData.getDouble(), 0, startData.getDouble());
		BlockPos leanedBlock = new BlockPos(
				player.getX() + wallDirection.x(),
				player.getBoundingBox().minY + player.getBbHeight() * 0.25,
				player.getZ() + wallDirection.z()
		);
		float slipperiness = player.isEveryLoaded(leanedBlock) ?
				player.getSlipperiness(leanedBlock)
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
	public void onWorkingTickInClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
		super.onWorkingTickInClient(player, parkourability, stamina);
	}

	@OnlyIn(Dist.CLIENT)
	private void spawnJumpParticles(PlayerWrapper player, Vec3Wrapper wallDirection, Vec3Wrapper jumpDirection) {
		if (!ParCoolConfig.Client.Booleans.EnableActionParticles.get()) return;
		LevelWrapper level = player.getLevel();
		Vec3Wrapper pos = player.position();
		BlockPos leanedBlock = new BlockPos(
				pos.add(wallDirection.x(), player.getBbHeight() * 0.25, wallDirection.z())
		);
		if (!level.isLoaded(leanedBlock)) return;
		float width = player.getBbWidth();
		BlockState blockstate = level.getBlockState(leanedBlock);

		Vec3Wrapper horizontalJumpDirection = jumpDirection.multiply(1, 0, 1).normalize();

		wallDirection = wallDirection.normalize();
		Vec3Wrapper orthogonalToWallVec = wallDirection.yRot((float) (Math.PI / 2)).normalize();

		//doing "Conjugate of (horizontalJumpDirection/-wallDirection)" as complex number(x + z i)
		Vec3Wrapper differenceVec =
				new Vec3Wrapper(
						-wallDirection.x() * horizontalJumpDirection.x() - wallDirection.z() * horizontalJumpDirection.z(), 0,
						wallDirection.z() * horizontalJumpDirection.x() - wallDirection.x() * horizontalJumpDirection.z()
				).multiply(1, 0, -1).normalize();
		Vec3Wrapper particleBaseDirection =
				new Vec3Wrapper(
						-wallDirection.x() * differenceVec.x() + wallDirection.z() * differenceVec.z(), 0,
						-wallDirection.x() * differenceVec.z() - wallDirection.z() * differenceVec.x()
				);
		if (blockstate.getRenderShape() != BlockRenderType.INVISIBLE) {
			for (int i = 0; i < 10; i++) {
				Vec3Wrapper particlePos = new Vec3Wrapper(
						pos.x() + (wallDirection.x() * 0.4 + orthogonalToWallVec.x() * (player.getRandom().nextDouble() - 0.5D)) * width,
						pos.y() + 0.1D + 0.3 * player.getRandom().nextDouble(),
						pos.z() + (wallDirection.z() * 0.4 + orthogonalToWallVec.z() * (player.getRandom().nextDouble() - 0.5D)) * width
				);
				Vec3Wrapper particleSpeed = particleBaseDirection
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
