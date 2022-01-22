package com.alrex.parcool.client.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class PlayerModelTransformer {
	private AbstractClientPlayerEntity player;
	private PlayerModel<AbstractClientPlayerEntity> model;
	private int tick;
	private float partial;
	private boolean renderRightArm = false, renderLeftArm = false, renderRightLeg = false, renderLeftLeg = false;

	private PlayerModelTransformer(AbstractClientPlayerEntity player, PlayerModel<AbstractClientPlayerEntity> model, int tick, float partial) {
		this.player = player;
		this.model = model;
		this.partial = partial;
		this.tick = tick;
	}

	public static PlayerModelTransformer wrap(AbstractClientPlayerEntity player, PlayerModel<AbstractClientPlayerEntity> model, int tick, float partialTick) {
		return new PlayerModelTransformer(player, model, tick, partialTick);
	}

	public PlayerModelTransformer rotateRightArm(float angleX, float angleY, float angleZ) {
		ModelRenderer rightArm = model.bipedRightArm;
		ModelRenderer rightWear = model.bipedRightArmwear;
		setRightArm(player, rightArm, angleX, angleY, angleZ);
		setRightArm(player, rightWear, angleX, angleY, angleZ);
		renderRightArm = true;
		return this;
	}

	public PlayerModelTransformer rotateLeftArm(float angleX, float angleY, float angleZ) {
		ModelRenderer leftArm = model.bipedLeftArm;
		ModelRenderer leftWear = model.bipedLeftArmwear;
		setLeftArm(player, leftArm, angleX, angleY, angleZ);
		setLeftArm(player, leftWear, angleX, angleY, angleZ);
		renderLeftArm = true;
		return this;
	}

	public PlayerModelTransformer rotateRightLeg(float angleX, float angleY, float angleZ) {
		ModelRenderer rightLeg = model.bipedRightLeg;
		ModelRenderer rightWear = model.bipedRightLegwear;
		setRightLeg(player, rightLeg, angleX, angleY, angleZ);
		setRightLeg(player, rightWear, angleX, angleY, angleZ);
		renderRightLeg = true;
		return this;
	}

	public PlayerModelTransformer rotateLeftLeg(float angleX, float angleY, float angleZ) {
		ModelRenderer leftLeg = model.bipedLeftLeg;
		ModelRenderer leftWear = model.bipedLeftLegwear;
		setLeftLeg(player, leftLeg, angleX, angleY, angleZ);
		setLeftLeg(player, leftWear, angleX, angleY, angleZ);
		renderLeftLeg = true;
		return this;
	}

	public PlayerModelTransformer render(MatrixStack stack, IRenderTypeBuffer buffer, PlayerRenderer renderer) {
		ResourceLocation location = player.getLocationSkin();
		int light = renderer.getPackedLight(player, partial);
		renderer.getRenderManager().textureManager.bindTexture(location);
		if (renderRightArm) {
			model.bipedRightArm.showModel = true;
			model.bipedRightArmwear.showModel = true;
			model.bipedRightArm.render(
					stack,
					buffer.getBuffer(RenderType.getEntitySolid(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedRightArmwear.render(
					stack,
					buffer.getBuffer(RenderType.getEntityTranslucent(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedRightArm.showModel = false;
			model.bipedRightArmwear.showModel = false;
		}
		if (renderLeftArm) {
			model.bipedLeftArm.showModel = true;
			model.bipedLeftArmwear.showModel = true;
			model.bipedLeftArm.render(
					stack,
					buffer.getBuffer(RenderType.getEntitySolid(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedLeftArmwear.render(
					stack,
					buffer.getBuffer(RenderType.getEntityTranslucent(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedLeftArm.showModel = false;
			model.bipedLeftArmwear.showModel = false;
		}
		if (renderRightLeg) {
			model.bipedRightLeg.showModel = true;
			model.bipedRightLegwear.showModel = true;
			model.bipedRightLeg.render(
					stack,
					buffer.getBuffer(RenderType.getEntitySolid(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedRightLegwear.render(
					stack,
					buffer.getBuffer(RenderType.getEntityTranslucent(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);

			model.bipedRightLeg.rotationPointX = 2.1F;
			model.bipedRightLeg.rotationPointZ = 0;
			model.bipedRightLeg.showModel = false;
			model.bipedRightLegwear.showModel = false;
		}
		if (renderLeftLeg) {
			model.bipedLeftLeg.showModel = true;
			model.bipedLeftLegwear.showModel = true;
			model.bipedLeftLeg.render(
					stack,
					buffer.getBuffer(RenderType.getEntitySolid(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedLeftLegwear.render(
					stack,
					buffer.getBuffer(RenderType.getEntityTranslucent(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.bipedLeftLeg.rotationPointX = -2.1F;
			model.bipedLeftLeg.rotationPointZ = 0;
			model.bipedLeftLeg.showModel = false;
			model.bipedLeftLegwear.showModel = false;
		}
		return this;
	}

	private static void setRightArm(AbstractClientPlayerEntity player, ModelRenderer rightArm, float angleX, float angleY, float angleZ) {
		rightArm.rotationPointX = -MathHelper.cos((float) Math.toRadians(player.renderYawOffset)) * 4.2F;
		rightArm.rotationPointY = 20.5f;
		rightArm.rotationPointZ = -MathHelper.sin((float) Math.toRadians(player.renderYawOffset)) * 5.0F;
		rightArm.rotateAngleX = angleX;
		rightArm.rotateAngleY = angleY;
		rightArm.rotateAngleZ = angleZ;
	}

	private static void setLeftArm(AbstractClientPlayerEntity player, ModelRenderer leftArm, float angleX, float angleY, float angleZ) {
		leftArm.rotationPointX = MathHelper.cos((float) Math.toRadians(player.renderYawOffset)) * 4.2F;
		leftArm.rotationPointY = 20.5f;
		leftArm.rotationPointZ = MathHelper.sin((float) Math.toRadians(player.renderYawOffset)) * 5.0F;
		leftArm.rotateAngleX = angleX;
		leftArm.rotateAngleY = angleY;
		leftArm.rotateAngleZ = angleZ;
	}

	private static void setRightLeg(AbstractClientPlayerEntity player, ModelRenderer rightLeg, float angleX, float angleY, float angleZ) {
		rightLeg.rotationPointX = -MathHelper.cos((float) Math.toRadians(player.renderYawOffset)) * 2.1F;
		rightLeg.rotationPointZ = -MathHelper.sin((float) Math.toRadians(player.renderYawOffset)) * 2.5F;
		rightLeg.rotateAngleX = angleX;
		rightLeg.rotateAngleY = angleY;
		rightLeg.rotateAngleZ = angleZ;
	}

	private static void setLeftLeg(AbstractClientPlayerEntity player, ModelRenderer leftLeg, float angleX, float angleY, float angleZ) {
		leftLeg.rotationPointX = MathHelper.cos((float) Math.toRadians(player.renderYawOffset)) * 2.1F;
		leftLeg.rotationPointZ = MathHelper.sin((float) Math.toRadians(player.renderYawOffset)) * 2.5F;
		leftLeg.rotateAngleX = angleX;
		leftLeg.rotateAngleY = angleY;
		leftLeg.rotateAngleZ = angleZ;
	}
}
