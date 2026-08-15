package com.alrex.parcool.common.potion;

import com.alrex.parcool.api.ParCoolPotions;
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
                        ParCoolPotions.POOR_ENERGY_DRINK
                );
        event.getBuilder()
                .addMix(
                        Potions.AWKWARD,
                        Items.CHICKEN,
                        ParCoolPotions.POOR_ENERGY_DRINK
                );
        event.getBuilder()
                .addMix(
                        Potions.AWKWARD,
                        Items.QUARTZ,
                        ParCoolPotions.ENERGY_DRINK
                );
        event.getBuilder()
                .addMix(
                        ParCoolPotions.POOR_ENERGY_DRINK,
                        Items.QUARTZ,
                        ParCoolPotions.ENERGY_DRINK
                );
    }
}