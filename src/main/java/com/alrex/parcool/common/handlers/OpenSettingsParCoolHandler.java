package com.alrex.parcool.common.handlers;

import com.alrex.parcool.client.gui.SettingActionLimitationScreen;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.compatibility.ClientPlayerWrapper;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class OpenSettingsParCoolHandler {
	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) return;

		if (KeyRecorder.keyOpenSettingsState.isPressed()) {
			ClientPlayerWrapper player = ClientPlayerWrapper.get();
			if (player == null) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			Minecraft.getInstance().setScreen(new SettingActionLimitationScreen(new StringTextComponent("ParCool Setting"), parkourability.getActionInfo(), ParCoolConfig.Client.GUIColorTheme.get()));
		}
	}
}
