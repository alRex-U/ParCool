package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

public class ParCoolPotions {
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ParCool.MOD_ID, Registry.POTION_REGISTRY);
    public static final RegistrySupplier<Potion> POOR_ENERGY_DRINK =
            POTIONS.register(
                    "poor_energy_drink",
                    () -> new Potion(
                            new MobEffectInstance(ParCoolMobEffects.INEXHAUSTIBLE.get(), 3600/*3 min*/),
                            new MobEffectInstance(MobEffects.HUNGER, 100),
                            new MobEffectInstance(MobEffects.POISON, 100)
                    )
            );
    public static final RegistrySupplier<Potion> ENERGY_DRINK =
            POTIONS.register(
                    "energy_drink",
                    () -> new Potion(
                            new MobEffectInstance(ParCoolMobEffects.INEXHAUSTIBLE.get(), 8400/*7 min*/)
                    )
            );

    public static void register() {
        POTIONS.register();
    }
}
