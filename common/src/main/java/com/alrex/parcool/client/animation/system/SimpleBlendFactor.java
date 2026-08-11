package com.alrex.parcool.client.animation.system;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class SimpleBlendFactor implements IBlendingFactor {
    private final Handler handler;
    private final BlendMethod method;
    private float old;
    private float current;

    public SimpleBlendFactor(Handler handler, BlendMethod method) {
        this.handler = handler;
        this.method = method;
    }

    @Override
    public float getFactor(AbstractClientPlayer player, float partial) {
        return Mth.lerp(partial, old, current);
    }

    @Override
    public void tick(AbstractClientPlayer player) {
        old = current;
        current = handler.getFactor(player);
    }

    @Override
    public BlendMethod getBlendMethod() {
        return method;
    }

    public interface Handler {
        float getFactor(AbstractClientPlayer player);
    }
}
