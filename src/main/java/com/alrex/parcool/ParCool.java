package com.alrex.parcool;

import com.alrex.parcool.api.Attributes;
import com.alrex.parcool.api.Effects;
import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.renderer.Renderers;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.block.TileEntities;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.entity.ParcoolEntityType;
import com.alrex.parcool.common.handlers.AddAttributesHandler;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.potion.PotionRecipeRegistry;
import com.alrex.parcool.common.potion.Potions;
import com.alrex.parcool.common.registries.EventBusForgeRegistry;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.proxy.ClientProxy;
import com.alrex.parcool.proxy.CommonProxy;
import com.alrex.parcool.proxy.ServerProxy;
import com.alrex.parcool.server.command.CommandRegistry;
import com.alrex.parcool.server.limitation.Limitations;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ParCool.MOD_ID)
public class ParCool {
	public static final String MOD_ID = "parcool";
	private static final String PROTOCOL_VERSION = "3.3.1.0";
	public static final SimpleChannel CHANNEL_INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(ParCool.MOD_ID, "message"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);
	public static final CommonProxy PROXY = DistExecutor.unsafeRunForDist(
			() -> ClientProxy::new,
			() -> ServerProxy::new
	);

	public static final Logger LOGGER = LogManager.getLogger();

	public static boolean isActive() {
		return PROXY.ParCoolIsActive();
	}

	public ParCool() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(this::setup);
		eventBus.addListener(this::doClientStuff);
		eventBus.addListener(this::loaded);
        EventBusForgeRegistry.register(MinecraftForge.EVENT_BUS);
		eventBus.register(AddAttributesHandler.class);

		Effects.registerAll(eventBus);
		Potions.registerAll(eventBus);
        Attributes.registerAll(eventBus);
		SoundEvents.registerAll(eventBus);

		MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.addListener(Limitations::init);
		MinecraftForge.EVENT_BUS.addListener(Limitations::save);

		Blocks.register(FMLJavaModLoadingContext.get().getModEventBus());
		Items.register(FMLJavaModLoadingContext.get().getModEventBus());
		Recipes.register(FMLJavaModLoadingContext.get().getModEventBus());
		ParcoolEntityType.register(FMLJavaModLoadingContext.get().getModEventBus());
		TileEntities.register(FMLJavaModLoadingContext.get().getModEventBus());

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ParCoolConfig.Server.BUILT_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ParCoolConfig.Client.BUILT_CONFIG);
	}

	private void loaded(FMLLoadCompleteEvent event) {
		AdditionalMods.init();
		PotionRecipeRegistry.register();
	}

	private void setup(final FMLCommonSetupEvent event) {
		CommandRegistry.registerArgumentTypes(event);
		Capabilities.register(CapabilityManager.INSTANCE);
		PROXY.registerMessages(CHANNEL_INSTANCE);
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		Renderers.register(Minecraft.getInstance().getEntityRenderDispatcher());
		KeyBindings.register(event);
		Capabilities.registerClient(CapabilityManager.INSTANCE);
		EventBusForgeRegistry.registerClient(MinecraftForge.EVENT_BUS);
		Items.registerColors();
	}

	private void registerCommand(final RegisterCommandsEvent event) {
		CommandRegistry.register(event.getDispatcher());
	}
}
