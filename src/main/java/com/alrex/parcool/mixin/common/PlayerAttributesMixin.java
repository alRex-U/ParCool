package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.handlers.AddAttributesHandler;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Замена EntityAttributeModificationEvent: атрибуты выносливости добавляются игроку. */
@Mixin(Player.class)
public abstract class PlayerAttributesMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void onCreateAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AddAttributesHandler.addPlayerAttributes(cir.getReturnValue());
    }
}
