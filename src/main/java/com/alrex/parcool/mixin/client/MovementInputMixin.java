package com.alrex.parcool.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import com.alrex.parcool.client.input.KeyRecorder;
import net.minecraft.client.player.KeyboardInput;

@Mixin({KeyboardInput.class})
public class MovementInputMixin {
   @Inject(
      method = "tick",
      at = @At(
         value = "FIELD",
         target = "Lnet/minecraft/client/player/KeyboardInput;shiftKeyDown:Z",
         shift = At.Shift.AFTER  // Execute after the field assignment
      )
   )
   public void tickHook(boolean moveSlowly, float sneakingSpeedMultiplier, CallbackInfo ci) {
      KeyRecorder.recordKeyboardMovingVector((KeyboardInput)(Object)this);
   }
}