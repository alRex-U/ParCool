package com.alrex.parcool.client.renderer.blockentity;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.renderer.RenderTypes;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;


@Environment(EnvType.CLIENT)
public class ZiplineHookRenderer implements BlockEntityRenderer<ZiplineHookTileEntity> {
    public static ResourceLocation TEXTURE_LOCATION = ParCool.resourceLocation("textures/misc/zipline.png");

    public ZiplineHookRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ZiplineHookTileEntity entity, float partial, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        var iterator = entity.getRenderAbleZiplines();
        var level = entity.getLevel();
        if (level == null) return;
        while (iterator.hasNext()) {
            renderRope(entity, iterator.next(), level, poseStack, multiBufferSource);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(ZiplineHookTileEntity entity) {
        return true;
    }

    @Override
    public boolean shouldRender(ZiplineHookTileEntity entity, Vec3 viewPoint) {
        return !entity.getConnectionPoints().isEmpty();
    }

    private void renderRope(
            ZiplineHookTileEntity entity,
            Zipline zipline,
            Level level,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource
    ) {
        int color = zipline.info().color();
        float r = ((0xFF0000 & color) >> 16) / 255f;
        float g = ((0x00FF00 & color) >> 8) / 255f;
        float b = (0x0000FF & color) / 255f;

        Vec3 hookPoint = entity.getHookPoint();
        BlockPos hookBlockPos = entity.getBlockPos();
        Vec3 endOffsetFromStart = zipline.shape().getOffsetFromStartToEnd();

        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);

        poseStack.pushPose();
        {
            poseStack.translate(
                    hookPoint.x() - hookBlockPos.getX(),
                    hookPoint.y() - hookBlockPos.getY(),
                    hookPoint.z() - hookBlockPos.getZ()
            );
            var vertexConsumer = multiBufferSource.getBuffer(RenderTypes.ZIPLINE_3D);
            Matrix4f transformMatrix = poseStack.last().pose();

            int startBlockLightLevel = level.getBrightness(LightLayer.BLOCK, zipline.start());
            int endBlockLightLevel = level.getBrightness(LightLayer.BLOCK, zipline.end());
            int startSkyBrightness = level.getBrightness(LightLayer.SKY, zipline.start());
            int endSkyBrightness = level.getBrightness(LightLayer.SKY, zipline.end());

            int divisionCount = Math.max(Mth.ceil(zipline.shape().getLength()), 2);
            float invLengthSqrtXZ = (float) Mth.fastInvSqrt(endOffsetFromStart.x() * endOffsetFromStart.x() + endOffsetFromStart.z() * endOffsetFromStart.z());
            float unitLengthX = (float) (endOffsetFromStart.x() * invLengthSqrtXZ);
            float unitLengthZ = (float) (endOffsetFromStart.z() * invLengthSqrtXZ);

            for (int i = 0; i < divisionCount; i++) {

                for (int j = 0; j < 2; j++) {
                    renderRopeSingleBlock3D(
                            transformMatrix, vertexConsumer,
                            zipline.shape(),
                            i, divisionCount,
                            unitLengthX, unitLengthZ,
                            startBlockLightLevel, endBlockLightLevel,
                            startSkyBrightness, endSkyBrightness,
                            r, g, b,
                            zipline.powered()
                    );
                }
            }
        }
        poseStack.popPose();
    }


