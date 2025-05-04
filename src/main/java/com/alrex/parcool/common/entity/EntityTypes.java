package com.alrex.parcool.common.entity;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityTypes {
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(Registries.ENTITY_TYPE, ParCool.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<ZiplineRopeEntity>> ZIPLINE_ROPE
            = REGISTER.register("zipline_rope", () -> EntityType.Builder
            .of((EntityType.EntityFactory<ZiplineRopeEntity>) ZiplineRopeEntity::new, MobCategory.MISC)
            .noSave()
            .clientTrackingRange((int) (Zipline.MAXIMUM_DISTANCE / 1.9))
            .updateInterval(Integer.MAX_VALUE)
            .noSummon()
            .build("zipline_rope")
    );

    public static void registerAll(IEventBus bus) {
        REGISTER.register(bus);
    }
}
