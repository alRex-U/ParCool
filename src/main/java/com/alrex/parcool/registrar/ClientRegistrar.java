package com.alrex.parcool.registrar;

import com.alrex.parcool.client.GrappleCameraHandler;
import com.alrex.parcool.client.GrappleTargetOverlay;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.PassiveAnimationProcessor;
import com.alrex.parcool.client.animation.system.config.AnimationSystemConfig;
import com.alrex.parcool.client.animation.system.event.RegisterAnimationEntryEvent;
import com.alrex.parcool.client.animation.system.handle.AnimationSystemEventHandler;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.resource.AnimationResourceManager;
import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.client.md.resource.GuideResourceManager;
import com.alrex.parcool.client.renderer.GrappleRopeRenderer;
import com.alrex.parcool.client.renderer.GrapplingHookItemRenderer;
import com.alrex.parcool.client.renderer.entity.layers.ParCoolModelLayers;
import com.alrex.parcool.client.skilltree.ParCoolSkillTrees;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.alrex.parcool.common.handlers.InputHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;

public class ClientRegistrar {
    @OnlyIn(Dist.CLIENT)
    public static void registerModLoadingEvent(IEventBus bus) {
        bus.addListener(ParCoolKeyBinds::registerAll);
        bus.addListener(HUDRegistry.getInstance()::onSetup);
        bus.addListener(AnimationRegistries::register);
        bus.addListener(ParCoolModelLayers::register);
        bus.addListener(ParCoolTextures::init);
        bus.addListener(AnimationResourceManager::register);
        bus.addListener(GuideResourceManager::register);
        bus.addListener(GrapplingHookItemRenderer::registerModels);
        var registerAnimationEntryEvent = new RegisterAnimationEntryEvent();
        bus.post(registerAnimationEntryEvent);
        registerAnimationEntryEvent.finish();
        AnimationSystemConfig.init(AnimationSets.getInstance());
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, AnimationSystemConfig.getInstance().getBuiltConfig(), "parcool-animation.toml");
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerGameEvent(IEventBus bus) {
        bus.addListener(ParCoolKeyBinds::onTick);
        bus.addListener(GrappleRopeRenderer::onRenderLevel);
        bus.addListener(HUDRegistry.getInstance()::onTick);
        bus.register(GrappleCameraHandler.class);
        bus.register(GrappleTargetOverlay.class);
        bus.register(InputHandler.class);
        bus.register(AnimationSystemEventHandler.class);
        bus.register(ParCoolSkillTrees.class);
        bus.register(new PassiveAnimationProcessor());
    }
}
