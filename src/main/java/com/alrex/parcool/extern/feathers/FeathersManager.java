package com.alrex.parcool.extern.feathers;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.stamina.Stamina;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

public class FeathersManager {
	private static boolean feathersInstalled = false;

	public static boolean isFeathersInstalled() {
		return feathersInstalled;
	}

	public static void init() {
		@Nullable
		var mod = ModList.get().getModFileById("feathers");
		feathersInstalled = mod != null;
	}

	public static IStamina newFeathersStaminaFor(Player player) {
        if (!feathersInstalled) return new Stamina(player);
		return new FeathersStamina(player);
	}

	@OnlyIn(Dist.CLIENT)
	public static boolean isUsingFeathers() {
        return feathersInstalled && ParCoolConfig.Client.StaminaType.get() == IStamina.Type.Feathers;
	}
}