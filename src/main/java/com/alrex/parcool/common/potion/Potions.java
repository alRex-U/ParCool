package com.alrex.parcool.common.potion;

import com.alrex.parcool.api.ParCoolMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Potions {
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, "parcool");
    public static final DeferredHolder<Potion, Potion> POOR_ENERGY_DRINK =
            POTIONS.register(
                    "poor_energy_drink",
                    () -> new Potion(
                            new MobEffectInstance(ParCoolMobEffects.INEXHAUSTIBLE, 2400/*2 min*/),
                            new MobEffectInstance(MobEffects.HUNGER, 100),
                            new MobEffectInstance(MobEffects.POISON, 100)
                    )
            );
    public static final DeferredHolder<Potion, Potion> ENERGY_DRINK =
            POTIONS.register(
                    "energy_drink",
                    () -> new Potion(
                            new MobEffectInstance(ParCoolMobEffects.INEXHAUSTIBLE, 9600/*8 min*/)
                    )
            );

    public static void register(IEventBus modBus) {
        POTIONS.register(modBus);
    }
}
