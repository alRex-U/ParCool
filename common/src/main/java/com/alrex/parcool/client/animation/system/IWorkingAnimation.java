package com.alrex.parcool.client.animation.system;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;

@Environment(EnvType.CLIENT)
public interface IWorkingAnimation {
    int getDuration();

    boolean loops();

    void tick(AbstractClientPlayer player);

    boolean isFinished();

    void reset();

    ModelTransform getTransformation(AbstractClientPlayer player, float partialTick, boolean allMirroring);
}
