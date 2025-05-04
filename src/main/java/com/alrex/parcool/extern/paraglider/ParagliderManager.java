package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.extern.ModManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tictim.paraglider.api.item.Paraglider;

import javax.annotation.Nullable;

public class ParagliderManager extends ModManager {

	public ParagliderManager() {
		super("paraglider");
	}

	@Nullable
	public IStamina newParagliderStaminaFor(Player player) {
		if (!isInstalled()) return IStamina.Type.Default.newInstance(player);
		return new ParagliderStamina(player);
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isUsingParagliderStamina(Player player) {
		return isInstalled() && IStamina.get(player) instanceof ParagliderStamina;
	}

	public boolean isFallingWithParaglider(Player player) {
		if (isInstalled()) {
			ItemStack stack;
			Paraglider paragliderItem;
			if (player.getMainHandItem().getItem() instanceof Paraglider p) {
				paragliderItem = p;
				stack = player.getMainHandItem();
			} else if (player.getOffhandItem().getItem() instanceof Paraglider p) {
				paragliderItem = p;
				stack = player.getMainHandItem();
			} else
				return false;
			return paragliderItem.isParagliding(stack);
		} else {
			return false;
		}
	}
}
