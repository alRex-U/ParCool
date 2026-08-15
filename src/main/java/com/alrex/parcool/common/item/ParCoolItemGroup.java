package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Arrays;

public class ParCoolItemGroup {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ParCool.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEMS = TABS.register("items", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ParCoolItems.PARCOOL_GUIDE.get()))
            .title(Component.translatable("itemGroup.ParCool"))
            .hideTitle()
            .displayItems((params, output) -> {
                output.accept(ParCoolItems.PARCOOL_GUIDE.get());
                output.accept(ParCoolItems.IRON_ZIPLINE_HOOK.get());
                output.accept(ParCoolItems.WOODEN_ZIPLINE_HOOK.get());
                output.accept(ParCoolItems.ZIPLINE_ROPE.get());
                output.accept(ParCoolItems.TRACEUR_BOOTS.get());
                output.accept(ParCoolItems.TRACEUR_GLOVES.get());
                Arrays.stream(DyeColor.values())
                        .map(color -> {
                            var coloredRope = new ItemStack(ParCoolItems.ZIPLINE_ROPE.get());
                            int r = Mth.clamp((color.getTextureDiffuseColor() & 0xFF0000) >> 16, 0, 255);
                            int g = Mth.clamp((color.getTextureDiffuseColor() & 0x00FF00) >> 8, 0, 255);
                            int b = Mth.clamp(color.getTextureDiffuseColor() & 0x0000FF, 0, 255);
                            if (coloredRope.getItem() instanceof DyeAble dyeAble) {
                                dyeAble.setColor(coloredRope, (r << 16) + (g << 8) + b);
                            }
                            return coloredRope;
                        })
                        .forEach(output::accept);
            })
            .build()
    );

    public static void register(IEventBus bus) {
        TABS.register(bus);
	}
}
