package com.alrex.parcool.client;

import net.minecraft.client.settings.PointOfView;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
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

    @Nullable
    private static Enforcer<PointOfView> cameraTypeEnforcer = null;

    @OnlyIn(Dist.CLIENT)
    public static void serMarkerEnforceCameraType(Marker marker, Supplier<PointOfView> cameraTypeSupplier) {
        cameraTypeEnforcer = new Enforcer<>(marker, cameraTypeSupplier);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public static PointOfView getEnforcedCameraType() {
        if (cameraTypeEnforcer != null && cameraTypeEnforcer.remain()) {
            return cameraTypeEnforcer.getBehavior();
        }
        cameraTypeEnforcer = null;
        return null;
    }
}
