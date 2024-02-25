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

public class ParCoolBrewingRecipe implements IBrewingRecipe {
	private static class MixPredicate {
		MixPredicate(Potion from, Item ingredient, Potion result) {
			this.from = from;
			this.ingredient = ingredient;
			this.result = result;
		}

		private final Potion from;
		private final Item ingredient;
		private final Potion result;
	}

	private static final List<MixPredicate> MIXES = Arrays.asList(
			addMix(Potions.AWKWARD, Items.POISONOUS_POTATO, com.alrex.parcool.common.potion.Potions.POOR_ENERGY_DRINK.get()),
			addMix(Potions.AWKWARD, Items.CHICKEN, com.alrex.parcool.common.potion.Potions.POOR_ENERGY_DRINK.get()),
			addMix(com.alrex.parcool.common.potion.Potions.POOR_ENERGY_DRINK.get(), Items.QUARTZ, com.alrex.parcool.common.potion.Potions.ENERGY_DRINK.get()),
			addMix(Potions.AWKWARD, Items.QUARTZ, com.alrex.parcool.common.potion.Potions.ENERGY_DRINK.get())
	);

	private static MixPredicate addMix(Potion from, Item ingredient, Potion result) {
		return new MixPredicate(from, ingredient, result);
	}

	@Nullable
	private static Potion mix(ItemStack input, ItemStack ingredient) {
		Potion inputPotion = PotionUtils.getPotion(input);
		Item ingredientItem = ingredient.getItem();
		return MIXES.stream()
				.filter((MixPredicate it) -> it.from == inputPotion && it.ingredient == ingredientItem)
				.findFirst()
				.orElse(new MixPredicate(null, null, null))
				.result;
	}

	private static boolean isPotionIngredient(Item item) {
		return MIXES.stream().anyMatch((MixPredicate it) -> item == it.ingredient);
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
