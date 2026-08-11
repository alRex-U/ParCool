package com.alrex.parcool.client.animation.system;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;

@Environment(EnvType.CLIENT)
public interface IBlendingFactor {
    float getFactor(AbstractClientPlayer player, float partial);

    void tick(AbstractClientPlayer player);

    BlendMethod getBlendMethod();
}
