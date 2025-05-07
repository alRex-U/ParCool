package com.alrex.parcool.common.attachment.client;

import com.alrex.parcool.api.unstable.animation.AnimationOption;
import com.alrex.parcool.api.unstable.animation.AnimationPart;
import com.alrex.parcool.api.unstable.animation.ParCoolAnimationInfoEvent;
import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PassiveCustomAnimation;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.ClientAttachments;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;

@OnlyIn(Dist.CLIENT)
public class Animation {

	public static Animation get(Player player) {
		return player.getData(ClientAttachments.ANIMATION);
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
		Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return false;
        if (animator != null && animator.shouldRemoved(player, parkourability)) animator = null;
        if (animator == null) return false;
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
        if (parkourability == null) return false;
        if (animator != null && animator.shouldRemoved(player, parkourability)) animator = null;
        if (animator == null) return false;
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
        if (animator.shouldRemoved(player, parkourability)) {
            animator = null;
            return;
        }
        if (option.isCanceled(AnimationPart.CAMERA)) return;
		animator.onCameraSetUp(event, player, parkourability);
	}

    public void tick(AbstractClientPlayer player, Parkourability parkourability) {
		passiveAnimation.tick(player, parkourability);
		if (animator != null) {
            animator.tick(player);
		}
        {
            ParCoolAnimationInfoEvent animationEvent = new ParCoolAnimationInfoEvent(player, animator);
			NeoForge.EVENT_BUS.post(animationEvent);
            option = animationEvent.getOption();
        }
	}

	public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
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
