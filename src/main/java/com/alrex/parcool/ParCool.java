package com.alrex.parcool;

import com.alrex.parcool.api.Attributes;
import com.alrex.parcool.api.Effects;
import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.block.TileEntities;
import com.alrex.parcool.common.entity.EntityTypes;
import com.alrex.parcool.common.item.CreativeTabs;
import com.alrex.parcool.common.item.DataComponents;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.network.NetworkRegistries;
import com.alrex.parcool.common.potion.ParCoolBrewingRecipe;
import com.alrex.parcool.common.potion.Potions;
import com.alrex.parcool.common.registries.EventRegistry;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.fabric.IEventBus;
import com.alrex.parcool.fabric.ServerHolder;
import com.alrex.parcool.server.command.CommandRegistry;
import com.alrex.parcool.server.command.args.ParCoolArgumentTypeInfos;
import com.alrex.parcool.server.limitation.Limitations;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.neoforged.fml.config.ModConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParCool implements ModInitializer {
    public static final String MOD_ID = "parcool";

    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        IEventBus bus = IEventBus.INSTANCE;

        Effects.registerAll(bus);
        Potions.registerAll(bus);
        Attributes.registerAll(bus);
        SoundEvents.registerAll(bus);
        Blocks.registerAll(bus);
        Items.registerAll(bus);
        CreativeTabs.registerAll(bus);
        Recipes.registerAll(bus);
        EntityTypes.registerAll(bus);
        TileEntities.registerAll(bus);
        DataComponents.registerAll(bus);
        Attachments.registerAll(bus);
        ParCoolArgumentTypeInfos.registerAll(bus);

        NeoForgeConfigRegistry.INSTANCE.register(
                MOD_ID, ModConfig.Type.SERVER, ParCoolConfig.Server.getConfigSpec());
        NeoForgeConfigRegistry.INSTANCE.register(
                MOD_ID, ModConfig.Type.CLIENT, ParCoolConfig.Client.getConfigSpec());

        NetworkRegistries.register();
        EventRegistry.register();
        ServerHolder.init();

        FabricBrewingRecipeRegistryBuilder.BUILD.register(ParCoolBrewingRecipe::onRegister);
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registry, environment) -> CommandRegistry.register(dispatcher));
        ServerLifecycleEvents.SERVER_STARTING.register(Limitations::init);
        ServerLifecycleEvents.SERVER_STOPPING.register(Limitations::save);

        AdditionalMods.init();
        AdditionalMods.initInDedicatedServer();
    }
}
