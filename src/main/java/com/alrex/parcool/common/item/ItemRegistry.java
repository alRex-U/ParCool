package com.alrex.parcool.common.item;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemRegistry {
	public static final Item PARCOOL_GUIDE = new Item(new Item.Properties().tab(ParCoolItemGroup.INSTANCE).stacksTo(1));
	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		ParCoolItemGroup.INSTANCE.getId();
		//event.getRegistry().register(PARCOOL_GUIDE);
	}
}