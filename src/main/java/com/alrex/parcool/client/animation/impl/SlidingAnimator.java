package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.util.math.vector.Vector3d;

public class SlidingAnimator extends Animator {
	private static final int MAX_TRANSITION_TICK = 5;
	@Override
	public boolean shouldRemoved(PlayerWrapper player, Parkourability parkourability) {
		return !parkourability.get(Slide.class).isDoing();
	}

	@Override
	public void animatePost(PlayerWrapper player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float animFactor = (getTick() + transformer.getPartialTick()) / MAX_TRANSITION_TICK;
		if (animFactor > 1) animFactor = 1;
		animFactor = new Easing(animFactor)
				.sinInOut(0, 1, 0, 1)
				.get();

		transformer
                .translateLeftLeg(
                        0,
                        -1.2f * animFactor,
                        -2f * animFactor
                )
                .translateRightArm(
                        0,
                        1.2f * animFactor,
                        1.2f * animFactor
                )
                .translateHead(0, 0, -animFactor)
				.rotateHeadPitch(50 * animFactor)
				.rotateAdditionallyHeadYaw(50 * animFactor)
				.rotateAdditionallyHeadRoll(-10 * animFactor)
				.rotateRightArm((float) Math.toRadians(50), (float) Math.toRadians(-40), 0, animFactor)
				.rotateLeftArm((float) Math.toRadians(20), 0, (float) Math.toRadians(-100), animFactor)
				.rotateRightLeg((float) Math.toRadians(-30), (float) Math.toRadians(40), 0, animFactor)
				.rotateLeftLeg((float) Math.toRadians(40), (float) Math.toRadians(-30), (float) Math.toRadians(15), animFactor)
				.makeLegsLittleMoving()
				.makeArmsNatural()
				.end();
	}

	@Override
	public boolean rotatePre(PlayerWrapper player, Parkourability parkourability, PlayerModelRotator rotator) {
		Vector3d vec = parkourability.get(Slide.class).getSlidingVector();
		if (vec == null) return false;
		float animFactor = (getTick() + rotator.getPartialTick()) / MAX_TRANSITION_TICK;
		float yRot = (float) VectorUtil.toYawDegree(vec);
		if (animFactor > 1) animFactor = 1;
		animFactor = new Easing(animFactor)
				.sinInOut(0, 1, 0, 1)
				.get();
		rotator
				.rotateYawRightward(180f + yRot)
				.rotatePitchFrontward(-55f * animFactor)
				.translate(0.35f * animFactor, 0, 0)
				.rotateYawRightward(-55f * animFactor)
				.translate(0, -0.7f * animFactor, -0.3f * animFactor);
		return true;
	}
}
