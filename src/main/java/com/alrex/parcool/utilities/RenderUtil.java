package com.alrex.parcool.utilities;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderUtil {
    public static void rotateRightArm(AbstractClientPlayerEntity player, ModelRenderer rightArm, float angleX, float angleY, float angleZ) {
        rightArm.rotationPointX = -MathHelper.cos((float) Math.toRadians(player.renderYawOffset)) * 4.2F;
        rightArm.rotationPointY = 20.5f;
        rightArm.rotationPointZ = -MathHelper.sin((float) Math.toRadians(player.renderYawOffset)) * 5.0F;
        rightArm.rotateAngleX = angleX;
        rightArm.rotateAngleY = angleY;
        rightArm.rotateAngleZ = angleZ;
    }

    public static void rotateLeftArm(AbstractClientPlayerEntity player, ModelRenderer leftArm, float angleX, float angleY, float angleZ) {
        leftArm.rotationPointX = MathHelper.cos((float) Math.toRadians(player.renderYawOffset)) * 4.2F;
        leftArm.rotationPointY = 20.5f;
        leftArm.rotationPointZ = MathHelper.sin((float) Math.toRadians(player.renderYawOffset)) * 5.0F;
        leftArm.rotateAngleX = angleX;
        leftArm.rotateAngleY = angleY;
        leftArm.rotateAngleZ = angleZ;
    }

    public static void rotateRightLeg(AbstractClientPlayerEntity player, ModelRenderer rightLeg, float angleX, float angleY, float angleZ) {
        rightLeg.rotateAngleX = angleX;
        rightLeg.rotateAngleY = angleY;
        rightLeg.rotateAngleZ = angleZ;
    }

    public static void rotateLeftLeg(AbstractClientPlayerEntity player, ModelRenderer leftLeg, float angleX, float angleY, float angleZ) {
        leftLeg.rotateAngleX = angleX;
        leftLeg.rotateAngleY = angleY;
        leftLeg.rotateAngleZ = angleZ;
    }

    public static Vector3d getPlayerOffset(PlayerEntity basePlayer, PlayerEntity targetPlayer, float partialTick) {
        return new Vector3d(
                (basePlayer.lastTickPosX + ((basePlayer.getPosX() - basePlayer.lastTickPosX) * partialTick)) - (targetPlayer.lastTickPosX + ((targetPlayer.getPosX() - targetPlayer.lastTickPosX) * partialTick)),
                (basePlayer.lastTickPosY + ((basePlayer.getPosY() - basePlayer.lastTickPosY) * partialTick)) - (targetPlayer.lastTickPosY + ((targetPlayer.getPosY() - targetPlayer.lastTickPosY) * partialTick)),
                (basePlayer.lastTickPosZ + ((basePlayer.getPosZ() - basePlayer.lastTickPosZ) * partialTick)) - (targetPlayer.lastTickPosZ + ((targetPlayer.getPosZ() - targetPlayer.lastTickPosZ) * partialTick))
        );
    }
}
