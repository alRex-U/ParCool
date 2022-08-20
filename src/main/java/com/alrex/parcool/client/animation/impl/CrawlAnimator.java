package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.world.entity.player.Player;

public class CrawlAnimator extends Animator {
	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return !parkourability.getCrawl().isCrawling();
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
		float angle = 140 + 35 * (float) Math.sin(transformer.getLimbSwing() / 10 * Math.PI);
		transformer
				.rotateLeftArm(0, 0, (float) Math.toRadians(-angle))
				.rotateRightArm(0, 0, (float) Math.toRadians(angle))
				.end();
	}
}
