package com.alrex.parcool.client.animation.system.handle;

import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class AnimationSystemEventHandler {
    public static void onTick(Minecraft mc) {
        var level = mc.level;
        if (level == null) return;
        for (var p : level.players()) {
            if (p instanceof IPlayerAnimatorHolder holder) {
                holder.getParCoolPlayerAnimator().tick();
            }
        }
    }

    public static void onRenderTickPost(float partialTick) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        for (var p : level.players()) {
            if (p instanceof IPlayerAnimatorHolder holder) {
                holder.getParCoolPlayerAnimator().onRenderTick(p, partialTick);
            }
        }
    }

    @Nullable
    public static Vec3f setupCamera(Entity cameraEntity) {
        if (cameraEntity instanceof IPlayerAnimatorHolder holder) {
            var transform = holder.getParCoolPlayerAnimator().getCurrentTransformation();
            if (transform == null) return null;
            return transform.cameraRotation();
        }
        return null;
    }
}
