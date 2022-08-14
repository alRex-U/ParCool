package com.alrex.parcool.common.capability;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class Animation {
	public static Animation get(PlayerEntity player) {
		LazyOptional<Animation> optional = player.getCapability(Capabilities.ANIMATION_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

	private Animator animator = null;

	public void setAnimator(Animator animator) {
		this.animator = animator;
	}

	public boolean animatePre(PlayerEntity player, PlayerModelTransformer modelTransformer) {
		if (animator == null) return false;
		Parkourability parkourability = Parkourability.get(player);
		return animator.animatePre(player, parkourability, modelTransformer);
	}

	public void animatePost(PlayerEntity player, PlayerModelTransformer modelTransformer) {
		if (animator == null) return;
		Parkourability parkourability = Parkourability.get(player);
		animator.animatePost(player, parkourability, modelTransformer);
	}

	public void applyRotate(AbstractClientPlayerEntity player, PlayerModelRotator rotator) {
		if (animator == null) return;
		Parkourability parkourability = Parkourability.get(player);

		animator.rotate(player, parkourability, rotator);
	}

	public void onRenderTick(TickEvent.RenderTickEvent event) {
		if (animator == null) return;
		PlayerEntity player = Minecraft.getInstance().player;
		if (player == null) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;

		animator.onRender(event, player, parkourability);
	}

	public void tick(PlayerEntity player, Parkourability parkourability) {
		if (animator != null) {
			animator.tick();
			if (animator.shouldRemoved(player, parkourability)) animator = null;
		}
	}

	public void removeAnimator() {
		animator = null;
	}
}
