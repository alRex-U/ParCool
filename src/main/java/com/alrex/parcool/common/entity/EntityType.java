package com.alrex.parcool.common.entity;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.entity.EntityClassification;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityType {
    private static final DeferredRegister<net.minecraft.entity.EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, ParCool.MOD_ID);
    public static final RegistryObject<net.minecraft.entity.EntityType<ZiplineRopeEntity>> ZIPLINE_ROPE
            = REGISTER.register("zipline_rope", () -> net.minecraft.entity.EntityType.Builder
            .of((net.minecraft.entity.EntityType.IFactory<ZiplineRopeEntity>) ZiplineRopeEntity::new, EntityClassification.MISC)
            .noSave()
            .clientTrackingRange((int) (Zipline.MAXIMUM_HORIZONTAL_DISTANCE / 1.9))
            .updateInterval(Integer.MAX_VALUE)
            .sized(0.1f, 0.1f)
            .noSummon()
            .build("zipline_rope")
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
