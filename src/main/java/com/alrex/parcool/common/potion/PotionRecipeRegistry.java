package com.alrex.parcool.common.potion;

import com.alrex.parcool.api.ParCoolPotions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

public class PotionRecipeRegistry {
	public static void register() {
		final ItemStack poorQualityEnergyDrink =
				PotionUtils.setPotion(new ItemStack(Items.POTION), ParCoolPotions.POOR_ENERGY_DRINK.get());
		final ItemStack energyDrink =
				PotionUtils.setPotion(new ItemStack(Items.POTION), ParCoolPotions.ENERGY_DRINK.get());
		final ItemStack awkwardPotion =
                PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD);

		BrewingRecipeRegistry.addRecipe(
				Ingredient.of(awkwardPotion),
                Ingredient.of(() -> Items.POISONOUS_POTATO, () -> Items.CHICKEN),
				poorQualityEnergyDrink
		);
		BrewingRecipeRegistry.addRecipe(
				Ingredient.of(awkwardPotion),
                Ingredient.of(() -> Items.QUARTZ, () -> Items.QUARTZ),
				energyDrink
		);
	}
}
