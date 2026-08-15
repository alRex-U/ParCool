package com.alrex.parcool.registrar;

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
import com.alrex.parcool.client.renderer.entity.layers.ParCoolModelLayers;
import com.alrex.parcool.client.skilltree.ParCoolSkillTrees;
import com.alrex.parcool.client.textures.ParCoolTextures;
import com.alrex.parcool.common.handlers.InputHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.bus.api.IEventBus;

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
        var registerAnimationEntryEvent = new RegisterAnimationEntryEvent();
        bus.post(registerAnimationEntryEvent);
        registerAnimationEntryEvent.finish();
        AnimationSystemConfig.init(AnimationSets.getInstance());
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, AnimationSystemConfig.getInstance().getBuiltConfig(), "parcool-animation.toml");
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerGameEvent(IEventBus bus) {
        bus.addListener(ParCoolKeyBinds::onTick);
        bus.addListener(HUDRegistry.getInstance()::onTick);
        bus.register(InputHandler.class);
        bus.register(AnimationSystemEventHandler.class);
        bus.register(ParCoolSkillTrees.class);
        bus.register(new PassiveAnimationProcessor());
    }
}
