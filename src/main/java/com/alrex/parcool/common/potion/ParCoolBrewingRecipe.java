package com.alrex.parcool.common.potion;


import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ParCoolBrewingRecipe implements IBrewingRecipe {
	private static class MixRecipe {
		MixRecipe(Supplier<Potion> from, Supplier<Item> ingredient, Supplier<Potion> result) {
			this.from = from;
			this.ingredient = ingredient;
			this.result = result;
		}

		private final Supplier<Potion> from;
		private final Supplier<Item> ingredient;
		private final Supplier<Potion> result;
	}


	private static final List<MixRecipe> MIXES = Arrays.asList(
			new MixRecipe(() -> Potions.AWKWARD, () -> Items.POISONOUS_POTATO, com.alrex.parcool.common.potion.Potions.POOR_ENERGY_DRINK::get),
			new MixRecipe(() -> Potions.AWKWARD, () -> Items.CHICKEN, com.alrex.parcool.common.potion.Potions.POOR_ENERGY_DRINK::get),
			new MixRecipe(com.alrex.parcool.common.potion.Potions.POOR_ENERGY_DRINK::get, () -> Items.QUARTZ, com.alrex.parcool.common.potion.Potions.ENERGY_DRINK::get),
			new MixRecipe(() -> Potions.AWKWARD, () -> Items.QUARTZ, com.alrex.parcool.common.potion.Potions.ENERGY_DRINK::get)
	);

	@Nullable
	private static Potion mix(ItemStack input, ItemStack ingredient) {
		Potion inputPotion = PotionUtils.getPotion(input);
		Item ingredientItem = ingredient.getItem();
		for (MixRecipe recipe : MIXES) {
			if (recipe.from.get() == inputPotion && recipe.ingredient.get() == ingredientItem) {
				return recipe.result.get();
			}
		}
		return null;
	}

	private static boolean isPotionIngredient(Item item) {
		return MIXES.stream().anyMatch((MixRecipe it) -> item == it.ingredient.get());
	}

	@Override
	public boolean isInput(ItemStack input) {
		Item item = input.getItem();
		return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION;
	}

	@Override
	public boolean isIngredient(ItemStack ingredient) {
		Item item = ingredient.getItem();
		return isPotionIngredient(item);
	}

	@Nonnull
	@Override
	public ItemStack getOutput(ItemStack input, @Nonnull ItemStack ingredient) {

		if (!input.isEmpty() && !ingredient.isEmpty() && isIngredient(ingredient)) {
			Potion result = mix(input, ingredient);
			if (result != null) return PotionUtils.setPotion(new ItemStack(input.getItem()), result);
		}
		return ItemStack.EMPTY;
	}
}
