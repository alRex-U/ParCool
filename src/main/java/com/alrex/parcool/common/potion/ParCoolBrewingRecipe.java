package com.alrex.parcool.common.potion;


import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

public class ParCoolBrewingRecipe {
    @SubscribeEvent
    public static void onRegister(RegisterBrewingRecipesEvent event) {
        event.getBuilder()
                .addMix(
                        Potions.AWKWARD,
                        Items.POISONOUS_POTATO,
                        com.alrex.parcool.common.potion.Potions.POOR_ENERGY_DRINK
                );
        event.getBuilder()
                .addMix(
                        Potions.AWKWARD,
                        Items.CHICKEN,
                        com.alrex.parcool.common.potion.Potions.POOR_ENERGY_DRINK
                );
        event.getBuilder()
                .addMix(
                        Potions.AWKWARD,
                        Items.QUARTZ,
                        com.alrex.parcool.common.potion.Potions.ENERGY_DRINK
                );
        event.getBuilder()
                .addMix(
                        com.alrex.parcool.common.potion.Potions.POOR_ENERGY_DRINK,
                        Items.QUARTZ,
                        com.alrex.parcool.common.potion.Potions.ENERGY_DRINK
                );
	}
}
