package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.action.impl.SkyDive;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class DiveAnimationHostAnimator extends Animator {
	public DiveAnimationHostAnimator(double ySpeed) {
		diveAnimator = new DiveAnimator(ySpeed);
	}

	final DiveAnimator diveAnimator;
	@Nullable
	SkyDiveAnimator skyDiveAnimator = null;
	final static int MaxTransitionTick = 6;

	@Override
	public void tick() {
		super.tick();
		diveAnimator.tick();
		if (skyDiveAnimator != null) skyDiveAnimator.tick();
		if (transitioning) {
			transitionTick++;
			if (transitionTick >= MaxTransitionTick) {
				transitionTick = 0;
				transitioning = false;
			}
		}
	}

	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.get(Dive.class).isDoing();
	}

	boolean oldSkyDiveDoing = false;

	void checkTransition(Parkourability parkourability) {
		boolean doing = parkourability.get(SkyDive.class).isDoing();
		if (doing != oldSkyDiveDoing) {
			startTransition();
		}
		oldSkyDiveDoing = doing;
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		checkTransition(parkourability);
		if (parkourability.get(SkyDive.class).isDoing()) {
			if (skyDiveAnimator == null) {
				skyDiveAnimator = new SkyDiveAnimator(diveAnimator.getPitchAngle());
			}
			float factor = getTransitionFactor(transformer.getPartialTick());
			if (transitioning) {
				diveAnimator.animatePost(player, parkourability, transformer);
			}
			skyDiveAnimator.animatePost(player, parkourability, transformer, factor);
		} else {
			if (transitioning && skyDiveAnimator != null) {
				float factor = getTransitionFactor(transformer.getPartialTick());
				skyDiveAnimator.animatePost(player, parkourability, transformer);
				diveAnimator.animatePost(player, parkourability, transformer, factor);
			} else {
				skyDiveAnimator = null;
				diveAnimator.animatePost(player, parkourability, transformer);
			}
		}
	}

	@Override
	public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
		checkTransition(parkourability);
		if (parkourability.get(SkyDive.class).isDoing()) {
			if (skyDiveAnimator == null) {
				skyDiveAnimator = new SkyDiveAnimator(diveAnimator.getPitchAngle());
			}
			skyDiveAnimator.rotate(player, parkourability, rotator);
		} else {
			if (transitioning && skyDiveAnimator != null) {
				float factor = getTransitionFactor(rotator.getPartialTick());
				diveAnimator.rotate(player, parkourability, rotator, factor, skyDiveAnimator.getPitchAngle());
			} else {
				skyDiveAnimator = null;
				diveAnimator.rotate(player, parkourability, rotator);
			}
		}
	}

	private void startTransition() {
		transitioning = true;
		transitionTick = 0;
	}

	private boolean transitioning = false;
	private int transitionTick = 0;

	private float getTransitionFactor(float partialTick) {
		float factor;
		if (transitioning) {
			factor = (transitionTick + partialTick) / MaxTransitionTick;
		} else {
			factor = 1;
		}
		return factor;
	}

	public static class SkyDiveAnimator extends Animator {
		private int forwardAngleCount = 0;
		private int rightAngleCount = 0;
		private final int maxCount = 8;
		private final float startPitchAngle;
		private float pitchAngle;

		public SkyDiveAnimator(float startPitchAngleDegree) {
			this.startPitchAngle = startPitchAngleDegree;
		}

		@Override
		public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
			return false;
		}

		@Override
		public void tick() {
			super.tick();
			if (KeyBindings.getKeyForward().isDown()) {
				if (KeyBindings.getKeyBack().isDown()) {
					if (forwardAngleCount > 0) forwardAngleCount--;
					if (forwardAngleCount < 0) forwardAngleCount++;
				} else {
					if (forwardAngleCount < maxCount) forwardAngleCount++;
				}
			} else if (KeyBindings.getKeyBack().isDown()) {
				if (forwardAngleCount > -maxCount) forwardAngleCount--;
			} else {
				if (forwardAngleCount > 0) forwardAngleCount--;
				if (forwardAngleCount < 0) forwardAngleCount++;
			}
			if (KeyBindings.getKeyRight().isDown()) {
				if (KeyBindings.getKeyLeft().isDown()) {
					if (rightAngleCount > 0) rightAngleCount--;
					if (rightAngleCount < 0) rightAngleCount++;
				} else {
					if (rightAngleCount < maxCount) rightAngleCount++;
				}
			} else if (KeyBindings.getKeyLeft().isDown()) {
				if (rightAngleCount > -maxCount) rightAngleCount--;
			} else {
				if (rightAngleCount > 0) rightAngleCount--;
				if (rightAngleCount < 0) rightAngleCount++;
			}
		}

		@Override
		public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
			animatePost(player, parkourability, transformer, 1);
		}

		public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer, float factor) {
			float forwardAngleFactor = getForwardAngleFactor(transformer.getPartialTick());
			float rightAngleFactor = getRightAngleFactor(transformer.getPartialTick());
			transformer.
					rotateHeadPitch(-20)
					.rotateRightArm((float) Math.toRadians(20 - 10 * rightAngleFactor), 0, (float) Math.toRadians(70 - 30 * forwardAngleFactor), factor)
					.rotateLeftArm((float) Math.toRadians(20 + 10 * rightAngleFactor), 0, (float) Math.toRadians(-(70 - 30 * forwardAngleFactor)), factor)
					.rotateRightLeg((float) Math.toRadians(20 - 10 * rightAngleFactor), 0, (float) Math.toRadians(25 - 10 * forwardAngleFactor), factor)
					.rotateLeftLeg((float) Math.toRadians(20 + 10 * rightAngleFactor), 0, (float) Math.toRadians(-(25 - 10 * forwardAngleFactor)), factor)
					.makeArmsMoveDynamically(0.06f)
					.makeLegsMoveDynamically(0.06f)
					.end();
		}

		private float getForwardAngleFactor(float partial) {
			float phase;
			if (forwardAngleCount > 0) phase = (forwardAngleCount + partial) / maxCount;
			else if (forwardAngleCount < 0) phase = (forwardAngleCount - partial) / maxCount;
			else phase = 0;
			if (phase > 1) phase = 1;
			if (phase < -1) phase = -1;
			if (phase > 0) {
				return EasingFunctions.CubicInOut(phase);
			} else if (phase < 0) {
				return -EasingFunctions.CubicInOut(-phase);
			}
			return 0;
		}

		private float getRightAngleFactor(float partial) {
			float phase;
			if (rightAngleCount > 0) phase = (rightAngleCount + partial) / maxCount;
			else if (rightAngleCount < 0) phase = (rightAngleCount - partial) / maxCount;
			else phase = 0;
			if (phase > 1) phase = 1;
			if (phase < -1) phase = -1;

			if (phase > 0) {
				return 1 - (1 - phase) * (1 - phase);
			} else if (phase < 0) {
				phase = -phase;
				return -1 + (1 - phase) * (1 - phase);
			}
			return 0;
		}

		@Override
		public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
			float forwardAngleFactor = getForwardAngleFactor(rotator.getPartialTick());
			float rightAngleFactor = getRightAngleFactor(rotator.getPartialTick());
			float basePitchAngle;
			if (getTick() >= MaxTransitionTick) basePitchAngle = 90;
			else {
				basePitchAngle = MathUtil.lerp(startPitchAngle, 90, (getTick() + rotator.getPartialTick()) / MaxTransitionTick);
			}
			pitchAngle = basePitchAngle + 24 * forwardAngleFactor;
			rotator.startBasedCenter()
					.rotatePitchFrontward(pitchAngle)
					.rotateYawRightward(24 * rightAngleFactor)
					.end();
		}

		float getPitchAngle() {
			return pitchAngle;
		}
	}

	public static class DiveAnimator extends Animator {
		public DiveAnimator(double startYSpeed) {
			this.startYSpeed = startYSpeed;
		}

		private final double startYSpeed;
		private float pitchAngle = 0;
		private float oldFactor = 0;

		private float getFactor(double yMovement) {
			return (float) (-2 * Math.atan((yMovement - startYSpeed) / startYSpeed) / Math.PI);
		}

		@Override
		public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
			return false;
		}

		@Override
		public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
			animatePost(player, parkourability, transformer, 1);
		}

		void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer, float factor) {
			double ySpeed = parkourability.get(Dive.class).getPlayerYSpeed(transformer.getPartialTick());
			float bodyFactor = getFactor(ySpeed);
			transformer
					.rotateHeadPitch(-50 * bodyFactor)
					.rotateRightArm(0, 0, (float) Math.toRadians(195 * bodyFactor), factor)
					.rotateLeftArm(0, 0, (float) Math.toRadians(-195 * bodyFactor), factor)
					.rotateRightLeg((float) Math.toRadians(-180 * (bodyFactor - oldFactor)), 0, 0, factor)
					.rotateLeftLeg((float) Math.toRadians(-180 * (bodyFactor - oldFactor)), 0, 0, factor)
					.end();
			oldFactor = bodyFactor;
		}

		@Override
		public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
			rotate(player, parkourability, rotator, 1, 0);
		}

		public void rotate(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator, float factor, float transitionBaseAngle) {
			double ySpeed = parkourability.get(Dive.class).getPlayerYSpeed(rotator.getPartialTick());
			float angleFactor = getFactor(ySpeed);
			pitchAngle = 180 * angleFactor;
			rotator.startBasedCenter()
					.rotatePitchFrontward(MathUtil.lerp(transitionBaseAngle, pitchAngle, factor))
					.end();
		}

		private float getPitchAngle() {
			return pitchAngle;
		}
	}
}
