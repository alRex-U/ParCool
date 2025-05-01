package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.api.compatibility.PlayerWrapper;
import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.capability.Parkourability;

public class CrawlAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerWrapper player, Parkourability parkourability) {
		return !parkourability.get(Crawl.class).isDoing();
	}

	@Override
	public void animatePost(PlayerWrapper player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float factor = (float) Math.sin(transformer.getLimbSwing() / 10 * Math.PI);
		float leftZFactor = (float) Math.cos(transformer.getLimbSwing() / 10 * Math.PI);
		float rightZFactor = -leftZFactor;
		if (leftZFactor < 0) leftZFactor /= 5;
		if (rightZFactor < 0) rightZFactor /= 5;
		transformer
                .translateLeftLeg(
                        0,
                        -0.1f + 0.8f * factor,
                        -0.6f + 0.6f * factor
                )
                .translateRightLeg(
                        0,
                        -0.1f - 0.8f * factor,
                        -0.6f - 0.6f * factor
                )
				.rotateLeftArm((float) Math.toRadians(-15 + 45 * leftZFactor), 0, (float) Math.toRadians(-130 - 30 * factor))
				.rotateRightArm((float) Math.toRadians(-15 + 45 * rightZFactor), 0, (float) Math.toRadians(130 - 30 * factor))
				.rotateLeftLeg((float) Math.toRadians(-11 + 7 * factor), 0, (float) Math.toRadians(-5 + 5 * factor))
				.rotateRightLeg((float) Math.toRadians(-11 - 7 * factor), 0, (float) Math.toRadians(5 + 5 * factor))
				.end();
	}
}
