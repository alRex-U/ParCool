package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.RideZiplineAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

public class RideZipline extends Action {
    private static final BehaviorEnforcer.ID ID_FALL_FLY_CANCEL = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_SPRINT_CANCEL = BehaviorEnforcer.newID();
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
                && !player.isFallFlying()
                && !player.isCrouching()
                && !player.isSwimming()
                && !parkourability.get(Dive.class).isDoing()
                && !parkourability.get(Vault.class).isDoing()
                && !parkourability.get(HangDown.class).isDoing()
                && !parkourability.get(Flipping.class).isDoing()
                && !parkourability.get(HorizontalWallRun.class).isDoing()
                && !parkourability.get(VerticalWallRun.class).isDoing()
        ) {
            ZiplineRopeEntity ropeEntity = Zipline.getHangableZipline(player.level, player);
            if (ropeEntity == null) return false;
            double t = ropeEntity.getZipline().getParameter(player.position());
            if (t < 0 || 1 < t) return false;
            ridingZipline = ropeEntity;
            startInfo.putInt(ridingZipline.getStartPos().getX())
                    .putInt(ridingZipline.getStartPos().getY())
                    .putInt(ridingZipline.getStartPos().getZ())
                    .putInt(ridingZipline.getEndPos().getX())
                    .putInt(ridingZipline.getEndPos().getY())
                    .putInt(ridingZipline.getEndPos().getZ());
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
    public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
        BlockPos start = new BlockPos(startData.getInt(), startData.getInt(), startData.getInt());
        BlockPos end = new BlockPos(startData.getInt(), startData.getInt(), startData.getInt());
        List<ZiplineRopeEntity> entities = player.level.getEntitiesOfClass(
                ZiplineRopeEntity.class,
                player.getBoundingBox().inflate(Zipline.MAXIMUM_DISTANCE * 0.52)
        );
        Optional<ZiplineRopeEntity> entity = entities.stream().filter(it -> (it.getStartPos().equals(start) && it.getEndPos().equals(end)) || (it.getEndPos().equals(start) && it.getStartPos().equals(end))).findAny();
        if (entity.isPresent()) {
            ridingZipline = entity.get();
            Animation animation = Animation.get(player);
            if (animation == null) return;
            animation.setAnimator(new RideZiplineAnimator());
        }
    }

    @Override
    public void onStart(PlayerEntity player, Parkourability parkourability) {
        player.setSprinting(false);
        parkourability.getBehaviorEnforcer().addMarkerCancellingFallFlying(ID_FALL_FLY_CANCEL, this::isDoing);
    }

    @Override
    public void onWorkingTickInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        if (ridingZipline == null) return;
        double oldSpeed = speed;
        Zipline zipline = ridingZipline.getZipline();

        double gravity = player.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
        slope = zipline.getSlope(currentT);
        speed *= 0.98;
        if (player.isInWater()) speed *= 0.8;
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
        acceleration = speed - oldSpeed;
        currentPos = zipline.getMidPoint(currentT);
    }

    private void rideNewZipline(ZiplineRopeEntity ziplineRopeEntity, Vector3d position, Vector3d deltaMovement) {
        ridingZipline = ziplineRopeEntity;
        Zipline zipline = ziplineRopeEntity.getZipline();
        acceleration = 0;
        currentT = MathHelper.clamp(zipline.getParameter(position), 0, 1);
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
    }

    private static Vector3d getDeltaMovement(Zipline zipline, double speed, float currentT) {
        Vector3d speedScale;
        {
            float yScale = zipline.getSlope(currentT);
            Vector3d pointsOffset = zipline.getOffsetToEndFromStart();
            double xzLenInvSqrt = MathHelper.fastInvSqrt(pointsOffset.x() * pointsOffset.x() + pointsOffset.z() * pointsOffset.z());
            double xScale = pointsOffset.x() * xzLenInvSqrt;
            double zScale = pointsOffset.z() * xzLenInvSqrt;
            speedScale = new Vector3d(xScale, yScale, zScale).normalize();
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
    public void onWorkingTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        player.fallDistance = 0;
        player.setDeltaMovement(Vector3d.ZERO);
    }

    @Override
    public void onStopInLocalClient(PlayerEntity player) {
        if (ridingZipline != null) {
            player.setDeltaMovement(
                    getDeltaMovement(ridingZipline.getZipline(), speed, currentT)
                            .add(0, KeyBindings.getKeyJump().isDown() ? 0.2 : 0, 0)
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
