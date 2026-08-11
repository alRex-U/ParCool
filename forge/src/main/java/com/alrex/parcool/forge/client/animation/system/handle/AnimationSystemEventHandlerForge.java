package com.alrex.parcool.forge.client.animation.system.handle;

import com.alrex.parcool.client.animation.system.handle.AnimationSystemEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AnimationSystemEventHandlerForge {
    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;
        AnimationSystemEventHandler.onRenderTickPost(event.renderTickTime);
    }

    @SubscribeEvent
    public static void onSetupCamera(ViewportEvent.ComputeCameraAngles event) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        var cameraRot = AnimationSystemEventHandler.setupCamera(event.getCamera().getEntity());
        if (cameraRot == null) return;
        event.setPitch(event.getPitch() + (float) Math.toDegrees(cameraRot.x()));
        event.setYaw(event.getYaw() + (float) Math.toDegrees(cameraRot.y()));
        event.setRoll(event.getRoll() - (float) Math.toDegrees(cameraRot.z()));
    }
}
