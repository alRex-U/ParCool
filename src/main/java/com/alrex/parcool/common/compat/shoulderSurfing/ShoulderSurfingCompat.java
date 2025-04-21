package com.alrex.parcool.common.compat.shoulderSurfing;

import com.github.exopandora.shouldersurfing.config.Config;
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
}