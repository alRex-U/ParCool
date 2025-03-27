package com.alrex.parcool.client.renderer.entity;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
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
    public void render(ZiplineRopeEntity entity, float p_225623_2_, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
        renderLeash(entity, partialTick, matrixStack, renderTypeBuffer);
    }

    private void renderLeash(ZiplineRopeEntity entity, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer) {
        BlockPos start = entity.getStartPos();
        BlockPos end = entity.getEndPos();
        if (start == BlockPos.ZERO && end == BlockPos.ZERO) return;

        if (start.getY() > end.getY()) { // start.y should be lower than end.y
            BlockPos temp = start;
            start = end;
            end = temp;
        }

        Vector3d entityPos = entity.position();
        Vector3f startPos = new Vector3f(start.getX() + 0.5f, start.getY() + 0.5f, start.getZ() + 0.5f);
        Vector3f endPos = new Vector3f(end.getX() + 0.5f, end.getY() + 0.5f, end.getZ() + 0.5f);
        Vector3f startPosOffset = new Vector3f(
                (float) (startPos.x() - entityPos.x()),
                (float) (startPos.y() - entityPos.y()),
                (float) (startPos.z() - entityPos.z())
        );
        Vector3f endOffsetFromStart = new Vector3f(
                endPos.x() - startPos.x(),
                endPos.y() - startPos.y(),
                endPos.z() - startPos.z()
        );

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
            float invLengthSqrtXZ = MathHelper.fastInvSqrt(endOffsetFromStart.x() * endOffsetFromStart.x() + endOffsetFromStart.z() + endOffsetFromStart.z());
            float unitLengthX = endOffsetFromStart.x() * invLengthSqrtXZ;
            float unitLengthZ = endOffsetFromStart.z() * invLengthSqrtXZ;
            for (int i = 0; i < divisionCount; i++) {
                float colorScale = i % 2 == 0 ? 1f : 0.8f;

                for (int j = 0; j < 2; j++) {
                    renderLeashSingleBlock(
                            transformMatrix, vertexBuilder,
                            endOffsetFromStart,
                            i, divisionCount,
                            invLengthSqrtXZ,
                            unitLengthX, unitLengthZ,
                            startBlockLightLevel, endBlockLightLevel,
                            startSkyBrightness, endSkyBrightness,
                            0.3f * colorScale, 0.5f * colorScale, 0.9f * colorScale,
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
            Vector3f endOffsetFromStart,
            int currentCount, int maxCount,
            float invLengthXZ,
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
            Vector3f midPoint = new Vector3f(
                    endOffsetFromStart.x() * phase,
                    endOffsetFromStart.y() * phase * phase,
                    endOffsetFromStart.z() * phase
            );

            final float width = 0.05f;
            float tilt = 2 * endOffsetFromStart.y() * invLengthXZ * phase;
            float tiltInv = MathHelper.fastInvSqrt(tilt * tilt + 1);
            float yOffset = width * tiltInv * (tiltType ? 1 : -1) / 1.41421356f /*sqrt(2)*/;
            float xBaseOffset = unitLengthX * width * tilt * tiltInv;
            float zBaseOffset = unitLengthZ * width * tilt * tiltInv;
            float xOffset = unitLengthZ * width / 1.41421356f;
            float zOffset = -unitLengthX * width / 1.41421356f;

            if (i == 0) {
                vertexBuilder
                        .vertex(transformMatrix,
                                midPoint.x() + xBaseOffset + xOffset,
                                midPoint.y() + yOffset,
                                midPoint.z() + zBaseOffset + zOffset
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
                vertexBuilder
                        .vertex(transformMatrix,
                                midPoint.x() + xBaseOffset - xOffset,
                                midPoint.y() - yOffset,
                                midPoint.z() + zBaseOffset - zOffset
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
            } else {
                vertexBuilder
                        .vertex(transformMatrix,
                                midPoint.x() + xBaseOffset - xOffset,
                                midPoint.y() - yOffset,
                                midPoint.z() + zBaseOffset - zOffset
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
                vertexBuilder
                        .vertex(transformMatrix,
                                midPoint.x() + xBaseOffset + xOffset,
                                midPoint.y() + yOffset,
                                midPoint.z() + zBaseOffset + zOffset
                        )
                        .color(r, g, b, 1f)
                        .uv2(lightLevel)
                        .endVertex();
            }
        }
    }
}
