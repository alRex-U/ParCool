package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.world.entity.player.Player;

;

public class BackwardWallJumpAnimator extends Animator {
	private final int maxTick = 14;

	@Override
	public boolean shouldRemoved(Player player, Parkourability parkourability) {
		return getTick() >= maxTick;
	}

	@Override
	public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
	}

	@Override
	public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
	}
}
