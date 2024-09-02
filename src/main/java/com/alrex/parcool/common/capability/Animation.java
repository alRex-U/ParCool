package com.alrex.parcool.common.capability;

import com.alrex.parcool.api.unstable.animation.AnimationOption;
import com.alrex.parcool.api.unstable.animation.AnimationPart;
import com.alrex.parcool.api.unstable.animation.ParCoolAnimationInfoEvent;
import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PassiveCustomAnimation;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class Animation {
	public static Animation get(Player player) {
		LazyOptional<Animation> optional = player.getCapability(Capabilities.ANIMATION_CAPABILITY);
		if (!optional.isPresent()) return null;
		return optional.orElseThrow(IllegalStateException::new);
	}

	private Animator animator = null;
    private AnimationOption option = new AnimationOption();
	private final PassiveCustomAnimation passiveAnimation = new PassiveCustomAnimation();

	public void setAnimator(Animator animator) {
		if (!ParCoolConfig.Client.Booleans.EnableAnimation.get()) return;
		if (!ParCoolConfig.Client.canAnimate(animator.getClass()).get()) return;
		this.animator = animator;
	}

	public boolean animatePre(Player player, PlayerModelTransformer modelTransformer) {
		if (animator == null) return false;
		Parkourability parkourability = Parkourability.get(player);
        modelTransformer.setOption(option);
        if (option.isAnimationCanceled()) return false;
		return animator.animatePre(player, parkourability, modelTransformer);
	}

	public void animatePost(Player player, PlayerModelTransformer modelTransformer) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (animator == null) {
			passiveAnimation.animate(player, parkourability, modelTransformer);
			return;
		}
        if (option.isAnimationCanceled()) return;
		animator.animatePost(player, parkourability, modelTransformer);
	}

    public boolean rotatePre(AbstractClientPlayer player, PlayerModelRotator rotator) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null || animator == null) return false;
        if (option.isAnimationCanceled() || option.isCanceled(AnimationPart.ROTATION)) return false;
        return animator.rotatePre(player, parkourability, rotator);
    }

    public void rotatePost(AbstractClientPlayer player, PlayerModelRotator rotator) {
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (animator == null) {
			passiveAnimation.rotate(player, parkourability, rotator);
			return;
		}
        if (option.isAnimationCanceled() || option.isCanceled(AnimationPart.ROTATION)) return;
        animator.rotatePost(player, parkourability, rotator);
	}

    public void cameraSetup(ViewportEvent.ComputeCameraAngles event, LocalPlayer player, Parkourability parkourability) {
		if (animator == null) return;
		if (player.isLocalPlayer()
				&& Minecraft.getInstance().options.getCameraType().isFirstPerson()
				&& !ParCoolConfig.Client.Booleans.EnableFPVAnimation.get()
		) return;
        if (option.isCanceled(AnimationPart.CAMERA)) return;
		animator.onCameraSetUp(event, player, parkourability);
	}

    public void tick(AbstractClientPlayer player, Parkourability parkourability) {
		passiveAnimation.tick(player, parkourability);
		if (animator != null) {
            animator.tick(player);
			if (animator.shouldRemoved(player, parkourability)) animator = null;
		}
        {
            ParCoolAnimationInfoEvent animationEvent = new ParCoolAnimationInfoEvent(player, animator);
            MinecraftForge.EVENT_BUS.post(animationEvent);
            option = animationEvent.getOption();
        }
	}

	public void onRenderTick(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {
		if (animator != null) {
			animator.onRenderTick(event, player, parkourability);
		}
	}

	public boolean hasAnimator() {
		return animator != null;
	}

	public void removeAnimator() {
		animator = null;
	}
}
