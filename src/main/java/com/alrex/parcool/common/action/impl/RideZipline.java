package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.Animation;
import com.alrex.parcool.client.animation.impl.RideZiplineAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class RideZipline extends Action {
    private static final BehaviorEnforcer.ID ID_FALL_FLY_CANCEL = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_SPRINT_CANCEL = BehaviorEnforcer.newID();
    @Nullable
    private ZiplineRopeEntity ridingZipline;
    @Nullable
    private Vec3 endOffsetFromStart;
    private double speed;
    private double acceleration;
    private double slope;
    private float currentT;
    @Nullable
    private Vec3 currentPos;

    public double getAcceleration() {
        return acceleration;
    }

    public double getSlope() {
        return slope;
    }

    @Nullable
    public Vec3 getEndOffsetFromStart() {
        return endOffsetFromStart;
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        if (KeyBindings.getKeyRideZipline().isDown()
                && !player.onGround()
                && !player.isInWater()
                && !player.isFallFlying()
                && !player.isCrouching()
                && !player.isSwimming()
                && (!KeyBindings.isKeyJumpDown() || getNotDoingTick() > 5)
                && !parkourability.get(Dive.class).isDoing()
                && !parkourability.get(Vault.class).isDoing()
                && !parkourability.get(HangDown.class).isDoing()
                && !parkourability.get(Flipping.class).isDoing()
                && !parkourability.get(HorizontalWallRun.class).isDoing()
                && !parkourability.get(VerticalWallRun.class).isDoing()
        ) {
            ZiplineRopeEntity ropeEntity = Zipline.getHangableZipline(player.level(), player);
            if (ropeEntity == null) return false;
            double t = ropeEntity.getZipline().getParameter(player.position());
            if (t < 0 || 1 < t) return false;
            ridingZipline = ropeEntity;
            BufferUtil.wrap(startInfo).putVec3(ridingZipline.getZipline().getOffsetToEndFromStart());
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return KeyBindings.getKeyRideZipline().isDown()
                && !KeyRecorder.keyJumpState.isPressed()
                && !player.isInWall()
                && !player.getData(Attachments.STAMINA).isExhausted()
                && ridingZipline != null
                && ridingZipline.isAlive()
                && 0 <= currentT && currentT <= 1
                && !player.horizontalCollision
                && !player.verticalCollision;
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        if (ridingZipline == null) {
            return;
        }
        rideNewZipline(ridingZipline, player.position(), player.getDeltaMovement());

        player.setSprinting(false);

        parkourability.getBehaviorEnforcer().setMarkerEnforceMovePoint(
                this::isDoing,
                () -> {
                    if (currentPos == null) return null;
                    return currentPos.subtract(0, player.getBbHeight() * 1.11, 0);
                }
        );
        parkourability.getBehaviorEnforcer().addMarkerCancellingSprint(ID_SPRINT_CANCEL, this::isDoing);
        Animation animation = Animation.get(player);
        if (animation == null) return;
        animation.setAnimator(new RideZiplineAnimator());
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation = Animation.get(player);
        if (animation == null) return;
        animation.setAnimator(new RideZiplineAnimator());
    }

    @Override
    public void onStart(Player player, Parkourability parkourability, ByteBuffer startData) {
        endOffsetFromStart = BufferUtil.getVec3(startData);
        player.setSprinting(false);
        parkourability.getBehaviorEnforcer().addMarkerCancellingFallFlying(ID_FALL_FLY_CANCEL, this::isDoing);
    }

    @Override
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        if (ridingZipline == null) return;
        double oldSpeed = speed;
        Zipline zipline = ridingZipline.getZipline();

        double gravity = player.getAttributeValue(Attributes.GRAVITY);
        slope = zipline.getSlope(currentT);
        speed *= 0.98;
        if (player.isInWater()) speed *= 0.8;
        speed -= gravity * slope * (Mth.invSqrt(slope * slope + 1));
        Vec3 input = new Vec3(
                (KeyBindings.isKeyRightDown() ? 1. : 0.) + (KeyBindings.isKeyLeftDown() ? -1. : 0.),
                0.,
                (KeyBindings.isKeyForwardDown() ? 1. : 0.) + (KeyBindings.isKeyBackDown() ? -1. : 0.)
        );
        Vec3 offset = zipline.getOffsetToEndFromStart();
        if (input.lengthSqr() > 0.01) {
            double dot = player.getLookAngle()
                    .yRot((float) Math.toRadians(VectorUtil.toYawDegree(input)))
                    .multiply(1, 0, 1)
                    .normalize()
                    .dot(new Vec3(offset.x(), 0, offset.z()).normalize());
            speed += dot * 0.01;
        }
        currentT = (float) zipline.getMovedPositionByParameterApproximately(currentT, (float) speed);
        acceleration = speed - oldSpeed;
        currentPos = zipline.getMidPoint(currentT);
    }

    private void rideNewZipline(ZiplineRopeEntity ziplineRopeEntity, Vec3 position, Vec3 deltaMovement) {
        ridingZipline = ziplineRopeEntity;
        Zipline zipline = ziplineRopeEntity.getZipline();
        acceleration = 0;
        currentT = Mth.clamp(zipline.getParameter(position), 0, 1);
        currentPos = zipline.getMidPoint(currentT);
        slope = zipline.getSlope(currentT);
        Vec3 speedScale;
        {
            float yScale = (float) slope;
            Vec3 pointsOffset = zipline.getOffsetToEndFromStart();
            double xzLenInvSqrt = Mth.fastInvSqrt(pointsOffset.x() * pointsOffset.x() + pointsOffset.z() * pointsOffset.z());
            double xScale = pointsOffset.x() * xzLenInvSqrt;
            double zScale = pointsOffset.z() * xzLenInvSqrt;
            speedScale = new Vec3(xScale, yScale, zScale).normalize();
        }
        speed = deltaMovement.dot(speedScale);
    }

    private static Vec3 getDeltaMovement(Zipline zipline, double speed, float currentT) {
        Vec3 speedScale;
        {
            float yScale = zipline.getSlope(currentT);
            Vec3 pointsOffset = zipline.getOffsetToEndFromStart();
            double xzLenInvSqrt = Mth.fastInvSqrt(pointsOffset.x() * pointsOffset.x() + pointsOffset.z() * pointsOffset.z());
            double xScale = pointsOffset.x() * xzLenInvSqrt;
            double zScale = pointsOffset.z() * xzLenInvSqrt;
            speedScale = new Vec3(xScale, yScale, zScale).normalize();
        }
        return speedScale.scale(speed);
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
    public void onWorkingTick(Player player, Parkourability parkourability) {
        player.fallDistance = 0;
        player.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void onStopInLocalClient(Player player) {
        if (ridingZipline != null) {
            player.setDeltaMovement(
                    getDeltaMovement(ridingZipline.getZipline(), speed, currentT)
                            .add(0, KeyBindings.isKeyJumpDown() ? 0.25 : 0, 0)
            );
        }
        currentT = 0;
        currentPos = null;
        acceleration = 0;
        speed = 0;
        slope = 0;
    }


    @Override
    public void onStop(Player player) {
        ridingZipline = null;
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnWorking;
    }
}
