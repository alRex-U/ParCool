package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.common.capability.Animation;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
	public PlayerRendererMixin(EntityRendererManager p_i50965_1_, PlayerModel<AbstractClientPlayerEntity> p_i50965_2_, float p_i50965_3_) {
		super(p_i50965_1_, p_i50965_2_, p_i50965_3_);
	}

	@Inject(method = "setupRotations(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lcom/mojang/blaze3d/matrix/MatrixStack;FFF)V", at = @At("RETURN"))
	protected void onSetupRotations(AbstractClientPlayerEntity player, MatrixStack stack, float xRot, float yRot, float zRot, CallbackInfo ci) {
		// arg names may be incorrect
		Animation animation = Animation.get(player);
		if (animation == null) {
			return;
		}
		PlayerModelRotator rotator = new PlayerModelRotator(stack, player, Minecraft.getInstance().getFrameTime());
		animation.applyRotate(player, rotator);
	}
}
