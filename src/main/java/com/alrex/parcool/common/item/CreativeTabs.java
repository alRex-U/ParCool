package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;

public class CreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ParCool.MOD_ID);
    public static final RegistryObject<CreativeModeTab> ITEMS = TABS.register("items", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(Items.PARCOOL_GUIDE.get()))
            .title(Component.literal(ParCool.MOD_ID))
            .hideTitle()
            .displayItems((params, output) -> {
                output.accept(Items.IRON_ZIPLINE_HOOK.get());
                output.accept(Items.WOODEN_ZIPLINE_HOOK.get());
                output.accept(Items.ZIPLINE_ROPE.get());
                Arrays.stream(DyeColor.values())
                        .map(color -> {
                            var coloredRope = new ItemStack(Items.ZIPLINE_ROPE.get());
                            int r = Mth.clamp((int) (color.getTextureDiffuseColors()[0] * 255f), 0, 255);
                            int g = Mth.clamp((int) (color.getTextureDiffuseColors()[1] * 255f), 0, 255);
                            int b = Mth.clamp((int) (color.getTextureDiffuseColors()[2] * 255f), 0, 255);
                            ZiplineRopeItem.setColor(coloredRope, (r << 16) + (g << 8) + b);
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
