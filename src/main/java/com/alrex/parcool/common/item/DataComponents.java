package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.component.ZiplineColorComponent;
import com.alrex.parcool.common.item.component.ZiplinePositionComponent;
import com.alrex.parcool.common.item.component.ZiplineTensionComponent;
import com.alrex.parcool.fabric.IEventBus;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class DataComponents {
    public static final DataComponentType<ZiplineColorComponent> ZIPLINE_COLOR =
            register(
                    "zipline_color",
                    DataComponentType.<ZiplineColorComponent>builder()
                            .persistent(ZiplineColorComponent.CODEC)
                            .networkSynchronized(ZiplineColorComponent.STREAM_CODEC)
                            .build());
    public static final DataComponentType<ZiplinePositionComponent> ZIPLINE_POSITION =
            register(
                    "zipline_pos",
                    DataComponentType.<ZiplinePositionComponent>builder()
                            .persistent(ZiplinePositionComponent.CODEC)
                            .networkSynchronized(ZiplinePositionComponent.STREAM_CODEC)
                            .build());
    public static final DataComponentType<ZiplineTensionComponent> ZIPLINE_TENSION =
            register(
                    "zipline_tension",
                    DataComponentType.<ZiplineTensionComponent>builder()
                            .persistent(ZiplineTensionComponent.CODEC)
                            .networkSynchronized(ZiplineTensionComponent.STREAM_CODEC)
                            .build());

    private static <T> DataComponentType<T> register(String name, DataComponentType<T> type) {
        return Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, name),
                type);
    }

    public static void registerAll(IEventBus bus) {}
}
