package com.alrex.parcool.client.animation.system.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IEasingFunction {
    float easeIn(float t);

    float easeOut(float t);

    float easeInOut(float t);
}
