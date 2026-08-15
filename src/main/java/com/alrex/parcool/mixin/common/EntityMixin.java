package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.Parkourability;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.ScoreHolder;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin extends AttachmentHolder implements SyncedDataHolder, EntityAccess, CommandSource, ScoreHolder, IEntityExtension {
    @Shadow
    public boolean noPhysics;

    @Shadow
    protected abstract void setSharedFlag(int p_20116_, boolean p_20117_);

    @Inject(method = "move", at = @At("HEAD"))
    public void onMove(MoverType moverType, Vec3 movement, CallbackInfo ci) {
        if (!(((Object) this) instanceof Player player)) {
            return;
        }
        var parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        if (parkourability.getBehaviorEnforcer().enforceNoPhysics()) {
            noPhysics = true;
        }
    }

    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    public void onSetSprinting(boolean sprint, CallbackInfo ci) {
        if (!(((Object) this) instanceof Player player)) {
            return;
        }
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability.getBehaviorEnforcer().enforceNoSprint()) {
            this.setSharedFlag(3, false);
            ci.cancel();
        } else if (parkourability.getBehaviorEnforcer().enforceSprint()) {
            this.setSharedFlag(3, true);
            ci.cancel();
        }
    }

}
