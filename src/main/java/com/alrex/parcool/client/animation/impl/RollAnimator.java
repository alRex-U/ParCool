package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class RollAnimator extends Animator {
	private final Roll.Direction direction;

	public RollAnimator(Roll.Direction direction) {
		this.direction = direction;
	}

	private static float calculateMovementFactor(float progress) {
		return -MathUtil.squaring(progress - 1) + 1;
	}

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.get(Roll.class).isDoing();
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		switch (direction) {
			case Front:
			case Back: {
				animatePostFrontBack(player, parkourability, transformer);
			}
			break;
			case Right:
			case Left: {
				animatePostLeftRight(player, parkourability, transformer);
			}
			break;
		}
	}

	void animatePostFrontBack(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		Roll roll = parkourability.get(Roll.class);
		float phase = (getTick() + transformer.getPartialTick()) / roll.getRollMaxTick();
        if (phase > 1) return;
		float factor = 1 - 4 * (0.5f - phase) * (0.5f - phase);
        float animationFactor = new Easing(phase)
                .squareOut(0, 0.1f, 0, 1)
                .linear(0, 0.8f, 1, 1)
                .sinInOut(0.8f, 1, 1, 0)
                .get();
		transformer
				.addRotateLeftLeg(
                        (float) Math.toRadians(-70 * factor), 0, 0, animationFactor
				)
				.addRotateRightLeg(
                        (float) Math.toRadians(-70 * factor), 0, 0, animationFactor
				)
				.addRotateRightArm(
                        (float) Math.toRadians(-80 * factor), 0, 0, animationFactor
				)
				.addRotateLeftArm(
                        (float) Math.toRadians(-80 * factor), 0, 0, animationFactor
				)
				.end();
	}

	void animatePostLeftRight(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		Roll roll = parkourability.get(Roll.class);
		float phase = (getTick() + transformer.getPartialTick()) / roll.getRollMaxTick();
		if (phase > 1) {
			return;
		}
		float animFactor = new Easing(phase)
				.squareOut(0, 0.25f, 0, 1)
				.linear(0.25f, 0.75f, 1, 1)
				.squareIn(0.75f, 1, 1, 0)
				.get();
		if (direction == Roll.Direction.Left) {
			float rightArmXFactor = new Easing(phase)
					.sinInOut(0, 0.40f, 0, 1)
					.linear(0.40f, 0.7f, 1, 1)
					.sinInOut(0.7f, 1, 1, 0)
					.get();
			float leftArmXFactor = new Easing(phase)
					.sinInOut(0, 0.3f, 0, -0.8f)
					.sinInOut(0.3f, 0.6f, -0.8f, 1)
					.sinInOut(0.6f, 1, 1, 0)
					.get();
			float leftArmZFactor = new Easing(phase)
					.linear(0, 0.3f, 0, 0)
					.sinInOut(0.4f, 0.7f, 0, 1)
					.sinInOut(0.7f, 1, 1, 0)
					.get();
			float rightArmZFactor = new Easing(phase)
					.sinInOut(0, 0.5f, 0, 1)
					.sinInOut(0.5f, 1, 1, 0)
					.get();
			float leftLegXFactor = new Easing(phase)
					.sinInOut(0, 0.35f, 0, 1)
					.linear(0.35f, 0.65f, 1, 1)
					.sinInOut(0.65f, 1, 1, 0)
					.get();
			float leftLegZFactor = new Easing(phase)
					.linear(0, 0.5f, 0, 0.2f)
					.sinInOut(0.5f, 0.75f, 0.2f, 1)
					.sinInOut(0.75f, 1, 1, 0)
					.get();
			float rightLegXFactor = new Easing(phase)
					.linear(0, 0.2f, 0, 0)
					.sinInOut(0.2f, 0.6f, 0, 1)
					.sinInOut(0.6f, 1, 1, 0).get();
			float rightLegZFactor = new Easing(phase)
					.squareOut(0, 0.4f, 0, 1)
					.sinInOut(0.4f, 0.75f, 1, 0.2f)
					.linear(0.75f, 1, 0.2f, 0)
					.get();
			float headYawFactor = new Easing(phase)
					.sinInOut(0, 0.5f, 0, 1)
					.sinInOut(0.5f, 1, 1, 0)
					.get();
			float headPitchFactor = new Easing(phase)
					.sinInOut(0, 0.5f, 0, 1)
					.sinInOut(0.5f, 1, 1, 0)
					.get();
			transformer
					.rotateRightLeg((float) Math.toRadians(-85 * rightLegXFactor + 15), 0, (float) Math.toRadians(30 * rightLegZFactor), animFactor)
					.rotateLeftLeg((float) Math.toRadians(-85 * leftLegXFactor), 0, (float) Math.toRadians(-35 * leftLegZFactor), animFactor)
					.rotateRightArm((float) Math.toRadians(-60 * rightArmXFactor), 0, (float) Math.toRadians(40 * rightArmZFactor), animFactor)
					.rotateLeftArm((float) Math.toRadians(-5 + 45 * leftArmXFactor), 0, (float) Math.toRadians(-20 * leftArmZFactor), animFactor)
					.rotateAdditionallyHeadPitch(40 * headPitchFactor)
					.rotateAdditionallyHeadYaw(30 * headYawFactor)
					.end();
		} else {
			float leftArmXFactor = new Easing(phase)
					.sinInOut(0, 0.40f, 0, 1)
					.linear(0.40f, 0.7f, 1, 1)
					.sinInOut(0.7f, 1, 1, 0)
					.get();
			float rightArmXFactor = new Easing(phase)
					.sinInOut(0, 0.3f, 0, -0.8f)
					.sinInOut(0.3f, 0.6f, -0.8f, 1)
					.sinInOut(0.6f, 1, 1, 0)
					.get();
			float rightArmZFactor = new Easing(phase)
					.linear(0, 0.3f, 0, 0)
					.sinInOut(0.4f, 0.7f, 0, 1)
					.sinInOut(0.7f, 1, 1, 0)
					.get();
			float leftArmZFactor = new Easing(phase)
					.sinInOut(0, 0.5f, 0, 1)
					.sinInOut(0.5f, 1, 1, 0)
					.get();
			float rightLegXFactor = new Easing(phase)
					.sinInOut(0, 0.35f, 0, 1)
					.linear(0.35f, 0.65f, 1, 1)
					.sinInOut(0.65f, 1, 1, 0)
					.get();
			float rightLegZFactor = new Easing(phase)
					.linear(0, 0.5f, 0, 0.2f)
					.sinInOut(0.5f, 0.75f, 0.2f, 1)
					.sinInOut(0.75f, 1, 1, 0)
					.get();
			float leftLegXFactor = new Easing(phase)
					.linear(0, 0.2f, 0, 0)
					.sinInOut(0.2f, 0.6f, 0, 1)
					.sinInOut(0.6f, 1, 1, 0).get();
			float leftLegZFactor = new Easing(phase)
					.squareOut(0, 0.4f, 0, 1)
					.sinInOut(0.4f, 0.75f, 1, 0.2f)
					.linear(0.75f, 1, 0.2f, 0)
					.get();
			float headYawFactor = new Easing(phase)
					.sinInOut(0, 0.5f, 0, 1)
					.sinInOut(0.5f, 1, 1, 0)
					.get();
			float headPitchFactor = new Easing(phase)
					.sinInOut(0, 0.5f, 0, 1)
					.sinInOut(0.5f, 1, 1, 0)
					.get();
			transformer
					.rotateRightLeg((float) Math.toRadians(-85 * rightLegXFactor), 0, (float) Math.toRadians(30 * rightLegZFactor), animFactor)
					.rotateLeftLeg((float) Math.toRadians(-85 * leftLegXFactor + 15), 0, (float) Math.toRadians(-35 * leftLegZFactor), animFactor)
					.rotateRightArm((float) Math.toRadians(-5 + 45 * rightArmXFactor), 0, (float) Math.toRadians(-20 * rightArmZFactor), animFactor)
					.rotateLeftArm((float) Math.toRadians(-60 * leftArmXFactor), 0, (float) Math.toRadians(40 * leftArmZFactor), animFactor)
					.rotateAdditionallyHeadPitch(40 * headPitchFactor)
					.rotateAdditionallyHeadYaw(-30 * headYawFactor)
					.end();
		}
	}

	@Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		switch (direction) {
			case Front:
			case Back: {
				rotateFrontBack(player, parkourability, rotator);
			}
			break;
			case Left:
			case Right: {
				rotateLeftRight(player, parkourability, rotator);
			}
			break;
		}
    }

	private void rotateFrontBack(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		Roll roll = parkourability.get(Roll.class);
		float phase = (getTick() + rotator.getPartialTick()) / roll.getRollMaxTick();
        if (phase > 1) return;
		float factor = calculateMovementFactor(phase);
		float sign = direction == Roll.Direction.Front ? 1 : -1;
        float translateYFactor;
        if (direction == Roll.Direction.Front) {
            translateYFactor = new Easing(phase)
                    .squareOut(0, 0.5f, 0, 1)
                    .sinInOut(0.5f, 1, 1, 0)
                    .get();
        } else {
            translateYFactor = new Easing(phase)
                    .squareOut(0, 0.35f, 0, 0.6f)
                    .sinInOut(0.35f, 0.9f, 0.6f, 0)
                    .linear(0.9f, 1, 0, 0)
                    .get();
        }
		rotator
				.startBasedCenter()
                .translateY(-translateYFactor * player.getBbHeight() / 4f)
				.rotatePitchFrontward(sign * MathUtil.lerp(0, 360, factor))
				.end();
	}

	private void rotateLeftRight(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / parkourability.get(Roll.class).getRollMaxTick();
		if (phase > 1) {
			return;
		}
		switch (direction) {
			case Left:
			case Right: {//side rolling
				float bodyPitchFactor = new Easing(phase)
						.squareOut(0, 0.45f, 0, 1)
						.linear(0.45f, 0.55f, 1, 1)
						.sinInOut(0.55f, 1, 1, 0)
						.get();
				float bodyYawFactor = new Easing(phase)
						.sinInOut(0, 1, 0, 1)
						.get();
				float yawFactor = new Easing(phase)
						.sinInOut(0, 0.3f, 0, -1)
						.sinInOut(0.3f, 0.7f, -1, 1)
						.sinInOut(0.7f, 1, 1, 0)
						.get();
				float yTranslateFactor = new Easing(phase)
						.squareOut(0, 0.45f, 0, 1f)
						.linear(0.45f, 0.55f, 1, 1)
						.sinInOut(0.55f, 1, 1, 0)
						.get();
				if (direction == Roll.Direction.Left) {
					rotator.startBasedCenter()
							.translateY(-yTranslateFactor * player.getBbHeight() / 3.5f)
							.rotateYawRightward(25 * yawFactor - 13)
							.rotatePitchFrontward(110 * bodyPitchFactor)
							.rotateYawRightward(360 * bodyYawFactor)
							.end();
				} else {
					rotator.startBasedCenter()
							.translateY(-yTranslateFactor * player.getBbHeight() / 3.5f)
							.rotateYawRightward(-25 * yawFactor + 13)
							.rotatePitchFrontward(110 * bodyPitchFactor)
							.rotateYawRightward(-360 * bodyYawFactor)
							.end();
				}
				break;
			}
		}
	}

	@Override
	public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
		switch (direction) {
			case Front:
			case Back: {
				onCameraSetUpFrontBack(event, clientPlayer, parkourability);
			}
			break;
			case Left:
			case Right: {
				onCameraSetUpLeftRight(event, clientPlayer, parkourability);
			}
			break;
		}
	}

	void onCameraSetUpFrontBack(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
		Roll roll = parkourability.get(Roll.class);
		float sign = direction == Roll.Direction.Front ? 1 : -1;
		if (roll.isDoing() &&
				clientPlayer.isLocalPlayer() &&
				Minecraft.getInstance().options.getCameraType().isFirstPerson() &&
				ParCoolConfig.Client.Booleans.EnableCameraAnimationOfRolling.get()
		) {
			float factor = calculateMovementFactor((float) ((roll.getDoingTick() + event.getPartialTick()) / (float) roll.getRollMaxTick()));
			event.setPitch(sign * (factor > 0.5 ? factor - 1 : factor) * 360f + clientPlayer.getViewXRot((float) event.getPartialTick()));
		}
	}

	void onCameraSetUpLeftRight(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
		float phase = (float) ((getTick() + event.getPartialTick()) / parkourability.get(Roll.class).getRollMaxTick());
		if (phase > 1) {
			return;
		}
		if (parkourability.get(Roll.class).isDoing() &&
				clientPlayer.isLocalPlayer() &&
				Minecraft.getInstance().options.getCameraType().isFirstPerson() &&
				ParCoolConfig.Client.Booleans.EnableCameraAnimationOfRolling.get()
		) {
			float rollFactor = new Easing(phase)
					.squareOut(0, 1, 0, 1)
					.get();
			if (direction == Roll.Direction.Right)
				event.setRoll(event.getRoll() + 360 * rollFactor);
			else
				event.setRoll(event.getRoll() - 360 * rollFactor);
		}
	}

	@Override
	public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
		switch (direction) {
			case Right: {
				player.setYBodyRot(player.getYHeadRot() - 5);
				break;
			}
			case Left: {
				player.setYBodyRot(player.getYHeadRot() + 5);
				break;
			}
		}
	}
}
