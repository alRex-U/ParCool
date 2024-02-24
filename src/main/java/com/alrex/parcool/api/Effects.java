package com.alrex.parcool.api;


import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.potion.effects.InexhaustibleEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Effects {
	private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ParCool.MOD_ID);
	public static final RegistryObject<MobEffect> INEXHAUSTIBLE = EFFECTS.register(
			"inexhaustible", InexhaustibleEffect::new
	);

	public static void registerAll(IEventBus modBus) {
		EFFECTS.register(modBus);
	}
}
