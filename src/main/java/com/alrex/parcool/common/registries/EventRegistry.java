package com.alrex.parcool.common.registries;

import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.handlers.LoginLogoutHandler;
import com.alrex.parcool.common.handlers.PlayerCloneHandler;
import com.alrex.parcool.common.handlers.PlayerDamageHandler;
import com.alrex.parcool.common.network.ActionSynchronizationBroadcaster;
import com.alrex.parcool.common.network.StaminaSynchronizationBroadcaster;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingAttackEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingFallEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.tick.PlayerTickEvent;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/**
 * Замена EventBusForgeRegistry: у NeoForge всё висело на одной шине, под Fabric каждое событие
 * приезжает из своего источника -- Fabric API, Porting Lib или собственного миксина ParCool.
 * Клиентская половина живёт в ClientEventRegistry: этот класс грузится и на сервере.
 */
public class EventRegistry {
    private static final ActionProcessor ACTION_PROCESSOR = new ActionProcessor();

    public static void register() {
        LivingFallEvent.EVENT.register(PlayerDamageHandler::onFall);
        LivingAttackEvent.EVENT.register(PlayerDamageHandler::onAttack);
        ServerPlayConnectionEvents.DISCONNECT.register(
                (handler, server) -> LoginLogoutHandler.onLogoutInServer(handler.getPlayer()));
        ServerPlayerEvents.COPY_FROM.register(PlayerCloneHandler::onClone);
        ServerTickEvents.END_SERVER_TICK.register(
                server -> {
                    ActionSynchronizationBroadcaster.onTick();
                    StaminaSynchronizationBroadcaster.onTick();
                });
        PlayerTickEvent.Post.EVENT.register(ACTION_PROCESSOR::onTick);
    }
}
