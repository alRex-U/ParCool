package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public interface IAnimationComponent {
    @Nullable
    Transform getTransform(AbstractClientPlayer player, AnimatableModelPart part, float progress, float partial, boolean mirror);
}
