package com.alrex.parcool.common.event;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.gui.ParCoolSettingScreen;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.capability.impl.Parkourability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

;

@OnlyIn(Dist.CLIENT)
public class EventOpenSettingsParCool {
	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) return;

		if (KeyRecorder.keyOpenSettingsState.isPressed()) {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player == null) return;
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) return;
			Minecraft.getInstance().setScreen(new ParCoolSettingScreen(new TextComponent("ParCool Setting"), parkourability.getActionInfo(), ParCoolConfig.CONFIG_CLIENT.guiColorTheme.get()));
		}
	}
}
