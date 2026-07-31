package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import com.alrex.parcool.client.animation.system.math.IEasingFunction;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public abstract class Transition {
    protected final TimedValue start;

    private Transition(TimedValue startValue) {
        this.start = startValue;
    }

    public TimedValue getStart() {
        return start;
    }

    public final float getValueAt(float tick, @Nullable Transition next) {
        if (next == null) {
            return this.start.value();
        }
        return getValue(Mth.clamp((tick - start.time()) / (next.start.time() - start.time()), 0f, 1f), next.start);
    }

    /// @param t : Special variable, which indices the time between start.time and next.start.time in [0,1]
    protected abstract float getValue(float t, @Nonnull TimedValue end);

    public static class End extends Transition {
        public End(TimedValue startValue) {
            super(startValue);
        }

        @Override
        protected float getValue(float t, @Nonnull TimedValue end) {
            return 0;
        }
    }

    public static class Constant extends Transition {
        public Constant(TimedValue startValue) {
            super(startValue);
        }

        @Override
        protected float getValue(float t, @Nonnull TimedValue end) {
            return start.value();
        }
    }

    public static class Linear extends Transition {
        public Linear(TimedValue startValue) {
            super(startValue);
        }

        @Override
        protected float getValue(float t, @Nonnull TimedValue end) {
            return Mth.lerp(t, start.value(), end.value());
        }
    }

    /// [easing.net](https://easings.net/)
    public static abstract class Easing extends Transition {
        private final Type easingType;
        private final IEasingFunction easingFunction;

        private Easing(TimedValue startValue, Type type, IEasingFunction easingFunction) {
            super(startValue);
            this.easingType = type;
            this.easingFunction = easingFunction;
        }

        public enum Type {
            EASE_IN_OUT, EASE_IN, EASE_OUT
        }

        @Override
        protected float getValue(float t, @Nonnull TimedValue end) {
            return Mth.lerp(
                    switch (easingType) {
                        case EASE_IN_OUT -> easingFunction.easeInOut(t);
                        case EASE_IN -> easingFunction.easeIn(t);
                        case EASE_OUT -> easingFunction.easeOut(t);
                    },
                    start.value(),
                    end.value()
            );
        }
    }

    public static class Sine extends Easing {
        public Sine(TimedValue startValue, Type type) {
            super(startValue, type, EasingFunctions.SINE);
        }
    }

    public static class Quad extends Easing {
        public Quad(TimedValue startValue, Type type) {
            super(startValue, type, EasingFunctions.QUAD);
        }
    }

    public static class Cubic extends Easing {
        public Cubic(TimedValue startValue, Type type) {
            super(startValue, type, EasingFunctions.CUBE);
        }
    }

    public static class Circle extends Easing {
        public Circle(TimedValue startValue, Type type) {
            super(startValue, type, EasingFunctions.CIRCLE);
        }
    }

    public static class BazierCubic extends Transition {
        private final float[] cache;
        private final float lastCacheDuration;
        private final float timeBetweenCache;

        public BazierCubic(TimedValue startValue, TimedValue firstControlPoint, TimedValue secondControlPoint, TimedValue endValue) {
            this(startValue, firstControlPoint, secondControlPoint, endValue, 0.5f);
        }

        public BazierCubic(TimedValue startValue, TimedValue firstControlPoint, TimedValue secondControlPoint, TimedValue endValue, float timeBetweenCache) {
            super(startValue);
            assert (startValue.time() <= firstControlPoint.time());
            assert (firstControlPoint.time() <= secondControlPoint.time());
            assert (secondControlPoint.time() <= endValue.time());

            var duration = endValue.time() - startValue.time();
            this.cache = new float[Math.max((int) (duration / timeBetweenCache), 0) + 1];
            this.cache[0] = startValue.value();
            this.lastCacheDuration = duration - timeBetweenCache * (cache.length - 1);
            this.timeBetweenCache = timeBetweenCache;
            // Cache bazier curve
            {
                float x0 = startValue.time(), x1 = firstControlPoint.time(), x2 = secondControlPoint.time(), x3 = endValue.time();
                float y0 = startValue.value(), y1 = firstControlPoint.value(), y2 = secondControlPoint.value(), y3 = endValue.value();
                float vx1 = -x0 + 3 * x1 - 3 * x2 + x3;
                float vx2 = x0 - 2 * x1 + x2;
                float vx3 = -x0 + x1;
                float vy1 = -y0 + 3 * y1 - 3 * y2 + y3;
                float vy2 = y0 - 2 * y1 + y2;
                float vy3 = -y0 + y1;
                for (var i = 1; i < cache.length; i++) {
                    var timeAtThisCachePoint = startValue.time() + i * timeBetweenCache;

                    // t is auxiliary variable of bazier curve
                    var t = i / (cache.length - 1f);
                    // Newton's method
                    for (var j = 0; j < 10; j++) {
                        var timeByT = vx1 * t * t * t
                                + 3 * vx2 * t * t
                                + 3 * vx3 * t
                                + x0 - timeAtThisCachePoint;
                        var timeGradByT = 3 * vx1 * t * t
                                + 6 * vx2 * t
                                + 3 * vx3;

                        var tNew = t - (timeByT) / timeGradByT;
                        var diff = Math.abs(tNew - t);
                        t = tNew;
                        if (diff < 1e-4) {
                            break;
                        }
                    }
                    cache[i] = vy1 * t * t * t
                            + 3 * vy2 * t * t
                            + 3 * vy3 * t
                            + y0;
                }
            }
        }

        @Override
        protected float getValue(float t, @Nonnull TimedValue end) {
            t = Mth.clamp(t, 0f, 1f);
            float cacheIndex = ((cache.length - 1) * t);
            int intCacheIndex = (int) cacheIndex;
            float subPartial = cacheIndex - intCacheIndex;
            if (intCacheIndex >= cache.length - 1) {
                subPartial = (timeBetweenCache * subPartial) / lastCacheDuration;
                if (subPartial >= 1) {
                    return end.value();
                }
                return Mth.lerp(subPartial, cache[cache.length - 1], end.value());
            }
            return Mth.lerp(subPartial, cache[intCacheIndex], cache[intCacheIndex + 1]);
        }
    }
}
