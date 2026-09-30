package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.potion.effects.InexhaustibleEffect;
import com.alrex.parcool.fabric.DeferredHolder;
import com.alrex.parcool.fabric.DeferredRegister;
import com.alrex.parcool.fabric.IEventBus;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;

public class Effects {
    private static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, ParCool.MOD_ID);
    public static final DeferredHolder<MobEffect, MobEffect> INEXHAUSTIBLE =
            EFFECTS.register("inexhaustible", InexhaustibleEffect::new);

    public static void registerAll(IEventBus modBus) {
        EFFECTS.register(modBus);
    }
}
