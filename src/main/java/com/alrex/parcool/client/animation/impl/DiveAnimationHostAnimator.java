package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.action.impl.SkyDive;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class DiveAnimationHostAnimator extends Animator {
	public DiveAnimationHostAnimator(double ySpeed, boolean fromInAir) {
		diveAnimator = new DiveAnimator(ySpeed);
		this.fromInAir = fromInAir;
	}
	final DiveAnimator diveAnimator;
	@Nullable
	SkyDiveAnimator skyDiveAnimator = null;
	final boolean fromInAir;
	final static int MaxTransitionStartedInAirTick = 10;
	final static int MaxTransitionTick = 6;

	@Override
	public void tick(Player player) {
		super.tick(player);
		diveAnimator.tick(player);
		if (skyDiveAnimator != null) skyDiveAnimator.tick(player);
		if (transitioning) {
			transitionTick++;
			if (transitionTick >= MaxTransitionTick) {
				transitionTick = 0;
				transitioning = false;
			}
		}
	}

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
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
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		if (fromInAir && getTick() < MaxTransitionStartedInAirTick) { // transition when started in air
			float phase = (getTick() + transformer.getPartialTick()) / MaxTransitionStartedInAirTick;
			diveAnimator.animatePost(
					player, parkourability, transformer,
					new Easing(phase)
							.squareOut(0, 1, 0, 1)
							.get()
			);
			float legAngle = -45 * (float) Math.toRadians
					(new Easing(phase)
							.squareOut(0, 0.4f, 0, 1)
							.sinInOut(0.4f, 1, 1, 0)
							.get()
					);
			transformer
					.addRotateLeftLeg(legAngle, 0, 0)
					.addRotateRightLeg(legAngle, 0, 0)
					.end();
		} else {// normal animation
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
	}

	@Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		if (fromInAir && getTick() < MaxTransitionStartedInAirTick) { // transition when started in air
			float factor = new Easing((getTick() + rotator.getPartialTick()) / MaxTransitionStartedInAirTick)
					.squareOut(0, 1, 0, 1)
					.get();
			diveAnimator.rotate(
					player, parkourability, rotator, factor, 0
			);
		} else {
			checkTransition(parkourability);
			if (parkourability.get(SkyDive.class).isDoing()) {
				if (skyDiveAnimator == null) {
					skyDiveAnimator = new SkyDiveAnimator(diveAnimator.getPitchAngle());
				}
                skyDiveAnimator.rotatePost(player, parkourability, rotator);
			} else {
				if (transitioning && skyDiveAnimator != null) {
					float factor = getTransitionFactor(rotator.getPartialTick());
					diveAnimator.rotate(player, parkourability, rotator, factor, skyDiveAnimator.getPitchAngle());
				} else {
					skyDiveAnimator = null;
                    diveAnimator.rotatePost(player, parkourability, rotator);
				}
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
		private int forwardAngleCountOld = 0;
		private int rightAngleCount = 0;
		private int rightAngleCountOld = 0;
		private final int maxCount = 8;
		private final float startPitchAngle;
		private float pitchAngle;

		public SkyDiveAnimator(float startPitchAngleDegree) {
			this.startPitchAngle = startPitchAngleDegree;
		}

		@Override
		public boolean shouldRemoved(Player player, Parkourability parkourability) {
			return false;
		}

		@Override
		public void tick(Player player) {
			super.tick(player);
			forwardAngleCountOld = forwardAngleCount;
			rightAngleCountOld = rightAngleCount;
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
		public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
			animatePost(player, parkourability, transformer, 1);
		}

		public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer, float factor) {
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
			if (forwardAngleCount > 0)
				phase = MathUtil.lerp(forwardAngleCountOld, forwardAngleCount, partial) / maxCount;
			else if (forwardAngleCount < 0)
				phase = MathUtil.lerp(forwardAngleCountOld, forwardAngleCount, partial) / maxCount;
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
			if (rightAngleCount > 0) phase = MathUtil.lerp(rightAngleCountOld, rightAngleCount, partial) / maxCount;
			else if (rightAngleCount < 0)
				phase = MathUtil.lerp(rightAngleCountOld, rightAngleCount, partial) / maxCount;
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
        public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
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
					.rotateYawRightward(-24 * rightAngleFactor)
					.end();
        }

		float getPitchAngle() {
			return pitchAngle;
		}
	}

	public static class DiveAnimator extends Animator {
		public DiveAnimator(double startYSpeed) {
			this.initialYSpeed = startYSpeed;
		}

		private final double initialYSpeed;
		private float pitchAngle = 0;
		private float oldFactor = 0;

		private float getFactor(double yMovement) {
			return (float) Math.max(
					0,
					2 / (1 + Math.exp(yMovement / (initialYSpeed) - 1)) - 0.9621 // -0.9621 is - 2 / (1+exp(-1)) + 0.5
			);
		}

		@Override
		public boolean shouldRemoved(Player player, Parkourability parkourability) {
			return false;
		}

		@Override
		public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
			animatePost(player, parkourability, transformer, 1);
		}

		void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer, float factor) {
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
        public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
			rotate(player, parkourability, rotator, 1, 0);
        }

		public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator, float factor, float transitionBaseAngle) {
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