    private void renderRopeSingleBlock3D(
            Matrix4f transformMatrix,
            VertexConsumer vertexConsumer,
            ZiplineShape zipline,
            int currentCount, int maxCount,
            float unitLengthX,
            float unitLengthZ,
            int startBlockLightLevel, int endBlockLightLevel,
            int startSkyBrightness, int endSkyBrightness,
            float r, float g, float b,
            boolean powered
    ) {
        Vector3f[] vertexList = new Vector3f[8];
        int[] lightLevelList = new int[2];
        for (int i = 0; i < 2; i++) {
            float phase = (float) (currentCount + i) / maxCount;

            lightLevelList[i] = LightTexture.pack((int) Mth.lerp(phase, startBlockLightLevel, endBlockLightLevel), (int) Mth.lerp(phase, startSkyBrightness, endSkyBrightness));
            Vec3 midPointD = zipline.getMidPointOffsetFromStart(phase);
            Vector3f midPoint = new Vector3f((float) midPointD.x(), (float) midPointD.y(), (float) midPointD.z());

            final float width = 0.09375f;
            float tilt = zipline.getSlope(phase);
            float tiltInv = Mth.fastInvSqrt(tilt * tilt + 1);
            float yOffset = width * tiltInv / 1.41421356f /*sqrt(2)*/;
            float xBaseOffset = unitLengthX * width * tilt * tiltInv / 1.41421356f;
            float zBaseOffset = unitLengthZ * width * tilt * tiltInv / 1.41421356f;
            float xOffset = unitLengthZ * width / 1.41421356f;
            float zOffset = -unitLengthX * width / 1.41421356f;
            vertexList[4 * i] = new Vector3f(
                    (midPoint.x() - xBaseOffset + xOffset),
                    (midPoint.y() + yOffset),
                    (midPoint.z() - zBaseOffset + zOffset)
            );
            vertexList[4 * i + 1] = new Vector3f(
                    (midPoint.x() - xBaseOffset - xOffset),
                    (midPoint.y() + yOffset),
                    (midPoint.z() - zBaseOffset - zOffset)
            );
            vertexList[4 * i + 2] = new Vector3f(
                    (midPoint.x() + xBaseOffset - xOffset),
                    (midPoint.y() - yOffset),
                    (midPoint.z() + zBaseOffset - zOffset)
            );
            vertexList[4 * i + 3] = new Vector3f(
                    (midPoint.x() + xBaseOffset + xOffset),
                    (midPoint.y() - yOffset),
                    (midPoint.z() + zBaseOffset + zOffset)
            );
        }
        // Render side planes
        for (int i = 0; i < 4; i++) {
            vertexConsumer
                    .vertex(transformMatrix, vertexList[i].x(), vertexList[i].y(), vertexList[i].z())
                    .color(r, g, b, 1f).uv(0, (i + (powered ? 4 : 0)) / 8f).uv2(lightLevelList[0])
                    .endVertex();
            vertexConsumer
                    .vertex(transformMatrix, vertexList[(i + 1) % 4].x(), vertexList[(i + 1) % 4].y(), vertexList[(i + 1) % 4].z())
                    .color(r, g, b, 1f).uv(0, (i + (powered ? 5 : 1)) / 8f).uv2(lightLevelList[0])
                    .endVertex();
            vertexConsumer
                    .vertex(transformMatrix, vertexList[4 + (i + 1) % 4].x(), vertexList[4 + (i + 1) % 4].y(), vertexList[4 + (i + 1) % 4].z())
                    .color(r, g, b, 1f).uv(1, (i + (powered ? 5 : 1)) / 8f).uv2(lightLevelList[1])
                    .endVertex();
            vertexConsumer
                    .vertex(transformMatrix, vertexList[4 + i].x(), vertexList[4 + i].y(), vertexList[4 + i].z())
                    .color(r, g, b, 1f).uv(1, (i + (powered ? 4 : 0)) / 8f).uv2(lightLevelList[1])
                    .endVertex();
        }

        // Render caps
        if (currentCount == 0) {
            vertexConsumer.vertex(transformMatrix, vertexList[3].x(), vertexList[3].y(), vertexList[3].z()).color(r, g, b, 1f).uv(0, (powered ? 4 : 0) / 8f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[2].x(), vertexList[2].y(), vertexList[2].z()).color(r, g, b, 1f).uv(0, (powered ? 5 : 1) / 8f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[1].x(), vertexList[1].y(), vertexList[1].z()).color(r, g, b, 1f).uv(1f / 16f, (powered ? 5 : 1) / 8f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[0].x(), vertexList[0].y(), vertexList[0].z()).color(r, g, b, 1f).uv(1f / 16f, (powered ? 4 : 0) / 8f).uv2(lightLevelList[0]).endVertex();
        } else if (currentCount == maxCount - 1) {
            vertexConsumer.vertex(transformMatrix, vertexList[4].x(), vertexList[4].y(), vertexList[4].z()).color(r, g, b, 1f).uv(15f / 16f, (powered ? 4 : 0) / 8f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[5].x(), vertexList[5].y(), vertexList[5].z()).color(r, g, b, 1f).uv(15f / 16f, (powered ? 5 : 1) / 8f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[6].x(), vertexList[6].y(), vertexList[6].z()).color(r, g, b, 1f).uv(1f, (powered ? 5 : 1) / 8f).uv2(lightLevelList[0]).endVertex();
            vertexConsumer.vertex(transformMatrix, vertexList[7].x(), vertexList[7].y(), vertexList[7].z()).color(r, g, b, 1f).uv(1f, (powered ? 4 : 0) / 8f).uv2(lightLevelList[0]).endVertex();
        }
    }
}
