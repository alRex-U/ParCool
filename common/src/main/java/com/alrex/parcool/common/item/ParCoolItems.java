package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.ParCoolBlocks;
import com.alrex.parcool.common.item.armor.TraceurBootsItem;
import com.alrex.parcool.common.item.armor.TraceurGlovesItem;
import com.alrex.parcool.common.item.misc.ParCoolGuideItem;
import com.alrex.parcool.common.item.misc.ZiplineRopeItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ParCoolItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ParCool.MOD_ID, Registry.ITEM_REGISTRY);
    public static final RegistrySupplier<BlockItem> WOODEN_ZIPLINE_HOOK = ITEMS.register("wooden_zipline_hook", () -> new BlockItem(ParCoolBlocks.WOODEN_ZIPLINE_HOOK.get(), new Item.Properties().tab(ParCoolItemGroup.INSTANCE)));
    public static final RegistrySupplier<BlockItem> IRON_ZIPLINE_HOOK = ITEMS.register("iron_zipline_hook", () -> new BlockItem(ParCoolBlocks.IRON_ZIPLINE_HOOK.get(), new Item.Properties().tab(ParCoolItemGroup.INSTANCE)));
    public static final RegistrySupplier<ZiplineRopeItem> ZIPLINE_ROPE = ITEMS.register("zipline_rope", () -> new ZiplineRopeItem(new Item.Properties().tab(ParCoolItemGroup.INSTANCE)));
    public static final RegistrySupplier<TraceurGlovesItem> TRACEUR_GLOVES = ITEMS.register("traceur_gloves", () -> new TraceurGlovesItem(new Item.Properties().tab(ParCoolItemGroup.INSTANCE).stacksTo(1)));
    public static final RegistrySupplier<TraceurBootsItem> TRACEUR_BOOTS = ITEMS.register("traceur_boots", () -> new TraceurBootsItem(new Item.Properties().tab(ParCoolItemGroup.INSTANCE).stacksTo(1)));
    public static final RegistrySupplier<ParCoolGuideItem> PARCOOL_GUIDE = ITEMS.register("parcool_guide", () -> new ParCoolGuideItem(new Item.Properties().tab(ParCoolItemGroup.INSTANCE).stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static void register() {
        ITEMS.register();
    }

    @Environment(EnvType.CLIENT)
    public static void registerColors(Minecraft mc) {
        mc.getItemColors().register(new DyeAble.DyedColor(0), ZIPLINE_ROPE::get);
        mc.getItemColors().register(new DyeAble.DyedColor(0), TRACEUR_GLOVES::get);
        mc.getItemColors().register(new DyeAble.DyedColor(0), TRACEUR_BOOTS::get);
    }
}