package com.alrex.parcool.common.damage;


import com.alrex.parcool.ParCool;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class DamageSources {
    public static final ResourceKey<DamageType> WALL_SLIDE = register("parcool.wall_slide");

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(ParCool.MOD_ID, name));
    }
}
