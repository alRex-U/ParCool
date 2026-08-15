package com.alrex.parcool.client.animation.system.handle;

import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@OnlyIn(Dist.CLIENT)
public class AnimationSystemEventHandler {
    @SubscribeEvent
    public static void onTick(ClientTickEvent.Pre event) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        for (var p : level.players()) {
            if (p instanceof IPlayerAnimatorHolder holder) {
                holder.getParCoolPlayerAnimator().tick();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTick(RenderFrameEvent.Pre event) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        for (var p : level.players()) {
            if (p instanceof IPlayerAnimatorHolder holder) {
                holder.getParCoolPlayerAnimator().onRenderTick(p, event.getPartialTick().getGameTimeDeltaPartialTick(true));
            }
        }
    }

    @SubscribeEvent
    public static void onSetupCamera(ViewportEvent.ComputeCameraAngles event) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        if (event.getCamera().getEntity() instanceof IPlayerAnimatorHolder holder) {
            var transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();
            if (transform == null) return;
            var cameraRot = transform.cameraRotation();
            if (cameraRot == null) return;
            event.setPitch(event.getPitch() + (float) Math.toDegrees(cameraRot.x()));
            event.setYaw(event.getYaw() + (float) Math.toDegrees(cameraRot.y()));
            event.setRoll(event.getRoll() - (float) Math.toDegrees(cameraRot.z()));
        }
    }
}
