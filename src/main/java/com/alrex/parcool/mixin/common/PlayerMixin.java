package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.registries.ParCoolPoses;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> p_i48577_1_, Level p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
	}

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
	public void onTryToStartFallFlying(CallbackInfoReturnable<Boolean> cir) {
		Player player = (Player) (Object) this;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability != null && parkourability.get(ClingToCliff.class).isDoing()) {
			cir.setReturnValue(false);
        }
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    public void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (pose == ParCoolPoses.ROLLING.get()) {
            cir.setReturnValue(EntityDimensions.fixed(0.6F, 1.45F));
        } else if (pose == ParCoolPoses.VAULTING.get()) {
            cir.setReturnValue(EntityDimensions.fixed(0.6F, 1.5F));
        }
    }

    @Inject(method = "getStandingEyeHeight", at = @At("HEAD"), cancellable = true)
    public void getStandingEyeHeight(Pose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        if (pose == ParCoolPoses.ROLLING.get()) {
            cir.setReturnValue(1.27F);
        } else if (pose == ParCoolPoses.VAULTING.get()) {
            cir.setReturnValue(1.27F);
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    public void onJumpFromGround(CallbackInfo ci) {
        Parkourability parkourability = Parkourability.get((Player) (Object) this);
        if (parkourability == null) return;
        if (parkourability.getCancelMarks().cancelJump()) {
            ci.cancel();
        }
    }

    @Inject(method = "isStayingOnGroundSurface", at = @At("HEAD"), cancellable = true)
    public void onIsStayingOnGroundSurface(CallbackInfoReturnable<Boolean> cir) {
        Parkourability parkourability = Parkourability.get((Player) (Object) this);
        if (parkourability == null) return;
        if (parkourability.getCancelMarks().cancelDescendFromEdge()) {
            cir.setReturnValue(true);
		}
	}
}
