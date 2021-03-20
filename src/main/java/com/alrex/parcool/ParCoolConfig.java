package com.alrex.parcool;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParCoolConfig {
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER=new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CLIENT;

    static {
        CLIENT=CLIENT_BUILDER.build();
    }
    @SubscribeEvent
    public static void onLoad(ModConfig.Loading event){ }
    @SubscribeEvent
    public static void onReload(ModConfig.Reloading event){ }
}
