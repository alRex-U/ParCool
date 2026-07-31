package com.alrex.parcool.client.animation.system;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IWorkingAnimation {
    int getDuration();

    boolean loops();

    void tick(AbstractClientPlayer player);

    boolean isFinished();

    void reset();

    ModelTransform getTransformation(AbstractClientPlayer player, float partialTick, boolean allMirroring);
}
