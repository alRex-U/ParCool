package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.RideZiplineAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class RideZipline extends Action {
    private static final BehaviorEnforcer.ID ID_FALL_FLY_CANCEL = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_SPRINT_CANCEL = BehaviorEnforcer.newID();
    @Nullable
    private ZiplineRopeEntity ridingZipline;
    @Nullable
    private Vec3Wrapper endOffsetFromStart;
    private double speed;
    private double acceleration;
    private double slope;
    private float currentT;
    @Nullable
    private Vec3Wrapper currentPos;

    public double getAcceleration() {
        return acceleration;
    }

    public double getSlope() {
        return slope;
    }

    @Nullable
    public Vec3Wrapper getEndOffsetFromStart() {
        return endOffsetFromStart;
    }

    @Override
    public boolean canStart(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
        if (KeyBindings.getKeyBindRideZipline().isDown()
                && !player.isOnGround()
                && !player.isInWater()
                && !player.isFallFlying()
                && !player.isCrouching()
                && !player.isSwimming()
                && !stamina.isExhausted()
                && (!KeyBindings.isKeyJumpDown() || getNotDoingTick() > 5)
                && !parkourability.get(Dive.class).isDoing()
                && !parkourability.get(Vault.class).isDoing()
                && !parkourability.get(HangDown.class).isDoing()
                && !parkourability.get(Flipping.class).isDoing()
                && !parkourability.get(HorizontalWallRun.class).isDoing()
                && !parkourability.get(VerticalWallRun.class).isDoing()
        ) {
            ZiplineRopeEntity ropeEntity = Zipline.getHangableZipline(player.getLevel(), player);
            if (ropeEntity == null) return false;
            double t = ropeEntity.getZipline().getParameter(player.position());
            if (t < 0 || 1 < t) return false;
            ridingZipline = ropeEntity;
            BufferUtil.wrap(startInfo).putVector3d(ridingZipline.getZipline().getOffsetToEndFromStart());
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinue(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
        return KeyBindings.getKeyBindRideZipline().isDown()
                && !KeyRecorder.keyJumpState.isPressed()
                && !player.isInWall()
                && !stamina.isExhausted()
                && ridingZipline != null
                && ridingZipline.isAlive()
                && 0 <= currentT && currentT <= 1
                && !player.hasSomeCollision();
    }

    @Override
    public void onStartInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
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
    public void onStartInOtherClient(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation = Animation.get(player);
        if (animation == null) return;
        animation.setAnimator(new RideZiplineAnimator());
    }

    @Override
    public void onStart(PlayerWrapper player, Parkourability parkourability, ByteBuffer startData) {
        endOffsetFromStart = BufferUtil.getVector3d(startData);
        player.setSprinting(false);
        parkourability.getBehaviorEnforcer().addMarkerCancellingFallFlying(ID_FALL_FLY_CANCEL, this::isDoing);
    }

    @Override
    public void onWorkingTickInLocalClient(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
        if (ridingZipline == null) return;
        double oldSpeed = speed;
        Zipline zipline = ridingZipline.getZipline();

        double gravity = player.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
        slope = zipline.getSlope(currentT);
        speed *= 0.98;
        if (player.isInWater()) speed *= 0.8;
        speed -= gravity * slope * (MathHelper.fastInvSqrt(slope * slope + 1));
        Vec3Wrapper input = new Vec3Wrapper(
                (KeyBindings.isKeyRightDown() ? 1. : 0.) + (KeyBindings.isKeyLeftDown() ? -1. : 0.),
                0.,
                (KeyBindings.isKeyForwardDown() ? 1. : 0.) + (KeyBindings.isKeyBackDown() ? -1. : 0.)
        );
        Vec3Wrapper offset = zipline.getOffsetToEndFromStart();
        if (input.lengthSqr() > 0.01) {
            double dot = player.getLookAngle()
                    .yRot((float) Math.toRadians(VectorUtil.toYawDegree(input)))
                    .multiply(1, 0, 1)
                    .normalize()
                    .dot(new Vec3Wrapper(offset.x(), 0, offset.z()).normalize());
            speed += dot * 0.01;
        }
        currentT = (float) zipline.getMovedPositionByParameterApproximately(currentT, (float) speed);
        acceleration = speed - oldSpeed;
        currentPos = zipline.getMidPoint(currentT);
    }

    private void rideNewZipline(ZiplineRopeEntity ziplineRopeEntity, Vec3Wrapper position, Vec3Wrapper deltaMovement) {
        ridingZipline = ziplineRopeEntity;
        Zipline zipline = ziplineRopeEntity.getZipline();
        acceleration = 0;
        currentT = MathHelper.clamp(zipline.getParameter(position), 0, 1);
        currentPos = zipline.getMidPoint(currentT);
        slope = zipline.getSlope(currentT);
        Vec3Wrapper speedScale;
        {
            float yScale = (float) slope;
            Vec3Wrapper pointsOffset = zipline.getOffsetToEndFromStart();
            double xzLenInvSqrt = MathHelper.fastInvSqrt(pointsOffset.x() * pointsOffset.x() + pointsOffset.z() * pointsOffset.z());
            double xScale = pointsOffset.x() * xzLenInvSqrt;
            double zScale = pointsOffset.z() * xzLenInvSqrt;
            speedScale = new Vec3Wrapper(xScale, yScale, zScale).normalize();
        }
        speed = deltaMovement.dot(speedScale);
    }

    private static Vec3Wrapper getDeltaMovement(Zipline zipline, double speed, float currentT) {
        Vec3Wrapper speedScale;
        {
            float yScale = zipline.getSlope(currentT);
            Vec3Wrapper pointsOffset = zipline.getOffsetToEndFromStart();
            double xzLenInvSqrt = MathHelper.fastInvSqrt(pointsOffset.x() * pointsOffset.x() + pointsOffset.z() * pointsOffset.z());
            double xScale = pointsOffset.x() * xzLenInvSqrt;
            double zScale = pointsOffset.z() * xzLenInvSqrt;
            speedScale = new Vec3Wrapper(xScale, yScale, zScale).normalize();
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
    public void onWorkingTick(PlayerWrapper player, Parkourability parkourability, IStamina stamina) {
        player.resetFallDistance();
        player.setDeltaMovement(Vec3Wrapper.ZERO);
    }

    @Override
    public void onStopInLocalClient(PlayerWrapper player) {
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
    public void onStop(PlayerWrapper player) {
        ridingZipline = null;
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnWorking;
    }
}
