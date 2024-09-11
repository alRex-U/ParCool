package com.alrex.parcool;

import com.alrex.parcool.api.Attributes;
import com.alrex.parcool.api.Effects;
import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.handlers.AddAttributesHandler;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.network.NetworkRegistries;
import com.alrex.parcool.common.potion.Potions;
import com.alrex.parcool.common.registries.EventBusForgeRegistry;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.command.CommandRegistry;
import com.alrex.parcool.server.limitation.Limitations;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ParCool.MOD_ID)
public class ParCool {
	public static final String MOD_ID = "parcool";

	public static final Logger LOGGER = LogManager.getLogger();

	public ParCool() {
		IEventBus eventBus = ModLoadingContext.get().getActiveContainer().getEventBus();
		assert eventBus != null;
		eventBus.addListener(this::setup);
		eventBus.addListener(this::doClientStuff);
		eventBus.addListener(this::loaded);
		eventBus.register(AddAttributesHandler.class);
		eventBus.register(NetworkRegistries.class);
		eventBus.register(HUDRegistry.class);
		EventBusForgeRegistry.register(NeoForge.EVENT_BUS);
		Effects.registerAll(eventBus);
		Potions.registerAll(eventBus);
		Attributes.registerAll(eventBus);
		SoundEvents.registerAll(eventBus);
		Attachments.registerAll(eventBus);
		Items.registerAll(eventBus);
		NeoForge.EVENT_BUS.addListener(this::registerCommand);
		NeoForge.EVENT_BUS.addListener(Limitations::init);
		NeoForge.EVENT_BUS.addListener(Limitations::save);
		ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER, ParCoolConfig.Server.BUILT_CONFIG);
		ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, ParCoolConfig.Client.BUILT_CONFIG);
	}

	private void loaded(FMLLoadCompleteEvent event) {
	}

	private void setup(final FMLCommonSetupEvent event) {
		CommandRegistry.registerArgumentTypes(event);
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		EventBusForgeRegistry.registerClient(NeoForge.EVENT_BUS);
		IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
		assert bus != null;
		bus.addListener(KeyBindings::register);
	}

	private void registerCommand(final RegisterCommandsEvent event) {
		CommandRegistry.register(event.getDispatcher());
	}
}
