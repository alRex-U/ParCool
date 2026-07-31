package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.InteractingWallDirection;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.MathUtil;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class HangOn extends ContinuableAction implements ActionExtension.LeaveFromWallListener {
    private static final double REACH_SCALE = 0.25;
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<InteractingWallDirection> propertyDirection;
    private final SynchronizedProperty<Boolean> propertyFullWall;

    @Nullable
    private InteractingWallDirection oldDirection = null;
    // For Client
    private AnimationData currentAnimData = AnimationData.NONE;

    // Only for Local Client
    @Nullable
    private HangState currentHangState;
    @Nullable
    private HangState startingHangState;
    private short cooldown = 0;

    public HangOn(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(
                ParCoolActions.CLIMB_UP,
                ParCoolActions.DIVE,
                ParCoolActions.HANG_DOWN,
                ParCoolActions.CASTAWAY,
                ParCoolActions.POLE_CLIMB
        ));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyDirection = SynchronizedProperty.newEnum(InteractingWallDirection.class),
                propertyFullWall = SynchronizedProperty.newBoolean()
        );
    }

    @Override
    public boolean canStart() {
        return cooldown == 0 && ParCoolKeyBinds.HANG.key().isDown() && (startingHangState = getHangState()) != null;
    }

    @Override
    public boolean canContinue() {
        return cooldown == 0 && ParCoolKeyBinds.HANG.key().isDown() && currentHangState != null && !ParCoolKeyBinds.JUMP.state().isJustPressed();
    }

    @Override
    public void onStartInLocalClient() {
        if (!(parkourability.player() instanceof LocalPlayer player)) return;
        parkourability.getBehaviorEnforcer().setMarkerEnforcingMovePoint(
                this::isDoing, () -> {
                    if (currentHangState == null) return null;
                    var speed = currentHangState.fullWall
                            ? player.getSpeed() * MathUtil.mapLinear((float) currentHangState.direction.asVec().dot(player.getLookAngle().multiply(1, 0, 1).normalize()), -0.7071f, 1f, 0f, 1f)
                            : player.getSpeed();
                    var moveVec = player.input.getMoveVector().scale(speed);
                    var actualMoveVec = new Vec3(moveVec.x, 0, moveVec.y).yRot((float) Math.toRadians(-player.getYRot()));
                    if (currentHangState.direction.isProtrusion()) {
                        return player.position()
                                .add(new Vec3(
                                        currentHangState.direction.getSignX() > 0 ? Math.max(0, actualMoveVec.x) : Math.min(0, actualMoveVec.x),
                                        currentHangState.yCollisionDistance,
                                        currentHangState.direction.getSignZ() > 0 ? Math.max(0, actualMoveVec.z) : Math.min(0, actualMoveVec.z)
                                ));
                    } else if (currentHangState.direction.isOblique()) {
                        var directionVec = currentHangState.direction.asVec().yRot(Mth.HALF_PI);
                        return player.position()
                                .add(0, currentHangState.yCollisionDistance, 0)
                                .add(directionVec.scale(directionVec.dot(actualMoveVec)));
                    } else {
                        return player.position()
                                .add(currentHangState.direction.getSignX() * 0.2, currentHangState.yCollisionDistance, currentHangState.direction.getSignZ() * 0.2)
                                .add(actualMoveVec);
                    }
                }
        );
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().HANG_ON);
        parkourability.player().playSound(ParCoolSoundEvents.HANG_ON.get());
        oldDirection = propertyDirection.get();
    }

    @Override
    public void onWorkingTickInClient() {
        oldDirection = propertyDirection.get();
    }

    @Override
    public void onWorkingTickInLocalClient() {
        if (startingHangState != null) {
            currentHangState = startingHangState;
            startingHangState = null;
        } else {
            currentHangState = getHangState();
        }
        propertyDirection.set(currentHangState != null ? currentHangState.direction : null);
        propertyFullWall.set(currentHangState != null ? currentHangState.fullWall : null);

        onWorkingTickInOtherClient();
    }

    @Override
    public void onWorkingTickInOtherClient() {
        currentAnimData = AnimationData.get(this, this.parkourability.player(), currentAnimData);
    }

    @Override
    public void onTickInLocalClient() {
        if (cooldown > 0) cooldown--;
    }

    @Override
    public void onStopInLocalClient() {
        if (cooldown == 0 && currentHangState != null && ParCoolKeyBinds.JUMP.state().isJustPressed()) {
            if (currentHangState.direction().asVec().dot(EntityUtil.getHorizontalLookAngle(parkourability.player())) > 0.98
                    && ParCoolKeyBinds.getMovementInput(LogicalMovement.BACKWARD).isDown()
            ) {
                parkourability.request(ParCoolActions.CASTAWAY, new Castaway.RequestContext(this.currentHangState));
            } else {
                parkourability.request(ParCoolActions.CLIMB_UP, new ClimbUp.RequestContext(this.currentHangState));
            }
        }
    }

    @Nullable
    public Vec3 getWallVec(float partial) {
        var direction = propertyDirection.get();
        if (oldDirection == null) {
            if (direction == null) {
                return null;
            }
            return direction.asVec();
        }
        if (direction == null) return oldDirection.asVec();
        return VectorUtil.lerp(partial, oldDirection.asVec(), direction.asVec()).normalize();
    }

    public float getBlendFactorRightToWall() {
        return currentAnimData.blendFactorRightToWall;
    }

    public float getBlendFactorLeftToWall() {
        return currentAnimData.blendFactorLeftToWall;
    }

    public float getBlendFactorBackToWall() {
        return currentAnimData.blendFactorBackToWall;
    }

    public float getBlendFactorMovingLeft() {
        return currentAnimData.blendFactorMovementLeft;
    }

    @Override
    public void onLeaveFromWall() {
        cooldown = 3;
    }

    private record AnimationData(
            float blendFactorRightToWall,
            float blendFactorLeftToWall,
            float blendFactorBackToWall,
            float blendFactorMovementLeft
    ) {
        static final AnimationData NONE = new AnimationData(0, 0, 0, 0);

        public static AnimationData get(HangOn hangOn, Player player, AnimationData old) {
            var direction = hangOn.propertyDirection.get();
            if (direction == null) return NONE;
            var fullWall = hangOn.propertyFullWall.get();
            var wallVec = direction.asVec();
            var movementLeftBlendFactor =
                    getBlendFactorMovementLeft(
                            wallVec,
                            player.position().subtract(new Vec3(player.xo, player.yo, player.zo)),
                            old.blendFactorMovementLeft
                    );
            if (fullWall == null || !fullWall) {
                return new AnimationData(0, 0, 0, movementLeftBlendFactor);
            }
            var horizontalLookVec = EntityUtil.getHorizontalLookAngle(player);
            var dotOfWallVecLookVec = (float) horizontalLookVec.dot(wallVec);

            return new AnimationData(
                    getBlendFactorRightToWall(horizontalLookVec, wallVec, dotOfWallVecLookVec),
                    getBlendFactorLeftToWall(horizontalLookVec, wallVec, dotOfWallVecLookVec),
                    getBlendFactorBackToWall(horizontalLookVec, wallVec, dotOfWallVecLookVec),
                    movementLeftBlendFactor
            );
        }

        private static float getBlendFactorLeftToWall(Vec3 horizontalLookVec, Vec3 wallVec, float dotOfWallVecLookVec) {
            if (wallVec.yRot(Mth.HALF_PI).dot(horizontalLookVec) < 0) return 0;
            return MathUtil.mapLinear(
                    -dotOfWallVecLookVec, 0, 0.7071f /*cos(pi/4)*/, 0, 1
            );
        }

        private static float getBlendFactorRightToWall(Vec3 horizontalLookVec, Vec3 wallVec, float dotOfWallVecLookVec) {
            if (wallVec.yRot(Mth.HALF_PI).dot(horizontalLookVec) > 0) return 0;
            return MathUtil.mapLinear(
                    -dotOfWallVecLookVec, 0, 0.7071f /*cos(pi/4)*/, 0, 1
            );
        }

        private static float getBlendFactorBackToWall(Vec3 horizontalLookVec, Vec3 wallVec, float dotOfWallVecLookVec) {
            return MathUtil.mapLinear(
                    -dotOfWallVecLookVec, 0.7071f /*cos(pi/4)*/, 1, 0, 1
            );
        }

        private static float getBlendFactorMovementLeft(Vec3 wallVec, Vec3 deltaMovement, float oldBlendFactor) {
            var dot = wallVec.yRot(Mth.HALF_PI).dot(deltaMovement);
            if (Math.abs(dot) < 1e-5) return oldBlendFactor;
            return Mth.clamp(
                    oldBlendFactor + (dot > 0 ? 0.2f : -0.2f),
                    0, 1f
            );
        }
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    public record HangState(
            InteractingWallDirection direction,
            AABB handBoundingBox,
            double yCollisionDistance,
            boolean fullWall
    ) {
    }

    @Nullable
    private HangState getHangState() {
        var player = parkourability.player();
        var level = player.level;
        var playerBB = player.getBoundingBox();
        double xRange = playerBB.getXsize() * 0.25, zRange = playerBB.getZsize() * 0.25;
        var direction = parkourability.getAdditionalProperties().getDefaultWallInteraction();

        if (direction == null) return null;

        var bb = playerBB.expandTowards(
                direction.getSignX() * REACH_SCALE * playerBB.getXsize(), 0,
                direction.getSignZ() * REACH_SCALE * playerBB.getZsize()
        );
        if (level.noCollision(player, bb)) return null;
        var grabbingBB = getGrabbingHandAABB(direction);
        if (!level.noCollision(player, grabbingBB)) return null;
        var downReach = -playerBB.getYsize() * 0.2;
        var collision = Entity.collideBoundingBox(player, new Vec3(0, downReach, 0), grabbingBB, level, Collections.emptyList());
        if (collision.y > downReach) {
            var legBB = new AABB(playerBB.minX, playerBB.minY, playerBB.minZ, playerBB.maxX, playerBB.minY + playerBB.getYsize() / 3, playerBB.maxZ).expandTowards(direction.getSignX() * xRange, 0, direction.getSignZ() * zRange);
            return new HangState(direction, grabbingBB, collision.y, !level.noCollision(legBB));
        }
        return null;
    }

    private AABB getGrabbingHandAABB(InteractingWallDirection direction) {
        var player = parkourability.player();
        var playerBB = player.getBoundingBox();
        var center = playerBB.getCenter();
        double x1, x2;
        if (direction.getSignX() != 0) {
            x1 = center.x;
            x2 = x1 + direction.getSignX() * playerBB.getXsize() * (0.5 + REACH_SCALE);
        } else {
            x1 = playerBB.minX;
            x2 = playerBB.maxX;
        }
        double z1, z2;
        if (direction.getSignZ() != 0) {
            z1 = center.z;
            z2 = z1 + direction.getSignZ() * playerBB.getZsize() * (0.5 + REACH_SCALE);
        } else {
            z1 = playerBB.minZ;
            z2 = playerBB.maxZ;
        }

        return new AABB(x1, playerBB.maxY + playerBB.getYsize() * 0.125, z1, x2, playerBB.maxY + playerBB.getYsize() * 0.25, z2);
    }
}
