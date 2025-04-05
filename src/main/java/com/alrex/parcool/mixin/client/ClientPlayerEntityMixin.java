package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.capability.Parkourability;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
	@Shadow
	public abstract void move(MoverType p_213315_1_, Vector3d p_213315_2_);

	public ClientPlayerEntityMixin(ClientWorld p_i50991_1_, GameProfile p_i50991_2_) {
		super(p_i50991_1_, p_i50991_2_);
	}

	private boolean oldSprinting = false;

	@Inject(method = "isShiftKeyDown", at = @At("HEAD"), cancellable = true)
	public void onIsShiftKeyDown(CallbackInfoReturnable<Boolean> cir) {
		Parkourability parkourability = Parkourability.get((PlayerEntity) (Object) this);

		if (parkourability == null) return;
		if (parkourability.getBehaviorEnforcer().cancelSneak()) {
			cir.setReturnValue(false);
		}
	}
	@Inject(method = "aiStep", at = @At("HEAD"))
	public void onAiStep(CallbackInfo ci) {
		ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
		if (player.isLocalPlayer()) {
			boolean flag = !player.input.hasForwardImpulse() || !((float) player.getFoodData().getFoodLevel() > 6.0F || this.abilities.mayfly);
			boolean flag1 = flag || this.isInWater() && !this.isUnderWater();
			if (oldSprinting && !flag1) {
				player.setSprinting(true);
			}
			oldSprinting = player.isSprinting();
		}
	}

	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
	public void onMove(MoverType moverType, Vector3d movement, CallbackInfo ci) {
		if (moverType != MoverType.SELF) return;
		ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		Vector3d enforced = parkourability.getBehaviorEnforcer().getEnforcedMovePoint();
		if (enforced != null) {
			ci.cancel();
			Vector3d dMove = enforced.subtract(player.position());
			player.setDeltaMovement(dMove);
			super.move(moverType, dMove);
		}
	}

    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    public void onSetSprinting(boolean sprint, CallbackInfo ci) {
        Parkourability parkourability = Parkourability.get((ClientPlayerEntity) (Object) this);
        if (parkourability != null && parkourability.getBehaviorEnforcer().cancelSprint()) {
            ci.cancel();
        }
    }

}
