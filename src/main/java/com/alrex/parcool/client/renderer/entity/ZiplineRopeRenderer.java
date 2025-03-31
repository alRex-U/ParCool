package com.alrex.parcool.client.renderer.entity;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
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
        renderLeash(entity, partialTick, matrixStack, renderTypeBuffer);
    }

    private void renderLeash(ZiplineRopeEntity entity, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer) {
        BlockPos start = entity.getStartPos();
        BlockPos end = entity.getEndPos();
        if (start == BlockPos.ZERO && end == BlockPos.ZERO) return;

        int color = entity.getColor();
        float r = ((0xFF0000 & color) >> 16) / 255f;
        float g = ((0x00FF00 & color) >> 8) / 255f;
        float b = (0x0000FF & color) / 255f;

        Vector3d entityPos = entity.position();
        Zipline zipline = entity.getZipline();
        Vector3f startPos = zipline.getStartPos();
        Vector3f startPosOffset = new Vector3f(
                (float) (startPos.x() - entityPos.x()),
                (float) (startPos.y() - entityPos.y()),
                (float) (startPos.z() - entityPos.z())
        );
        Vector3f endOffsetFromStart = zipline.getOffsetToEndFromStart();

        matrixStack.pushPose();
        {
            matrixStack.translate(startPosOffset.x(), startPosOffset.y(), startPosOffset.z());
            IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(RenderType.leash());
            Matrix4f transformMatrix = matrixStack.last().pose();

            int startBlockLightLevel = this.getBlockLightLevel(entity, start.below());
            int endBlockLightLevel = this.getBlockLightLevel(entity, end.below());
            int startSkyBrightness = entity.level.getBrightness(LightType.SKY, start.below());
            int endSkyBrightness = entity.level.getBrightness(LightType.SKY, end.below());


            final int divisionCount = 24;
            float invLengthSqrtXZ = MathHelper.fastInvSqrt(endOffsetFromStart.x() * endOffsetFromStart.x() + endOffsetFromStart.z() * endOffsetFromStart.z());
            float unitLengthX = endOffsetFromStart.x() * invLengthSqrtXZ;
            float unitLengthZ = endOffsetFromStart.z() * invLengthSqrtXZ;
            for (int i = 0; i < divisionCount; i++) {
                float colorScale = i % 2 == 0 ? 1f : 0.8f;

                for (int j = 0; j < 2; j++) {
                    renderLeashSingleBlock(
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
        matrixStack.popPose();
    }

    private void renderLeashSingleBlock(
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
            Vector3d midPoint = zipline.getMidPointOffsetFromStart(phase);

            final float width = 0.075f;
            Vector3f endOffsetFromStart = zipline.getOffsetToEndFromStart();
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
                                (float) (midPoint.x() + xBaseOffset + xOffset),
                                (float) (midPoint.y() - yOffset),
                                (float) (midPoint.z() + zBaseOffset + zOffset)
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
                vertexBuilder
                        .vertex(transformMatrix,
                                (float) (midPoint.x() - xBaseOffset - xOffset),
                                (float) (midPoint.y() + yOffset),
                                (float) (midPoint.z() - zBaseOffset - zOffset)
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
            } else {
                vertexBuilder
                        .vertex(transformMatrix,
                                (float) (midPoint.x() - xBaseOffset - xOffset),
                                (float) (midPoint.y() + yOffset),
                                (float) (midPoint.z() - zBaseOffset - zOffset)
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
                vertexBuilder
                        .vertex(transformMatrix,
                                (float) (midPoint.x() + xBaseOffset + xOffset),
                                (float) (midPoint.y() - yOffset),
                                (float) (midPoint.z() + zBaseOffset + zOffset)
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
            }
        }
    }
}
