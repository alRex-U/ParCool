package com.alrex.parcool.api.compatibility;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

public class AbstractClientPlayerWrapper extends PlayerWrapper {
    private AbstractClientPlayerEntity player;
    private static final WeakCache<AbstractClientPlayerEntity, AbstractClientPlayerWrapper> cache = new WeakCache<>();

    protected AbstractClientPlayerWrapper(AbstractClientPlayerEntity player) {
        super(player);
        this.player = player;
    }

    public static AbstractClientPlayerWrapper get(AbstractClientPlayerEntity player) {
        return cache.get(player, () -> new AbstractClientPlayerWrapper(player));
    }

    public static AbstractClientPlayerWrapper get(PlayerTickEvent event) {
        return get((AbstractClientPlayerEntity)event.player);
    }

    public AbstractClientPlayerEntity getInstance() {
        return this.player;
    }
}
