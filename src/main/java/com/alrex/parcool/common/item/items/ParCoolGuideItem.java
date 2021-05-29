package com.alrex.parcool.common.item.items;

import com.alrex.parcool.ParCool;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ParCoolGuideItem extends Item {

	public static final ParCoolGuideItem INSTANCE = new ParCoolGuideItem(new Item.Properties());

	private ParCoolGuideItem(Properties properties) {
		super(properties);
		properties
				.group(ItemGroup.SEARCH)
				.maxStackSize(1);
		setRegistryName(ParCool.MOD_ID, "parcool_guide");
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ParCool.PROXY.showParCoolGuideScreen(playerIn);
		return ActionResult.resultConsume(playerIn.getHeldItem(handIn));
	}
}
