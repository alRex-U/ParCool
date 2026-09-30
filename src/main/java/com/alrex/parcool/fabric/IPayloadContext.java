package com.alrex.parcool.fabric;

import net.minecraft.world.entity.player.Player;

/**
 * Аналог net.neoforged.neoforge.network.handling.IPayloadContext: ParCool зовёт только player() и
 * enqueueWork().
 */
public interface IPayloadContext {
    Player player();

    void enqueueWork(Runnable work);
}
