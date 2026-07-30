package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.data.Transform;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.alrex.parcool.client.renderer.entity.layers.ParCoolGeneralEquipmentLayer;
import com.alrex.parcool.client.renderer.entity.layers.ParCoolModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public PlayerRendererMixin(EntityRendererProvider.Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
		super(p_174289_, p_174290_, p_174291_);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(EntityRendererProvider.Context context, boolean slim, CallbackInfo ci) {
		this.addLayer(new ParCoolGeneralEquipmentLayer<>(this,
				new HumanoidModel<>(context.bakeLayer(slim ? ParCoolModelLayers.INNER_EQUIPMENT_SLIM : ParCoolModelLayers.INNER_EQUIPMENT)),
				new HumanoidModel<>(context.bakeLayer(slim ? ParCoolModelLayers.OUTER_EQUIPMENT_SLIM : ParCoolModelLayers.OUTER_EQUIPMENT))
		));
	}

	@Inject(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At("HEAD"), cancellable = true)
	protected void onSetupRotationsHead(AbstractClientPlayer player, PoseStack stack, float xRot, float yRot, float partial, CallbackInfo ci) {
		if (player.isFallFlying()) return;
		if (player instanceof IPlayerAnimatorHolder holder) {
			if (player.isLocalPlayer()) {
				if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) return;
			}
			var transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();
			if (transform == null) return;

			var bodyTransformation = transform.transformation().transforms().get(AnimatableModelPart.BODY);
			if (bodyTransformation == null) return;

			if (!transform.isOverwriting()) {
				bodyTransformation = Transform.NO_TRANSFORMATION.morph(bodyTransformation, transform.blendFactor(), true);
			}
			var translation = bodyTransformation.translation();
			stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - yRot));

			var swimAmount = player.getSwimAmount(partial);
			if (transform.isOverwriting() || swimAmount <= 0f) {
				stack.translate(translation.x(), translation.y(), translation.z());
				stack.translate(0, 0.9, 0);
				stack.mulPose(bodyTransformation.rotation());
				stack.translate(0, -0.9, 0);
			} else {
				var swimmingXRot = player.isInWater() || player.isInFluidType((fluidType, height) -> player.canSwimInFluidType(fluidType)) ?
						-90.0f - player.getXRot() : -90.0f;
				var swimmingActualXRot = Mth.lerp(swimAmount, 0.0F, swimmingXRot);

				stack.translate(translation.x(), translation.y(), translation.z());
				stack.translate(0, 0.9 * transform.blendFactor(), 0);
				stack.mulPose((new Transform(Vec3f.ZERO, Vector3f.XP.rotationDegrees(swimmingActualXRot))).morph(bodyTransformation, transform.blendFactor(), true).rotation());
				stack.translate(0, -0.9 * transform.blendFactor(), 0);
				if (player.isVisuallySwimming()) {
					stack.translate(0, -1.0f * (1f - transform.blendFactor()), 0.3f * (1f - transform.blendFactor()));
				}
			}

			ci.cancel();
		}
	}
}
