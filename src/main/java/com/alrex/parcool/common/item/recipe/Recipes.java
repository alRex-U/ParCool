package com.alrex.parcool.common.item.recipe;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.recipe.special.ZiplineRopeDyeRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Recipes {
    private static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ParCool.MOD_ID);
    public static final RegistryObject<IRecipeSerializer<ZiplineRopeDyeRecipe>> ZIPLINE_ROPE_DYE = RECIPES.register("zipline_rope_dye", () -> new SpecialRecipeSerializer<>(ZiplineRopeDyeRecipe::new));

    public static void register(IEventBus bus) {
        RECIPES.register(bus);
    }
}
