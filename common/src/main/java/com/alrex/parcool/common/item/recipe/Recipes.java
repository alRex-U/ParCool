package com.alrex.parcool.common.item.recipe;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.recipe.special.ParCoolDyeRecipe;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

public class Recipes {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ParCool.MOD_ID, Registry.RECIPE_SERIALIZER_REGISTRY);
    public static final RegistrySupplier<RecipeSerializer<ParCoolDyeRecipe>> DYE_ITEM = RECIPES.register("dye_item", () -> new SimpleRecipeSerializer<>(ParCoolDyeRecipe::new));

    public static void register() {
        RECIPES.register();
    }
}
