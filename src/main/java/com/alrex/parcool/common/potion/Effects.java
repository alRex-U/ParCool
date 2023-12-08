package com.alrex.parcool.common.potion;


import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.potion.effects.InexhaustibleEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Effects {
	private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ParCool.MOD_ID);
	public static RegistryObject<MobEffect> INEXHAUSTIBLE = null;

	public static void registerAll(IEventBus modBus) {
		EFFECTS.register(modBus);
		INEXHAUSTIBLE = EFFECTS.register("inexhaustible", InexhaustibleEffect::new);
	}
}
