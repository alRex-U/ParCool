package com.alrex.parcool.common.item.recipe;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.recipe.special.ZiplineRopeDyeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Recipes {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ParCool.MOD_ID);
    public static final RegistryObject<RecipeSerializer<ZiplineRopeDyeRecipe>> ZIPLINE_ROPE_DYE = RECIPES.register("zipline_rope_dye", () -> new SimpleCraftingRecipeSerializer<>(ZiplineRopeDyeRecipe::new));

    public static void register(IEventBus bus) {
        RECIPES.register(bus);
    }
}
