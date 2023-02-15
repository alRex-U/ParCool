package com.alrex.parcool.common.item.items;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.ParCoolItemGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParCoolGuideItem extends Item {
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(ParCool.MOD_ID, "parcool_guide");
	public static final ParCoolGuideItem INSTANCE = new ParCoolGuideItem(
			new Properties()
					.tab(ParCoolItemGroup.INSTANCE)
					.stacksTo(1)
	);

	private ParCoolGuideItem(Properties properties) {
		super(properties);
		setRegistryName(RESOURCE_LOCATION);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ParCool.PROXY.showParCoolGuideScreen(playerIn);
		return ActionResult.consume(playerIn.getMainHandItem());
	}
}
