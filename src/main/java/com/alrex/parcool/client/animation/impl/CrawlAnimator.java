package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.player.PlayerEntity;

public class CrawlAnimator extends Animator {
	@Override
	public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
		return !parkourability.getCrawl().isDoing();
	}

	@Override
	public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float angle = 140 + 35 * (float) Math.sin(transformer.getLimbSwing() / 10 * Math.PI);
		transformer
				.rotateLeftArm(0, 0, (float) Math.toRadians(-angle))
				.rotateRightArm(0, 0, (float) Math.toRadians(angle))
				.end();
	}
}
