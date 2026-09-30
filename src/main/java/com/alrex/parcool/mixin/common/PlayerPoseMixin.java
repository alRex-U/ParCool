package com.alrex.parcool.mixin.common;

import com.alrex.parcool.fabric.ForcedPoseHolder;

import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Замена Entity#forcedPose из NeoForge: ParCool так держит позу для скольжения и ползания. */
@Mixin(Player.class)
public abstract class PlayerPoseMixin {
    @Inject(method = "updatePlayerPose", at = @At("HEAD"), cancellable = true)
    private void onUpdatePlayerPose(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        var forced = ((ForcedPoseHolder) player).parCool$getForcedPose();
        if (forced != null) {
            player.setPose(forced);
            ci.cancel();
        }
    }
}
