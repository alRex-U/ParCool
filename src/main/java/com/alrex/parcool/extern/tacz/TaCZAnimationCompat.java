package com.alrex.parcool.extern.tacz;

import com.alrex.parcool.api.unstable.animation.AnimationPart;
import com.alrex.parcool.api.unstable.animation.ParCoolAnimationInfoEvent;
import com.alrex.parcool.client.animation.impl.*;
import com.alrex.parcool.fabric.ParCoolEvents;
import com.tacz.guns.api.item.IGun;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Порт TaCZ-части ParCool Compatibility Addon (alRex-U/ParCool-CompatibilityAddon, LGPL): с оружием
 * в руке поза от TaCZ важнее анимации ParCool.
 *
 * <p>Класс трогает com.tacz.*, поэтому грузится только из-под проверки isModLoaded("tacz").
 */
@Environment(EnvType.CLIENT)
public final class TaCZAnimationCompat {
    private TaCZAnimationCompat() {}

    public static void register() {
        ParCoolEvents.register(
                event -> {
                    if (event instanceof ParCoolAnimationInfoEvent animEvent)
                        onAnimationInfo(animEvent);
                });
    }

    private static void onAnimationInfo(ParCoolAnimationInfoEvent event) {
        if (IGun.getIGunOrNull(event.getPlayer().getMainHandItem()) == null) return;

        // Акробатика, где поза с оружием всё равно нечитаема: ParCool рисует как есть.
        if (event.getAnimator() instanceof DiveAnimationHostAnimator
                || event.getAnimator() instanceof DiveIntoWaterAnimator
                || event.getAnimator() instanceof FastSwimAnimator
                || event.getAnimator() instanceof DodgeAnimator
                || event.getAnimator() instanceof KongVaultAnimator
                || event.getAnimator() instanceof SpeedVaultAnimator
                || event.getAnimator() instanceof ChargeJumpAnimator
                || event.getAnimator() instanceof JumpChargingAnimator
                || event.getAnimator() instanceof ClingToCliffAnimator
                || event.getAnimator() instanceof BackwardWallJumpAnimator
                || event.getAnimator() instanceof WallJumpAnimator
                || event.getAnimator() instanceof ClimbUpAnimator
                || event.getAnimator() instanceof FlippingAnimator
                || event.getAnimator() instanceof HangAnimator
                || event.getAnimator() instanceof JumpFromBarAnimator
                || event.getAnimator() instanceof VerticalWallRunAnimator
                || event.getAnimator() instanceof WallSlideAnimator
                || event.getAnimator() instanceof TapAnimator) {
            return;
        }

        if (event.getAnimator() instanceof FastRunningAnimator) {
            event.getOption().cancel(AnimationPart.LEFT_LEG);
            event.getOption().cancel(AnimationPart.RIGHT_LEG);
            event.getOption().cancel(AnimationPart.LEFT_ARM);
            event.getOption().cancel(AnimationPart.RIGHT_ARM);
            return;
        }

        if (event.getAnimator() instanceof CrawlAnimator) {
            event.getOption().cancelAnimation();
            return;
        }

        event.getOption().cancel(AnimationPart.LEFT_ARM);
        event.getOption().cancel(AnimationPart.RIGHT_ARM);
    }
}
