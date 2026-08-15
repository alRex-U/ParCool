package com.alrex.parcool.client;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.common.action.impl.Grapple;
import com.alrex.parcool.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class GrappleCameraHandler {
    private static final double REFERENCE_SPEED = 1.6;

    private static final float MAX_FOV_GAIN = 0.18f;

    private static final float MAX_ROLL_DEGREES = 8f;

    private static final double SMOOTHING = 0.18;

    private static float previousIntensity = 0;
    private static float currentIntensity = 0;
    private static float previousRoll = 0;
    private static float currentRoll = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        previousIntensity = currentIntensity;
        previousRoll = currentRoll;

        float targetIntensity = 0;
        float targetRoll = 0;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !Minecraft.getInstance().isPaused()) {
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability != null) {
                Grapple grapple = parkourability.get(ParCoolActions.GRAPPLE);
                if (grapple.isAttached() || grapple.isMomentumActive()) {
                    Vec3 movement = player.getDeltaMovement();
                    double speed = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
                    targetIntensity = (float) Mth.clamp(speed / REFERENCE_SPEED, 0, 1);
                    targetRoll = grapple.isAttached() ? bankAngle(player, grapple, targetIntensity) : 0;
                }
            }
        }
        currentIntensity = (float) Mth.lerp(SMOOTHING, currentIntensity, targetIntensity);
        currentRoll = (float) Mth.lerp(SMOOTHING, currentRoll, targetRoll);
    }

    private static float bankAngle(LocalPlayer player, Grapple grapple, float intensity) {
        Vec3 pivot = grapple.getPivot();
        if (pivot == null) return 0;
        Vec3 toPivot = pivot.subtract(player.position());
        double horizontal = Math.sqrt(toPivot.x * toPivot.x + toPivot.z * toPivot.z);
        if (horizontal < 1.0e-4) return 0;

        Vec3 forward = EntityUtil.getHorizontalLookAngle(player);
        Vec3 right = new Vec3(-forward.z, 0, forward.x);
        double lateral = (toPivot.x * right.x + toPivot.z * right.z) / horizontal;
        return (float) (lateral * MAX_ROLL_DEGREES * intensity);
    }

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        double configured = ParCool.getConfig().client().grapplingHook.fovIntensity().get();
        if (configured <= 0) return;
        float partialTick = Minecraft.getInstance().getFrameTime();
        float intensity = Mth.lerp(partialTick, previousIntensity, currentIntensity);
        if (intensity <= 1.0e-4) return;
        event.setNewFovModifier(event.getNewFovModifier() * (1f + MAX_FOV_GAIN * intensity * (float) configured));
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        double configured = ParCool.getConfig().client().grapplingHook.cameraRollIntensity().get();
        if (configured <= 0) return;
        float roll = Mth.lerp((float) event.getPartialTick(), previousRoll, currentRoll);
        if (Math.abs(roll) <= 1.0e-4) return;
        event.setRoll(event.getRoll() + roll * (float) configured);
    }
}
