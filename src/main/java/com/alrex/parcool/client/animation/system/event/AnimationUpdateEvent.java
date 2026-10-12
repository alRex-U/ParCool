package com.alrex.parcool.client.animation.system.event;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.AnimationProcessor;
import com.alrex.parcool.client.animation.system.ModelTransform;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class AnimationUpdateEvent extends Event {
    private final AbstractClientPlayer player;
    private final AnimationProcessor processor;

    private AnimationUpdateEvent(AbstractClientPlayer player, AnimationProcessor processor) {
        this.player = player;
        this.processor = processor;
    }

    public AbstractClientPlayer getPlayer() {
        return player;
    }

    public AnimationProcessor getProcessor() {
        return processor;
    }

    @Cancelable
    public static class Pre extends AnimationUpdateEvent {
        private boolean stopAnimation;

        public Pre(AbstractClientPlayer player, AnimationProcessor processor) {
            super(player, processor);
        }

        @Override
        public boolean isCancelable() {
            return true;
        }

        public boolean isAnimationStopped() {
            return stopAnimation;
        }

        public void stop() {
            this.stopAnimation = true;
            setCanceled(true);
        }
    }

    public static class Post extends AnimationUpdateEvent {
        private final ModelTransform transformation;

        public Post(AbstractClientPlayer player, AnimationProcessor processor, ModelTransform transformation) {
            super(player, processor);
            this.transformation = transformation;
        }

        @Nullable
        public ModelTransform getModelTransform() {
            return transformation;
        }

        public void stop(AnimatableModelPart part) {
            var transform = transformation.transforms();
            if (transform == null) return;
            transform.remove(part);
        }
    }
}

