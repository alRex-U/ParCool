package com.alrex.parcool.common.event;

import com.alrex.parcool.client.gui.ParCoolSettingScreen;
import com.alrex.parcool.client.input.KeyRecorder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class EventOpenSettingsParCool {
	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) return;

		if (KeyRecorder.keyOpenSettingsState.isPressed()) {
			Minecraft.getInstance().displayGuiScreen(new ParCoolSettingScreen(new StringTextComponent("Setting")));
		}
	}
}
