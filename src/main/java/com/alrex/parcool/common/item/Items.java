package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Items {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ParCool.MOD_ID);
	public static final DeferredHolder<Item, Item> PARCOOL_GUIDE_REGISTRY = ITEMS.register("parcool_guide", () -> new Item(new Item.Properties().stacksTo(1)));

	public static void registerAll(IEventBus modBus) {
		ITEMS.register(modBus);
	}
}