package com.alrex.parcool.common.damage;


import com.alrex.parcool.ParCool;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypes {
    public static final ResourceKey<DamageType> FRICTION = register("parcool.friction");

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ParCool.resourceLocation(name));
    }
}
