package com.alrex.parcool.util;

import net.minecraft.util.Mth;

public class MathUtil {
    public static float normalizeRadian(float angle) {
        return (float) (angle - 2 * Math.PI * Math.floor((angle + Math.PI) / (2. * Math.PI)));
    }

    public static float normalizeDegree(float angle) {
        return (float) (angle - 360f * Math.floor((angle + 180f) / 360f));
    }

    public static float mapLinear(float value, float rangeMin, float rangeMax, float start, float end) {
        if (value <= rangeMin) return start;
        if (value >= rangeMax) return end;
        return Mth.lerp((value - rangeMin) / (rangeMax - rangeMin), start, end);
    }
}
