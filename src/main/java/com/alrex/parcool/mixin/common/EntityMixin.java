package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.impl.HideInBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin extends AttachmentHolder {

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
}
