package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.RideZipline;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import org.joml.Vector3f;

public class RideZiplineAnimator extends Animator {
    private double oldAngleRadian = 0;
    private double currentAngleRadian = 0;

    @Override
    public void tick(Player player) {
        super.tick(player);

        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        RideZipline action = parkourability.get(RideZipline.class);
        if (!action.isDoing()) return;
        oldAngleRadian = currentAngleRadian;
        double acceleration = action.getAcceleration();
        double slope = action.getSlope();
        double gravity = player.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
        double invSqrt = Mth.fastInvSqrt(slope * slope + 1);
        double xz = -acceleration * invSqrt;
        double y = gravity + acceleration * slope * invSqrt;
        currentAngleRadian = Mth.lerp(0.1, oldAngleRadian, Math.atan2(xz, y));
    }

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(RideZipline.class).isDoing();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        var offset = parkourability.get(RideZipline.class).getEndOffsetFromStart();
        if (offset == null) return;
        double angleDifference = VectorUtil.toYawRadian(player.getLookAngle()) - VectorUtil.toYawRadian(new Vec3(offset.x(), 0, offset.z()));
        double angleCos = Math.cos(angleDifference);
        double angleSin = Math.sin(angleDifference);
        double angleCosAbs = Math.abs(angleCos);
        transformer
                .translateRightArm(0.6f, -2.2f, 0)
                .translateLeftArm(-0.6f, -2.2f, 0)
                .rotateRightArm((float) -Math.PI, 0f, (float) Math.toRadians(Mth.lerp(angleCosAbs, 10., 15.)))
                .rotateLeftArm((float) -Math.PI, 0f, (float) Math.toRadians(Mth.lerp(angleCosAbs, -10., -15.)))
                .rotateRightLeg((float) (-currentAngleRadian * angleCos), 0, (float) (-currentAngleRadian * angleSin))
                .rotateLeftLeg((float) (-currentAngleRadian * angleCos), 0, (float) (-currentAngleRadian * angleSin))
                .makeLegsLittleMoving()
                .end();
    }

    @Override
    public void onRenderTick(TickEvent.RenderTickEvent event, Player player, Parkourability parkourability) {
        player.yBodyRot = player.getYRot();
        player.yBodyRotO = player.yRotO;
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        Vec3 offset = parkourability.get(RideZipline.class).getEndOffsetFromStart();
        if (offset == null) return;
        Vec3 rotationAxis = new Vec3(0, 0, 1)
                .yRot((float) (Math.PI / 2 + VectorUtil.toYawRadian(player.getLookAngle()) - VectorUtil.toYawRadian(new Vec3(offset.x(), 0, offset.z()))))
                .normalize();
        double angle = Mth.lerp(rotator.getPartialTick(), oldAngleRadian, currentAngleRadian);
        rotator.startBasedTop()
                .rotate((float) angle, new Vector3f((float) rotationAxis.x(), 0f, (float) rotationAxis.z()))
                .end();
    }
}
