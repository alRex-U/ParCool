package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class ItemRegistry {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ParCool.MOD_ID);
	public static final RegistryObject<Item> PARCOOL_GUIDE_REGISTRY = ITEMS.register("parcool_guide", () -> new Item(new Item.Properties().stacksTo(1)));
	@ObjectHolder("parcool:parcool_guide")
	public static final Item PARCOOL_GUIDE = null;
}