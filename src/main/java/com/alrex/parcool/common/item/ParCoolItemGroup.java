package com.alrex.parcool.common.item;

import com.alrex.parcool.common.item.items.ParCoolGuideItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ParCoolItemGroup extends ItemGroup {
	public static final ParCoolItemGroup INSTANCE = new ParCoolItemGroup();

	public ParCoolItemGroup() {
		super("ParCool");
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(ParCoolGuideItem.INSTANCE);
	}
}
