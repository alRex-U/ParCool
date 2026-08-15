package com.alrex.parcool.client.animation.system.data;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record TimedValue(float time, float value) {
}
