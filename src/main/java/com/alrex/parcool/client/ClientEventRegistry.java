package com.alrex.parcool.client;

import com.alrex.parcool.client.hud.HUDManager;
import com.alrex.parcool.common.handlers.EnableOrDisableParCoolHandler;
import com.alrex.parcool.common.handlers.InputHandler;
import com.alrex.parcool.common.handlers.OpenSettingsParCoolHandler;
import com.alrex.parcool.common.handlers.PlayerJoinHandler;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.InputEvent;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.ViewportEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityJoinLevelEvent;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

@Environment(EnvType.CLIENT)
public class ClientEventRegistry {
    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> OpenSettingsParCoolHandler.onTick());
        ClientTickEvents.END_CLIENT_TICK.register(
                client -> {
                    EnableOrDisableParCoolHandler.onTick();
                    HUDManager.getInstance().onTick();
                });
        EntityJoinLevelEvent.EVENT.register(PlayerJoinHandler::onPlayerJoin);
        InputEvent.InteractionKeyMappingTriggered.EVENT.register(InputHandler::onInput);
        ViewportEvent.ComputeCameraAngles.EVENT.register(ClientRenderProcessor::onViewRender);
    }
}
