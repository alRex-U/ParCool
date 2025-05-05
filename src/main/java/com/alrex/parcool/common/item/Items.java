package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Items {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ParCool.MOD_ID);
    public static final DeferredHolder<Item, Item> PARCOOL_GUIDE = ITEMS.register("parcool_guide", (name) -> new Item(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, name))));
    public static final DeferredHolder<Item, Item> WOODEN_ZIPLINE_HOOK = ITEMS.register("wooden_zipline_hook", (name) -> new BlockItem(Blocks.WOODEN_ZIPLINE_HOOK.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, name))));
    public static final DeferredHolder<Item, Item> IRON_ZIPLINE_HOOK = ITEMS.register("iron_zipline_hook", (name) -> new BlockItem(Blocks.IRON_ZIPLINE_HOOK.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, name))));
    public static final DeferredHolder<Item, Item> ZIPLINE_ROPE = ITEMS.register("zipline_rope", (name) -> new ZiplineRopeItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, name))));

	public static void registerAll(IEventBus modBus) {
		ITEMS.register(modBus);
	}
}