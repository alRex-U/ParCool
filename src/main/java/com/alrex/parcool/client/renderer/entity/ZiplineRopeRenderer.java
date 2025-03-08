package com.alrex.parcool.client.renderer.entity;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
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
    public void render(ZiplineRopeEntity entity, float p_225623_2_, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
        renderLeash(entity, partialTick, matrixStack, renderTypeBuffer);
    }

    private void renderLeash(ZiplineRopeEntity entity, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer) {
        BlockPos start = entity.getStartPos();
        BlockPos end = entity.getEndPos();
        if (start == BlockPos.ZERO && end == BlockPos.ZERO) return;
        matrixStack.pushPose();
        {
            /*
            double lvt_7_1_ = (double)(MathHelper.lerp(partialTick, p_229118_1_.yBodyRot, p_229118_1_.yBodyRotO) * 0.017453292F) + 1.5707963267948966;
            Vector3d lvt_9_1_ = p_229118_1_.getLeashOffset();
            double lvt_10_1_ = Math.cos(lvt_7_1_) * lvt_9_1_.z + Math.sin(lvt_7_1_) * lvt_9_1_.x;
            double lvt_12_1_ = Math.sin(lvt_7_1_) * lvt_9_1_.z - Math.cos(lvt_7_1_) * lvt_9_1_.x;
             */
            float yOffset = (float) (start.getY() - end.getY());
            float ySign = Math.signum(yOffset);
            yOffset *= ySign;
            float xOffset = ySign * (float) (start.getX() - end.getX());
            float zOffset = ySign * (float) (start.getZ() - end.getZ());
            matrixStack.translate(-xOffset / 2.0, 0.5, -zOffset / 2.0);
            IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(RenderType.leash());
            Matrix4f lastPose = matrixStack.last().pose();
            float widthScale = MathHelper.fastInvSqrt(xOffset * xOffset + zOffset * zOffset) * 0.1F / 2.0F;
            float lvt_27_1_ = zOffset * widthScale;
            float lvt_28_1_ = xOffset * widthScale;
            int blockLightLevel = this.getBlockLightLevel(entity, start.below());
            int startSkyBrightness = entity.level.getBrightness(LightType.SKY, start.below());
            int endSkyBrightness = entity.level.getBrightness(LightType.SKY, end.below());
            MobRenderer.renderSide(vertexBuilder, lastPose, xOffset, yOffset, zOffset, blockLightLevel, blockLightLevel, startSkyBrightness, endSkyBrightness, 0.025F, 0.025F, lvt_27_1_, lvt_28_1_);
            MobRenderer.renderSide(vertexBuilder, lastPose, xOffset, yOffset, zOffset, blockLightLevel, blockLightLevel, startSkyBrightness, endSkyBrightness, 0.025F, 0.0F, lvt_27_1_, lvt_28_1_);
        }
        matrixStack.popPose();
    }
}
