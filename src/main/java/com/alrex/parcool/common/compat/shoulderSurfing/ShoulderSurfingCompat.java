package com.alrex.parcool.common.compat.shoulderSurfing;

import com.github.exopandora.shouldersurfing.config.Config;
import com.alrex.parcool.common.action.impl.Dodge.DodgeDirection;
import com.github.exopandora.shouldersurfing.config.Config;
import net.minecraft.client.Minecraft;
import java.lang.reflect.Field;

/**
 * Compatibility class for the "Should Surfing" mod
 */
public class ShoulderSurfingCompat {
    private static Boolean isCameraDecoupled = false;
    private static boolean isLoaded = false;
    private static Object configClient = null;
    static {
        try {
            // Try to load the Config class dynamically
            Class<?> configClass = Class.forName("com.github.exopandora.shouldersurfing.config.Config");
            Field clientField = configClass.getField("CLIENT");
            configClient = clientField.get(null);
            isLoaded = true;
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            isLoaded = false;
        }
    }

    public static Boolean isCameraDecoupled() {
        if (!isLoaded) return false;
        return Config.CLIENT.isCameraDecoupled();
    }

    public static void forceCoupledCamera() {
        if (!isLoaded) return;
        ShoulderSurfingCompat.isCameraDecoupled = Config.CLIENT.isCameraDecoupled();
        if (isCameraDecoupled) {
            Config.CLIENT.toggleCameraCoupling();
            org.apache.logging.log4j.LogManager.getLogger("ParCool").info("coupling camera");
        }
    }

    public static void releaseCoupledCamera() {
        if (!isLoaded) return;
        if (isCameraDecoupled && !Config.CLIENT.isCameraDecoupled()) {
            Config.CLIENT.toggleCameraCoupling();
            org.apache.logging.log4j.LogManager.getLogger("ParCool").info("decoupling camera");
            isCameraDecoupled = false;
        }
    }
 
     public static DodgeDirection handleCustomCameraRotationForDodge(DodgeDirection direction) {
        if (!isLoaded || IsCameraInFirstPerson()) return direction;
        var player = mc.player;
        if (player == null) return direction;
        var camera = mc.cameraEntity;
        float yaw = camera.getYRot() - player.getYRot();
        if (yaw < 0) yaw += 360;
        
        if (Config.CLIENT.isCameraDecoupled()) {
            if (yaw <= 45 || yaw >= 270) return DodgeDirection.Front;
            if (yaw >= 135 && yaw <= 225) return DodgeDirection.Back;
            if (yaw > 180) return DodgeDirection.Left;
            return DodgeDirection.Right;
        }
        if (yaw < 45) return direction;
        if (yaw > 135) return direction.inverse();
        if (yaw > 225) return direction.left();
        return direction.right();
     }

     private static boolean IsCameraInFirstPerson() {
        return Minecraft.getInstance().options.getCameraType().isFirstPerson();
     }
}