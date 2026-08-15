package com.alrex.parcool.common.entity;

import com.alrex.parcool.ParCool;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityTypes {
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(Registries.ENTITY_TYPE, ParCool.MOD_ID);

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
