package com.alrex.parcool.common.item.items;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.item.ParCoolItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
	private static final Component toolTips = new TranslatableComponent("toolTip.parcool_guide");

	private ParCoolGuideItem(Properties properties) {
		super(properties);
		setRegistryName(RESOURCE_LOCATION);
	}

	@Override
	public Component getHighlightTip(ItemStack item, Component displayName) {
		return toolTips;
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}


	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
		ParCool.PROXY.showParCoolGuideScreen(playerIn);
		return InteractionResultHolder.consume(playerIn.getMainHandItem());
	}


}
