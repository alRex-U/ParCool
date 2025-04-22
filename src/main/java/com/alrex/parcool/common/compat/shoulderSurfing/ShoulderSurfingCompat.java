package com.alrex.parcool.common.compat.shoulderSurfing;

import com.alrex.parcool.common.action.impl.Dodge.DodgeDirection;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import com.github.exopandora.shouldersurfing.config.Config;
import com.github.exopandora.shouldersurfing.plugin.ShoulderSurfingRegistrar;
import net.minecraft.client.Minecraft;
import java.lang.reflect.Field;

/**
 * Compatibility class for the "Should Surfing" mod
 */
public class ShoulderSurfingCompat {
    private static Minecraft mc = Minecraft.getInstance();
    private static boolean isLoaded = false;
    private static Object configClient = null;
    static {
        try {
            // Try to load the Config class dynamically
            Class<?> configClass = Class.forName("com.github.exopandora.shouldersurfing.config.Config");
            Field clientField = configClass.getField("CLIENT");
            configClient = clientField.get(null);
            ShoulderSurfingRegistrar.getInstance().registerCameraCouplingCallback(new ShoulderSurfingDecoupledCamera());
            isLoaded = true;
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            isLoaded = false;
        }
    }

    public static Boolean isCameraDecoupled() {
        if (!isLoaded) return false;
        return ShoulderSurfingImpl.getInstance().isCameraDecoupled();
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