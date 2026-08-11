package com.alrex.parcool.client;

import com.alrex.parcool.common.action.BehaviorEnforcer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.CameraType;

import javax.annotation.Nullable;
import java.util.TreeMap;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public final class RenderBehaviorEnforcer {
    private static RenderBehaviorEnforcer INSTANCE = null;

    public static RenderBehaviorEnforcer getInstance() {
        if (INSTANCE == null) INSTANCE = new RenderBehaviorEnforcer();
        return INSTANCE;
    }

    public static void reset() {
        INSTANCE = null;
    }

    private final TreeMap<BehaviorEnforcer.ID, BehaviorEnforcer.Marker> enforceImmediateEyeHeightChangeMarker = new TreeMap<>();
    @Nullable
    private BehaviorEnforcer.Enforcer<CameraType> cameraTypeEnforcer = null;

    public void addMarkerEnforcingImmediateEyeHeightChange(BehaviorEnforcer.ID id, BehaviorEnforcer.Marker marker) {
        enforceImmediateEyeHeightChangeMarker.put(id, marker);
    }

    public void setMarkerEnforcingCameraType(BehaviorEnforcer.Marker marker, Supplier<CameraType> cameraTypeSupplier) {
        cameraTypeEnforcer = new BehaviorEnforcer.Enforcer<>(marker, cameraTypeSupplier);
    }

    public boolean enforceImmediateEyeHeightChange() {
        enforceImmediateEyeHeightChangeMarker.values().removeIf(it -> !it.remain());
        return !enforceImmediateEyeHeightChangeMarker.isEmpty();
    }

    @Nullable
    public CameraType getEnforcedCameraType() {
        if (cameraTypeEnforcer != null && cameraTypeEnforcer.remain()) {
            return cameraTypeEnforcer.getBehavior();
        }
        cameraTypeEnforcer = null;
        return null;
    }
}
