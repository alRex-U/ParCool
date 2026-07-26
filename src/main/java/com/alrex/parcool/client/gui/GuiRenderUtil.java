package com.alrex.parcool.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
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
}
