package com.alrex.parcool.client.animation.system.math;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IEasingFunction {
    float easeIn(float t);

    float easeOut(float t);

    float easeInOut(float t);
}
