package com.alrex.parcool.client.animation;

import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Using Radians
 */
public class PlayerModelTransformer {
	private final PlayerEntity player;
	private final PlayerModel model;
	private final boolean slim;
	private final float partial;
	private final float ageInTicks;
	private final float limbSwing;
	private final float limbSwingAmount;
	private final float netHeadYaw;
	private final float headPitch;

	public float getPartialTick() {
		return partial;
	}

	public float getHeadPitch() {
		return headPitch;
	}

	public float getNetHeadYaw() {
		return netHeadYaw;
	}

	public float getLimbSwing() {
		return limbSwing;
	}

	public float getLimbSwingAmount() {
		return limbSwingAmount;
	}

	public PlayerModel getRawModel() {
		return model;
	}

	public PlayerModelTransformer(
			PlayerEntity player,
			PlayerModel model,
			boolean slim,
			float ageInTicks,
			float limbSwing,
			float limbSwingAmount,
			float netHeadYaw,
			float headPitch
	) {
		this.player = player;
		this.model = model;
		this.slim = slim;
		this.partial = Minecraft.getInstance().getFrameTime();
		this.ageInTicks = ageInTicks;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.netHeadYaw = netHeadYaw;
		this.headPitch = headPitch;
	}

	/**
	 * @param angleX swing arm frontward or backward
	 * @param angleY rotate arm around
	 * @param angleZ swing arm upward or downward
	 */
	public PlayerModelTransformer rotateRightArm(float angleX, float angleY, float angleZ) {
		ModelRenderer rightArm = model.rightArm;
		if (rightArm.visible) {
			setRotations(rightArm, angleX, angleY, angleZ);
		}
		return this;
	}

	public PlayerModelTransformer rotateRightArm(float angleX, float angleY, float angleZ, float factor) {
		ModelRenderer rightArm = model.rightArm;
		if (rightArm.visible) {
			setRotations(rightArm,
					MathUtil.lerp(rightArm.xRot, angleX, factor),
					MathUtil.lerp(rightArm.yRot, angleY, factor),
					MathUtil.lerp(rightArm.zRot, angleZ, factor)
			);
		}
		return this;
	}

	/**
	 * @param angleX swing arm frontward or backward
	 * @param angleY rotate arm around
	 * @param angleZ swing arm upward or downward
	 */
	public PlayerModelTransformer rotateLeftArm(float angleX, float angleY, float angleZ) {
		ModelRenderer leftArm = model.leftArm;
		if (leftArm.visible) {
			setRotations(leftArm, angleX, angleY, angleZ);
		}
		return this;
	}

	public PlayerModelTransformer rotateLeftArm(float angleX, float angleY, float angleZ, float factor) {
		ModelRenderer leftArm = model.leftArm;
		if (leftArm.visible) {
			setRotations(leftArm,
					MathUtil.lerp(leftArm.xRot, angleX, factor),
					MathUtil.lerp(leftArm.yRot, angleY, factor),
					MathUtil.lerp(leftArm.zRot, angleZ, factor)
			);
		}
		return this;
	}

	/**
	 * @param angleX swing leg frontward or backward
	 * @param angleY rotate leg around
	 * @param angleZ swing leg upward or downward
	 */
	public PlayerModelTransformer rotateRightLeg(float angleX, float angleY, float angleZ) {
		ModelRenderer rightLeg = model.rightLeg;
		if (rightLeg.visible) {
			setRotations(rightLeg, angleX, angleY, angleZ);
		}
		return this;
	}

	public PlayerModelTransformer rotateRightLeg(float angleX, float angleY, float angleZ, float factor) {
		ModelRenderer rightLeg = model.rightLeg;
		if (rightLeg.visible) {
			setRotations(rightLeg,
					MathUtil.lerp(rightLeg.xRot, angleX, factor),
					MathUtil.lerp(rightLeg.yRot, angleY, factor),
					MathUtil.lerp(rightLeg.zRot, angleZ, factor)
			);
		}
		return this;
	}

