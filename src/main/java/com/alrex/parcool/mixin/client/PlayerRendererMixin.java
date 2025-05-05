package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.common.attachment.client.Animation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {

    @Unique
    private PlayerModelRotator parCool$rotator = null;

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }


    @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At("RETURN"))
    protected void onSetupRotationsTail(PlayerRenderState renderState, PoseStack poseStack, float bodyRot, float scale, CallbackInfo ci) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        var entity = level.getEntity(renderState.id);
        if (!(entity instanceof AbstractClientPlayer player)) return;
		// arg names may be incorrect
		Animation animation = Animation.get(player);
        if (parCool$rotator != null) {
            animation.rotatePost(player, parCool$rotator);
            parCool$rotator = null;
        }
    }

    @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At("HEAD"), cancellable = true)
    protected void onSetupRotationsHead(PlayerRenderState renderState, PoseStack poseStack, float bodyRot, float scale, CallbackInfo ci) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        var entity = level.getEntity(renderState.id);
        if (!(entity instanceof AbstractClientPlayer player)) return;
        Animation animation = Animation.get(player);
        parCool$rotator = new PlayerModelRotator(poseStack, player, renderState, renderState.partialTick, renderState.bodyRot);
        if (animation.rotatePre(player, parCool$rotator)) {
            parCool$rotator = null;
            ci.cancel();
        }
	}
}
