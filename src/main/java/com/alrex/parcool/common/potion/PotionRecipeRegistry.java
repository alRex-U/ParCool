package com.alrex.parcool.common.potion;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

public class PotionRecipeRegistry {
	public static void register() {
		final ItemStack poorQualityEnergyDrink =
				PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.POOR_ENERGY_DRINK.get());
		final ItemStack energyDrink =
				PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.ENERGY_DRINK.get());
		final ItemStack awkwardPotion =
				PotionUtils.setPotion(new ItemStack(Items.POTION), net.minecraft.potion.Potions.AWKWARD);

		BrewingRecipeRegistry.addRecipe(
				Ingredient.of(awkwardPotion),
				Ingredient.of(() -> Items.POISONOUS_POTATO),
				poorQualityEnergyDrink
		);
		BrewingRecipeRegistry.addRecipe(
				Ingredient.of(awkwardPotion),
				Ingredient.of(() -> Items.CHICKEN),
				poorQualityEnergyDrink
		);
		BrewingRecipeRegistry.addRecipe(
				Ingredient.of(awkwardPotion),
				Ingredient.of(() -> Items.QUARTZ),
				energyDrink
		);
		BrewingRecipeRegistry.addRecipe(
				Ingredient.of(poorQualityEnergyDrink),
				Ingredient.of(() -> Items.QUARTZ),
				energyDrink
		);
	}
}
