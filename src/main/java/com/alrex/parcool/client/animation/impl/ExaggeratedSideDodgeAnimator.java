package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

public class ExaggeratedSideDodgeAnimator extends Animator {
	public static final int Dodge_Max_Tick = 14;

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return getTick() >= Dodge_Max_Tick;
	}

	final Dodge.DodgeDirection direction;

	public ExaggeratedSideDodgeAnimator(Dodge.DodgeDirection dodgeDirection) {
		direction = dodgeDirection;
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float phase = (getTick() + transformer.getPartialTick()) / Dodge_Max_Tick;
		if (phase > 1) {
			return;
		}
		float animFactor = new Easing(phase)
				.squareIn(0, 0.25f, 0, 1)
				.linear(0.25f, 0.75f, 1, 1)
				.squareOut(0.75f, 1, 1, 0)
				.get();
		switch (direction) {
			case Front:
				break;
			case Right:
				break;
			case Left: {
				float rightArmZFactor = new Easing(phase)
						.sinInOut(0, 0.2f, 0, 1)
						.linear(0.2f, 0.4f, 1, 1)
						.sinInOut(0.4f, 1, 1, 0)
						.get();
				float rightArmXFactor = new Easing(phase)
						.sinInOut(0, 0.2f, 0, -1)
						.sinInOut(0.2f, 0.6f, -1, 1)
						.sinInOut(0.6f, 1, 1, 0)
						.get();
				float leftLegFactor = new Easing(phase)
						.sinInOut(0, 0.35f, 0, 1)
						.linear(0.35f, 0.65f, 1, 1)
						.sinInOut(0.65f, 1, 1, 0)
						.get();
				float leftArmZFactor = new Easing(phase)
						.sinInOut(0, 0.5f, 0, 1)
						.sinInOut(0.5f, 1, 1, 0)
						.get();
				float leftArmXFactor = new Easing(phase)
						.sinInOut(0, 0.25f, 0, -1)
						.sinInOut(0.25f, 0.75f, -1, 1)
						.sinInOut(0.75f, 1, 1, 0)
						.get();
				float rightLegXFactor = new Easing(phase)
						.linear(0, 0.2f, 0, 0)
						.sinInOut(0.2f, 0.6f, 0, 1)
						.sinInOut(0.6f, 1, 1, 0).get();
				float rightLegZFactor = new Easing(phase)
						.squareOut(0, 0.25f, 0, 1)
						.sinInOut(0.25f, 0.6f, 1, 0.2f)
						.linear(0.6f, 1, 0.2f, 0)
						.get();
				transformer
						.rotateRightLeg((float) Math.toRadians(70 * rightLegXFactor), 0, (float) Math.toRadians(80 * rightLegZFactor), animFactor)
						.rotateRightArm((float) Math.toRadians(70 * rightArmXFactor), 0, (float) Math.toRadians(90 * rightArmZFactor), animFactor)
						.rotateLeftLeg((float) Math.toRadians(-70 * leftLegFactor), 0, 0, animFactor)
						.rotateLeftArm((float) Math.toRadians(70 * leftArmXFactor), 0, (float) Math.toRadians(-60 * leftArmZFactor), animFactor)
						.end();
				break;
			}
			case Back:
				float leftArmFactor = new Easing(phase)
						.linear(0, 0.25f, 0, 0)
						.sinInOut(0.20f, 0.45f, 0, 1)
						.linear(0.45f, 0.55f, 1, 1)
						.sinInOut(0.55f, 0.8f, 1, 0)
						.linear(0.75f, 1, 0, 0)
						.get();
				float leftLegFactor = new Easing(phase)
						.squareOut(0, 0.4f, 0, 1)
						.sinInOut(0.4f, 0.8f, 1, 0)
						.linear(0.8f, 1, 0, 0)
						.get();
				transformer
						.rotateLeftArm(0, 0, (float) Math.toRadians(-100 * leftArmFactor), animFactor)
						.rotateLeftLeg((float) Math.toRadians(-40 * leftLegFactor), 0, 0, animFactor)
						.end();
				break;
		}
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
		float phase = (getTick() + rotator.getPartialTick()) / Dodge_Max_Tick;
		if (phase > 1) {
			return;
		}
		switch (direction) {
			case Left: {
				float bodyPitchFactor = new Easing(phase)
						.linear(0, 0.1f, 0, 0.1f)
						.sinInOut(0.1f, 0.35f, 0.1f, 1)
						.linear(0.35f, 0.65f, 1, 1)
						.sinInOut(0.65f, 0.9f, 1, 0.1f)
						.linear(0.9f, 1, 0.1f, 0)
						.get();
				float yawFactor = new Easing(phase)
						.squareIn(0, 0.3f, 0, 0.4f)
						.linear(0.30f, 0.7f, 0.40f, 0.6f)
						.squareOut(0.7f, 1, 0.6f, 1).get();
				float bodyYawFactor = new Easing(phase)
						.cubicInOut(0, 1, 0, 1)
						.get();
				rotator.startBasedCenter()
						.rotateYawRightward(-360 * yawFactor)
						.rotatePitchFrontward(90 * bodyPitchFactor)
						.rotateYawRightward(-360 * bodyYawFactor)
						.end();
				break;
			}
			case Back:
				float bodyRollFactor = new Easing(phase)
						.sinInOut(0, 0.5f, 0, 1)
						.sinInOut(0.5f, 1, 1, 0)
						.get();
				float bodyYawFactor = new Easing(phase)
						.sinInOut(0, 1, 0, 1).get();
				rotator.startBasedCenter()
						.rotateRollRightward(95 * bodyRollFactor)
						.rotateYawRightward(-360 * bodyYawFactor)
						.end();
				break;
		}
	}

	@Override
	public void onRenderTick(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {
		player.setYBodyRot(player.getYHeadRot());
	}
}
