package com.alrex.parcool.client.animation.system.resource.json;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public class JsonAnimationSet {
    private ResourceLocation name;
    private List<AnimationItem> animations;

    public ResourceLocation getName() {
        return name;
    }

    public List<AnimationItem> getAnimations() {
        return animations;
    }

    public static class AnimationItem {
        @Nullable
        private ResourceLocation intro;
        private ResourceLocation main;
        @Nullable
        private ResourceLocation outro;
        @SerializedName("fpv_blend")
        private float fpvBlend = 1f;
        @SerializedName("fade_in_duration")
        private int fadeInDuration = 0;
        @SerializedName("fade_out_duration")
        private int fadeOutDuration = 0;

        @Nullable
        public ResourceLocation getIntro() {
            return intro;
        }

        public ResourceLocation getMain() {
            return main;
        }

        @Nullable
        public ResourceLocation getOutro() {
            return outro;
        }

        public float getFpvBlend() {
            return fpvBlend;
        }

        public int getFadeInDuration() {
            return fadeInDuration;
        }

        public int getFadeOutDuration() {
            return fadeOutDuration;
        }
    }
}
