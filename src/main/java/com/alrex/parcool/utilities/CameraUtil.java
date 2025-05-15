package com.alrex.parcool.utilities;

import javax.annotation.Nullable;
import com.alrex.parcool.extern.AdditionalMods;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class CameraUtil {
    public static boolean isCameraDecoupled() {
        return AdditionalMods.shoulderSurfing().isCameraDecoupled() || AdditionalMods.betterThirdPerson().isCameraDecoupled();
    }

    public static float getCameraYaw() {
        @Nullable Float yaw = AdditionalMods.shoulderSurfing().getCameraYaw();
        if (yaw != null) return yaw;
        yaw = AdditionalMods.betterThirdPerson().getCameraYaw();
        if (yaw != null) return yaw;
        return Minecraft.getInstance().cameraEntity.getYRot();
    }

    public static Vec3 alignVectorToCamera(Vec3 vector) {
        if (vector == null) return null;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return vector;
        Float cameraYaw = getCameraYaw();
        return VectorUtil.rotateYDegrees(vector, cameraYaw);
    }
}
