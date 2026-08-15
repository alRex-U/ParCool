package com.alrex.parcool.client.animation.system.math;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

@OnlyIn(Dist.CLIENT)
public class MathUtil {
    // normalize angle in [-pi,pi)
    public static float warpRadian(float angleRadian) {
        return (float) (angleRadian - 2 * Math.PI * Math.floor((angleRadian + Math.PI) / (2. * Math.PI)));
    }

    public static float rotLerp(float factor, float fromRadian, float toRadian) {
        return fromRadian + factor * warpRadian(toRadian - fromRadian);
    }

    public static Quaternionf slerp(float factor, Quaternionfc from, Quaternionfc to, boolean useShortestPath) {
        var fromNormalized = from.normalize(new Quaternionf());
        var toNormalized = to.normalize(new Quaternionf());
        var dot = fromNormalized.dot(toNormalized);
        if (useShortestPath) {
            if (dot < 0) {
                toNormalized = toNormalized.mul(-1);
                dot = -dot;
            }
        }
        var diffAngle = (float) Math.acos(Mth.clamp(dot, 0, 0.999999f));
        var sinDiffAngle = Mth.sin(diffAngle);
        var fromScale = Mth.sin((1 - factor) * diffAngle) / sinDiffAngle;
        var toScale = Mth.sin(factor * diffAngle) / sinDiffAngle;
        return new Quaternionf(
                fromScale * fromNormalized.x() + toScale * toNormalized.x(),
                fromScale * fromNormalized.y() + toScale * toNormalized.y(),
                fromScale * fromNormalized.z() + toScale * toNormalized.z(),
                fromScale * fromNormalized.w() + toScale * toNormalized.w()
        );
    }

    public static Vec3f rotate(Vec3f point, Quaternionf rotation) {
        var conjRot = rotation.conjugate(new Quaternionf());
        var rot = new Quaternionf().set(rotation);
        var pointQ = new Quaternionf(point.x(), point.y(), point.z(), 0);
        rot.mul(pointQ);
        rot.mul(conjRot);
        return new Vec3f(rot.x(), rot.y(), rot.z());
    }

    public static double toYawRadian(Vec3 vec) {
        return (Math.atan2(vec.x(), vec.z()));
    }

    public static Quaternionf fromModelPartRotation(float rotX, float rotY, float rotZ) {
        var q = new Quaternionf();
        if (rotZ != 0f) {
            q.mul(MathUtil.rotation(Vec3f.ZP, rotZ));
        }
        if (rotY != 0f) {
            q.mul(MathUtil.rotation(Vec3f.YP, rotY));
        }
        if (rotX != 0f) {
            q.mul(MathUtil.rotation(Vec3f.XP, rotX));
        }
        return q;
    }

    public static Vec3f toCameraRotation(Quaternionfc q) {
        float xRot_pitch, yRot_yaw, zRot_roll;
        xRot_pitch = (float) -Math.asin(2 * (q.y() * q.z() + q.x() * q.w()));
        if (Math.abs(Math.cos(xRot_pitch)) > 1e-4) {
            yRot_yaw = (float) -Math.atan2(
                    q.y() * q.w() - q.x() * q.z(),
                    q.w() * q.w() + q.z() * q.z() - 0.5
            );
            zRot_roll = (float) -Math.atan2(
                    q.z() * q.w() - q.x() * q.y(),
                    q.w() * q.w() + q.y() * q.y() - 0.5
            );
        } else {
            yRot_yaw = 0;
            zRot_roll = (float) -Math.atan2(
                    q.z() * q.w() + q.x() * q.y(),
                    q.w() * q.w() + q.x() * q.x() - 0.5
            );
        }
        return new Vec3f(xRot_pitch, yRot_yaw, zRot_roll);
    }

    public static Vec3f toModelPartRotation(Quaternionfc q) {
        float xRot, zRot, yRot = (float) Math.asin(2 * (-q.x() * q.z() + q.y() * q.w()));
        double cosY = Math.cos(yRot);
        if (Math.abs(cosY) > 1e-4) {
            xRot = (float) Math.atan2(
                    q.y() * q.z() + q.x() * q.w(),
                    q.w() * q.w() + q.z() * q.z() - 0.5
            );
            zRot = (float) Math.atan2(
                    q.x() * q.y() + q.z() * q.w(),
                    q.w() * q.w() + q.x() * q.x() - 0.5
            );
        } else {
            xRot = 0;
            zRot = (float) Math.atan2(
                    -q.x() * q.y() + q.z() * q.w(),
                    q.w() * q.w() + q.y() * q.y() - 0.5
            );
        }
        return new Vec3f(-xRot, -yRot, zRot);
    }

    public static Quaternionf rotation(Vec3f axis, float angle) {
        var sin = Mth.sin(angle * 0.5f);
        return new Quaternionf(
                axis.x() * sin, axis.y() * sin, axis.z() * sin, Mth.cos(angle * 0.5f)
        );
    }
}
