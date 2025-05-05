package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> {
    protected LivingRendererMixin(EntityRendererProvider.Context p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z", at = @At("HEAD"), cancellable = true)
    protected void onShouldShowName(T entity, double distanceToCameraSq, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player player) {
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability.getBehaviorEnforcer().cancelShowingName()) {
                cir.setReturnValue(false);
            }
        }
    }

}
