package com.alrex.parcool;

import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.api.ParCoolMobEffects;
import com.alrex.parcool.api.ParCoolPotions;
import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.RegisterParCoolActionEvent;
import com.alrex.parcool.api.stamina.RegisterParCoolStaminaTypeEvent;
import com.alrex.parcool.client.animation.system.config.AnimationSystemConfig;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.animation.system.resource.AnimationResourceManager;
import com.alrex.parcool.client.renderer.Renderers;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.action.ActionRegistry;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.block.TileEntities;
import com.alrex.parcool.common.entity.EntityTypes;
import com.alrex.parcool.common.handlers.AddAttributesHandler;
import com.alrex.parcool.common.item.ParCoolItems;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.potion.PotionRecipeRegistry;
import com.alrex.parcool.common.stamina.StaminaTypeRegistry;
import com.alrex.parcool.common.stamina.StaminaTypes;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.proxy.ClientProxy;
import com.alrex.parcool.proxy.CommonProxy;
import com.alrex.parcool.proxy.ServerProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(ParCool.MOD_ID)
public class ParCool {
	public static final String MOD_ID = "parcool";
	private static final String PROTOCOL_VERSION = "4.0.0.0";
	public static final SimpleChannel CONNECTION = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(ParCool.MOD_ID, "message"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);
	public static final CommonProxy PROXY = DistExecutor.unsafeRunForDist(
			() -> ClientProxy::new,
			() -> ServerProxy::new
	);

	private static final ActionRegistry actionRegistry = new ActionRegistry();
	private static final StaminaTypeRegistry staminaTypeRegistry = new StaminaTypeRegistry();
	private static final ActionProcessor actionProcessor = new ActionProcessor();
	private static ParCoolConfig config;
	private static AnimationSystemConfig animConfig;

	public static ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	public static StaminaTypeRegistry getStaminaTypeRegistry() {
		return staminaTypeRegistry;
	}

	public static ParCoolConfig getConfig() {
		return config;
	}

	public static AnimationSystemConfig getAnimationConfig() {
		return animConfig;
	}

	public static void setAnimationConfig(AnimationSystemConfig config) {
		if (animConfig == null) {
			animConfig = config;
		}
	}

	public static ActionProcessor getActionProcessor() {
		return actionProcessor;
	}

	public ParCool() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(this::setup);
		eventBus.addListener(this::setupClient);
		eventBus.addListener(this::loaded);
        eventBus.addListener(this::registerResource);
		eventBus.register(AddAttributesHandler.class);
		eventBus.register(ParCoolActions.class);
		eventBus.register(StaminaTypes.class);

		PROXY.init();
		MinecraftForge.EVENT_BUS.register(actionProcessor);

		ParCoolMobEffects.register(eventBus);
		ParCoolPotions.register(eventBus);
		ParCoolAttributes.register(eventBus);
		ParCoolSoundEvents.register(eventBus);
		Blocks.register(eventBus);
		ParCoolItems.register(eventBus);
		Recipes.register(eventBus);
		EntityTypes.register(eventBus);
		TileEntities.register(eventBus);

		FMLJavaModLoadingContext.get().getModEventBus().post(new RegisterParCoolStaminaTypeEvent(staminaTypeRegistry));
		staminaTypeRegistry.freeze();
		FMLJavaModLoadingContext.get().getModEventBus().post(new RegisterParCoolActionEvent(actionRegistry));
		actionRegistry.freeze();
		config = new ParCoolConfig(actionRegistry, staminaTypeRegistry);
		config.register(ModLoadingContext.get());
	}

	private void loaded(FMLLoadCompleteEvent event) {
		PotionRecipeRegistry.register();
		AdditionalMods.init();
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> AdditionalMods::initInClient);
		DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> AdditionalMods::initInDedicatedServer);
	}

	private void setup(final FMLCommonSetupEvent event) {
		PROXY.registerMessages(CONNECTION);
	}

	private void setupClient(final FMLClientSetupEvent event) {
		Renderers.register();
		ParCoolItems.registerColors();
        AnimationSets.getInstance().freeze();
    }

    private void registerResource(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(AnimationResourceManager.getInstance());
	}
}