	/**
	 * @param angleX swing leg frontward or backward
	 * @param angleY rotate leg around
	 * @param angleZ swing leg upward or downward
	 */
	public PlayerModelTransformer rotateLeftLeg(float angleX, float angleY, float angleZ) {
		ModelRenderer leftLeg = model.leftLeg;
		if (leftLeg.visible) {
			setRotations(leftLeg, angleX, angleY, angleZ);
		}
		return this;
	}

	public PlayerModelTransformer rotateLeftLeg(float angleX, float angleY, float angleZ, float factor) {
		ModelRenderer leftLeg = model.leftLeg;
		if (leftLeg.visible) {
			setRotations(leftLeg,
					MathUtil.lerp(leftLeg.xRot, angleX, factor),
					MathUtil.lerp(leftLeg.yRot, angleY, factor),
					MathUtil.lerp(leftLeg.zRot, angleZ, factor)
			);
		}
		return this;
	}

	public PlayerModelTransformer addRotateRightArm(float angleX, float angleY, float angleZ) {
		ModelRenderer arm = model.rightArm;
		if (arm.visible) {
			setRotations(arm, arm.xRot + angleX, arm.yRot + angleY, arm.zRot + angleZ);
		}
		return this;
	}

	public PlayerModelTransformer addRotateLeftArm(float angleX, float angleY, float angleZ) {
		ModelRenderer arm = model.leftArm;
		if (arm.visible) {
			setRotations(arm, arm.xRot + angleX, arm.yRot + angleY, arm.zRot + angleZ);
		}
		return this;
	}

	public PlayerModelTransformer addRotateRightLeg(float angleX, float angleY, float angleZ) {
		ModelRenderer leg = model.rightLeg;
		if (leg.visible) {
			setRotations(leg, leg.xRot + angleX, leg.yRot + angleY, leg.zRot + angleZ);
		}
		return this;
	}

	public PlayerModelTransformer addRotateLeftLeg(float angleX, float angleY, float angleZ) {
		ModelRenderer leg = model.leftLeg;
		if (leg.visible) {
			setRotations(leg, leg.xRot + angleX, leg.yRot + angleY, leg.zRot + angleZ);
		}
		return this;
	}

	public PlayerModelTransformer addRotateRightArm(float angleX, float angleY, float angleZ, float factor) {
		return addRotateRightArm(angleX * factor, angleY * factor, angleZ * factor);
	}

	public PlayerModelTransformer addRotateLeftArm(float angleX, float angleY, float angleZ, float factor) {
		return addRotateLeftArm(angleX * factor, angleY * factor, angleZ * factor);
	}

	public PlayerModelTransformer addRotateRightLeg(float angleX, float angleY, float angleZ, float factor) {
		return addRotateRightLeg(angleX * factor, angleY * factor, angleZ * factor);
	}

	public PlayerModelTransformer addRotateLeftLeg(float angleX, float angleY, float angleZ, float factor) {
		return addRotateLeftLeg(angleX * factor, angleY * factor, angleZ * factor);
	}

	public PlayerModelTransformer makeArmsNatural() {
		ModelHelper.bobArms(model.rightArm, model.leftArm, ageInTicks);
		return this;
	}

	public PlayerModelTransformer makeLegsMoveDynamically(float factor) {
		model.rightLeg.zRot += MathHelper.cos(ageInTicks * 0.56F) * 0.8F * factor + 0.05F;
		model.leftLeg.zRot -= MathHelper.cos(ageInTicks * 0.56F) * 0.8F * factor + 0.05F;
		model.rightLeg.xRot += MathHelper.sin(ageInTicks * 0.56F) * 0.8F * factor;
		model.leftLeg.xRot -= MathHelper.sin(ageInTicks * 0.56F) * 0.8F * factor;
		return this;
	}

