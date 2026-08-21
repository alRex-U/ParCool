package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.MathUtil;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class HangDown extends ContinuableAction implements ActionExtension.KeyMapTriggeredListener {
    private static final BehaviorEnforcer.ID ID_FALL_FLY_CANCEL = BehaviorEnforcer.newID();

    @Override
    public void onInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (isDoing() && (event.isAttack() || event.isUseItem())) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }

    enum BarAxis {
        X(new Vec3(1, 0, 0), new Vec3(0, 0, 1)),
        Z(new Vec3(0, 0, 1), new Vec3(1, 0, 0));
        private final Vec3 positiveVec;
        private final Vec3 orthogonalVec;

        BarAxis(Vec3 positiveVec, Vec3 orthogonalVec) {
            this.positiveVec = positiveVec;
            this.orthogonalVec = orthogonalVec;
        }

        public Vec3 getOrthogonalVec() {
            return orthogonalVec;
        }

        public Vec3 getPositiveVec() {
            return positiveVec;
        }
    }

    private record HangAbleBarInfo(BlockPos pos, HangDown.BarAxis axis, double yCollideDist) {
    }

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<BarAxis> propertyHangingBarAxis;
    private final SynchronizedProperty<Float> propertyBodySwingAngleInRad;
    private final SynchronizedProperty<Float> propertyBodyAngularSpeedInRad;
    private final SynchronizedProperty<Boolean> propertyJumpOff;

    // Only for local client
    @Nullable
    private BlockPos hangingPos;
    private boolean barNotFound;
    private double yCollideDist;
    // Only for client
    private float oldAngle;
    private float oldAngularSpeed;

    public HangDown(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(ParCoolActions.GRAPPLE, ParCoolActions.CLIMB_UP, ParCoolActions.DIVE, ParCoolActions.HANG_ON, ParCoolActions.POLE_CLIMB));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyHangingBarAxis = SynchronizedProperty.newEnum(BarAxis.class),
                propertyBodySwingAngleInRad = SynchronizedProperty.newFloat(),
                propertyBodyAngularSpeedInRad = SynchronizedProperty.newFloat(),
                propertyJumpOff = SynchronizedProperty.newBoolean()
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canContinue() {
        var continuing = !barNotFound && ParCoolKeyBinds.HANG.key().isDown() && !ParCoolKeyBinds.JUMP.state().isJustPressed();
        if (continuing) {
            if (Math.abs(propertyBodyAngularSpeedInRad.getOrDefaultIfNull(0f)) > Mth.PI / 20f) {
                propertyJumpOff.set(true);
            }
        }
        return continuing;
    }

    @Override
    public boolean canStart() {
        var player = parkourability.player();
        if (0 <= getNotDoingTick() && getNotDoingTick() < 7) return false;
        if (Math.abs(player.getDeltaMovement().y) > 0.2 || !ParCoolKeyBinds.HANG.key().isDown()) return false;
        var hangingBar = getHangAbleBars(player);
        if (hangingBar == null) return false;

        var barOrthogonalVec = hangingBar.axis.getOrthogonalVec().dot(EntityUtil.getHorizontalLookAngle(player)) > 0
                ? hangingBar.axis.getOrthogonalVec()
                : hangingBar.axis.getOrthogonalVec().reverse();
        var pos = player.position();
        var speed = barOrthogonalVec.x * (pos.x - player.xo) + barOrthogonalVec.z * (pos.z - player.zo);

        propertyBodyAngularSpeedInRad.set((float) (speed / player.getBbHeight()));
        propertyBodySwingAngleInRad.set(0f);
        propertyHangingBarAxis.set(hangingBar.axis);
        propertyJumpOff.set(false);
        barNotFound = false;
        yCollideDist = hangingBar.yCollideDist;
        hangingPos = hangingBar.pos;

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInLocalClient() {
        if (!(parkourability.player() instanceof LocalPlayer player)) return;
        parkourability.getBehaviorEnforcer().addMarkerEnforcingNoFallFlying(ID_FALL_FLY_CANCEL, this::isDoing);
        parkourability.getBehaviorEnforcer().setMarkerEnforcingMovePoint(
                this::isDoing, () -> {
                    var currentBarAxis = propertyHangingBarAxis.get();
                    if (currentBarAxis == null) return null;
                    if (hangingPos == null) return null;
                    var speed = player.getSpeed();
                    var moveVec = player.input.getMoveVector().scale(speed);
                    var actualMoveVec = new Vec3(moveVec.x, 0, moveVec.y).yRot((float) Math.toRadians(-player.getYRot()));
                    var barAxisVec = currentBarAxis.getPositiveVec();
                    var movementLength = actualMoveVec.dot(barAxisVec);
                    var currentPos = player.position();
                    var currentHangingPos = new Vec3(hangingPos.getX() + 0.5, currentPos.y, hangingPos.getZ() + 0.5);
                    var t = (currentPos.subtract(currentHangingPos).dot(barAxisVec)) / barAxisVec.lengthSqr();
                    var alignedPos = currentHangingPos.add(barAxisVec.scale(t));

                    return alignedPos.add(barAxisVec.scale(movementLength)).add(0, yCollideDist - 0.1 + 0.01, 0);
                }
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        oldAngularSpeed = 0;
        oldAngle = 0;
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().HANG_DOWN);
        parkourability.player().playSound(ParCoolSoundEvents.HANG_DOWN.get());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStopInClient() {
        if (propertyJumpOff.getOrDefaultIfNull(false)) {
            if (propertyBodyAngularSpeedInRad.getOrDefaultIfNull(1f) > 0f) {
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().HANG_DOWN_JUMP_FORWARD);
            } else {
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().HANG_DOWN_JUMP_BACKWARD);
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStopInLocalClient() {
        var player = parkourability.player();
        var barAxis = propertyHangingBarAxis.get();
        if (barAxis == null) return;
        var orthogonalVec = VectorUtil.reverseIfInReverseDirection(EntityUtil.getHorizontalLookAngle(player), barAxis.getOrthogonalVec());
        float angle = Mth.clamp(propertyBodySwingAngleInRad.getOrDefaultIfNull(0f), -Mth.PI / 4f, Mth.PI / 4f);
        var sinAngle = Math.sin(angle);
        var cosAngle = Math.cos(angle);
        var finishedPos = player.position();
        var playerHeight = player.getBbHeight();
        var swingingPos = finishedPos.add(
                orthogonalVec.x * sinAngle,
                0.5 * playerHeight * (1. - cosAngle),
                orthogonalVec.z * sinAngle
        );
        if (propertyJumpOff.getOrDefaultIfNull(false)) {
            var movement = new Vec3(
                    orthogonalVec.x * cosAngle * 1.5,
                    Math.abs(sinAngle),
                    orthogonalVec.z * cosAngle * 1.5
            ).scale(2. * playerHeight * propertyBodyAngularSpeedInRad.getOrDefaultIfNull(0f));
            parkourability.getBehaviorEnforcer().setMarkerEnforcingMovePoint(
                    () -> getNotDoingTick() <= 5,
                    () -> swingingPos.add(movement.scale((getNotDoingTick() + 1.) / 6.))
            );
        } else {
            parkourability.getBehaviorEnforcer().setMarkerEnforcingPosition(
                    () -> getNotDoingTick() <= 1,
                    () -> VectorUtil.lerp((getNotDoingTick() + 1.) / 2., finishedPos, swingingPos)
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onWorkingTickInLocalClient() {
        var player = parkourability.player();
        if (!(player instanceof LocalPlayer localPlayer)) return;
        var gravityAttr = localPlayer.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        if (gravityAttr == null) return;
        var barAxis = propertyHangingBarAxis.get();
        if (barAxis == null) return;
        float angularSpeedInRad = propertyBodyAngularSpeedInRad.getOrDefaultIfNull(0f);
        float angle = propertyBodySwingAngleInRad.getOrDefaultIfNull(0f);
        var lookVec = EntityUtil.getHorizontalLookAngle(localPlayer);
        var barOrthogonalVec = barAxis.getOrthogonalVec();
        var dotOfOrthogonalAndLook = (float) barOrthogonalVec.dot(lookVec);
        if (dotOfOrthogonalAndLook < 0) barOrthogonalVec = barOrthogonalVec.reverse();
        var absDotOfOrthogonalAndLook = Mth.abs(dotOfOrthogonalAndLook);
        var input = localPlayer.input;
        var movementInput = lookVec.scale(input.forwardImpulse).add(lookVec.yRot(Mth.HALF_PI).scale(input.leftImpulse));
        var inputAcceleration = 0.02f * (float) barOrthogonalVec.dot(movementInput);
        var cosAngle = Mth.cos(angle);
        var sinAngle = Mth.sin(angle);
        var acceleration = new Vec2( // x is for horizontal, y is for vertical
                cosAngle * inputAcceleration,
                sinAngle * inputAcceleration + (float) -gravityAttr.getValue()
        ).dot(new Vec2(cosAngle, sinAngle));

        angularSpeedInRad += acceleration / player.getBbHeight();
        angularSpeedInRad *= absDotOfOrthogonalAndLook * 0.98f;
        var newAngle = absDotOfOrthogonalAndLook * (angle + angularSpeedInRad);
        if (newAngle > Mth.PI * 0.75f) {
            newAngle = Mth.PI * 0.75f;
            angularSpeedInRad = 0;
        } else if (newAngle < Mth.PI * -0.75f) {
            newAngle = Mth.PI * -0.75f;
            angularSpeedInRad = 0;
        }

        propertyBodySwingAngleInRad.set(newAngle);
        propertyBodyAngularSpeedInRad.set(angularSpeedInRad);
        var hangingBar = getHangAbleBars(player);
        if (hangingBar != null) {
            propertyHangingBarAxis.set(hangingBar.axis);
            hangingPos = hangingBar.pos;
            yCollideDist = hangingBar.yCollideDist;
        } else {
            barNotFound = true;
            hangingPos = null;
            yCollideDist = 0;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onWorkingTickInClient() {
        oldAngle = propertyBodySwingAngleInRad.getOrDefaultIfNull(0f);
        oldAngularSpeed = propertyBodyAngularSpeedInRad.getOrDefaultIfNull(0f);
    }

    public float getRotationAngle(float partial) {
        return isDoing()
                ? Mth.lerp(partial, oldAngle, propertyBodySwingAngleInRad.getOrDefaultIfNull(0f))
                : propertyBodySwingAngleInRad.getOrDefaultIfNull(0f);
    }

    public float getAngularSpeed(float partial) {
        return isDoing()
                ? Mth.lerp(partial, oldAngularSpeed, propertyBodyAngularSpeedInRad.getOrDefaultIfNull(0f))
                : propertyBodyAngularSpeedInRad.getOrDefaultIfNull(0f);
    }

    public float getBlendFactorOrthogonalToBar(float partial) {
        var currentBarAxis = propertyHangingBarAxis.get();
        if (currentBarAxis == null) {
            return 0;
        }
        var barOrthogonalVec = currentBarAxis.getOrthogonalVec();
        var player = parkourability.player();
        var lookingDirection = VectorUtil.fromYawDegree(Mth.lerp(partial, player.yRotO, player.getYRot()));
        return MathUtil.mapLinear((float) Math.abs(lookingDirection.dot(barOrthogonalVec)), 0.5736f /*cos(55 degrees)*/, 0.8129f /*cos(35 degrees)*/, 0, 1);
    }

    @Nullable
    public Vec3 getBodyDirection(float partial) {
        var barAxis = propertyHangingBarAxis.get();
        if (barAxis == null) return null;
        var lookVec = EntityUtil.getHorizontalLookAngle(parkourability.player());
        return VectorUtil.lerp(
                getBlendFactorOrthogonalToBar(partial),
                VectorUtil.reverseIfInReverseDirection(lookVec, barAxis.getPositiveVec()),
                VectorUtil.reverseIfInReverseDirection(lookVec, barAxis.getOrthogonalVec())
        );
    }

    @Nullable
    private static HangAbleBarInfo getHangAbleBars(LivingEntity entity) {
        var level = entity.level();
        var collideDistY = Entity.collideBoundingBox(
                entity,
                new Vec3(0, 1., 0),
                entity.getBoundingBox().deflate(0.05, 0, 0.05),
                entity.level(),
                Collections.emptyList()
        ).y;
        if (Math.abs(collideDistY - 1.) < 1e-5) return null;
        var pos = new BlockPos(
                Mth.floor(entity.getX()),
                Mth.floor(entity.getY() + entity.getBbHeight() + collideDistY + 0.1),
                Mth.floor(entity.getZ())
        );
        if (!level.isLoaded(pos)) return null;
        var state = level.getBlockState(pos);
        var block = state.getBlock();
        HangDown.BarAxis axis = null;
        if (block instanceof RotatedPillarBlock) {
            if (state.isCollisionShapeFullBlock(level, pos)) {
                return null;
            }
            var pillarAxis = state.getValue(RotatedPillarBlock.AXIS);
            axis = switch (pillarAxis) {
                case X -> BarAxis.X;
                case Z -> BarAxis.Z;
                default -> axis;
            };
        } else if (block instanceof EndRodBlock) {
            if (state.isCollisionShapeFullBlock(entity.level(), pos)) {
                return null;
            }
            var direction = state.getValue(DirectionalBlock.FACING);
            axis = switch (direction) {
                case EAST, WEST -> BarAxis.X;
                case NORTH, SOUTH -> BarAxis.Z;
                default -> axis;
            };
        } else if (block instanceof CrossCollisionBlock) {
            int zCount = 0;
            int xCount = 0;
            if (state.getValue(CrossCollisionBlock.NORTH)) zCount++;
            if (state.getValue(CrossCollisionBlock.SOUTH)) zCount++;
            if (state.getValue(CrossCollisionBlock.EAST)) xCount++;
            if (state.getValue(CrossCollisionBlock.WEST)) xCount++;
            if (zCount > 0 && xCount == 0) axis = HangDown.BarAxis.Z;
            if (xCount > 0 && zCount == 0) axis = HangDown.BarAxis.X;
        } else if (block instanceof WallBlock) {
            int zCount = 0;
            int xCount = 0;
            if (state.getValue(WallBlock.NORTH_WALL) != WallSide.NONE) zCount++;
            if (state.getValue(WallBlock.SOUTH_WALL) != WallSide.NONE) zCount++;
            if (state.getValue(WallBlock.EAST_WALL) != WallSide.NONE) xCount++;
            if (state.getValue(WallBlock.WEST_WALL) != WallSide.NONE) xCount++;
            if (zCount > 0 && xCount == 0) axis = HangDown.BarAxis.Z;
            if (xCount > 0 && zCount == 0) axis = HangDown.BarAxis.X;
        }

        return axis != null ? new HangAbleBarInfo(pos, axis, collideDistY) : null;
    }
}
