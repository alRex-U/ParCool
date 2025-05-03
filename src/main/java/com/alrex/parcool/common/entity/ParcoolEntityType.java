package com.alrex.parcool.common.entity;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ParcoolEntityType {
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, ParCool.MOD_ID);
    public static final RegistryObject<EntityType<ZiplineRopeEntity>> ZIPLINE_ROPE
            = REGISTER.register("zipline_rope", () -> EntityType.Builder
            .of((EntityType.IFactory<ZiplineRopeEntity>) ZiplineRopeEntity::new, EntityClassification.MISC)
            .noSave()
            .clientTrackingRange(32)
            .updateInterval(Integer.MAX_VALUE)
            .noSummon()
            .build("zipline_rope")
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
