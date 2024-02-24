package com.alrex.parcool.common.potion;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.potion.effects.InexhaustibleEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Potions {
	private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, ParCool.MOD_ID);
	public static final Potion POOR_ENERGY_DRINK =
			new Potion(
					new MobEffectInstance(new InexhaustibleEffect(), 2400/*2 min*/),
					new MobEffectInstance(MobEffects.HUNGER, 100),
					new MobEffectInstance(MobEffects.POISON, 100)
			);
	public static final Potion ENERGY_DRINK =
			new Potion(
					new MobEffectInstance(new InexhaustibleEffect(), 9600/*8 min*/)
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
