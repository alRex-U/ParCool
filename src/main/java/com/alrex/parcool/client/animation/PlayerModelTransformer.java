package com.alrex.parcool.client.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class PlayerModelTransformer {
	private AbstractClientPlayerEntity player;
	private PlayerModel<AbstractClientPlayerEntity> model;
	private int tick;
	private float partial;
	private boolean
			renderRightArm = false,
			renderLeftArm = false,
			renderRightLeg = false,
			renderLeftLeg = false,
			renderRightSleeve = false,
			renderLeftSleeve = false,
			renderRightPants = false,
			renderLeftPants = false;

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
		ModelRenderer rightArm = model.rightArm;
		ModelRenderer rightWear = model.rightSleeve;
		if (rightArm.visible) {
			setRightArm(player, rightArm, angleX, angleY, angleZ);
			renderRightArm = true;
		}
		if (rightWear.visible) {
			setRightArm(player, rightWear, angleX, angleY, angleZ);
			renderRightSleeve = true;
		}
		return this;
	}

	public PlayerModelTransformer rotateLeftArm(float angleX, float angleY, float angleZ) {
		ModelRenderer leftArm = model.leftArm;
		ModelRenderer leftWear = model.leftSleeve;
		if (leftArm.visible) {
			setLeftArm(player, leftArm, angleX, angleY, angleZ);
			renderLeftArm = true;
		}
		if (leftWear.visible) {
			setLeftArm(player, leftWear, angleX, angleY, angleZ);
			renderLeftSleeve = true;
		}
		return this;
	}

	public PlayerModelTransformer rotateRightLeg(float angleX, float angleY, float angleZ) {
		ModelRenderer rightLeg = model.rightLeg;
		ModelRenderer rightWear = model.rightPants;
		if (rightLeg.visible) {
			setRightLeg(player, rightLeg, angleX, angleY, angleZ);
			renderRightLeg = true;
		}
		if (rightWear.visible) {
			setRightLeg(player, rightWear, angleX, angleY, angleZ);
			renderRightPants = true;
		}
		return this;
	}

	public PlayerModelTransformer rotateLeftLeg(float angleX, float angleY, float angleZ) {
		ModelRenderer leftLeg = model.leftLeg;
		ModelRenderer leftWear = model.leftPants;
		if (leftLeg.visible) {
			setLeftLeg(player, leftLeg, angleX, angleY, angleZ);
			renderLeftLeg = true;
		}
		if (leftWear.visible) {
			setLeftLeg(player, leftWear, angleX, angleY, angleZ);
			renderLeftPants = true;
		}
		return this;
	}

	public PlayerModelTransformer render(MatrixStack stack, IRenderTypeBuffer buffer, PlayerRenderer renderer) {
		ResourceLocation location = player.getSkinTextureLocation();
		int light = renderer.getPackedLightCoords(player, partial);
		renderer.getDispatcher().textureManager.bind(location);
		boolean rightHandHoldingItem;
		boolean leftHandHoldingItem;
		if (player.getMainArm() == HandSide.RIGHT) {
			rightHandHoldingItem = !player.getMainHandItem().isEmpty();
			leftHandHoldingItem = !player.getOffhandItem().isEmpty();
		} else {
			rightHandHoldingItem = !player.getOffhandItem().isEmpty();
			leftHandHoldingItem = !player.getMainHandItem().isEmpty();
		}
		if (renderRightArm && !rightHandHoldingItem) {
			model.rightArm.visible = true;
			model.rightSleeve.visible = true;
			model.rightArm.render(
					stack,
					buffer.getBuffer(RenderType.entitySolid(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.rightSleeve.render(
					stack,
					buffer.getBuffer(RenderType.entityTranslucent(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.rightArm.visible = false;
			model.rightSleeve.visible = false;
		}
		if (renderLeftArm && !leftHandHoldingItem) {
			model.leftArm.visible = true;
			model.leftSleeve.visible = true;
			model.leftArm.render(
					stack,
					buffer.getBuffer(RenderType.entitySolid(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.leftSleeve.render(
					stack,
					buffer.getBuffer(RenderType.entityTranslucent(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.leftArm.visible = false;
			model.leftSleeve.visible = false;
		}
		if (renderRightLeg) {
			model.rightLeg.visible = true;
			model.rightPants.visible = true;
			model.rightLeg.render(
					stack,
					buffer.getBuffer(RenderType.entitySolid(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.rightPants.render(
					stack,
					buffer.getBuffer(RenderType.entityTranslucent(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);

			model.rightLeg.x = -1.95F;
			model.rightLeg.z = 0;
			model.rightLeg.visible = false;
			model.rightPants.visible = false;
		}
		if (renderLeftLeg) {
			model.leftLeg.visible = true;
			model.leftPants.visible = true;
			model.leftLeg.render(
					stack,
					buffer.getBuffer(RenderType.entitySolid(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.leftPants.render(
					stack,
					buffer.getBuffer(RenderType.entityTranslucent(location)),
					light,
					OverlayTexture.NO_OVERLAY
			);
			model.leftLeg.x = 1.95F;
			model.leftLeg.z = 0;
			model.leftLeg.visible = false;
			model.leftPants.visible = false;
		}
		return this;
	}

	private void setRightArm(AbstractClientPlayerEntity player, ModelRenderer rightArm, float angleX, float angleY, float angleZ) {
		float rot = MathHelper.lerp(partial, player.yBodyRotO, player.yBodyRot);
		rightArm.x = -MathHelper.cos((float) Math.toRadians(rot)) * 4.2F;
		rightArm.y = 20.5f;
		rightArm.z = -MathHelper.sin((float) Math.toRadians(rot)) * 5.0F;
		rightArm.xRot = angleX;
		rightArm.yRot = angleY;
		rightArm.zRot = angleZ;
	}

	private void setLeftArm(AbstractClientPlayerEntity player, ModelRenderer leftArm, float angleX, float angleY, float angleZ) {
		float rot = MathHelper.lerp(partial, player.yBodyRotO, player.yBodyRot);
		leftArm.x = MathHelper.cos((float) Math.toRadians(rot)) * 4.2F;
		leftArm.y = 20.5f;
		leftArm.z = MathHelper.sin((float) Math.toRadians(rot)) * 5.0F;
		leftArm.xRot = angleX;
		leftArm.yRot = angleY;
		leftArm.zRot = angleZ;
	}

	private void setRightLeg(AbstractClientPlayerEntity player, ModelRenderer rightLeg, float angleX, float angleY, float angleZ) {
		float rot = MathHelper.lerp(partial, player.yBodyRotO, player.yBodyRot);
		rightLeg.x = -MathHelper.cos((float) Math.toRadians(rot)) * 1.95F;
		rightLeg.z = -MathHelper.sin((float) Math.toRadians(rot)) * 1.95F;
		rightLeg.xRot = angleX;
		rightLeg.yRot = angleY;
		rightLeg.zRot = angleZ;
	}

	private void setLeftLeg(AbstractClientPlayerEntity player, ModelRenderer leftLeg, float angleX, float angleY, float angleZ) {
		float rot = MathHelper.lerp(partial, player.yBodyRotO, player.yBodyRot);
		leftLeg.x = MathHelper.cos((float) Math.toRadians(rot)) * 1.95F;
		leftLeg.z = MathHelper.sin((float) Math.toRadians(rot)) * 1.95F;
		leftLeg.xRot = angleX;
		leftLeg.yRot = angleY;
		leftLeg.zRot = angleZ;
	}
}
