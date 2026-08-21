package com.alrex.parcool.common.grapple;

import net.minecraft.world.phys.Vec3;

public final class GrapplePhysics {
    private GrapplePhysics() {
    }

    public static final double ATTACH_HEIGHT = 1.4;

    public static Vec3 attachmentOf(Vec3 feetPosition) {
        return feetPosition.add(0, ATTACH_HEIGHT, 0);
    }

    public record State(Vec3 position, Vec3 velocity) {
    }

    public record Solve(Vec3 position, Vec3 velocity, double tension) {
    }

    public static Solve substep(
            Vec3 position,
            Vec3 velocity,
            Vec3 pivot,
            double ropeLength,
            Vec3 acceleration,
            double dragPerSubstep,
            double dt,
            double compliance
    ) {
        velocity = velocity.add(acceleration.scale(dt)).scale(dragPerSubstep);
        Vec3 predicted = position.add(velocity.scale(dt));

        Vec3 offset = predicted.subtract(pivot);
        double distance = offset.length();
        double error = distance - ropeLength;

        double tension = 0;
        if (error > 0 && distance > 1.0e-7) {
            double alpha = compliance / (dt * dt);
            double deltaLambda = -error / (1 + alpha);
            predicted = predicted.add(offset.scale(deltaLambda / distance));
            tension = Math.abs(deltaLambda) / (dt * dt);
        }
        return new Solve(predicted, predicted.subtract(position).scale(1 / dt), tension);
    }

    public static Vec3 tangential(Vec3 vector, Vec3 ropeDirection) {
        return vector.subtract(ropeDirection.scale(vector.dot(ropeDirection)));
    }

    public static Vec3 ropeDirection(Vec3 pivot, Vec3 position) {
        Vec3 offset = position.subtract(pivot);
        double length = offset.length();
        return length < 1.0e-6 ? new Vec3(0, -1, 0) : offset.scale(1.0 / length);
    }

    public static Vec3 clampSpeed(Vec3 velocity, double maxSpeed) {
        double speedSqr = velocity.lengthSqr();
        if (speedSqr <= maxSpeed * maxSpeed || speedSqr < 1.0e-12) return velocity;
        return velocity.scale(maxSpeed / Math.sqrt(speedSqr));
    }
}
