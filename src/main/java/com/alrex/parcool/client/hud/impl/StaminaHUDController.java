package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.stamina.Stamina;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.extern.feathers.FeathersManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
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
	}

	public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height) {
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
		if (!ParCoolConfig.Client.Booleans.ParCoolIsActive.get() ||
                !(IStamina.get(player) instanceof Stamina) ||
                FeathersManager.isUsingFeathers()
        )
			return;

		switch (ParCoolConfig.Client.StaminaHUDType.get()) {
			case Light:
				lightStaminaHUD.render(gui, poseStack, partialTick, width, height);
				break;
			case Normal:
				staminaHUD.render(gui, poseStack, partialTick, width, height);
				break;
		}
	}
}
