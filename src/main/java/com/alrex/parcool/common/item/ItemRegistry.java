package com.alrex.parcool.common.item;

import com.alrex.parcool.common.item.items.ParCoolGuideItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class ItemRegistry {
	@SubscribeEvent
	public static void register(RegisterEvent event) {
		event.register(
				ForgeRegistries.Keys.ITEMS,
				helper -> {
					helper.register(ParCoolGuideItem.RESOURCE_LOCATION, ParCoolGuideItem.INSTANCE);
				}
		);
	}
}
