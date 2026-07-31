package com.alrex.parcool.client.animation.system;

import net.minecraft.client.player.AbstractClientPlayer;

public interface IBlendingFactor {
    float getFactor(AbstractClientPlayer player, float partial);

    void tick(AbstractClientPlayer player);

    BlendMethod getBlendMethod();
}
