package com.alrex.parcool.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

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

    public static void renderScaledGuiItem(GuiGraphics graphics, ItemStack itemStack, float notScaledX, float notScaledY, int scaledX, int scaledY, float scale) {
        var poseStack = graphics.pose();
        poseStack.pushPose();
        {
            poseStack.translate(notScaledX, notScaledY, 0);
            poseStack.scale(scale, scale, 1);
            graphics.renderItem(itemStack, scaledX, scaledY);
        }
        poseStack.popPose();
    }
}
