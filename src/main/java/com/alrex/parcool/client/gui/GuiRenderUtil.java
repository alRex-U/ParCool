package com.alrex.parcool.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiRenderUtil {
    public static void enableScissorTestInGuiCoordinate(double x, double y, double width, double height) {
        var window = Minecraft.getInstance().getWindow();
        var guiScale = window.getGuiScale();

        RenderSystem.enableScissor(
                (int) (guiScale * x),
                window.getHeight() - (int) (guiScale * (y + height)),
                Mth.ceil(width * guiScale),
                Mth.ceil(height * guiScale)
        );
    }

    public static void renderScaledGuiItem(ItemRenderer itemRenderer, ItemStack itemStack, float notScaledX, float notScaledY, int scaledX, int scaledY, float scale) {
        var modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        {
            modelViewStack.translate(notScaledX, notScaledY, 0);
            modelViewStack.scale(scale, scale, 1);
            itemRenderer.renderGuiItem(itemStack, scaledX, scaledY);
        }
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
