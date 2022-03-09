package com.alrex.parcool.common.capability.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.common.capability.provider.AnimationProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.LazyOptional;

@OnlyIn(Dist.CLIENT)
public class Animation {
	public static Animation get(Player player) {
		LazyOptional<Animation> optional = player.getCapability(AnimationProvider.ANIMATION_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

	private Animator animator = null;

	public void setAnimator(Animator animator) {
		this.animator = animator;
	}

	public void animate(RenderPlayerEvent.Pre event) {
		if (animator == null) return;
		AbstractClientPlayer player = (AbstractClientPlayer) event.getPlayer();
		Parkourability parkourability = Parkourability.get(player);
		animator.animate(event, player, parkourability);
		if (animator.isRemoved()) animator = null;
	}

	public void tick() {
		if (animator != null) animator.tick();
	}

	public void removeAnimator() {
		animator = null;
	}
}
