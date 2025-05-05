package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin extends AttachmentHolder {

    @Shadow
    public abstract void setBoundingBox(AABB bb);

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract void setPos(double x, double y, double z);

    @Shadow
    public boolean noPhysics;

    @Inject(method = "getEyeHeight()F", at = @At("HEAD"), cancellable = true)
    public void onGetEyeHeight(CallbackInfoReturnable<Float> cir) {
        if (!(((Object) this) instanceof Player player)) {
            return;
        }

        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        HideInBlock ability = parkourability.get(HideInBlock.class);
        Tuple<BlockPos, BlockPos> area = ability.getHidingArea();
        if (ability.isDoing() && area != null) {
            int areaHeight = area.getB().getY() - area.getA().getY() + 1;
            float eyeHeight = player.getDimensions(Pose.STANDING).height() * 0.85f;
            if (areaHeight < eyeHeight) {
                cir.setReturnValue(eyeHeight);
            } else {
                cir.setReturnValue(areaHeight + 0.2f);
            }
            return;
        }
    }

    @Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
    public void onIsInWall(CallbackInfoReturnable<Boolean> cir) {
        if (!(((Object) this) instanceof Player player)) {
            return;
        }
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        HideInBlock hideInBlock = parkourability.get(HideInBlock.class);
        if (hideInBlock.isDoing() || hideInBlock.getNotDoingTick() < 2) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void onMove(MoverType type, Vec3 pos, CallbackInfo ci) {
        if (!((Object) this instanceof Player player)) return;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        var enforcedPos = parkourability.getBehaviorEnforcer().getEnforcedPosition();
        if (enforcedPos != null) {
            ci.cancel();
            var dMove = enforcedPos.subtract(player.position());
            noPhysics = true;
            setBoundingBox(getBoundingBox().move(dMove));
            setPos(player.getX() + dMove.x, player.getY() + dMove.y, player.getZ() + dMove.z);
        }
    }
}
