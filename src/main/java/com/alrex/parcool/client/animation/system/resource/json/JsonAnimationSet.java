package com.alrex.parcool.client.animation.system.resource.json;

import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

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
        @SerializedName("camera_blend")
        @Nullable
        private float[] cameraBlending;

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
            return Mth.clamp(fpvBlend, 0, 1f);
        }

        public int getFadeInDuration() {
            return Math.max(0, fadeInDuration);
        }

        public int getFadeOutDuration() {
            return Math.max(0, fadeOutDuration);
        }

        @Nullable
        public Vec3f getCameraAnimationScales() {
            if (cameraBlending == null || cameraBlending.length != 3) return null;
            return new Vec3f(
                    Mth.clamp(cameraBlending[0], 0, 1),
                    Mth.clamp(cameraBlending[1], 0, 1),
                    Mth.clamp(cameraBlending[2], 0, 1)
            );
        }
    }
}
