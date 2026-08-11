package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.math.Vec3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public record BlendingModelTransform(ModelTransform transformation, boolean isOverwriting, float blendFactor,
                                     @Nullable Vec3f cameraRotation) {
    public static BlendingModelTransform from(ModelTransform transform, float blendFactor, @Nullable Vec3f cameraRotation) {
        return new BlendingModelTransform(transform, Math.abs(blendFactor - 1f) < 1e-6, blendFactor, cameraRotation);
    }

    public static BlendingModelTransform from(ModelTransform transform, @Nullable Vec3f cameraRotation) {
        return new BlendingModelTransform(transform, true, 1f, cameraRotation);
    }
}
