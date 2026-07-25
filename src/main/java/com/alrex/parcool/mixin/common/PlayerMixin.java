package com.alrex.parcool.mixin.common;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.IParkourabilityHolder;
import com.alrex.parcool.common.Parkourability;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IParkourabilityHolder {
    @Unique
    @Nonnull
    private Parkourability parcool$parkourability = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(Level level, BlockPos blockPos, float p_219729_, GameProfile profile, ProfilePublicKey publicKey, CallbackInfo ci) {
        parcool$parkourability = new Parkourability((Player) (Object) this, ParCool.getActionRegistry());
    }

    @Nonnull
    @Override
    public Parkourability getParkourability() {
        return parcool$parkourability;
    }

    protected PlayerMixin(EntityType<? extends LivingEntity> p_i48577_1_, Level p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
	}

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    public void onTryToStartFallFlying(CallbackInfoReturnable<Boolean> cir) {
        var player = (Player) (Object) this;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability != null && parkourability.getBehaviorEnforcer().enforceNoFallFlying()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    public void onJumpFromGround(CallbackInfo ci) {
        Parkourability parkourability = Parkourability.get((Player) (Object) this);
        if (parkourability == null) return;
        if (parkourability.getBehaviorEnforcer().enforceNoJump()) {
            ci.cancel();
        }
    }

    @Inject(method = "isStayingOnGroundSurface", at = @At("HEAD"), cancellable = true)
    public void onIsStayingOnGroundSurface(CallbackInfoReturnable<Boolean> cir) {
        Parkourability parkourability = Parkourability.get((Player) (Object) this);
        if (parkourability == null) return;
        if (parkourability.getBehaviorEnforcer().enforceNoDescendingFromEdge()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    public void onAddAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.put("parcool.parkourability", parcool$parkourability.saveToTag());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    public void onReadAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.get("parcool.parkourability") instanceof CompoundTag pTag) {
            parcool$parkourability.readFrom(pTag);
        }
    }
}
