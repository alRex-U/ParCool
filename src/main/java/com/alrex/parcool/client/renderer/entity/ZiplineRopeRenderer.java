package com.alrex.parcool.client.renderer.entity;

import com.alrex.parcool.client.renderer.RenderTypes;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.compatibility.AxisWrapper;
import com.alrex.parcool.compatibility.EntityWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;
import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.LightType;

import javax.annotation.Nonnull;


public class ZiplineRopeRenderer extends EntityRenderer<ZiplineRopeEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/arrow");

    public ZiplineRopeRenderer(EntityRendererManager p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull ZiplineRopeEntity ziplineRopeEntity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public boolean shouldRender(ZiplineRopeEntity entity, ClippingHelper clippingHelper, double x, double y, double z) {
        return entity.shouldRender(x, y, z);
    }

    @Override
    public void render(ZiplineRopeEntity entity, float p_225623_2_, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
        renderRope(entity, partialTick, matrixStack, renderTypeBuffer);
    }

    private void renderRope(ZiplineRopeEntity entity, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer) {
        BlockPos start = entity.getStartPos();
        BlockPos end = entity.getEndPos();
        if (start == BlockPos.ZERO && end == BlockPos.ZERO) return;
        EntityWrapper entityWrapper = EntityWrapper.get(entity);

        int color = entity.getColor();
        float r = ((0xFF0000 & color) >> 16) / 255f;
        float g = ((0x00FF00 & color) >> 8) / 255f;
        float b = (0x0000FF & color) / 255f;

        Vec3Wrapper entityPos = entityWrapper.position();
        Zipline zipline = entity.getZipline();
        Vec3Wrapper startPos = zipline.getStartPos();
        Vec3Wrapper startPosOffset = startPos.subtract(entityPos);
        Vec3Wrapper endOffsetFromStart = zipline.getOffsetToEndFromStart();

        boolean render3d = ParCoolConfig.Client.Booleans.Enable3DRenderingForZipline.get();

        matrixStack.pushPose();
        {
            matrixStack.translate(startPosOffset.x(), startPosOffset.y(), startPosOffset.z());
            IVertexBuilder vertexBuilder = render3d ?
                    renderTypeBuffer.getBuffer(RenderType.leash()) :
                    renderTypeBuffer.getBuffer(RenderTypes.ZIPLINE_3D);
            Matrix4f transformMatrix = matrixStack.last().pose();

            int startBlockLightLevel = this.getBlockLightLevel(entity, start);
            int endBlockLightLevel = this.getBlockLightLevel(entity, end);
            int startSkyBrightness = entity.level.getBrightness(LightType.SKY, start);
            int endSkyBrightness = entity.level.getBrightness(LightType.SKY, end);


            final int divisionCount = 24;
            float invLengthSqrtXZ = (float) MathHelper.fastInvSqrt(endOffsetFromStart.x() * endOffsetFromStart.x() + endOffsetFromStart.z() * endOffsetFromStart.z());
            float unitLengthX = (float) (endOffsetFromStart.x() * invLengthSqrtXZ);
            float unitLengthZ = (float) (endOffsetFromStart.z() * invLengthSqrtXZ);
            for (int i = 0; i < divisionCount; i++) {
                float colorScale = i % 2 == 0 ? 1f : 0.8f;

                for (int j = 0; j < 2; j++) {
                    if (render3d) {
                        renderRopeSingleBlock3D(
                                transformMatrix, vertexBuilder,
                                zipline,
                                i, divisionCount,
                                unitLengthX, unitLengthZ,
                                startBlockLightLevel, endBlockLightLevel,
                                startSkyBrightness, endSkyBrightness,
                                r * colorScale, g * colorScale, b * colorScale//,
                                //j % 2 == 0
                        );
                    } else {
                        renderRopeSingleBlock2D(
                                transformMatrix, vertexBuilder,
                                zipline,
                                i, divisionCount,
                                unitLengthX, unitLengthZ,
                                startBlockLightLevel, endBlockLightLevel,
                                startSkyBrightness, endSkyBrightness,
                                r * colorScale, g * colorScale, b * colorScale,
                                j % 2 == 0
                        );
                    }
                }
            }
        }
        matrixStack.popPose();
    }

    private void renderRopeSingleBlock2D(
            Matrix4f transformMatrix,
            IVertexBuilder vertexBuilder,
            Zipline zipline,
            int currentCount, int maxCount,
            float unitLengthX,
            float unitLengthZ,
            int startBlockLightLevel, int endBlockLightLevel,
            int startSkyBrightness, int endSkyBrightness,
            float r, float g, float b,
            boolean tiltType
    ) {
        for (int i = 0; i < 2; i++) {
            float phase = (float) (currentCount + i) / maxCount;

            int lightLevel = LightTexture.pack((int) MathHelper.lerp(phase, startBlockLightLevel, endBlockLightLevel), (int) MathHelper.lerp(phase, startSkyBrightness, endSkyBrightness));
            Vec3Wrapper midPointD = zipline.getMidPointOffsetFromStart(phase);
            AxisWrapper midPoint = AxisWrapper.fromVector(midPointD);

            final float width = 0.075f;
            float tilt = zipline.getSlope(phase);
            float tiltInv = MathHelper.fastInvSqrt(tilt * tilt + 1);
            float yOffset = width * tiltInv / 1.41421356f /*sqrt(2)*/;
            float xBaseOffset = unitLengthX * width * tilt * tiltInv / 1.41421356f;
            float zBaseOffset = unitLengthZ * width * tilt * tiltInv / 1.41421356f;
            float sign = tiltType ? 1 : -1;
            float xOffset = sign * unitLengthZ * width / 1.41421356f;
            float zOffset = sign * -unitLengthX * width / 1.41421356f;

            if (i == 0) {
                vertexBuilder
                        .vertex(transformMatrix,
                                (midPoint.x() + xBaseOffset + xOffset),
                                (midPoint.y() - yOffset),
                                (midPoint.z() + zBaseOffset + zOffset)
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
                vertexBuilder
                        .vertex(transformMatrix,
                                (midPoint.x() - xBaseOffset - xOffset),
                                (midPoint.y() + yOffset),
                                (midPoint.z() - zBaseOffset - zOffset)
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
            } else {
                vertexBuilder
                        .vertex(transformMatrix,
                                (midPoint.x() - xBaseOffset - xOffset),
                                (midPoint.y() + yOffset),
                                (midPoint.z() - zBaseOffset - zOffset)
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
                vertexBuilder
                        .vertex(transformMatrix,
                                (midPoint.x() + xBaseOffset + xOffset),
                                (midPoint.y() - yOffset),
                                (midPoint.z() + zBaseOffset + zOffset)
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
            }
        }
    }

    private void renderRopeSingleBlock3D(
            Matrix4f transformMatrix,
            IVertexBuilder vertexBuilder,
            Zipline zipline,
            int currentCount, int maxCount,
            float unitLengthX,
            float unitLengthZ,
            int startBlockLightLevel, int endBlockLightLevel,
            int startSkyBrightness, int endSkyBrightness,
            float r, float g, float b
    ) {
        AxisWrapper[] vertexList = new AxisWrapper[8];
        int[] lightLevelList = new int[2];
        for (int i = 0; i < 2; i++) {
            float phase = (float) (currentCount + i) / maxCount;

            lightLevelList[i] = LightTexture.pack((int) MathHelper.lerp(phase, startBlockLightLevel, endBlockLightLevel), (int) MathHelper.lerp(phase, startSkyBrightness, endSkyBrightness));
            Vec3Wrapper midPointD = zipline.getMidPointOffsetFromStart(phase);
            AxisWrapper midPoint = AxisWrapper.fromVector(midPointD);

            final float width = 0.075f;
            float tilt = zipline.getSlope(phase);
            float tiltInv = MathHelper.fastInvSqrt(tilt * tilt + 1);
            float yOffset = width * tiltInv / 1.41421356f /*sqrt(2)*/;
            float xBaseOffset = unitLengthX * width * tilt * tiltInv / 1.41421356f;
            float zBaseOffset = unitLengthZ * width * tilt * tiltInv / 1.41421356f;
            float xOffset = unitLengthZ * width / 1.41421356f;
            float zOffset = -unitLengthX * width / 1.41421356f;
            vertexList[4 * i] = new AxisWrapper(
                    (midPoint.x() - xBaseOffset + xOffset),
                    (midPoint.y() + yOffset),
                    (midPoint.z() - zBaseOffset + zOffset)
            );
            vertexList[4 * i + 1] = new AxisWrapper(
                    (midPoint.x() - xBaseOffset - xOffset),
                    (midPoint.y() + yOffset),
                    (midPoint.z() - zBaseOffset - zOffset)
            );
            vertexList[4 * i + 2] = new AxisWrapper(
                    (midPoint.x() + xBaseOffset - xOffset),
                    (midPoint.y() - yOffset),
                    (midPoint.z() + zBaseOffset - zOffset)
            );
            vertexList[4 * i + 3] = new AxisWrapper(
                    (midPoint.x() + xBaseOffset + xOffset),
                    (midPoint.y() - yOffset),
                    (midPoint.z() + zBaseOffset + zOffset)
            );
        }
        for (int i = 0; i < 4; i++) {
            vertexBuilder.vertex(transformMatrix, vertexList[i].x(), vertexList[i].y(), vertexList[i].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexBuilder.vertex(transformMatrix, vertexList[(i + 1) % 4].x(), vertexList[(i + 1) % 4].y(), vertexList[(i + 1) % 4].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexBuilder.vertex(transformMatrix, vertexList[4 + (i + 1) % 4].x(), vertexList[4 + (i + 1) % 4].y(), vertexList[4 + (i + 1) % 4].z()).color(r, g, b, 1f).uv2(lightLevelList[1]).endVertex();
            vertexBuilder.vertex(transformMatrix, vertexList[4 + i].x(), vertexList[4 + i].y(), vertexList[4 + i].z()).color(r, g, b, 1f).uv2(lightLevelList[1]).endVertex();
        }
        if (currentCount == 0) {
            vertexBuilder.vertex(transformMatrix, vertexList[3].x(), vertexList[3].y(), vertexList[3].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexBuilder.vertex(transformMatrix, vertexList[2].x(), vertexList[2].y(), vertexList[2].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexBuilder.vertex(transformMatrix, vertexList[1].x(), vertexList[1].y(), vertexList[1].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexBuilder.vertex(transformMatrix, vertexList[0].x(), vertexList[0].y(), vertexList[0].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
        } else if (currentCount == maxCount - 1) {
            vertexBuilder.vertex(transformMatrix, vertexList[4].x(), vertexList[4].y(), vertexList[4].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexBuilder.vertex(transformMatrix, vertexList[5].x(), vertexList[5].y(), vertexList[5].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexBuilder.vertex(transformMatrix, vertexList[6].x(), vertexList[6].y(), vertexList[6].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
            vertexBuilder.vertex(transformMatrix, vertexList[7].x(), vertexList[7].y(), vertexList[7].z()).color(r, g, b, 1f).uv2(lightLevelList[0]).endVertex();
        }
    }
}
