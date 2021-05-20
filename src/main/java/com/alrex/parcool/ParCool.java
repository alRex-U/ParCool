package com.alrex.parcool;

import com.alrex.parcool.common.command.ParCoolCommands;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ParCool.MOD_ID)
public class ParCool {
	public static final String MOD_ID = "parcool";
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL_INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(ParCool.MOD_ID, "parcool.message"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	public static final Logger LOGGER = LogManager.getLogger();


	@OnlyIn(Dist.CLIENT)
	public static boolean isActive() {
		return ParCoolConfig.CONFIG_CLIENT.ParCoolActivation.get();
	}

	@OnlyIn(Dist.CLIENT)
	public static void setActivation(boolean activation) {
		ParCoolConfig.CONFIG_CLIENT.canWallJump.get();
		ParCoolConfig.CONFIG_CLIENT.ParCoolActivation.set(activation);
	}

	public ParCool() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommand);

		MinecraftForge.EVENT_BUS.register(this);

		ModLoadingContext context = ModLoadingContext.get();
		context.registerConfig(ModConfig.Type.CLIENT, ParCoolConfig.spec);
	}

	private void setup(final FMLCommonSetupEvent event) {
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
	}

	private void processIMC(final InterModProcessEvent event) {
	}

	private void registerCommand(final RegisterCommandsEvent event) {
		ParCoolCommands.register(event.getDispatcher());
	}
}
