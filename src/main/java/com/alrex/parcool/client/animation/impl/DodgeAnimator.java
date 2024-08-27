package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent;

public class DodgeAnimator extends Animator {
	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return getTick() >= Dodge.MAX_TICK;
	}

	final Dodge.DodgeDirection direction;

	public DodgeAnimator(Dodge.DodgeDirection dodgeDirection) {
		direction = dodgeDirection;
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / Dodge.MAX_TICK;
		if (phase > 1) {
			return;
		}
		float animFactor = new Easing(phase)
				.squareOut(0, 0.25f, 0, 1)
				.linear(0.25f, 0.75f, 1, 1)
				.sinInOut(0.70f, 1, 1, 0)
				.get();
		switch (direction) {
			case Right: {
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
				break;
			}
			case Left: {
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
				break;
			}
			case Front: {
				float rightLegXFactor = new Easing(phase)
						.sinInOut(0, 0.2f, 0, -0.3f)
						.sinInOut(0.2f, 0.4f, -0.3f, 1)
						.sinInOut(0.4f, 0.85f, 1, -0.3f)
						.sinInOut(0.85f, 1, -0.3f, 0)
						.get();
				float leftLegXFactor = new Easing(phase)
						.sinInOut(0, 0.1f, 0, -0.3f)
						.sinInOut(0.1f, 0.3f, -0.3f, 1)
						.sinInOut(0.3f, 0.75f, 1, -0.3f)
						.sinInOut(0.75f, 1, -0.3f, 0)
						.get();
				float armXFactor = new Easing(phase)
						.squareOut(0.0f, 0.20f, -0.1f, 1)
						.sinInOut(0.0f, 1, 1f, 0)
						.get();
				float headPitchFactor = new Easing(phase)
						.sinInOut(0, 0.10f, 0, -0.2f)
						.sinInOut(0.10f, 0.25f, -0.2f, 1)
						.sinInOut(0.25f, 1, 1f, 0)
						.get();
				float armZFactor = new Easing(phase)
						.sinInOut(0, 0.4f, 0, 1)
						.sinInOut(0.4f, 1, 1, 0)
						.get();
                transformer.getRawModel().leftArm.x -= (float) (0.8 * armZFactor);
                transformer.getRawModel().rightArm.x += (float) (0.8 * armZFactor);
				transformer
						.rotateRightLeg((float) Math.toRadians(rightLegXFactor * -70), 0, 0, animFactor)
						.rotateLeftLeg((float) Math.toRadians(leftLegXFactor * -70), 0, 0, animFactor)
                        .rotateRightArm((float) Math.toRadians(-170 * armXFactor), 0, (float) Math.toRadians(15 * armZFactor), animFactor)
                        .rotateLeftArm((float) Math.toRadians(-170 * armXFactor), 0, (float) Math.toRadians(-15 * armZFactor), animFactor)
						.rotateAdditionallyHeadPitch(40 * headPitchFactor)
						.end();
				break;
			}
			case Back: {
				float leftLegXFactor = new Easing(phase)
						.sinInOut(0, 0.2f, 0, -0.3f)
						.sinInOut(0.2f, 0.4f, -0.3f, 1)
						.sinInOut(0.4f, 0.85f, 1, -0.3f)
						.sinInOut(0.85f, 1, -0.3f, 0)
						.get();
				float rightLegXFactor = new Easing(phase)
						.sinInOut(0, 0.1f, 0, -0.3f)
                        .sinInOut(0.1f, 0.4f, -0.3f, 1)
                        .sinInOut(0.4f, 0.8f, 1, -0.3f)
                        .sinInOut(0.8f, 1, -0.3f, 0)
						.get();
				float armXFactor = new Easing(phase)
						.sinInOut(0, 0.10f, 0, -0.2f)
                        .sinInOut(0.10f, 0.4f, -0.2f, 1)
                        .sinInOut(0.4f, 1, 1f, 0)
						.get();
				float headPitchFactor = new Easing(phase)
						.sinInOut(0, 0.10f, 0, -0.2f)
                        .sinInOut(0.10f, 0.35f, -0.2f, 1)
                        .sinInOut(0.35f, 1, 1f, 0)
						.get();
				float armZFactor = new Easing(phase)
						.sinInOut(0, 0.4f, 0, 1)
						.sinInOut(0.4f, 1, 1, 0)
						.get();
				transformer
						.rotateRightLeg((float) Math.toRadians(rightLegXFactor * 70), 0, 0, animFactor)
						.rotateLeftLeg((float) Math.toRadians(leftLegXFactor * 70), 0, 0, animFactor)
						.rotateRightArm((float) Math.toRadians(-180 * armXFactor), 0, (float) Math.toRadians(10 * armZFactor), animFactor)
						.rotateLeftArm((float) Math.toRadians(-180 * armXFactor), 0, (float) Math.toRadians(-10 * armZFactor), animFactor)
						.rotateAdditionallyHeadPitch(-40 * headPitchFactor)
						.end();
				break;
			}
		}
	}

	@Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / Dodge.MAX_TICK;
		if (phase > 1) {
            return;
		}
		switch (direction) {
			case Front: {
				float bodyPitchFactor = new Easing(phase)
						.squareOut(0, 1, 0, 1)
                        .get();
                float translateYFactor = new Easing(phase)
                        .squareOut(0, 0.4f, 0, 1)
                        .sinInOut(0.4f, 1, 1, 0)
						.get();
				rotator.startBasedCenter()
                        .translateY(-translateYFactor * player.getBbHeight() / 4f)
						.rotatePitchFrontward(360 * bodyPitchFactor)
						.end();
				break;
			}
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
				if (direction == Dodge.DodgeDirection.Left) {
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
			case Back: {//backward handspring
				float bodyPitchFactor = new Easing(phase)
                        .linear(0, 0.4f, 0, 0.4f)
                        .sinInOut(0.4f, 1, 0.4f, 1)
						.get();
				rotator.startBasedCenter()
						.rotatePitchFrontward(-360 * bodyPitchFactor)
						.end();
				break;
			}
		}
    }

	@Override
	public void onCameraSetUp(EntityViewRenderEvent.CameraSetup event, Player clientPlayer, Parkourability parkourability) {
		if (!(clientPlayer.isLocalPlayer() &&
				Minecraft.getInstance().options.getCameraType().isFirstPerson() &&
				ParCoolConfig.Client.Booleans.EnableCameraAnimationOfDodge.get()
		)) return;
		float phase = (float) ((getTick() + event.getPartialTicks()) / Dodge.MAX_TICK);
		switch (direction) {
			case Front: {
				float bodyPitchFactor = new Easing(phase)
						.sinInOut(0, 1, 0, 1)
						.get();
				event.setPitch(event.getPitch() + 360 * bodyPitchFactor);
				break;
			}
			case Left:
			case Right: {
				float rollFactor = new Easing(phase)
						.squareOut(0, 1, 0, 1)
						.get();
				if (direction == Dodge.DodgeDirection.Right)
					event.setRoll(event.getRoll() + 360 * rollFactor);
				else
					event.setRoll(event.getRoll() - 360 * rollFactor);
				break;
			}
			case Back: {
				float bodyPitchFactor = new Easing(phase)
						.sinInOut(0, 1, 0, 1)
						.get();
				event.setPitch(event.getPitch() - 360 * bodyPitchFactor);
				break;
			}
		}
	}

	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {
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
