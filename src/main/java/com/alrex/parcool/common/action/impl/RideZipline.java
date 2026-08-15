package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.client.sound.ZiplineUseSoundInstance;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.common.damage.DamageSources;
import com.alrex.parcool.common.item.armor.TraceurGlovesItem;
import com.alrex.parcool.common.zipline.ILoadedZiplineHolderProvider;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;

public class RideZipline extends ContinuableAction implements ActionExtension.KeyMapTriggeredListener {
    private static final BehaviorEnforcer.ID ID_FALL_FLY_CANCEL = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_SPRINT_CANCEL = BehaviorEnforcer.newID();

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Float> propertyAcceleration;
    private final SynchronizedProperty<Float> propertySlope;
    private final SynchronizedProperty<Vec3> propertyHorizontalZiplineOffset;
    private final SynchronizedProperty<Boolean> propertyZiplinePowered;

    // Only for client
    private float oldAngleRadian;
    private float currentAngleRadian;
    // Only for local client
    @Nullable
    private Zipline ridingZipline;
    @Nullable
    private Vec3 currentPos;
    private double speed;
    private float currentT;
    private boolean previouslyStopByCollision = false;

    public RideZipline(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(
                ParCoolActions.VAULT,
                ParCoolActions.HANG_ON,
                ParCoolActions.HANG_DOWN,
                ParCoolActions.HORIZONTAL_WALL_RUN
        ));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyAcceleration = SynchronizedProperty.newFloat(),
                propertySlope = SynchronizedProperty.newFloat(),
                propertyHorizontalZiplineOffset = SynchronizedProperty.newVec3Horizontal(),
                propertyZiplinePowered = SynchronizedProperty.newBoolean()
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canStart() {
        if (!ParCoolKeyBinds.HANG.key().isDown()
                || (getNotDoingTick() <= 5 && (ParCoolKeyBinds.JUMP.state().isDown() || previouslyStopByCollision))
        ) return false;
        var player = parkourability.player();
        if (!(player.getLevel() instanceof ClientLevel clientLevel)) return false;
        var zipline = Zipline.getHangAbleZipline(clientLevel, player);
        if (zipline == null) return false;
        double t = zipline.shape().getParameter(player.position());
        if (t < 0 || 1 < t) return false;
        rideNewZipline(zipline, t, player.getDeltaMovement());
        return true;
    }

