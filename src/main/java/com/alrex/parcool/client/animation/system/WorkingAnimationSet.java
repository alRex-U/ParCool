package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.data.AnimationSet;
import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import net.minecraft.client.player.AbstractClientPlayer;

import javax.annotation.Nullable;

public class WorkingAnimationSet {
    private final IWorkingAnimation mainAnimation;
    @Nullable
    private final IWorkingAnimation introAnimation;
    @Nullable
    private final IWorkingAnimation outroAnimation;
    @Nullable
    private IWorkingAnimation currentAnimation;
    private final IAnimationController controller;
    private int tickInPhase = 0;
    private int primaryTick = 0;
    private final int fadeInTick;
    private final int fadeOutTick;
    private final float fpvBlend;
    private final boolean mirror;
    private int forceFinishingTick = -1;
    private AnimationPhase phase;

    public WorkingAnimationSet(AnimationSet animationSet, IAnimationController controller, boolean mirror) {
        this.mainAnimation = new WorkingAnimation(animationSet.mainAnimation());
        this.introAnimation = animationSet.introAnimation() != null ? new WorkingAnimation(animationSet.introAnimation()) : null;
        this.outroAnimation = animationSet.outroAnimation() != null ? new WorkingAnimation(animationSet.outroAnimation()) : null;
        this.controller = controller;
        this.fadeInTick = animationSet.fadeInDuration();
        this.fadeOutTick = animationSet.fadeOutDuration();
        this.fpvBlend = animationSet.blendFactorInFirstPersonView();
        this.mirror = mirror;
        this.phase = animationSet.introAnimation() != null ? AnimationPhase.INTRO : AnimationPhase.MAIN;
        this.currentAnimation = getAnimation(phase);
    }

    @Nullable
    public IWorkingAnimation getIntroAnimation() {
        return introAnimation;
    }

    @Nullable
    public IWorkingAnimation getOutroAnimation() {
        return outroAnimation;
    }

    public IWorkingAnimation getMainAnimation() {
        return mainAnimation;
    }

    @Nullable
    IWorkingAnimation getAnimation(AnimationPhase phase) {
        return switch (phase) {
            case INTRO -> introAnimation;
            case MAIN -> mainAnimation;
            case OUTRO -> outroAnimation;
            case END -> null;
        };
    }

    public float getFadeInTick() {
        return fadeInTick;
    }

    public float getFadeOutTick() {
        return fadeOutTick;
    }

    public void tick(AbstractClientPlayer player) {
        tickInPhase++;
        primaryTick++;
        if ((phase == AnimationPhase.INTRO || phase == AnimationPhase.MAIN) && !controller.continueAnimation(player)) {
            if (outroAnimation != null) {
                enter(AnimationPhase.OUTRO);
            } else {
                startForceFinishing();
            }
        }
        if (currentAnimation != null) {
            currentAnimation.tick(player);
            if (currentAnimation.isFinished()) {
                nextPhase();
            }
        }
        if (forceFinishingTick >= 0) {
            if (forceFinishingTick < fadeOutTick) {
                forceFinishingTick++;
            }
        }
    }

    @Nullable
    public ModelTransform getTransform(AbstractClientPlayer player, float partialTick) {
        var animation = getAnimation(phase);
        if (animation == null) return null;
        return animation.getTransformation(player, partialTick, mirror);
    }

    public float getCurrentBlendFactor(boolean firstPersonView, float partialTick) {
        float tick = primaryTick + partialTick;
        float fpvBlendScale = firstPersonView ? fpvBlend : 1f;
        if (forceFinishingTick >= 0) {
            return EasingFunctions.QUAD.easeInOut(1 - ((forceFinishingTick + partialTick) / fadeOutTick)) * fpvBlendScale;
        }
        if (tick < fadeInTick) return EasingFunctions.QUAD.easeInOut(tick / fadeInTick) * fpvBlendScale;
        if (isOnFinalPhase()) {
            var animation = getAnimation(phase);
            if (animation == null) return 1f;
            var tickInPhase = this.tickInPhase + partialTick;
            var animationDuration = animation.getDuration();
            if (animationDuration - fadeOutTick <= tickInPhase) {
                return EasingFunctions.QUAD.easeInOut((animationDuration - tickInPhase) / fadeOutTick) * (fpvBlendScale);
            }
        }
        return fpvBlendScale;
    }

    public void enter(AnimationPhase phase) {
        phase = switch (phase) {
            case INTRO -> introAnimation != null ? AnimationPhase.INTRO : AnimationPhase.MAIN;
            case OUTRO -> outroAnimation != null ? AnimationPhase.OUTRO : AnimationPhase.END;
            default -> phase;
        };
        this.phase = phase;
        this.currentAnimation = getAnimation(phase);
        if (this.currentAnimation != null) this.currentAnimation.reset();
        this.tickInPhase = 0;
    }

    public void nextPhase() {
        enter(switch (phase) {
            case INTRO -> AnimationPhase.MAIN;
            case MAIN -> outroAnimation != null ? AnimationPhase.OUTRO : AnimationPhase.END;
            case OUTRO, END -> AnimationPhase.END;
        });
    }

    public boolean isFinished() {
        return phase == AnimationPhase.END || forceFinishingTick >= fadeOutTick;
    }

    public void startForceFinishing() {
        if (forceFinishingTick < 0) forceFinishingTick = 0;
    }

    public boolean isOnFinalPhase() {
        return outroAnimation == null ? phase == AnimationPhase.MAIN : phase == AnimationPhase.OUTRO;
    }
}
