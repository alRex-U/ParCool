package com.alrex.parcool.common.item.recipe;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.recipe.special.ZiplineRopeDyeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Recipes {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ParCool.MOD_ID);
    public static final Supplier<RecipeSerializer<ZiplineRopeDyeRecipe>> ZIPLINE_ROPE_DYE = RECIPES.register("zipline_rope_dye", () -> new CustomRecipe.Serializer<>(ZiplineRopeDyeRecipe::new));

    public static void registerAll(IEventBus bus) {
        RECIPES.register(bus);
    }
}
