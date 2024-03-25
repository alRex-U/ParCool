package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Attributes {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ParCool.MOD_ID);
    public static final RegistryObject<Attribute> MAX_STAMINA = ATTRIBUTES.register("max_stamina", () -> new RangedAttribute("parcool.max_stamina", 2000, 10, 10000).setSyncable(true));
    public static final RegistryObject<Attribute> STAMINA_RECOVERY = ATTRIBUTES.register("stamina_recovery", () -> new RangedAttribute("parcool.stamina_recovery", 20, 0, 10000).setSyncable(true));

    public static void registerAll(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }
}
