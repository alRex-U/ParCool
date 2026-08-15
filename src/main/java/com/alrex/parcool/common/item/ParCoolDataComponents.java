package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.component.ZiplinePositionComponent;
import com.alrex.parcool.common.item.component.ZiplineTensionComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ParCoolDataComponents {
    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ParCool.MOD_ID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ZiplinePositionComponent>> ZIPLINE_POSITION = COMPONENTS.registerComponentType(
            "zipline_pos",
            builder -> builder.persistent(ZiplinePositionComponent.CODEC).networkSynchronized(ZiplinePositionComponent.STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ZiplineTensionComponent>> ZIPLINE_TENSION = COMPONENTS.registerComponentType(
            "zipline_tension",
            builder -> builder.persistent(ZiplineTensionComponent.CODEC).networkSynchronized(ZiplineTensionComponent.STREAM_CODEC)
    );

    public static void register(IEventBus bus) {
        COMPONENTS.register(bus);
    }
}