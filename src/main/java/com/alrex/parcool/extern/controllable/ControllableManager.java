package com.alrex.parcool.extern.controllable;

import com.alrex.parcool.ParCool;
import com.mrcrayfish.controllable.client.ButtonBindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ControllableManager {
	private static boolean controllableInstalled = false;

	public static boolean isControllableInstalled() {
		return controllableInstalled;
	}

	public static void init() {
		@Nullable
		ModFileInfo mod = ModList.get().getModFileById("controllable");
		controllableInstalled = mod != null;
		if (isControllableInstalled()) {
			ParCool.LOGGER.info("Controllable was detected. Binding Adapting starts.");
			ButtonAdapterBindings.adapt();
			ParCool.LOGGER.info("Binding Adapting completed.");
		}
	}

	public static boolean isJumpPressed() {
		if (isControllableInstalled()) {
			return ButtonBindings.JUMP.isButtonPressed();
		} else {
			return false;
		}
	}
}
