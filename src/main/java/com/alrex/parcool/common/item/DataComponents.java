package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.component.ZiplinePositionComponent;
import com.alrex.parcool.common.item.component.ZiplineTensionComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DataComponents {
    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ParCool.MOD_ID);
    public static final Supplier<DataComponentType<ZiplinePositionComponent>> ZIPLINE_POSITION = COMPONENTS.registerComponentType(
            "zipline_pos",
            builder -> builder.persistent(ZiplinePositionComponent.CODEC).networkSynchronized(ZiplinePositionComponent.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<ZiplineTensionComponent>> ZIPLINE_TENSION = COMPONENTS.registerComponentType(
            "zipline_tension",
            builder -> builder.persistent(ZiplineTensionComponent.CODEC).networkSynchronized(ZiplineTensionComponent.STREAM_CODEC)
    );

    public static void registerAll(IEventBus bus) {
        COMPONENTS.register(bus);
    }
}
