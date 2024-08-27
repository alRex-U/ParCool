package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.common.capability.Animation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public PlayerRendererMixin(EntityRendererProvider.Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
		super(p_174289_, p_174290_, p_174291_);
	}

	@Unique
	private PlayerModelRotator parCool$rotator = null;

	@Inject(method = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At("RETURN"))
	protected void onSetupRotationsTail(AbstractClientPlayerEntity player, MatrixStack stack, float xRot, float yRot, float zRot, CallbackInfo ci) {
		// arg names may be incorrect
		Animation animation = Animation.get(player);
		if (animation == null) {
			return;
		}
		if (parCool$rotator != null) {
			animation.rotatePost(player, parCool$rotator);
			parCool$rotator = null;
		}
	}

	@Inject(method = "setupRotations(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lcom/mojang/blaze3d/matrix/MatrixStack;FFF)V", at = @At("HEAD"), cancellable = true)
	protected void onSetupRotationsHead(AbstractClientPlayerEntity player, MatrixStack stack, float xRot, float yRot, float zRot, CallbackInfo ci) {
		Animation animation = Animation.get(player);
		if (animation == null) {
			return;
		}
		parCool$rotator = new PlayerModelRotator(stack, player, Minecraft.getInstance().getFrameTime(), xRot, yRot, zRot);
		if (animation.rotatePre(player, parCool$rotator)) {
			parCool$rotator = null;
			ci.cancel();
		}
	}
}
