package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.Collections;

public class CreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ParCool.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEMS = TABS.register("items", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(Items.PARCOOL_GUIDE.get()))
            .title(Component.translatable("itemGroup.ParCool"))
            .hideTitle()
            .displayItems((params, output) -> {
                output.accept(Items.IRON_ZIPLINE_HOOK.get());
                output.accept(Items.WOODEN_ZIPLINE_HOOK.get());
                output.accept(Items.ZIPLINE_ROPE.get());
                Arrays.stream(DyeColor.values())
                        .map(DyeItem::byColor)
                        .map(dye -> {
                            var coloredRope = new ItemStack(Items.ZIPLINE_ROPE.get());
                            return DyedItemColor.applyDyes(coloredRope, Collections.singletonList(dye));
                        })
                        .forEach(output::accept);
            })
            .build()
    );

    public static void registerAll(IEventBus bus) {
        TABS.register(bus);
    }
}
