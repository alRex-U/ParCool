package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.capability.Parkourability;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    private boolean oldSprinting = false;

	public LocalPlayerMixin(ClientLevel p_234112_, GameProfile p_234113_, @Nullable ProfilePublicKey p_234114_) {
		super(p_234112_, p_234113_, p_234114_);
	}

	@Inject(method = "isShiftKeyDown", at = @At("HEAD"), cancellable = true)
	public void onIsShiftKeyDown(CallbackInfoReturnable<Boolean> cir) {
		Parkourability parkourability = Parkourability.get((Player) (Object) this);

		if (parkourability == null) return;
        if (parkourability.getBehaviorEnforcer().cancelSneak()) {
			cir.setReturnValue(false);
		}
	}

    @Inject(method = "aiStep", at = @At("HEAD"))
	public void onAiStep(CallbackInfo ci) {
        var player = (LocalPlayer) (Object) this;
        if (player.isLocalPlayer()) {
            boolean flag = !player.input.hasForwardImpulse() || !((float) player.getFoodData().getFoodLevel() > 6.0F || this.getAbilities().mayfly);
            boolean flag1 = flag || this.isInWater() && !this.isUnderWater();
            if (oldSprinting && !flag1) {
                player.setSprinting(true);
            }
            oldSprinting = player.isSprinting();
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void onMove(MoverType moverType, Vec3 movement, CallbackInfo ci) {
        var player = (LocalPlayer) (Object) this;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        var enforcedPos = parkourability.getBehaviorEnforcer().getEnforcedPosition();
        if (enforcedPos != null) {
            ci.cancel();
            var dMove = enforcedPos.subtract(player.position());
            setBoundingBox(getBoundingBox().move(dMove));
            setPos(player.getX() + dMove.x, player.getY() + dMove.y, player.getZ() + dMove.z);
            return;
        }
        if (moverType != MoverType.SELF) return;
        var enforcedMovePos = parkourability.getBehaviorEnforcer().getEnforcedMovePoint();
        if (enforcedMovePos != null) {
            ci.cancel();
            var dMove = enforcedMovePos.subtract(player.position());
            player.setDeltaMovement(dMove);
            super.move(moverType, dMove);
        }
    }

    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    public void onSetSprinting(boolean sprint, CallbackInfo ci) {
        Parkourability parkourability = Parkourability.get((LocalPlayer) (Object) this);
        if (parkourability != null && parkourability.getBehaviorEnforcer().cancelSprint()) {
            ci.cancel();
		}
	}
}
