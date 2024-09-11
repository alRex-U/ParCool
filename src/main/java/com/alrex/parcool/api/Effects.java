package com.alrex.parcool.api;


import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.potion.effects.InexhaustibleEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Effects {
	private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ParCool.MOD_ID);
	public static final DeferredHolder<MobEffect, MobEffect> INEXHAUSTIBLE = EFFECTS.register(
			"inexhaustible", InexhaustibleEffect::new
	);

	public static void registerAll(IEventBus modBus) {
		EFFECTS.register(modBus);
	}
}
