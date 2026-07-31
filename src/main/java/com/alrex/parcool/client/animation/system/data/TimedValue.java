package com.alrex.parcool.client.animation.system.data;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record TimedValue(float time, float value) {
}
