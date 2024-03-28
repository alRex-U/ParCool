package com.alrex.parcool.api;


import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.potion.effects.InexhaustibleEffect;
import net.minecraft.potion.Effect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Effects {
	private static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, ParCool.MOD_ID);
    public static final RegistryObject<Effect> INEXHAUSTIBLE = EFFECTS.register(
            "inexhaustible", InexhaustibleEffect::new
	);
	public static void registerAll(IEventBus modBus) {
		EFFECTS.register(modBus);
	}
}
