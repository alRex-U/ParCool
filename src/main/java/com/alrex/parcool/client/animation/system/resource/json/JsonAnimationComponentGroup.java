package com.alrex.parcool.client.animation.system.resource.json;

import com.alrex.parcool.client.animation.system.resource.Argument;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public class JsonAnimationComponentGroup {
    private int duration = Integer.MAX_VALUE;
    private boolean loop = false;
    private boolean infinite = false;
    public List<AnimationComponent> components;

    public int getDuration() {
        return duration;
    }

    public boolean isLoop() {
        return loop;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public List<AnimationComponent> getComponents() {
        return components;
    }

    public static class AnimationComponent {
        private ResourceLocation name;
        @Nullable
        private JsonBlendingFactor blend;
        @Nullable
        private Progress progress;
        private boolean mirror = false;

        public ResourceLocation getName() {
            return name;
        }

        @Nullable
        public JsonBlendingFactor getBlend() {
            return blend;
        }

        @Nullable
        public Progress getProgress() {
            return progress;
        }

        public boolean isMirror() {
            return mirror;
        }

        public static class Progress {
            private ResourceLocation name;
            @Nullable
            private Argument args;

            public Argument getArgs() {
                if (args == null) return Argument.EMPTY;
                return args;
            }

            public ResourceLocation getName() {
                return name;
            }
        }
    }
}
