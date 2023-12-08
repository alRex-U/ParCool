package com.alrex.parcool;

import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.item.ItemRegistry;
import com.alrex.parcool.common.potion.Effects;
import com.alrex.parcool.common.potion.PotionRecipeRegistry;
import com.alrex.parcool.common.potion.Potions;
import com.alrex.parcool.common.registries.EventBusForgeRegistry;
import com.alrex.parcool.common.registries.EventBusModRegistry;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.proxy.ClientProxy;
import com.alrex.parcool.proxy.CommonProxy;
import com.alrex.parcool.proxy.ServerProxy;
import com.alrex.parcool.server.command.CommandRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ParCool.MOD_ID)
public class ParCool {
	public static final String MOD_ID = "parcool";
	private static final int PROTOCOL_VERSION = ActionList.ACTION_REGISTRIES.size();
	public static final SimpleChannel CHANNEL_INSTANCE = ChannelBuilder
			.named(
					new ResourceLocation(ParCool.MOD_ID, "message")
			)
			.networkProtocolVersion(PROTOCOL_VERSION)
			.acceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
			.simpleChannel();
	public static final CommonProxy PROXY = DistExecutor.unsafeRunForDist(
			() -> ClientProxy::new,
			() -> ServerProxy::new
	);

	public static final Logger LOGGER = LogManager.getLogger();

	public ParCool() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(this::setup);
		eventBus.addListener(this::processIMC);
		eventBus.addListener(this::doClientStuff);
		eventBus.addListener(this::loaded);
		eventBus.addListener(this::doServerStuff);
		eventBus.register(Capabilities.class);
		Effects.registerAll(eventBus);
		Potions.registerAll(eventBus);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
		MinecraftForge.EVENT_BUS.register(this);
		ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		PROXY.init();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ParCoolConfig.Server.BUILT_CONFIG);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ParCoolConfig.Client.BUILT_CONFIG);
	}

	private void loaded(FMLLoadCompleteEvent event) {
		//ParagliderManager.init();
	}

	private void setup(final FMLCommonSetupEvent event) {
		CommandRegistry.registerArgumentTypes(event);
		EventBusForgeRegistry.register(MinecraftForge.EVENT_BUS);
		EventBusModRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
		PotionRecipeRegistry.register(event);
		PROXY.registerMessages(CHANNEL_INSTANCE);
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
	}

	private void doServerStuff(final FMLDedicatedServerSetupEvent event) {
	}

	private void processIMC(final InterModProcessEvent event) {
	}

	private void registerCommand(final RegisterCommandsEvent event) {
		CommandRegistry.register(event.getDispatcher());
	}
}
