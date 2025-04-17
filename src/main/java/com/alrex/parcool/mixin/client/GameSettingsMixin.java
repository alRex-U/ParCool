package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.RenderBehaviorEnforcer;
import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.PointOfView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameSettings.class)
public abstract class GameSettingsMixin {
    @Inject(method = "getCameraType", at = @At("HEAD"), cancellable = true)
    public void onGetCameraType(CallbackInfoReturnable<PointOfView> cir) {
        PointOfView cameraType = RenderBehaviorEnforcer.getEnforcedCameraType();
        if (cameraType != null) {
            cir.setReturnValue(cameraType);
        }
    }
}
