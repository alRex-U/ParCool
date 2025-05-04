package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Items {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ParCool.MOD_ID);
    public static final RegistryObject<Item> PARCOOL_GUIDE = ITEMS.register("parcool_guide", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WOODEN_ZIPLINE_HOOK = ITEMS.register("wooden_zipline_hook", () -> new BlockItem(Blocks.WOODEN_ZIPLINE_HOOK.get(), new Item.Properties()));
    public static final RegistryObject<Item> IRON_ZIPLINE_HOOK = ITEMS.register("iron_zipline_hook", () -> new BlockItem(Blocks.IRON_ZIPLINE_HOOK.get(), new Item.Properties()));
    public static final RegistryObject<Item> ZIPLINE_ROPE = ITEMS.register("zipline_rope", () -> new ZiplineRopeItem(new Item.Properties()));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerColors() {
        Minecraft.getInstance().getItemColors().register(new ZiplineRopeItem.RopeColor(), ZIPLINE_ROPE::get);
    }
}