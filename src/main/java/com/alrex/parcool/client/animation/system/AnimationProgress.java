package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.resource.Argument;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AnimationProgress {
    protected AnimationProgress(boolean loop, float rangeMin, float rangeMax) {
        if (rangeMin < rangeMax) {
            this.rangeMin = rangeMin;
            this.rangeMax = rangeMax;
        } else {
            this.rangeMin = rangeMax;
            this.rangeMax = rangeMin;
        }
        this.range = rangeMax - rangeMin;
        this.loop = loop;
    }

    protected AnimationProgress() {
        this.rangeMin = 0f;
        this.rangeMax = Float.MAX_VALUE;
        this.range = rangeMax;
        this.loop = false;
    }

    private final boolean loop;
    private final float rangeMin, rangeMax, range;
    private float progress = 0f;
    private float oldProgress = 0f;

    protected abstract float getDeltaProgress(AbstractClientPlayer player);

    void tick(AbstractClientPlayer player) {
        if (loop) {
            oldProgress = progress - rangeMin - range * Mth.floor((progress - rangeMin) / range);
            progress = oldProgress + getDeltaProgress(player);
        } else {
            oldProgress = progress;
            progress += getDeltaProgress(player);
            progress = Mth.clamp(progress, rangeMin, rangeMax);
        }
    }

    public float getProgress(AbstractClientPlayer player, float partialTick) {
        if (loop) {
            var progressWithPartial = Mth.lerp(partialTick, this.oldProgress, this.progress);
            return progressWithPartial - rangeMin - range * Mth.floor((progressWithPartial - rangeMin) / range);
        } else {
            return Mth.lerp(partialTick, oldProgress, progress);
        }
    }

    void reset() {
        oldProgress = progress = 0;
    }

    public interface Constructor<T extends AnimationProgress> {
        T newInstance(boolean loop, float rangeMin, float rangeMax, Argument args);
    }

    public interface IDeltaProgressProvider {
        float get(AbstractClientPlayer player);
    }

    public interface IDirectProgressProvider {
        float get(AbstractClientPlayer player, float partialTick);
    }

    public static class FunctionDeltaAnimationProgress extends AnimationProgress {
        private final IDeltaProgressProvider progressProvider;
        private final float scale;

        public FunctionDeltaAnimationProgress(boolean loop, float rangeMin, float rangeMax, Argument args, IDeltaProgressProvider deltaProgressProvider) {
            super(loop, rangeMin, rangeMax);
            scale = args.request("scale", 1f);
            progressProvider = deltaProgressProvider;
        }

        public FunctionDeltaAnimationProgress(IDeltaProgressProvider deltaProgressProvider) {
            super();
            scale = 1f;
            progressProvider = deltaProgressProvider;
        }

        @Override
        protected float getDeltaProgress(AbstractClientPlayer player) {
            return progressProvider.get(player) * scale;
        }
    }

    public static class FunctionDirectAnimationProgress extends AnimationProgress {
        private final IDirectProgressProvider progressProvider;
        private final float scale;

        public FunctionDirectAnimationProgress(Argument args, IDirectProgressProvider progressProvider) {
            this.progressProvider = progressProvider;
            this.scale = args.request("scale", 1f);
        }

        @Override
        public float getProgress(AbstractClientPlayer player, float partialTick) {
            return Mth.clamp(progressProvider.get(player, partialTick), 0f, 1f) * scale;
        }

        @Override
        protected float getDeltaProgress(AbstractClientPlayer player) {
            return 0;
        }
    }
}
