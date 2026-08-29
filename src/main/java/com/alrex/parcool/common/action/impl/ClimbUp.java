package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.IRequestable;
import com.alrex.parcool.common.action.InteractingWallDirection;
import com.alrex.parcool.common.action.ParCoolActions;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;

public class ClimbUp extends ContinuableAction implements IRequestable<ClimbUp.RequestContext> {
    private enum Type {
        JUMP, SWOOCE, CLIMB
    }
    private static final int MAX_TICK = 10;
    private static final BehaviorEnforcer.ID ID_ENFORCE_SWIM = BehaviorEnforcer.newID();
    @Nullable
    private Vec3 startPos = null;
    @Nullable
    private Vec3 destination = null;
    private int duration = MAX_TICK;
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<InteractingWallDirection> propertyDirection;
    private final SynchronizedProperty<Type> propertyType;

    public ClimbUp(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(ParCoolActions.GRAPPLE));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyDirection = SynchronizedProperty.newEnum(InteractingWallDirection.class),
                propertyType = SynchronizedProperty.newEnum(Type.class)
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public boolean canStart(RequestContext requestContext) {
        var bb = requestContext.hangState.handBoundingBox();
        var center = bb.getCenter();
        var playerBB = parkourability.player().getBoundingBox();
        destination = new Vec3(center.x, bb.minY, center.z);
        startPos = parkourability.player().position();
        propertyDirection.set(requestContext.hangState.direction());
        Type type;
        if (parkourability.player().level().noCollision(new AABB(
                destination.x - playerBB.getXsize() * 0.6,
                destination.y,
                destination.z - playerBB.getZsize() * 0.6,
                destination.x + playerBB.getXsize() * 0.6,
                destination.y + playerBB.getYsize() * 1.0833,
                destination.z + playerBB.getZsize() * 0.6
        ))) {
            duration = MAX_TICK;
            type = Type.CLIMB;
        } else if (parkourability.player().level().noCollision(new AABB(
                destination.x - playerBB.getXsize() * 0.6,
                destination.y,
                destination.z - playerBB.getZsize() * 0.6,
                destination.x + playerBB.getXsize() * 0.6,
                destination.y + playerBB.getYsize() * 0.5,
                destination.z + playerBB.getZsize() * 0.6
        )) && parkourability.permit(ParCoolActions.CRAWL)) {
            duration = MAX_TICK;
            type = Type.SWOOCE;
        } else {
            duration = 3;
            type = Type.JUMP;
        }
        propertyType.set(type);
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInLocalClient() {
        final var fStartPos = this.startPos;
        final var fDestination = this.destination;
        if (fStartPos != null && fDestination != null) {
            var type = propertyType.get();
            if (type == null) return;
            switch (type) {
                case JUMP:
                    parkourability.getBehaviorEnforcer().setMarkerEnforcingMovePoint(this::isDoing, () -> {
                        var phase = EasingFunctions.CUBE.easeInOut(getDoingTick() / (float) MAX_TICK);
                        return fStartPos.add(0, (fDestination.y - fStartPos.y) * phase, 0);
                    });
                    break;
                case SWOOCE:
                case CLIMB:
                    parkourability.getBehaviorEnforcer().setMarkerEnforcingMovePoint(this::isDoing, () -> {
                        final int borderTick = 9;
                        final double borderPhase = borderTick / (double) MAX_TICK;

                        var phase = EasingFunctions.CUBE.easeInOut(getDoingTick() / (float) MAX_TICK);
                        if (phase < borderPhase) {
                            return fStartPos.add(0, (fDestination.y - fStartPos.y) * phase, 0);
                        } else {
                            var inPhase = (phase - borderPhase) / (1 - borderPhase);
                            return new Vec3(
                                    Mth.lerp(inPhase, fStartPos.x, fDestination.x),
                                    fDestination.y + inPhase * 0.0625,
                                    Mth.lerp(inPhase, fStartPos.z, fDestination.z)
                            );
                        }
                    });
                    break;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        var type = propertyType.get();
        if (type == null) return;
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(switch (type) {
            case JUMP -> AnimationRegistries.get().animations().CLIMB_UP_JUMP;
            case SWOOCE, CLIMB -> AnimationRegistries.get().animations().CLIMB_UP;
        });
        parkourability.player().playSound(ParCoolSoundEvents.CLIMB_UP.get());
    }

    @Override
    public void onWorkingTickInLocalClient() {
        if (propertyType.get() == Type.SWOOCE && getDoingTick() == 4) {
            parkourability.getBehaviorEnforcer().swimmingPoseMarks.add(ID_ENFORCE_SWIM, this::isDoing);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStopInLocalClient() {
        if (propertyType.get() == Type.JUMP && destination != null) {
            var player = parkourability.player();
            var delta = player.getDeltaMovement();
            var destYHeight = destination.y - player.position().y + 0.1;
            var gravityAttr = player.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
            if (gravityAttr == null) return;
            var gravity = gravityAttr.getValue();
            player.setDeltaMovement(delta.x, Math.sqrt(Math.abs(2 * gravity * destYHeight)), delta.z);
        }
    }

    public record RequestContext(HangOn.HangState hangState) {
    }

    @Override
    public boolean canContinue() {
        return getDoingTick() <= duration;
    }

    @Nullable
    public Vec3 getWallVec(float partial) {
        var direction = propertyDirection.get();
        if (direction == null) return null;
        return direction.asVec();
    }
}
