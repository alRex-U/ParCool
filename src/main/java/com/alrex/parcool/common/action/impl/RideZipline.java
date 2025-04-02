package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.RideZiplineAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class RideZipline extends Action {
    @Nullable
    private ZiplineRopeEntity ridingZipline;
    private double speed;
    private double acceleration;
    private double slope;
    private float currentT;
    @Nullable
    private Vector3d currentPos;

    @Nullable
    public ZiplineRopeEntity getRidingZipline() {
        return ridingZipline;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getSlope() {
        return slope;
    }

    @Override
    public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
        if (KeyBindings.getKeyBindRideZipline().isDown()
                && !player.isOnGround()
                && !player.isInWater()
                && !parkourability.get(Dive.class).isDoing()
                && !parkourability.get(Vault.class).isDoing()
                && !parkourability.get(HangDown.class).isDoing()
                && !parkourability.get(Flipping.class).isDoing()
                && !parkourability.get(HorizontalWallRun.class).isDoing()
                && !parkourability.get(VerticalWallRun.class).isDoing()
        ) {
            ZiplineRopeEntity ropeEntity = Zipline.getHangableZipline(player.level, player);
            if (ropeEntity == null) return false;
            ridingZipline = ropeEntity;
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        return KeyBindings.getKeyBindRideZipline().isDown()
                && !player.isInWall()
                && ridingZipline != null
                && ridingZipline.isAlive()
                && 0 <= currentT && currentT <= 1
                && !player.horizontalCollision
                && !player.verticalCollision;
    }

    @Override
    public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
        if (ridingZipline == null) {
            return;
        }
        Zipline zipline = ridingZipline.getZipline();
        Vector3d deltaMovement = player.getDeltaMovement();
        acceleration = 0;
        currentT = zipline.getParameter(player.position());
        currentPos = zipline.getMidPoint(currentT);
        slope = zipline.getSlope(currentT);
        Vector3d speedScale;
        {
            float yScale = (float) slope;
            Vector3d pointsOffset = zipline.getOffsetToEndFromStart();
            double xzLenInvSqrt = MathHelper.fastInvSqrt(pointsOffset.x() * pointsOffset.x() + pointsOffset.z() * pointsOffset.z());
            double xScale = pointsOffset.x() * xzLenInvSqrt;
            double zScale = pointsOffset.z() * xzLenInvSqrt;
            speedScale = new Vector3d(xScale, yScale, zScale).normalize();
        }
        speed = deltaMovement.dot(speedScale);

        parkourability.getBehaviorEnforcer().setMarkerEnforceMovePoint(
                () -> this.isDoing() && ridingZipline != null && !player.horizontalCollision && !player.verticalCollision,
                () -> {
                    if (currentPos == null) return null;
                    return currentPos.subtract(0, player.getBbHeight() * 1.11, 0);
                }
        );
        Animation animation = Animation.get(player);
        if (animation == null) return;
        animation.setAnimator(new RideZiplineAnimator());
    }

    @Override
    public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation = Animation.get(player);
        if (animation == null) return;
        animation.setAnimator(new RideZiplineAnimator());
    }

    @Override
    public void onWorkingTickInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        if (ridingZipline == null) return;
        double oldSpeed = speed;
        Zipline zipline = ridingZipline.getZipline();

        double gravity = player.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
        slope = zipline.getSlope(currentT);
        speed *= 0.98;
        speed -= gravity * slope * (MathHelper.fastInvSqrt(slope * slope + 1));
        Vector3d input = new Vector3d(
                (KeyBindings.getKeyRight().isDown() ? 1. : 0.) + (KeyBindings.getKeyLeft().isDown() ? -1. : 0.),
                0.,
                (KeyBindings.getKeyForward().isDown() ? 1. : 0.) + (KeyBindings.getKeyBack().isDown() ? -1. : 0.)
        );
        Vector3d offset = zipline.getOffsetToEndFromStart();
        if (input.lengthSqr() > 0.01) {
            double dot = player.getLookAngle()
                    .yRot((float) Math.toRadians(VectorUtil.toYawDegree(input)))
                    .multiply(1, 0, 1)
                    .normalize()
                    .dot(new Vector3d(offset.x(), 0, offset.z()).normalize());
            speed += dot * 0.01;
        }
        currentT = (float) zipline.getMovedPositionByParameterApproximately(currentT, (float) speed);
        currentPos = zipline.getMidPoint(currentT);
        acceleration = speed - oldSpeed;
    }

    @Override
    public void saveSynchronizedState(ByteBuffer buffer) {
        buffer.putDouble(acceleration);
        buffer.putDouble(slope);
    }

    @Override
    public void restoreSynchronizedState(ByteBuffer buffer) {
        acceleration = buffer.getDouble();
        slope = buffer.getDouble();
    }

    @Override
    public void onWorkingTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        player.fallDistance = 0;
    }

    @Override
    public void onStopInLocalClient(PlayerEntity player) {
        if (ridingZipline != null) {
            Zipline zipline = ridingZipline.getZipline();
            Vector3d speedScale;
            {
                float yScale = zipline.getSlope(currentT);
                Vector3d pointsOffset = zipline.getOffsetToEndFromStart();
                double xzLenInvSqrt = MathHelper.fastInvSqrt(pointsOffset.x() * pointsOffset.x() + pointsOffset.z() * pointsOffset.z());
                double xScale = pointsOffset.x() * xzLenInvSqrt;
                double zScale = pointsOffset.z() * xzLenInvSqrt;
                speedScale = new Vector3d(xScale, yScale, zScale).normalize();
            }
            player.setDeltaMovement(
                    speedScale.scale(speed).add(0, KeyBindings.getKeyJump().isDown() ? 0.1 : 0, 0)
            );
        }
        currentT = 0;
        currentPos = null;
        acceleration = 0;
        speed = 0;
        slope = 0;
    }

    @Override
    public void onStop(PlayerEntity player) {
        ridingZipline = null;
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnWorking;
    }
}
