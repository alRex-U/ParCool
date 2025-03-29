package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class RideZipline extends Action {
    @Nullable
    private ZiplineRopeEntity ridingZipline;
    private double speed;
    private float currentT;
    @Nullable
    private Vector3d currentPos;

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
        currentT = zipline.getParameter(player.position());
        Vector3d speedScale;
        {
            float yScale = zipline.getSlope(currentT);
            Vector3f pointsOffset = zipline.getOffsetToEndFromStart();
            float xzLenInvSqrt = MathHelper.fastInvSqrt(pointsOffset.x() * pointsOffset.x() + pointsOffset.z() * pointsOffset.z());
            float xScale = pointsOffset.x() * xzLenInvSqrt;
            float zScale = pointsOffset.z() * xzLenInvSqrt;
            speedScale = new Vector3d(xScale, yScale, zScale).normalize();
        }
        speed = deltaMovement.dot(speedScale);

        parkourability.getBehaviorEnforcer().setMarkerEnforceMovePoint(
                () -> this.isDoing() && ridingZipline != null && !player.horizontalCollision && !player.verticalCollision,
                () -> {
                    Zipline zipline_ = ridingZipline.getZipline();

                    Vector3d movedPos = zipline_.getMidPoint(currentT).subtract(0, player.getBbHeight() * 1.11, 0);
                    if (currentPos == null) currentPos = movedPos;
                    Vector3d d = movedPos.subtract(player.position());

                    return movedPos;
                }
        );
    }

    @Override
    public void onWorkingTickInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        if (ridingZipline == null) return;
        Zipline zipline = ridingZipline.getZipline();

        double gravity = player.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
        float slope = zipline.getSlope(currentT);
        speed *= 0.98;
        speed -= gravity * slope * (MathHelper.fastInvSqrt(slope * slope + 1));
        currentT = (float) zipline.getMovedPositionByParameterApproximately(currentT, (float) speed);
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
                Vector3f pointsOffset = zipline.getOffsetToEndFromStart();
                float xzLenInvSqrt = MathHelper.fastInvSqrt(pointsOffset.x() * pointsOffset.x() + pointsOffset.z() * pointsOffset.z());
                float xScale = pointsOffset.x() * xzLenInvSqrt;
                float zScale = pointsOffset.z() * xzLenInvSqrt;
                speedScale = new Vector3d(xScale, yScale, zScale).normalize();
            }
            player.setDeltaMovement(speedScale.scale(speed));
        }
        currentT = 0;
        speed = 0;
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
