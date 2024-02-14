package com.alrex.parcool.common.potion;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.effects.InexhaustibleEffect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Potions {
	private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, ParCool.MOD_ID);
	public static final Potion POOR_ENERGY_DRINK =
			new Potion(
                    new EffectInstance(new InexhaustibleEffect(), 2400/*2 min*/),
					new EffectInstance(net.minecraft.potion.Effects.HUNGER, 100),
					new EffectInstance(net.minecraft.potion.Effects.POISON, 100)
			);
	public static final Potion ENERGY_DRINK =
			new Potion(
                    new EffectInstance(new InexhaustibleEffect(), 9600/*8 min*/)
			);
	private static final RegistryObject<Potion> POOR_ENERGY_DRINK_REGISTRY =
			POTIONS.register(
					"poor_energy_drink",
					() -> POOR_ENERGY_DRINK
			);
	public static final RegistryObject<Potion> ENERGY_DRINK_REGISTRY =
			POTIONS.register(
					"energy_drink",
					() -> ENERGY_DRINK
			);

	public static void registerAll(IEventBus modBus) {
		POTIONS.register(modBus);
	}
}
