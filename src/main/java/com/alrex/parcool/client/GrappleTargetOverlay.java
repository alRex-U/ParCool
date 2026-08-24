package com.alrex.parcool.client;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;
import org.joml.Vector4f;

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

        var camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();
        var poseStack = event.getPoseStack();
        poseStack.pushPose();
        {
            var cameraRot = camera.rotation();
            poseStack.mulPose(new Quaternionf(-cameraRot.x, -cameraRot.y, -cameraRot.z, cameraRot.w));
            Vector4f point = new Vector4f(
                    (float) (target.x - cameraPos.x),
                    (float) (target.y - cameraPos.y),
                    (float) (target.z - cameraPos.z),
                    1f
            );
            point.mul(poseStack.last().pose());
            point.mul(event.getProjectionMatrix());

            if (Mth.abs(point.w()) <= 1.0e-4) return;

            float normalisedX = point.x() / point.w();
            float normalisedY = point.y() / point.w();
            if (Math.abs(normalisedX) > 1.5f || Math.abs(normalisedY) > 1.5f) return;

            var window = minecraft.getWindow();
            screenX = (normalisedX * 0.5f + 0.5f) * window.getGuiScaledWidth();
            screenY = (1 - (normalisedY * 0.5f + 0.5f)) * window.getGuiScaledHeight();
            visible = true;
        }
        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!visible) return;
        int size = ParCool.getConfig().client().targetIndicatorSize.get();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        event.getGuiGraphics().blit(
                TEXTURE,
                Math.round(screenX - size / 2f), Math.round(screenY - size / 2f),
                size, size,
                0, 0, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE
        );

        RenderSystem.disableBlend();
    }
}
