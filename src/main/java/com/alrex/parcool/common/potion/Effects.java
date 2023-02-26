package com.alrex.parcool.common.potion;


import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.potion.effects.InexhaustibleEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Effects {
	private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ParCool.MOD_ID);
	public static final MobEffect INEXHAUSTIBLE = new InexhaustibleEffect();
	public static final RegistryObject<MobEffect> INEXHAUSTIBLE_REGISTRY = EFFECTS.register(
			"inexhaustible", () -> INEXHAUSTIBLE
	);

	public static void registerAll(IEventBus modBus) {
		EFFECTS.register(modBus);
	}
}
