package com.alrex.parcool.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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

    public static void renderExtendableSprite(GuiGraphics graphics, TextureAtlasSprite sprite, int x, int y, int width, int height) {
        int halfWidth = width / 2, halfHeight = height / 2;
        int spriteWidth = sprite.contents().width(), spriteHeight = sprite.contents().height();

        float midU0 = sprite.getU(16. * halfWidth / spriteWidth), midV0 = sprite.getV(16. * halfHeight / spriteHeight);
        float midU1 = sprite.getU(16. - 16. * (width - halfWidth) / spriteWidth), midV1 = sprite.getV(16. - 16. * (height - halfHeight) / spriteHeight);
        graphics.innerBlit(
                sprite.atlasLocation(),
                x, x + halfWidth,
                y, y + halfHeight,
                0 /*z pos*/,
                sprite.getU0(), midU0,
                sprite.getV0(), midV0
        );
        graphics.innerBlit(
                sprite.atlasLocation(),
                x + halfWidth, x + width,
                y, y + halfHeight,
                0,
                midU1, sprite.getU1(),
                sprite.getV0(), midV0
        );
        graphics.innerBlit(
                sprite.atlasLocation(),
                x, x + halfWidth,
                y + halfHeight, y + height,
                0,
                sprite.getU0(), midU0,
                midV1, sprite.getV1()
        );
        graphics.innerBlit(
                sprite.atlasLocation(),
                x + halfWidth, x + width,
                y + halfHeight, y + height,
                0,
                midU1, sprite.getU1(),
                midV1, sprite.getV1()
        );
    }
}
