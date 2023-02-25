package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;

public class CrawlAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.get(Crawl.class).isDoing();
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float factor = (float) Math.sin(transformer.getLimbSwing() / 10 * Math.PI);
		transformer
				.rotateLeftArm((float) Math.toRadians(-15), 0, (float) Math.toRadians(-120 - 25 * factor))
				.rotateRightArm((float) Math.toRadians(-15), 0, (float) Math.toRadians(120 - 25 * factor))
				.rotateLeftLeg((float) Math.toRadians(-15), 0, (float) Math.toRadians(-5 + 5 * factor))
				.rotateRightLeg((float) Math.toRadians(-15), 0, (float) Math.toRadians(5 + 5 * factor))
				.end();
	}
}
