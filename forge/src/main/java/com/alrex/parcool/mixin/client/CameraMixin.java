package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.RenderBehaviorEnforcer;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private float eyeHeightOld;

    @Shadow
    private float eyeHeight;

    @Shadow
    private Entity entity;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void onTick(CallbackInfo ci) {
        if (this.entity != null && RenderBehaviorEnforcer.getInstance().enforceImmediateEyeHeightChange()) {
            eyeHeight = eyeHeightOld = entity.getEyeHeight();
            ci.cancel();
        }
    }

    @Inject(method = "reset", at = @At("HEAD"))
    public void onReset(CallbackInfo ci) {
        RenderBehaviorEnforcer.reset();
    }
}
