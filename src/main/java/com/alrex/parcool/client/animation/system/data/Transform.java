package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.math.MathUtil;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record Transform(Vec3f translation, Quaternion rotation) {
    public static final Transform NO_TRANSFORMATION = new Transform(Vec3f.ZERO, Quaternion.ONE.copy());
    public static final Transform NO_TRANSFORMATION_INV = NO_TRANSFORMATION.getInverseRotated();

    public static Transform fromRotationParams(float xRot, float yRot, float zRot) {
        var rot = Quaternion.ONE.copy();
        if (zRot != 0f) {
            rot.mul(Vector3f.ZP.rotation(zRot));
        }
        if (yRot != 0f) {
            rot.mul(Vector3f.YP.rotation(yRot));
        }
        if (xRot != 0f) {
            rot.mul(Vector3f.XP.rotation(xRot));
        }
        return new Transform(Vec3f.ZERO, rot);
    }

    public Transform getInverseRotated() {
        return new Transform(translation, new Quaternion(-rotation.i(), -rotation.j(), -rotation.k(), -rotation.r()));
    }

    /// @param t : blending factor, in [0,1]
    public Transform morph(Transform to, float t, boolean useShortestPath) {
        return new Transform(
                new Vec3f(
                        Mth.lerp(t, translation.x(), to.translation.x()),
                        Mth.lerp(t, translation.y(), to.translation.y()),
                        Mth.lerp(t, translation.z(), to.translation.z())
                ),
                MathUtil.slerp(t, this.rotation, to.rotation, useShortestPath)
        );
    }

    public Transform mirror() {
        return new Transform(
                new Vec3f(-translation.x(), translation().y(), translation().z()),
                new Quaternion(rotation.i(), -rotation.j(), -rotation.k(), rotation.r())
        );
    }

    public Transform append(Transform after, float t, boolean useShortestPath) {
        var rot = MathUtil.slerp(t, Quaternion.ONE, after.rotation, useShortestPath);
        var translation = MathUtil.rotate(this.translation, rot);
        rot.mul(this.rotation);
        return new Transform(translation.add(after.translation.scale(t)), rot);
    }

    public void applyInQuaternion(ModelPart part, float blendingFactor, boolean useShortestPath) {
        var modelRot = MathUtil.fromModelPartRotation(-part.xRot, -part.yRot, part.zRot);
        var appliedRotation = MathUtil.slerp(blendingFactor, modelRot, rotation, useShortestPath);

        applyTransformation(
                part,
                translation.scale(blendingFactor),
                MathUtil.toModelPartRotation(appliedRotation)
        );
    }

    public void apply(ModelPart part, float blendingFactor) {
        var appliedRot = MathUtil.toModelPartRotation(rotation);
        applyTransformation(
                part,
                translation.scale(blendingFactor),
                new Vec3f(
                        MathUtil.rotLerp(blendingFactor, part.xRot, appliedRot.x()),
                        MathUtil.rotLerp(blendingFactor, part.yRot, appliedRot.y()),
                        MathUtil.rotLerp(blendingFactor, part.zRot, appliedRot.z())
                )
        );
    }

    public void apply(ModelPart part) {
        applyTransformation(part, translation, MathUtil.toModelPartRotation(rotation));
    }

    private static void applyTransformation(ModelPart part, Vec3f translation, Vec3f rotParams) {
        part.xRot = rotParams.x();
        part.yRot = rotParams.y();
        part.zRot = rotParams.z();
        part.x += -translation.x() * 16f;
        part.y += -translation.y() * 16f;
        part.z += translation.z() * 16f;
    }
}
