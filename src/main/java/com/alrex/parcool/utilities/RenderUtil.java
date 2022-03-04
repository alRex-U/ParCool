package com.alrex.parcool.utilities;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

//only in Client
public class RenderUtil {
	public static void rotateRightArm(AbstractClientPlayerEntity player, ModelRenderer rightArm, float angleX, float angleY, float angleZ) {
		rightArm.x = -MathHelper.cos((float) Math.toRadians(player.yBodyRot)) * 4.2F;
		rightArm.y = 20.5f;
		rightArm.z = -MathHelper.sin((float) Math.toRadians(player.yBodyRot)) * 5.0F;
		rightArm.xRot = angleX;
		rightArm.yRot = angleY;
		rightArm.zRot = angleZ;
	}

	public static void rotateLeftArm(AbstractClientPlayerEntity player, ModelRenderer leftArm, float angleX, float angleY, float angleZ) {
		leftArm.x = MathHelper.cos((float) Math.toRadians(player.yBodyRot)) * 4.2F;
		leftArm.y = 20.5f;
		leftArm.z = MathHelper.sin((float) Math.toRadians(player.yBodyRot)) * 5.0F;
		leftArm.xRot = angleX;
		leftArm.yRot = angleY;
		leftArm.zRot = angleZ;
	}

	public static void rotateRightLeg(AbstractClientPlayerEntity player, ModelRenderer rightLeg, float angleX, float angleY, float angleZ) {
		rightLeg.xRot = angleX;
		rightLeg.yRot = angleY;
		rightLeg.zRot = angleZ;
	}

	public static void rotateLeftLeg(AbstractClientPlayerEntity player, ModelRenderer leftLeg, float angleX, float angleY, float angleZ) {
		leftLeg.xRot = angleX;
		leftLeg.yRot = angleY;
		leftLeg.zRot = angleZ;
	}

	public static Vector3d getPlayerOffset(PlayerEntity basePlayer, PlayerEntity targetPlayer, float partialTick) {
		Vector3d posTarget = targetPlayer.position();
		Vector3d posBase = basePlayer.position();
		return new Vector3d(
				(targetPlayer.xOld + ((posTarget.x() - targetPlayer.xOld) * partialTick)) - (basePlayer.xOld + ((posBase.x() - basePlayer.xOld) * partialTick)),
				(targetPlayer.yOld + ((posTarget.y() - targetPlayer.yOld) * partialTick)) - (basePlayer.yOld + ((posBase.y() - basePlayer.yOld) * partialTick)),
				(targetPlayer.zOld + ((posTarget.z() - targetPlayer.zOld) * partialTick)) - (basePlayer.zOld + ((posBase.z() - basePlayer.zOld) * partialTick))
		);
	}
}
