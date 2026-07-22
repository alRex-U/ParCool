package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.item.armor.TraceurBootsItem;
import com.alrex.parcool.common.item.armor.TraceurGlovesItem;
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

public class ParCoolItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ParCool.MOD_ID);
    public static final RegistryObject<Item> PARCOOL_GUIDE = ITEMS.register("parcool_guide", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BlockItem> WOODEN_ZIPLINE_HOOK = ITEMS.register("wooden_zipline_hook", () -> new BlockItem(Blocks.WOODEN_ZIPLINE_HOOK.get(), new Item.Properties().tab(ParCoolItemGroup.INSTANCE)));
    public static final RegistryObject<BlockItem> IRON_ZIPLINE_HOOK = ITEMS.register("iron_zipline_hook", () -> new BlockItem(Blocks.IRON_ZIPLINE_HOOK.get(), new Item.Properties().tab(ParCoolItemGroup.INSTANCE)));
    public static final RegistryObject<ZiplineRopeItem> ZIPLINE_ROPE = ITEMS.register("zipline_rope", () -> new ZiplineRopeItem(new Item.Properties().tab(ParCoolItemGroup.INSTANCE)));
    public static final RegistryObject<TraceurGlovesItem> TRACEUR_GLOVES = ITEMS.register("traceur_gloves", () -> new TraceurGlovesItem(new Item.Properties().tab(ParCoolItemGroup.INSTANCE).stacksTo(1)));
    public static final RegistryObject<TraceurBootsItem> TRACEUR_BOOTS = ITEMS.register("traceur_boots", () -> new TraceurBootsItem(new Item.Properties().tab(ParCoolItemGroup.INSTANCE).stacksTo(1)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerColors() {
        Minecraft.getInstance().getItemColors().register(new DyeAble.DyedColor(0), ZIPLINE_ROPE::get);
        Minecraft.getInstance().getItemColors().register(new DyeAble.DyedColor(0), TRACEUR_GLOVES::get);
        Minecraft.getInstance().getItemColors().register(new DyeAble.DyedColor(0), TRACEUR_BOOTS::get);
    }
}