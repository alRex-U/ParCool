package com.alrex.parcool.client.animation.system.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record TimedValue(float time, float value) {
}
