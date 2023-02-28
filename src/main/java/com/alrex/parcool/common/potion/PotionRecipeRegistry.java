package com.alrex.parcool.common.potion;

import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class PotionRecipeRegistry {
	public static void register(FMLCommonSetupEvent event) {
		BrewingRecipeRegistry.addRecipe(new ParCoolBrewingRecipe());
	}
}
