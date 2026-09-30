package com.alrex.parcool.common.potion;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;

public class ParCoolBrewingRecipe {
    public static void onRegister(PotionBrewing.Builder builder) {
        builder.addMix(
                Potions.AWKWARD,
                Items.POISONOUS_POTATO,
                com.alrex.parcool.common.potion.Potions.POOR_ENERGY_DRINK);
        builder.addMix(
                Potions.AWKWARD,
                Items.CHICKEN,
                com.alrex.parcool.common.potion.Potions.POOR_ENERGY_DRINK);
        builder.addMix(
                Potions.AWKWARD,
                Items.QUARTZ,
                com.alrex.parcool.common.potion.Potions.ENERGY_DRINK);
        builder.addMix(
                com.alrex.parcool.common.potion.Potions.POOR_ENERGY_DRINK,
                Items.QUARTZ,
                com.alrex.parcool.common.potion.Potions.ENERGY_DRINK);
    }
}
