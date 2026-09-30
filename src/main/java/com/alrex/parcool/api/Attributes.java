package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.fabric.IEventBus;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

/**
 * Регистрация здесь ранняя, а не отложенная: DefaultAttributes инициализируется внутри
 * Bootstrap.bootStrap, то есть до onInitialize мода, и миксин на Player.createAttributes читает эти
 * два атрибута уже там. Отложенный DeferredRegister на этом падал.
 */
public class Attributes {
    public static final Holder<Attribute> MAX_STAMINA =
            register(
                    "max_stamina",
                    new RangedAttribute("parcool.max_stamina", 2000, 10, 10000).setSyncable(true));
    public static final Holder<Attribute> STAMINA_RECOVERY =
            register(
                    "stamina_recovery",
                    new RangedAttribute("parcool.stamina_recovery", 20, 0, 10000)
                            .setSyncable(true));

    private static Holder<Attribute> register(String name, Attribute attribute) {
        return Registry.registerForHolder(
                BuiltInRegistries.ATTRIBUTE,
                ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, name),
                attribute);
    }

    public static void registerAll(IEventBus bus) {}
}
