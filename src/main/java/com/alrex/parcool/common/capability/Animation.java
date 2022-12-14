package com.alrex.parcool.common.capability;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PassiveCustomAnimation;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.client.animation.impl.*;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.util.LazyOptional;

@OnlyIn(Dist.CLIENT)
public class Animation {
	public static Animation get(PlayerEntity player) {
		LazyOptional<Animation> optional = player.getCapability(Capabilities.ANIMATION_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

	private Animator animator = null;
	private final PassiveCustomAnimation passiveAnimation = new PassiveCustomAnimation();

	public void setAnimator(Animator animator) {
		if (ParCoolConfig.CONFIG_CLIENT.disableAnimation.get()) return;
		ParCoolConfig.Client config = ParCoolConfig.CONFIG_CLIENT;
		if (animator instanceof CatLeapAnimator && config.disableCatLeapAnimation.get()) return;
		if (animator instanceof ClimbUpAnimator && config.disableClimbUpAnimation.get()) return;
		if (animator instanceof ClingToCliffAnimator && config.disableClingToCliffAnimation.get()) return;
		if (animator instanceof CrawlAnimator && config.disableCrawlAnimation.get()) return;
		if (animator instanceof DodgeAnimator && config.disableDodgeAnimation.get()) return;
		if (animator instanceof FastRunningAnimator && config.disableFastRunAnimation.get()) return;
		if (animator instanceof FlippingAnimator && config.disableFlippingAnimation.get()) return;
		if (animator instanceof HorizontalWallRunAnimator && config.disableHorizontalWallRunAnimation.get()) return;
		if ((animator instanceof KongVaultAnimator || animator instanceof SpeedVaultAnimator) && config.disableVaultAnimation.get())
			return;
		if ((animator instanceof RollAnimator || animator instanceof TapAnimator) && config.disableBreakfallAnimation.get())
			return;
		if (animator instanceof SlidingAnimator && config.disableSlidingAnimation.get()) return;
		if (animator instanceof WallJumpAnimator && config.disableWallJumpAnimation.get()) return;
		if (animator instanceof WallSlideAnimator && config.disableWallSlideAnimation.get()) return;
		this.animator = animator;
	}

	public boolean animatePre(PlayerEntity player, PlayerModelTransformer modelTransformer) {
		if (animator == null) return false;
		Parkourability parkourability = Parkourability.get(player);
		return animator.animatePre(player, parkourability, modelTransformer);
	}

	public void animatePost(PlayerEntity player, PlayerModelTransformer modelTransformer) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (animator == null) {
			passiveAnimation.animate(player, parkourability, modelTransformer);
			return;
		}
		animator.animatePost(player, parkourability, modelTransformer);
	}

	public void applyRotate(AbstractClientPlayerEntity player, PlayerModelRotator rotator) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (animator == null) {
			passiveAnimation.rotate(player, parkourability, rotator);
			return;
		}
		animator.rotate(player, parkourability, rotator);
	}

	public void cameraSetup(EntityViewRenderEvent.CameraSetup event, PlayerEntity player, Parkourability parkourability) {
		if (animator == null) return;
		if (player.isLocalPlayer()
				&& Minecraft.getInstance().options.getCameraType().isFirstPerson()
				&& ParCoolConfig.CONFIG_CLIENT.disableFPVAnimation.get()
		) return;
		animator.onCameraSetUp(event, player, parkourability);
	}

	public void tick(PlayerEntity player, Parkourability parkourability) {
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
