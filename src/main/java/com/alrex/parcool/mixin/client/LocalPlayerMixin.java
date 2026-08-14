package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.Parkourability;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    public LocalPlayerMixin(ClientLevel p_234112_, GameProfile p_234113_) {
        super(p_234112_, p_234113_);
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

    @Inject(method = "aiStep", at = @At("HEAD"))
    public void onAiStep(CallbackInfo ci) {
        setSprinting(isSprinting());
    }
}
