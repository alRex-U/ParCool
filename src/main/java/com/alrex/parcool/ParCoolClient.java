package com.alrex.parcool;

import com.alrex.parcool.client.ClientEventRegistry;
import com.alrex.parcool.client.ClientNetworkRegistries;
import com.alrex.parcool.client.hud.HUDManager;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.renderer.Renderers;
import com.alrex.parcool.common.attachment.ClientAttachments;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.fabric.IEventBus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Environment(EnvType.CLIENT)
public class ParCoolClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientAttachments.registerAll(IEventBus.INSTANCE);
        KeyBindings.register();
        Renderers.register();
        ClientNetworkRegistries.register();
        ClientEventRegistry.register();

        HudRenderCallback.EVENT.register(
                (graphics, deltaTracker) ->
                        HUDManager.getInstance().getStaminaHUD().render(graphics, deltaTracker));

        // Клиентский entrypoint зовётся из конструктора Minecraft: getItemColors() там ещё null.
        ClientLifecycleEvents.CLIENT_STARTED.register(
                client -> {
                    Items.registerColors();
                    AdditionalMods.initInClient();
                });
    }
}
