package com.alrex.parcool;

import com.alrex.parcool.client.animation.AnimatorList;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.common.capability.capabilities.Capabilities;
import com.alrex.parcool.common.item.ItemRegistry;
import com.alrex.parcool.common.potion.Effects;
import com.alrex.parcool.common.potion.PotionRecipeRegistry;
import com.alrex.parcool.common.potion.Potions;
import com.alrex.parcool.common.registries.EventBusForgeRegistry;
import com.alrex.parcool.common.registries.EventBusModRegistry;
import com.alrex.parcool.extern.feathers.FeathersManager;
import com.alrex.parcool.proxy.ClientProxy;
import com.alrex.parcool.proxy.CommonProxy;
import com.alrex.parcool.proxy.ServerProxy;
import com.alrex.parcool.server.command.CommandRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ParCool.MOD_ID)
public class ParCool {
	public static final String MOD_ID = "parcool";
	private static final String PROTOCOL_VERSION =
			Integer.toHexString(ActionList.ACTION_REGISTRIES.size())
					+ "."
					+ Integer.toHexString(AnimatorList.ANIMATORS.size());
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

	//only in Client
	public static boolean isActive() {
		return ParCoolConfig.CONFIG_CLIENT.parCoolActivation.get();
	}

	//only in Client
	public static void setActivation(boolean activation) {
		ParCoolConfig.CONFIG_CLIENT.parCoolActivation.set(activation);
	}

	public ParCool() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(this::setup);
		eventBus.addListener(this::processIMC);
		eventBus.addListener(this::doClientStuff);
		eventBus.addListener(this::doServerStuff);
		eventBus.register(Capabilities.class);
		Effects.registerAll(eventBus);
		Potions.registerAll(eventBus);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommand);
		MinecraftForge.EVENT_BUS.register(this);
		ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		FeathersManager.init();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ParCoolConfig.SERVER_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ParCoolConfig.CLIENT_SPEC);
		DistExecutor.unsafeRunWhenOn(
				Dist.CLIENT,
				() -> () -> {
					EventBusForgeRegistry.registerClient(MinecraftForge.EVENT_BUS);
					EventBusModRegistry.registerClient(FMLJavaModLoadingContext.get().getModEventBus());
				}
		);
	}

	private void setup(final FMLCommonSetupEvent event) {
		CommandRegistry.registerArgumentTypes(event);
		EventBusForgeRegistry.register(MinecraftForge.EVENT_BUS);
		EventBusModRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
		PotionRecipeRegistry.register(event);
		PROXY.registerMessages(CHANNEL_INSTANCE);
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		KeyBindings.register(event);
	}

	private void doServerStuff(final FMLDedicatedServerSetupEvent event) {
	}

	private void processIMC(final InterModProcessEvent event) {
	}

	private void registerCommand(final RegisterCommandsEvent event) {
		CommandRegistry.register(event.getDispatcher());
	}
}
