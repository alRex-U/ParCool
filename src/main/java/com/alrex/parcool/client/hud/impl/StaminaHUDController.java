package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.stamina.ParCoolStamina;
import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class StaminaHUDController implements IIngameOverlay {
	LightStaminaHUD lightStaminaHUD;
	StaminaHUD staminaHUD;

	public StaminaHUDController() {
		lightStaminaHUD = new LightStaminaHUD();
		staminaHUD = new StaminaHUD();
	}

	public void onTick(TickEvent.ClientTickEvent event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || player.isCreative()) return;
		lightStaminaHUD.onTick(event, player);
		staminaHUD.onTick(event, player);
		IStamina stamina = IStamina.get(player);
		if (stamina == null) return;
		stamina.updateOldValue();
	}

	public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height) {
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
		if (!ParCoolConfig.Client.Booleans.ParCoolIsActive.get()) return;

        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;

        if (ParCoolConfig.Client.Booleans.HideStaminaHUDWhenStaminaIsInfinite.get() &&
                parkourability.getActionInfo().isStaminaInfinite(player.isCreative() || player.isSpectator())
        ) return;

        IStamina stamina = IStamina.get(player);
        if (stamina == null) return;

        if (!(stamina instanceof ParCoolStamina)) return;

		if (MinecraftForge.EVENT_BUS.post(new ParCoolHUDEvent.RenderEvent(gui, poseStack, partialTick, width, height)))
			return;

		switch (ParCoolConfig.Client.StaminaHUDType.get()) {
			case Light:
				lightStaminaHUD.render(gui, poseStack, parkourability, stamina, partialTick, width, height);
				break;
			case Normal:
				staminaHUD.render(gui, poseStack, parkourability, stamina, partialTick, width, height);
				break;
		}
	}
}
