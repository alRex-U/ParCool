package com.alrex.parcool.compatibility;

import java.lang.ref.WeakReference;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

public class AbstractClientPlayerWrapper extends PlayerWrapper {
    private WeakReference<AbstractClientPlayerEntity> player;
    private static final WeakCache<AbstractClientPlayerEntity, AbstractClientPlayerWrapper> cache = new WeakCache<>();

    protected AbstractClientPlayerWrapper(AbstractClientPlayerEntity player) {
        super(player);
        this.player = new WeakReference<>(player);
    }

    public static AbstractClientPlayerWrapper get(AbstractClientPlayerEntity player) {
        return cache.get(player, () -> new AbstractClientPlayerWrapper(player));
    }

    public static AbstractClientPlayerWrapper get(PlayerTickEvent event) {
        return get((AbstractClientPlayerEntity)event.player);
    }

    public AbstractClientPlayerEntity getInstance() {
        return this.player.get();
    }
}
