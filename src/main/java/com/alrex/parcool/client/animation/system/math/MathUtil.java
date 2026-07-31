package com.alrex.parcool.client.animation.system.math;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MathUtil {
    // normalize angle in [-pi,pi)
    public static float warpRadian(float angleRadian) {
        return (float) (angleRadian - 2 * Math.PI * Math.floor((angleRadian + Math.PI) / (2. * Math.PI)));
    }

    public static float rotLerp(float factor, float fromRadian, float toRadian) {
        return fromRadian + factor * warpRadian(toRadian - fromRadian);
    }

    public static Quaternion slerp(float factor, Quaternion from, Quaternion to, boolean useShortestPath) {
        from = from.copy();
        from.normalize();
        to = to.copy();
        to.normalize();
        var dot = MathUtil.dot(from, to);
        if (useShortestPath) {
            if (dot < 0) {
                to = new Quaternion(-to.i(), -to.j(), -to.k(), -to.r());
                dot = -dot;
            }
        }
        var diffAngle = (float) Math.acos(Mth.clamp(dot, 0, 0.999999f));
        var sinDiffAngle = Mth.sin(diffAngle);
        var fromScale = Mth.sin((1 - factor) * diffAngle) / sinDiffAngle;
        var toScale = Mth.sin(factor * diffAngle) / sinDiffAngle;
        return new Quaternion(
                fromScale * from.i() + toScale * to.i(),
                fromScale * from.j() + toScale * to.j(),
                fromScale * from.k() + toScale * to.k(),
                fromScale * from.r() + toScale * to.r()
        );
    }

    public static Vec3f rotate(Vec3f point, Quaternion rotation) {
        var conjRot = rotation.copy();
        var rot = rotation.copy();
        conjRot.conj();
        var pointQ = new Quaternion(point.x(), point.y(), point.z(), 0);
        rot.mul(pointQ);
        rot.mul(conjRot);
        return new Vec3f(rot.i(), rot.j(), rot.k());
    }

    public static float dot(Quaternion q1, Quaternion q2) {
        return q1.i() * q2.i() + q1.j() * q2.j() + q1.k() * q2.k() + q1.r() * q2.r();
    }

    public static double toYawRadian(Vec3 vec) {
        return (Math.atan2(vec.x(), vec.z()));
    }

    public static Quaternion fromModelPartRotation(float rotX, float rotY, float rotZ) {
        var q = Quaternion.ONE.copy();
        if (rotZ != 0f) {
            q.mul(Vector3f.ZP.rotation(rotZ));
        }
        if (rotY != 0f) {
            q.mul(Vector3f.YP.rotation(rotY));
        }
        if (rotX != 0f) {
            q.mul(Vector3f.XP.rotation(rotX));
        }
        return q;
    }

    public static Vec3f toCameraRotation(Quaternion q) {
        float xRot_pitch, yRot_yaw, zRot_roll;
        xRot_pitch = (float) -Math.asin(2 * (q.j() * q.k() + q.i() * q.r()));
        if (Math.abs(Math.cos(xRot_pitch)) > 1e-4) {
            yRot_yaw = (float) -Math.atan2(
                    q.j() * q.r() - q.i() * q.k(),
                    q.r() * q.r() + q.k() * q.k() - 0.5
            );
            zRot_roll = (float) -Math.atan2(
                    q.k() * q.r() - q.i() * q.j(),
                    q.r() * q.r() + q.j() * q.j() - 0.5
            );
        } else {
            yRot_yaw = 0;
            zRot_roll = (float) -Math.atan2(
                    q.k() * q.r() + q.i() * q.j(),
                    q.r() * q.r() + q.i() * q.i() - 0.5
            );
        }
        return new Vec3f(xRot_pitch, yRot_yaw, zRot_roll);
    }

    public static Vec3f toModelPartRotation(Quaternion q) {
        float xRot, zRot, yRot = (float) Math.asin(2 * (-q.i() * q.k() + q.j() * q.r()));
        double cosY = Math.cos(yRot);
        if (Math.abs(cosY) > 1e-4) {
            xRot = (float) Math.atan2(
                    q.j() * q.k() + q.i() * q.r(),
                    q.r() * q.r() + q.k() * q.k() - 0.5
            );
            zRot = (float) Math.atan2(
                    q.i() * q.j() + q.k() * q.r(),
                    q.r() * q.r() + q.i() * q.i() - 0.5
            );
        } else {
            xRot = 0;
            zRot = (float) Math.atan2(
                    -q.i() * q.j() + q.k() * q.r(),
                    q.r() * q.r() + q.j() * q.j() - 0.5
            );
        }
        return new Vec3f(-xRot, -yRot, zRot);
    }
}
