package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Items {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ParCool.MOD_ID);
	public static final RegistryObject<Item> PARCOOL_GUIDE = ITEMS.register("parcool_guide", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> ZIPLINE_HOOK = ITEMS.register("zipline_point", () -> new BlockItem(Blocks.ZIPLINE_HOOK.get(), new Item.Properties().tab(ParCoolItemGroup.INSTANCE)));
	public static final RegistryObject<Item> ZIPLINE_ROPE = ITEMS.register("zipline_rope", () -> new ZiplineRopeItem(new Item.Properties().tab(ParCoolItemGroup.INSTANCE)));

	public static void register(IEventBus bus) {
		ITEMS.register(bus);
	}
}