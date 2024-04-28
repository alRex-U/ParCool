package com.alrex.parcool.common.item;

import net.minecraft.world.item.CreativeModeTab;

public class ParCoolItemGroup extends CreativeModeTab {
	public static final ParCoolItemGroup INSTANCE = new ParCoolItemGroup();

	public ParCoolItemGroup() {
		super(new Builder(Row.BOTTOM, 0).hideTitle());
	}
}
