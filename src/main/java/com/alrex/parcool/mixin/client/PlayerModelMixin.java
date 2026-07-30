package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.system.AnimatableModelPart;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.data.Transform;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> {

	public PlayerModelMixin(ModelPart p_i1148_1_) {
		super(p_i1148_1_);
	}

	@Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"), cancellable = true)
	protected void onSetupAnimHead(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
		if (entity.isFallFlying()) return;
		if (entity instanceof IPlayerAnimatorHolder holder) {
			var transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();
			if (transform == null) return;
			parcool$resetModel();
			if (transform.isOverwriting()) {
				var headTransform = transform.transformation().transforms().get(AnimatableModelPart.HEAD);
				if (headTransform != null) headTransform.apply(head);
				var rATransform = transform.transformation().transforms().get(AnimatableModelPart.RIGHT_ARM);
				if (rATransform != null) rATransform.apply(rightArm);
				var rLTransform = transform.transformation().transforms().get(AnimatableModelPart.RIGHT_LEG);
				if (rLTransform != null) rLTransform.apply(rightLeg);
				var lATransform = transform.transformation().transforms().get(AnimatableModelPart.LEFT_ARM);
				if (lATransform != null) lATransform.apply(leftArm);
				var lLTransform = transform.transformation().transforms().get(AnimatableModelPart.LEFT_LEG);
				if (lLTransform != null) lLTransform.apply(leftLeg);
				info.cancel();
			}
		}
	}

	@Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
	protected void onSetupAnimTail(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
		if (entity.isFallFlying()) return;
		if (entity instanceof IPlayerAnimatorHolder holder) {
			var transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();
			if (transform == null) return;
			var blendingFactor = transform.blendFactor();
			var headTransform = transform.transformation().transforms().get(AnimatableModelPart.HEAD);
			if (headTransform != null) headTransform.applyInQuaternion(head, blendingFactor);
			var rATransform = transform.transformation().transforms().get(AnimatableModelPart.RIGHT_ARM);
			if (rATransform != null) rATransform.applyInQuaternion(rightArm, blendingFactor);
			var rLTransform = transform.transformation().transforms().get(AnimatableModelPart.RIGHT_LEG);
			if (rLTransform != null) rLTransform.applyInQuaternion(rightLeg, blendingFactor);
			var lATransform = transform.transformation().transforms().get(AnimatableModelPart.LEFT_ARM);
			if (lATransform != null) lATransform.applyInQuaternion(leftArm, blendingFactor);
			var lLTransform = transform.transformation().transforms().get(AnimatableModelPart.LEFT_LEG);
			if (lLTransform != null) lLTransform.applyInQuaternion(leftLeg, blendingFactor);
			Transform.NO_TRANSFORMATION.applyInQuaternion(body, blendingFactor);
		}
	}

	@Unique
	public void parcool$resetModel() {
		for (var part : this.bodyParts()) {
			part.resetPose();
		}
		for (var part : this.headParts()) {
			part.resetPose();
		}
	}
}
