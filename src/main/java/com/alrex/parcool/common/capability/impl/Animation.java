package com.alrex.parcool.common.capability.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PassiveCustomAnimation;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.provider.AnimationProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class Animation {
	public static Animation get(Player player) {
		LazyOptional<Animation> optional = player.getCapability(AnimationProvider.ANIMATION_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

	private Animator animator = null;
	private final PassiveCustomAnimation passiveAnimation = new PassiveCustomAnimation();

	public void setAnimator(Animator animator) {
		this.animator = animator;
	}

	public boolean animatePre(Player player, PlayerModelTransformer modelTransformer) {
		if (animator == null) return false;
		Parkourability parkourability = Parkourability.get(player);
		return animator.animatePre(player, parkourability, modelTransformer);
	}

	public void animatePost(Player player, PlayerModelTransformer modelTransformer) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (animator == null) {
			passiveAnimation.animate(player, parkourability, modelTransformer);
			return;
		}
		animator.animatePost(player, parkourability, modelTransformer);
	}

	public void applyRotate(AbstractClientPlayer player, PlayerModelRotator rotator) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (animator == null) {
			passiveAnimation.rotate(player, parkourability, rotator);
			return;
		}
		animator.rotate(player, parkourability, rotator);
	}

	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (animator == null) return;
		Player player = Minecraft.getInstance().player;
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;

		animator.onRender(event, player, parkourability);
	}

	public void tick(Player player, Parkourability parkourability) {
		passiveAnimation.tick(player, parkourability);
		if (animator != null) {
			animator.tick();
			if (animator.shouldRemoved(player, parkourability)) animator = null;
		}
	}

	public boolean hasAnimator() {
		return animator != null;
	}

	public void removeAnimator() {
		animator = null;
	}
}
