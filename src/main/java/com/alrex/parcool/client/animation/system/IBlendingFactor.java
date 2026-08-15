package com.alrex.parcool.client.animation.system;

import net.minecraft.client.player.AbstractClientPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IBlendingFactor {
    float getFactor(AbstractClientPlayer player, float partial);

    void tick(AbstractClientPlayer player);

    BlendMethod getBlendMethod();
}
