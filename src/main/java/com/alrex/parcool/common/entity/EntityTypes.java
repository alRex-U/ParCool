package com.alrex.parcool.common.entity;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityTypes {
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ParCool.MOD_ID);
    public static final RegistryObject<EntityType<ZiplineRopeEntity>> ZIPLINE_ROPE
            = REGISTER.register("zipline_rope", () -> EntityType.Builder
            .of((EntityType.EntityFactory<ZiplineRopeEntity>) ZiplineRopeEntity::new, MobCategory.MISC)
            .noSave()
            .clientTrackingRange((int) (Zipline.MAXIMUM_DISTANCE / 1.9))
            .updateInterval(Integer.MAX_VALUE)
            .noSummon()
            .build("zipline_rope")
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
