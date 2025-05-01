package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin extends CapabilityProvider<Entity> {
    protected EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Inject(method = "getEyeHeight()F", at = @At("HEAD"), cancellable = true)
    public void onGetEyeHeight(CallbackInfoReturnable<Float> cir) {
        if (!(((Object) this) instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) (Object) this;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        HideInBlock ability = parkourability.get(HideInBlock.class);
        Tuple<BlockPos, BlockPos> area = ability.getHidingArea();
        if (ability.isDoing() && area != null) {
            int areaHeight = area.getB().getY() - area.getA().getY() + 1;
            float eyeHeight = player.getDimensions(Pose.STANDING).height * 0.85f;
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
        if (!(((Object) this) instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) (Object) this;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        HideInBlock hideInBlock = parkourability.get(HideInBlock.class);
        if (hideInBlock.isDoing() || hideInBlock.getNotDoingTick() < 2) {
            cir.setReturnValue(false);
        }
    }
}
