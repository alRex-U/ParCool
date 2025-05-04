package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.RenderBehaviorEnforcer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Inject(method = "getCameraType", at = @At("HEAD"), cancellable = true)
    public void onGetCameraType(CallbackInfoReturnable<CameraType> cir) {
        var cameraType = RenderBehaviorEnforcer.getEnforcedCameraType();
        if (cameraType != null) {
            cir.setReturnValue(cameraType);
        }
    }
}
