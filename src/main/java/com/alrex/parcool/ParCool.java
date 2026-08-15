package com.alrex.parcool;

import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.api.ParCoolMobEffects;
import com.alrex.parcool.api.ParCoolPotions;
import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.RegisterParCoolActionEvent;
import com.alrex.parcool.api.stamina.RegisterParCoolStaminaTypeEvent;
import com.alrex.parcool.client.animation.system.registration.AnimationSets;
import com.alrex.parcool.client.renderer.Renderers;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.action.ActionRegistry;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.block.TileEntities;
import com.alrex.parcool.common.entity.EntityTypes;
import com.alrex.parcool.common.item.ParCoolDataComponents;
import com.alrex.parcool.common.item.ParCoolItemGroup;
import com.alrex.parcool.common.item.ParCoolItems;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.potion.Potions;
import com.alrex.parcool.common.stamina.StaminaTypeRegistry;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.registrar.ClientRegistrar;
import com.alrex.parcool.registrar.CommonRegistrar;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ParCool.MOD_ID)
public class ParCool {
	public static final String MOD_ID = "parcool";

	private static final ActionRegistry actionRegistry = new ActionRegistry();
	private static final StaminaTypeRegistry staminaTypeRegistry = new StaminaTypeRegistry();
	private static final ActionProcessor actionProcessor = new ActionProcessor();
	private static ParCoolConfig config;

	public static ResourceLocation resourceLocation(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	public static ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	public static StaminaTypeRegistry getStaminaTypeRegistry() {
		return staminaTypeRegistry;
	}

	public static ParCoolConfig getConfig() {
		return config;
	}

	public static ActionProcessor getActionProcessor() {
		return actionProcessor;
	}

	public ParCool(ModContainer container) {
		var eventBus = container.getEventBus();
		assert eventBus != null;
		eventBus.addListener(this::setupClient);
		eventBus.addListener(this::loaded);

		CommonRegistrar.registerModLoadingEvent(eventBus);
		CommonRegistrar.registerGameEvent(NeoForge.EVENT_BUS);
		NeoForge.EVENT_BUS.register(actionProcessor);

		if (FMLEnvironment.dist.isClient()) {
			ClientRegistrar.registerModLoadingEvent(eventBus);
			ClientRegistrar.registerGameEvent(NeoForge.EVENT_BUS);
		}

		ParCoolMobEffects.register(eventBus);
		ParCoolPotions.register(eventBus);
		ParCoolAttributes.register(eventBus);
		ParCoolSoundEvents.register(eventBus);
		Blocks.register(eventBus);
		ParCoolItems.register(eventBus);
		Recipes.register(eventBus);
		EntityTypes.register(eventBus);
		TileEntities.register(eventBus);
		ParCoolItemGroup.register(eventBus);
		ParCoolDataComponents.register(eventBus);
		Potions.register(eventBus);

		AdditionalMods.init();

		eventBus.post(new RegisterParCoolStaminaTypeEvent(staminaTypeRegistry));
		staminaTypeRegistry.freeze();
		eventBus.post(new RegisterParCoolActionEvent(actionRegistry));
		actionRegistry.freeze();
		config = new ParCoolConfig(actionRegistry, staminaTypeRegistry);
		config.register(container);
	}

	private void loaded(FMLLoadCompleteEvent event) {
		switch (FMLEnvironment.dist) {
			case CLIENT -> AdditionalMods.initInClient();
			case DEDICATED_SERVER -> AdditionalMods.initInDedicatedServer();
		}
	}

	private void setupClient(final FMLClientSetupEvent event) {
		Renderers.register();
		ParCoolItems.registerColors();
        AnimationSets.getInstance().freeze();
    }
}