    @Override
    public boolean canContinue() {
        var player = parkourability.player();
        if (player.horizontalCollision || player.verticalCollision) {
            previouslyStopByCollision = true;
            return false;
        }

        return ParCoolKeyBinds.HANG.state().isDown()
                && !ParCoolKeyBinds.JUMP.state().isJustPressed()
                && ridingZipline != null
                && 0 <= currentT && currentT <= 1;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInLocalClient() {
        if (ridingZipline == null) {
            return;
        }
        if (!(parkourability.player() instanceof LocalPlayer player)) return;
        parkourability.getBehaviorEnforcer().setMarkerEnforcingMovePoint(
                this::isDoing,
                () -> {
                    if (currentPos == null) return null;
                    return new Vec3(currentPos.x, currentPos.y - player.getBbHeight() * 1.1, currentPos.z);
                }
        );
        parkourability.getBehaviorEnforcer().addMarkerEnforcingNoSprint(ID_SPRINT_CANCEL, this::isDoing);
        parkourability.getBehaviorEnforcer().addMarkerEnforcingNoFallFlying(ID_FALL_FLY_CANCEL, this::isDoing);
        Minecraft.getInstance().getSoundManager().play(new ZiplineUseSoundInstance(player, this));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        currentAngleRadian = oldAngleRadian = 0;
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().RIDE_ZIPLINE);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onWorkingTickInClient() {
        var player = parkourability.player();
        updateAngle();
        if (propertyZiplinePowered.getOrDefaultIfNull(false)) {
            var playerPos = player.position();
            var particlePos = new Vec3(player.xo, player.yo + 1.1 * player.getBbHeight(), player.zo);
            var posDiffX = player.xo - playerPos.x;
            var posDiffZ = player.zo - playerPos.z;
            player.level.addParticle(
                    DustParticleOptions.REDSTONE,
                    particlePos.x, particlePos.y, particlePos.z,
                    -0.5 * posDiffX, 0.25 + Math.hypot(posDiffX, posDiffZ) * propertySlope.getOrDefaultIfNull(0f) * 0.5, -0.5 * posDiffZ
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onWorkingTickInLocalClient() {
        if (ridingZipline == null) return;
        if (!(parkourability.player() instanceof LocalPlayer player)) return;
        if (player.level instanceof ILoadedZiplineHolderProvider provider) {
            if (!provider.getZiplineHolder().checkAlive(ridingZipline)) {
                ridingZipline = null;
                return;
            }
        }

        var speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) return;

        double oldSpeed = speed;
        double gravity = player.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
        float slope = ridingZipline.shape().getSlope(currentT);
        Vec3 offset = ridingZipline.shape().getOffsetFromStartToEnd();
        Vec3 offsetNormalized = new Vec3(offset.x(), 0, offset.z()).normalize();
        var lookAngle = EntityUtil.getHorizontalLookAngle(player);
        double moveInputScale = lookAngle.scale(player.input.forwardImpulse)
                .add(lookAngle.yRot(Mth.HALF_PI).scale(player.input.leftImpulse))
                .normalize()
                .dot(offsetNormalized);
        speed *= player.isInFluidType() ? 0.8 : 0.98;
        speed += (ridingZipline.powered() ? 0.04 : 0) * lookAngle.dot(offsetNormalized)
                + Math.min(moveInputScale * 0.01 * (speedAttr.getValue() / speedAttr.getBaseValue()), 0.08)
                - gravity * slope * (Mth.fastInvSqrt(slope * slope + 1));
        currentT = (float) ridingZipline.shape().getMovedPositionByParameterApproximately(currentT, (float) speed);
        currentPos = ridingZipline.shape().getMidPoint(currentT);
        propertySlope.set(slope);
        propertyAcceleration.set((float) (speed - oldSpeed));
        propertyZiplinePowered.set(ridingZipline.powered());
    }

    @Override
    public void onWorkingTickInServer() {
        if (!propertyZiplinePowered.getOrDefaultIfNull(false)) return;
        if (!ParCool.getConfig().server().damageWithoutGlove.get()) return;
        var player = parkourability.player();
        if (player.tickCount % 4 == 0 && !TraceurGlovesItem.isEquipped(player)) {
            var tmp = player.invulnerableTime;
            player.invulnerableTime = 0;
            player.hurt(DamageSources.FRICTION, 0.3f);
            player.invulnerableTime = tmp;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void rideNewZipline(Zipline zipline, double t, Vec3 deltaMovement) {
        ridingZipline = zipline;
        var shape = zipline.shape();
        currentT = (float) t;
        currentPos = shape.getMidPoint(currentT);
        var slope = shape.getSlope(currentT);
        propertyAcceleration.set(0f);
        propertySlope.set(slope);
        propertyHorizontalZiplineOffset.set(ridingZipline.shape().getOffsetFromStartToEnd().multiply(1, 0, 1));
        Vec3 speedScale;
        {
            Vec3 pointsOffset = shape.getOffsetFromStartToEnd();
            double xzLenInvSqrt = Mth.fastInvSqrt(pointsOffset.x() * pointsOffset.x() + pointsOffset.z() * pointsOffset.z());
            double xScale = pointsOffset.x() * xzLenInvSqrt;
            double zScale = pointsOffset.z() * xzLenInvSqrt;
            speedScale = new Vec3(xScale, slope, zScale).normalize();
        }
        speed = deltaMovement.dot(speedScale);
    }

    @Override
    public void onWorkingTick() {
        parkourability.player().fallDistance = 0;
    }

    @Nullable
    public Vec3 getZiplineOffset() {
        return propertyHorizontalZiplineOffset.get();
    }

    public float getBodyAngle(float partial) {
        return Mth.lerp(partial, oldAngleRadian, currentAngleRadian);
    }

    @OnlyIn(Dist.CLIENT)
    public void updateAngle() {
        oldAngleRadian = currentAngleRadian;
        double acceleration = propertyAcceleration.getOrDefaultIfNull(0f);
        double slope = propertySlope.getOrDefaultIfNull(0f);
        double gravity = parkourability.player().getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
        double invSqrt = Mth.fastInvSqrt(slope * slope + 1);
        double xz = -acceleration * invSqrt;
        double y = gravity + acceleration * slope * invSqrt;
        currentAngleRadian = (float) Mth.lerp(0.1, oldAngleRadian, Math.atan2(xz, y));
    }

    @Override
    public void onInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (isDoing() && (event.isAttack() || event.isUseItem())) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }
}
