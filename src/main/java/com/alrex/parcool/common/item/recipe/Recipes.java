package com.alrex.parcool.common.item.recipe;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.recipe.special.ParCoolDyeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Recipes {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ParCool.MOD_ID);
    public static final RegistryObject<RecipeSerializer<ParCoolDyeRecipe>> DYE_ITEM = RECIPES.register("dye_item", () -> new SimpleRecipeSerializer<>(ParCoolDyeRecipe::new));

    public static void register(IEventBus bus) {
        RECIPES.register(bus);
    }
}
