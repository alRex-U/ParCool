package com.alrex.parcool.client.animation.system.math;

import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EasingFunctions {
    public static final IEasingFunction SINE = new Sine();
    public static final IEasingFunction QUAD = new Quad();
    public static final IEasingFunction CUBE = new Cube();
    public static final IEasingFunction CIRCLE = new Circle();

    private static class Sine implements IEasingFunction {
        @Override
        public float easeIn(float t) {
            return 1f - Mth.cos((t * Mth.PI) / 2f);
        }

        @Override
        public float easeOut(float t) {
            return Mth.sin((t * Mth.PI) / 2f);
        }

        @Override
        public float easeInOut(float t) {
            return -(Mth.cos(Mth.PI * t) - 1f) / 2f;
        }
    }

    private static class Quad implements IEasingFunction {
        @Override
        public float easeIn(float t) {
            return t * t;
        }

        @Override
        public float easeOut(float t) {
            return 1f - Mth.square(1 - t);
        }

        @Override
        public float easeInOut(float t) {
            return t < 0.5f ? 2f * t * t : 1f - Mth.square(-2f * t + 2f) / 2f;
        }
    }

    private static class Cube implements IEasingFunction {
        @Override
        public float easeIn(float t) {
            return t * t * t;
        }

        @Override
        public float easeOut(float t) {
            return 1f - cube(1f - t);
        }

        @Override
        public float easeInOut(float t) {
            return t < 0.5f ? 4f * t * t * t : 1f - cube(-2f * t + 2f) / 2f;
        }
    }

    private static class Circle implements IEasingFunction {
        @Override
        public float easeIn(float t) {
            return 1f - Mth.sqrt(1f - cube(t));
        }

        @Override
        public float easeOut(float t) {
            return Mth.sqrt(1f - cube(t - 1f));
        }

        @Override
        public float easeInOut(float t) {
            return t < 0.5f
                    ? (1f - Mth.sqrt(1f - cube(2f * t))) / 2f
                    : (Mth.sqrt(1f - cube(-2f * t + 2f)) + 1f) / 2f;
        }
    }

    private static float cube(float v) {
        return v * v * v;
    }
}