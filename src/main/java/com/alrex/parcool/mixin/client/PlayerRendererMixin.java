package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.common.capability.impl.Animation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
	public PlayerRendererMixin(EntityRendererProvider.Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
		super(p_174289_, p_174290_, p_174291_);
	}

	@Inject(method = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At("HEAD"))
	protected void onSetupRotations(AbstractClientPlayer player, PoseStack stack, float xRot, float yRot, float zRot, CallbackInfo ci) {
		// arg names may be incorrect
		Animation animation = Animation.get(player);
		if (animation == null) {
			return;
		}
		PlayerModelRotator rotator = new PlayerModelRotator(stack, player, Minecraft.getInstance().getFrameTime());
		animation.applyRotate(player, rotator);
	}
}
