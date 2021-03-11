package com.alrex.parcool.client.hud;


import com.alrex.parcool.common.capability.IStamina;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StaminaHUD extends AbstractGui {
    public static void render(RenderGameOverlayEvent event){
        ClientPlayerEntity player= Minecraft.getInstance().player;
        IStamina stamina;
        {
            LazyOptional<IStamina> staminaOptional = player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
            if (!staminaOptional.isPresent()) return;
            stamina = staminaOptional.resolve().get();
        }

        //I want more rich ui
        FontRenderer fontRenderer=Minecraft.getInstance().fontRenderer;
        fontRenderer.drawString(event.getMatrixStack(), String.format("%d/%d",stamina.getStamina(),stamina.getMaxStamina()),0,event.getWindow().getScaledHeight()- fontRenderer.FONT_HEIGHT,stamina.isExhausted() ? 0xFF0000 : 0x00FF00);
    }
    @SubscribeEvent
    public static void onOverlay(RenderGameOverlayEvent event){
        if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE)return;
        render(event);
    }
}
