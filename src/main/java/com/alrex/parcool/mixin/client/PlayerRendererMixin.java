package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.compatibility.AbstractClientPlayerWrapper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
	public PlayerRendererMixin(EntityRendererManager p_i50965_1_, PlayerModel<AbstractClientPlayerEntity> p_i50965_2_, float p_i50965_3_) {
		super(p_i50965_1_, p_i50965_2_, p_i50965_3_);
	}

	@Unique
	private PlayerModelRotator parCool$rotator = null;

	@Inject(method = "setupRotations(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lcom/mojang/blaze3d/matrix/MatrixStack;FFF)V", at = @At("TAIL"))
	protected void onSetupRotationsTail(AbstractClientPlayerEntity player, MatrixStack stack, float xRot, float yRot, float zRot, CallbackInfo ci) {
		AbstractClientPlayerWrapper playerWrapper = AbstractClientPlayerWrapper.get(player);
		// arg names may be incorrect
		Animation animation = Animation.get(playerWrapper);
		if (animation == null) {
			return;
		}
		if (parCool$rotator != null) {
			animation.rotatePost(playerWrapper, parCool$rotator);
			parCool$rotator = null;
		}
	}

	@Inject(method = "setupRotations(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lcom/mojang/blaze3d/matrix/MatrixStack;FFF)V", at = @At("HEAD"), cancellable = true)
	protected void onSetupRotationsHead(AbstractClientPlayerEntity player, MatrixStack stack, float xRot, float yRot, float zRot, CallbackInfo ci) {
		AbstractClientPlayerWrapper playerWrapper = AbstractClientPlayerWrapper.get(player);
		Animation animation = Animation.get(playerWrapper);
		if (animation == null) {
			return;
		}
		parCool$rotator = new PlayerModelRotator(stack, playerWrapper, Minecraft.getInstance().getFrameTime(), xRot, yRot, zRot);
		if (animation.rotatePre(playerWrapper, parCool$rotator)) {
			parCool$rotator = null;
			ci.cancel();
		}
	}
}
