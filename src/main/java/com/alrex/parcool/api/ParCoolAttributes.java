package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ParCoolAttributes {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, ParCool.MOD_ID);
    public static final DeferredHolder<Attribute, Attribute> MAX_STAMINA = register("parcool.max_stamina", 2000, 10, 10000);
    public static final DeferredHolder<Attribute, Attribute> STAMINA_RECOVERY = register("parcool.stamina_recovery", 10, 1, 10000);

    public static final DeferredHolder<Attribute, Attribute> BREAKFALL_DAMAGE_REDUCTION = register("parcool.breakfall.damage_reduction", 0.4, 0, 1);
    public static final DeferredHolder<Attribute, Attribute> FAST_RUN_SPEED = register("parcool.fast_run.speed", 0.02, 0, 10);
    public static final DeferredHolder<Attribute, Attribute> FAST_SWIM_SPEED = register("parcool.fast_swim.speed", 0.04, 0, 10);
    public static final DeferredHolder<Attribute, Attribute> SLIDE_DOWN_DECELERATION = register("parcool.slide_down.deceleration", 0.1, 0, 1);
    public static final DeferredHolder<Attribute, Attribute> HORIZONTAL_WALL_RUN_DURATION = register("parcool.horizontal_wall_run.duration", 20, 0, 10000);

    private static DeferredHolder<Attribute, Attribute> register(String name, double defaultValue, double min, double max) {
        return ATTRIBUTES.register(name, () -> new RangedAttribute("attribute.name." + name, defaultValue, min, max).setSyncable(true));
    }

    public static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }
}
