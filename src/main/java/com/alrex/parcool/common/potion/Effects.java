package com.alrex.parcool.common.potion;


import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.potion.effects.InexhaustibleEffect;
import net.minecraft.potion.Effect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Effects {
	private static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, ParCool.MOD_ID);
	public static final Effect INEXHAUSTIBLE = new InexhaustibleEffect();
	public static final RegistryObject<Effect> INEXHAUSTIBLE_REGISTRY = EFFECTS.register(
			"inexhaustible", () -> INEXHAUSTIBLE
	);

	public static void registerAll(IEventBus modBus) {
		EFFECTS.register(modBus);
	}
}
