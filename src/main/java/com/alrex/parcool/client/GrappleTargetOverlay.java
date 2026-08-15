package com.alrex.parcool.client;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class GrappleTargetOverlay {
    public static final ResourceLocation TEXTURE = ParCool.resourceLocation("textures/misc/grapple_target.png");
    private static final int TEXTURE_SIZE = 15;

    private static float screenX;
    private static float screenY;
    private static boolean visible = false;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        visible = false;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) return;
        Parkourability parkourability = Parkourability.get(minecraft.player);
        if (parkourability == null) return;
        Vec3 target = parkourability.get(ParCoolActions.GRAPPLE).getPreviewTarget();
        if (target == null) return;

        Vec3 camera = event.getCamera().getPosition();
        Vector4f point = new Vector4f(
                (float) (target.x - camera.x),
                (float) (target.y - camera.y),
                (float) (target.z - camera.z),
                1f
        );
        point.transform(event.getPoseStack().last().pose());
        point.transform(event.getProjectionMatrix());

        if (point.w() <= 1.0e-4) return;

        float normalisedX = point.x() / point.w();
        float normalisedY = point.y() / point.w();
        if (Math.abs(normalisedX) > 1.5f || Math.abs(normalisedY) > 1.5f) return;

        var window = minecraft.getWindow();
        screenX = (normalisedX * 0.5f + 0.5f) * window.getGuiScaledWidth();
        screenY = (1 - (normalisedY * 0.5f + 0.5f)) * window.getGuiScaledHeight();
        visible = true;
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!visible) return;
        int size = ParCool.getConfig().client().targetIndicatorSize.get();
        PoseStack poseStack = event.getPoseStack();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        GuiComponent.blit(
                poseStack,
                Math.round(screenX - size / 2f), Math.round(screenY - size / 2f),
                size, size,
                0, 0, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE
        );

        RenderSystem.disableBlend();
    }
}
