package com.alrex.parcool.client.animation.system.math;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IEasingFunction {
    float easeIn(float t);

    float easeOut(float t);

    float easeInOut(float t);
}
