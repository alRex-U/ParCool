package com.alrex.parcool.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.CameraType;

import java.util.function.Supplier;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public final class RenderBehaviorEnforcer {

    public interface Marker {
        boolean remain();
    }

    public static class Enforcer<T> {
        final Marker marker;
        final Supplier<T> behaviorSupplier;

        Enforcer(Marker marker, Supplier<T> supplier) {
            this.marker = marker;
            this.behaviorSupplier = supplier;
        }

        boolean remain() {
            return marker.remain();
        }

        T getBehavior() {
            return behaviorSupplier.get();
        }
    }

    @Nullable private static Enforcer<CameraType> cameraTypeEnforcer = null;

    @Environment(EnvType.CLIENT)
    public static void serMarkerEnforceCameraType(
            Marker marker, Supplier<CameraType> cameraTypeSupplier) {
        cameraTypeEnforcer = new Enforcer<>(marker, cameraTypeSupplier);
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public static CameraType getEnforcedCameraType() {
        if (cameraTypeEnforcer != null && cameraTypeEnforcer.remain()) {
            return cameraTypeEnforcer.getBehavior();
        }
        cameraTypeEnforcer = null;
        return null;
    }
}
