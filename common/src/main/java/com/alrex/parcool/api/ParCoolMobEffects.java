package com.alrex.parcool.api;


import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.potion.effects.InexhaustibleEffect;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;

public class ParCoolMobEffects {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ParCool.MOD_ID, Registry.MOB_EFFECT_REGISTRY);
    public static final RegistrySupplier<MobEffect> INEXHAUSTIBLE = EFFECTS.register(
            "inexhaustible", InexhaustibleEffect::new
    );

    public static void register() {
        EFFECTS.register();
    }
}
