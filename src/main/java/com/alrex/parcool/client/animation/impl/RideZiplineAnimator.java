package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.RideZipline;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;

public class RideZiplineAnimator extends Animator {
    private double oldAngleRadian = 0;
    private double currentAngleRadian = 0;

    @Override
    public void tick(PlayerEntity player) {
        super.tick(player);

        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        RideZipline action = parkourability.get(RideZipline.class);
        if (!action.isDoing()) return;
        oldAngleRadian = currentAngleRadian;
        double acceleration = action.getAcceleration();
        double slope = action.getSlope();
        double gravity = player.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
        double invSqrt = MathHelper.fastInvSqrt(slope * slope + 1);
        double xz = -acceleration * invSqrt;
        double y = gravity + acceleration * slope * invSqrt;
        currentAngleRadian = MathHelper.lerp(0.1, oldAngleRadian, Math.atan2(xz, y));
    }

    @Override
    public boolean shouldRemoved(PlayerEntity player, Parkourability parkourability) {
        return !parkourability.get(RideZipline.class).isDoing();
    }

    @Override
    public void animatePost(PlayerEntity player, Parkourability parkourability, PlayerModelTransformer transformer) {
        Vector3d offset = parkourability.get(RideZipline.class).getEndOffsetFromStart();
        if (offset == null) return;
        double angleDifference = VectorUtil.toYawRadian(player.getLookAngle()) - VectorUtil.toYawRadian(new Vector3d(offset.x(), 0, offset.z()));
        double angleCos = Math.cos(angleDifference);
        double angleSin = Math.sin(angleDifference);
        double angleCosAbs = Math.abs(angleCos);
        transformer
                .translateRightArm(0.6f, -2.2f, 0)
                .translateLeftArm(-0.6f, -2.2f, 0)
                .rotateRightArm((float) -Math.PI, 0f, (float) Math.toRadians(MathHelper.lerp(angleCosAbs, 10., 15.)))
                .rotateLeftArm((float) -Math.PI, 0f, (float) Math.toRadians(MathHelper.lerp(angleCosAbs, -10., -15.)))
                .rotateRightLeg((float) (-currentAngleRadian * angleCos), 0, (float) (-currentAngleRadian * angleSin))
                .rotateLeftLeg((float) (-currentAngleRadian * angleCos), 0, (float) (-currentAngleRadian * angleSin))
                .makeLegsLittleMoving()
                .end();
    }

    @Override
    public void onRenderTick(TickEvent.RenderTickEvent event, PlayerEntity player, Parkourability parkourability) {
        player.yBodyRot = player.yRot;
        player.yBodyRotO = player.yRotO;
    }

    @Override
    public void rotatePost(PlayerEntity player, Parkourability parkourability, PlayerModelRotator rotator) {
        Vector3d offset = parkourability.get(RideZipline.class).getEndOffsetFromStart();
        if (offset == null) return;
        Vector3d rotationAxis = new Vector3d(0, 0, 1)
                .yRot((float) (Math.PI / 2 + VectorUtil.toYawRadian(player.getLookAngle()) - VectorUtil.toYawRadian(new Vector3d(offset.x(), 0, offset.z()))))
                .normalize();
        double angle = MathHelper.lerp(rotator.getPartialTick(), oldAngleRadian, currentAngleRadian);
        rotator.startBasedTop()
                .rotate((float) angle, new Vector3f((float) rotationAxis.x(), 0f, (float) rotationAxis.z()))
                .end();
    }
}
