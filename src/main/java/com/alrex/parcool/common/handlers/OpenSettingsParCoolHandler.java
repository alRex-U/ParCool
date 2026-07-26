package com.alrex.parcool.common.handlers;

import com.alrex.parcool.client.gui.screen.MarkdownScreen;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class OpenSettingsParCoolHandler {
	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) return;

        if (ParCoolKeyBinds.SHIFT.state().isDown()) {
            /*
            var player = Minecraft.getInstance().player;
            if (player == null) return;
            var prepareEvent = new PrepareParCoolSkillTreeEvent();
            MinecraftForge.EVENT_BUS.post(prepareEvent);
            Minecraft.getInstance().setScreen(new SkillTreeScreen(Parkourability.get(player).getCapabilities(), prepareEvent.getSkillTrees()));
             */
			Minecraft.getInstance().setScreen(new MarkdownScreen(new ResourceLocation("parcool", "parcool_guide/test.md")));
		}
	}
}
