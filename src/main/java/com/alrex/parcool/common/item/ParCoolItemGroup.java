package com.alrex.parcool.common.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ParCoolItemGroup extends ItemGroup {
	public static final ParCoolItemGroup INSTANCE = new ParCoolItemGroup();

	public ParCoolItemGroup() {
		super("ParCool");
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(ItemRegistry.PARCOOL_GUIDE);
	}
}
