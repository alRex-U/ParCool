package com.alrex.parcool.common.item.items;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.ParCoolItemGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ParCoolGuideItem extends Item {
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(ParCool.MOD_ID, "parcool_guide");
	public static final ParCoolGuideItem INSTANCE = new ParCoolGuideItem(
			new Properties()
					.tab(ParCoolItemGroup.INSTANCE)
					.stacksTo(1)
	);

	private ParCoolGuideItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ParCool.PROXY.showParCoolGuideScreen(playerIn);
		return InteractionResultHolder.consume(playerIn.getMainHandItem());
	}
}
