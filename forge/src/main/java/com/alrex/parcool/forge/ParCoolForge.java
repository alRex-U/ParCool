package com.alrex.parcool.forge;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.forge.action.RegisterParCoolActionEvent;
import com.alrex.parcool.api.forge.stamina.RegisterParCoolStaminaTypeEvent;
import com.alrex.parcool.client.animation.system.config.AnimationSystemConfig;
import com.alrex.parcool.client.animation.system.handle.AnimationSystemEventHandler;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.common.architectury.event.RegisterParCoolActionArchEvent;
import com.alrex.parcool.common.architectury.event.RegisterParCoolStaminaArchEvent;
import com.alrex.parcool.forge.client.animation.system.config.ForgeAnimationSystemConfig;
import com.alrex.parcool.forge.common.handlers.InputHandler;
import com.alrex.parcool.forge.common.handlers.PlayerEventHandler;
import com.alrex.parcool.forge.config.ParCoolForgeConfig;
import com.alrex.parcool.forge.extern.AdditionalMods;
import com.alrex.parcool.forge.server.command.CommandRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ParCool.MOD_ID)
public class ParCoolForge extends ParCool {
    ParCoolForge() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        RegisterParCoolActionArchEvent.EVENT.register(registry ->
                bus.post(new RegisterParCoolActionEvent(registry))
        );
        RegisterParCoolStaminaArchEvent.EVENT.register(registry ->
                bus.post(new RegisterParCoolStaminaTypeEvent(registry))
        );
        bus.addListener(ParCoolForge::onClientSetup);
        bus.addListener(ParCoolForge::onCommonSetup);
        init();
        AdditionalMods.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> AdditionalMods::initInClient);
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> AdditionalMods::initInDedicatedServer);
        MinecraftForge.EVENT_BUS.addListener(CommandRegistry::onRegisterCommand);
        MinecraftForge.EVENT_BUS.register(PlayerEventHandler.class);

        config = new ParCoolForgeConfig(actionRegistry, staminaTypeRegistry);
        ((ParCoolForgeConfig) config).register(ModLoadingContext.get());
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(InputHandler::onPlayerInputUpdated);
        MinecraftForge.EVENT_BUS.register(AnimationSystemEventHandler.class);
        var animConfig = new ForgeAnimationSystemConfig(AnimationSets.getInstance());
        AnimationSystemConfig.init(animConfig);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, animConfig.getBuiltConfig(), "parcool-animation.toml");
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        CommandRegistry.registerArgumentTypes();
    }
}