	public PlayerModelTransformer makeArmsMoveDynamically(float factor) {
		model.rightArm.zRot += MathHelper.cos(ageInTicks * 0.56F) * 0.8F * factor + 0.05F;
		model.leftArm.zRot -= MathHelper.cos(ageInTicks * 0.56F) * 0.8F * factor + 0.05F;
		model.rightArm.xRot += MathHelper.sin(ageInTicks * 0.56F) * 0.8F * factor;
		model.leftArm.xRot -= MathHelper.sin(ageInTicks * 0.56F) * 0.8F * factor;
		return this;
	}

	public PlayerModelTransformer makeLegsLittleMoving() {
		ModelHelper.bobArms(model.rightLeg, model.leftLeg, ageInTicks);
		return this;
	}

	public PlayerModelTransformer makeLegsShakingDynamically(float factor) {
		model.rightLeg.zRot += MathHelper.cos(ageInTicks * 0.56F) * 0.8F * factor + 0.05F;
		model.leftLeg.zRot += MathHelper.cos(ageInTicks * 0.56F) * 0.8F * factor + 0.05F;
		model.rightLeg.xRot += MathHelper.sin(ageInTicks * 0.56F) * 0.2F * factor;
		model.leftLeg.xRot -= MathHelper.sin(ageInTicks * 0.56F) * 0.2F * factor;
		return this;
	}

	public PlayerModelTransformer rotateAdditionallyHeadPitch(float pitchDegree) {
		model.head.xRot = (float) Math.toRadians(pitchDegree + headPitch);
		return this;
	}

	public PlayerModelTransformer rotateHeadPitch(float pitchDegree) {
		model.head.xRot = (float) Math.toRadians(pitchDegree);
		return this;
	}

	public void end() {
	}

	public PlayerModelTransformer rotateAdditionallyHeadYaw(float yawDegree) {
		model.head.yRot = (float) Math.toRadians(yawDegree + netHeadYaw);
		return this;
	}

	public PlayerModelTransformer rotateAdditionallyHeadRoll(float yawDegree) {
		model.head.zRot = (float) Math.toRadians(yawDegree + netHeadYaw);
		return this;
	}

	public void copyFromBodyToWear() {
		model.rightSleeve.copyFrom(model.rightArm);
		model.leftSleeve.copyFrom(model.leftArm);
		model.rightPants.copyFrom(model.rightLeg);
		model.leftPants.copyFrom(model.leftLeg);
		model.jacket.copyFrom(model.body);
		model.hat.copyFrom(model.head);
	}

	private void setRotations(ModelRenderer renderer, float angleX, float angleY, float angleZ) {
		renderer.xRot = angleX;
		renderer.yRot = angleY;
		renderer.zRot = angleZ;
	}

	public void reset() {
		resetModel(model.head);
		resetModel(model.hat);
		resetModel(model.jacket);
		resetModel(model.body);
		{
			resetModel(model.rightArm);
			model.rightArm.x = 5.2F;
			model.rightArm.y = this.slim ? 2.5F : 2.0F;
			model.rightArm.z = 0.0F;
			model.rightSleeve.copyFrom(model.rightArm);
		}
		{
			resetModel(model.leftArm);
			model.leftArm.x = 2.5F;
			model.leftArm.y = this.slim ? 2.5F : 2.0F;
			model.leftArm.z = 0.0F;

			model.leftSleeve.copyFrom(model.leftArm);
		}
		{
			resetModel(model.leftLeg);
			model.leftLeg.x = 1.9F;
			model.leftLeg.y = 12.0F;
			model.leftLeg.z = 0.0F;

			model.leftPants.copyFrom(model.leftLeg);
		}
		{
			resetModel(model.rightLeg);
			model.rightLeg.x = -1.9F;
			model.rightLeg.y = 12.0F;
			model.rightLeg.z = 0.0F;

			model.rightPants.copyFrom(model.rightLeg);
		}
	}

	public void resetModel(ModelRenderer model) {
		model.xRot = 0;
		model.yRot = 0;
		model.zRot = 0;
		model.x = 0;
		model.y = 0;
		model.z = 0;
	}
}
