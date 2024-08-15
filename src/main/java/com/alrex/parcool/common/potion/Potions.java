package com.alrex.parcool.common.potion;

import com.alrex.parcool.ParCool;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Potions {
	private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, ParCool.MOD_ID);
	public static final RegistryObject<Potion> POOR_ENERGY_DRINK =
			POTIONS.register(
					"poor_energy_drink",
					() -> new Potion(
							new EffectInstance(com.alrex.parcool.api.Effects.INEXHAUSTIBLE.get(), 2400/*2 min*/),
							new EffectInstance(Effects.HUNGER, 100),
							new EffectInstance(Effects.POISON, 100)
					)
			);
	public static final RegistryObject<Potion> ENERGY_DRINK =
			POTIONS.register(
					"energy_drink",
					() -> new Potion(
							new EffectInstance(com.alrex.parcool.api.Effects.INEXHAUSTIBLE.get(), 9600/*8 min*/)
					)
			);

	public static void registerAll(IEventBus modBus) {
		POTIONS.register(modBus);
	}
}
