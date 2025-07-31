package com.alrex.parcool;

import com.alrex.parcool.api.Attributes;
import com.alrex.parcool.api.Effects;
import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.renderer.Renderers;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.ClientAttachments;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.block.TileEntities;
import com.alrex.parcool.common.entity.EntityTypes;
import com.alrex.parcool.common.handlers.AddAttributesHandler;
import com.alrex.parcool.common.item.CreativeTabs;
import com.alrex.parcool.common.item.DataComponents;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.network.NetworkRegistries;
import com.alrex.parcool.common.potion.Potions;
import com.alrex.parcool.common.registries.EventBusForgeRegistry;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.server.command.CommandRegistry;
import com.alrex.parcool.server.command.args.ParCoolArgumentTypeInfos;
import com.alrex.parcool.server.limitation.Limitations;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ParCool.MOD_ID)
public class ParCool {
	public static final String MOD_ID = "parcool";

	public static final Logger LOGGER = LogManager.getLogger();

	public ParCool(ModContainer container) {
		IEventBus eventBus = ModLoadingContext.get().getActiveContainer().getEventBus();
		assert eventBus != null;
		EventBusForgeRegistry.register(NeoForge.EVENT_BUS);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            EventBusForgeRegistry.registerClient(NeoForge.EVENT_BUS);
            eventBus.addListener(KeyBindings::register);
			eventBus.addListener(Renderers::register);
			ClientAttachments.registerAll(eventBus);
        }
        eventBus.addListener(this::setup);
		eventBus.addListener(this::loaded);
		eventBus.register(AddAttributesHandler.class);
		eventBus.register(NetworkRegistries.class);
		eventBus.register(HUDRegistry.class);

		Effects.registerAll(eventBus);
		Potions.registerAll(eventBus);
		Attributes.registerAll(eventBus);
		SoundEvents.registerAll(eventBus);
		Blocks.registerAll(eventBus);
		Items.registerAll(eventBus);
		CreativeTabs.registerAll(eventBus);
		EntityTypes.registerAll(eventBus);
		TileEntities.registerAll(eventBus);
		DataComponents.registerAll(eventBus);
		Attachments.registerAll(eventBus);
		ParCoolArgumentTypeInfos.registerAll(eventBus);

		NeoForge.EVENT_BUS.addListener(this::registerCommand);
		NeoForge.EVENT_BUS.addListener(Limitations::init);
		NeoForge.EVENT_BUS.addListener(Limitations::save);

		container.registerConfig(ModConfig.Type.SERVER, ParCoolConfig.Server.getConfigSpec());
		container.registerConfig(ModConfig.Type.CLIENT, ParCoolConfig.Client.getConfigSpec());
	}

	private void loaded(FMLLoadCompleteEvent event) {
		AdditionalMods.init();
		switch (FMLEnvironment.dist) {
			case CLIENT -> AdditionalMods.initInClient();
			case DEDICATED_SERVER -> AdditionalMods.initInDedicatedServer();
		}
	}

	private void setup(final FMLCommonSetupEvent event) {
	}

	private void registerCommand(final RegisterCommandsEvent event) {
		CommandRegistry.register(event.getDispatcher());
	}
}
