package com.alrex.parcool.common.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ParCoolItemGroup extends CreativeModeTab {
	public static final ParCoolItemGroup INSTANCE = new ParCoolItemGroup();

	public ParCoolItemGroup() {
		super("ParCool");
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(ItemRegistry.PARCOOL_GUIDE);
	}
}
