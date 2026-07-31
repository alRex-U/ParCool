package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public interface IAnimationComponent {
    @Nullable
    Transform getTransform(AbstractClientPlayer player, AnimatableModelPart part, float progress, float partial, boolean mirror);
}
