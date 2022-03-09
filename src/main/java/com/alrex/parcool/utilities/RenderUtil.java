package com.alrex.parcool.utilities;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

//only in Client
public class RenderUtil {
	public static void rotateRightArm(AbstractClientPlayer player, ModelPart rightArm, float angleX, float angleY, float angleZ) {
		rightArm.x = -Mth.cos((float) Math.toRadians(player.yBodyRot)) * 4.2F;
		rightArm.y = 20.5f;
		rightArm.z = -Mth.sin((float) Math.toRadians(player.yBodyRot)) * 5.0F;
		rightArm.xRot = angleX;
		rightArm.yRot = angleY;
		rightArm.zRot = angleZ;
	}

	public static void rotateLeftArm(AbstractClientPlayer player, ModelPart leftArm, float angleX, float angleY, float angleZ) {
		leftArm.x = Mth.cos((float) Math.toRadians(player.yBodyRot)) * 4.2F;
		leftArm.y = 20.5f;
		leftArm.z = Mth.sin((float) Math.toRadians(player.yBodyRot)) * 5.0F;
		leftArm.xRot = angleX;
		leftArm.yRot = angleY;
		leftArm.zRot = angleZ;
	}

	public static void rotateRightLeg(AbstractClientPlayer player, ModelPart rightLeg, float angleX, float angleY, float angleZ) {
		rightLeg.xRot = angleX;
		rightLeg.yRot = angleY;
		rightLeg.zRot = angleZ;
	}

	public static void rotateLeftLeg(AbstractClientPlayer player, ModelPart leftLeg, float angleX, float angleY, float angleZ) {
		leftLeg.xRot = angleX;
		leftLeg.yRot = angleY;
		leftLeg.zRot = angleZ;
	}

	public static Vec3 getPlayerOffset(Player basePlayer, Player targetPlayer, float partialTick) {
		Vec3 posTarget = targetPlayer.position();
		Vec3 posBase = basePlayer.position();
		return new Vec3(
				(targetPlayer.xOld + ((posTarget.x() - targetPlayer.xOld) * partialTick)) - (basePlayer.xOld + ((posBase.x() - basePlayer.xOld) * partialTick)),
				(targetPlayer.yOld + ((posTarget.y() - targetPlayer.yOld) * partialTick)) - (basePlayer.yOld + ((posBase.y() - basePlayer.yOld) * partialTick)),
				(targetPlayer.zOld + ((posTarget.z() - targetPlayer.zOld) * partialTick)) - (basePlayer.zOld + ((posBase.z() - basePlayer.zOld) * partialTick))
		);
	}
}
