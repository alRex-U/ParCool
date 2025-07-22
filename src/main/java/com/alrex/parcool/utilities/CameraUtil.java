package com.alrex.parcool.utilities;

import javax.annotation.Nullable;
import com.alrex.parcool.extern.AdditionalMods;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;

public class CameraUtil {
    public static boolean isCameraDecoupled() {
        return AdditionalMods.shoulderSurfingManager().isCameraDecoupled() || AdditionalMods.betterThirdPerson().isCameraDecoupled();
    }

    public static float getCameraYaw() {
        @Nullable Float yaw = AdditionalMods.shoulderSurfingManager().getCameraYaw();
        if (yaw != null) return yaw;
        yaw = AdditionalMods.betterThirdPerson().getCameraYaw();
        if (yaw != null) return yaw;
        return Minecraft.getInstance().cameraEntity.yRot;
    }

    public static Vector3d alignVectorToCamera(Vector3d vector) {
        if (vector == null) return null;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return vector;
        Float cameraYaw = getCameraYaw();
        return VectorUtil.rotateYDegrees(vector, cameraYaw);
    }
}
