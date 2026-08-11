package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.Parkourability;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    @Shadow
    public int sprintTime;

    @Shadow
    public abstract void setSprinting(boolean p_108751_);

    public LocalPlayerMixin(ClientLevel p_234112_, GameProfile p_234113_, @Nullable ProfilePublicKey p_234114_) {
        super(p_234112_, p_234113_, p_234114_);
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void onMove(MoverType moverType, Vec3 movement, CallbackInfo ci) {
        var player = (LocalPlayer) (Object) this;
        var parkourability = Parkourability.get(player);
        if (moverType != MoverType.SELF) return;

        var enforcedPos = parkourability.getBehaviorEnforcer().getEnforcedPosition();
        if (enforcedPos != null) {
            ci.cancel();
            player.setDeltaMovement(Vec3.ZERO);
            super.move(moverType, enforcedPos.subtract(player.position()));
            return;
        }
        var enforcedMovePos = parkourability.getBehaviorEnforcer().getEnforcedMovePoint();
        if (enforcedMovePos != null) {
            ci.cancel();
            var dMove = enforcedMovePos.subtract(player.position());
            player.setDeltaMovement(dMove);
            super.move(moverType, dMove);
            return;
        }
        var enforcedMovement = parkourability.getBehaviorEnforcer().getEnforcedDeltaMovement();
        if (enforcedMovement != null) {
            ci.cancel();
            player.setDeltaMovement(enforcedMovement);
            super.move(moverType, enforcedMovement);
        }
    }

    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    public void onSetSprinting(boolean sprint, CallbackInfo ci) {
        Parkourability parkourability = Parkourability.get((LocalPlayer) (Object) this);
        if (parkourability.getBehaviorEnforcer().enforceNoSprint()) {
            super.setSprinting(false);
            sprintTime = 0;
            ci.cancel();
        } else if (parkourability.getBehaviorEnforcer().enforceSprint()) {
            super.setSprinting(true);
            sprintTime = 0;
            ci.cancel();
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    public void onAiStep(CallbackInfo ci) {
        setSprinting(isSprinting());
    }
}
