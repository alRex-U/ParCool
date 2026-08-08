package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.IRequestable;
import com.alrex.parcool.common.action.InteractingWallDirection;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class ClimbUp extends ContinuableAction implements IRequestable<ClimbUp.RequestContext> {
    private static final int MAX_TICK = 10;
    @Nullable
    private Vec3 startPos = null;
    @Nullable
    private Vec3 destination = null;
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<InteractingWallDirection> propertyDirection;

    public ClimbUp(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyDirection = SynchronizedProperty.newEnum(InteractingWallDirection.class)
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
        var destinationBB = new AABB(
                center.x - playerBB.getXsize() * 0.6,
                bb.minY,
                center.z - playerBB.getZsize() * 0.6,
                center.x + playerBB.getXsize() * 0.6,
                bb.minY + playerBB.getYsize() * 1.4,
                center.z + playerBB.getZsize() * 0.6
        );

        return parkourability.player().level.noCollision(destinationBB);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInLocalClient() {
        final var fStartPos = this.startPos;
        final var fDestination = this.destination;
        if (fStartPos != null && fDestination != null) {
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
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().CLIMB_UP);
        parkourability.player().playSound(ParCoolSoundEvents.CLIMB_UP.get());
    }

    public record RequestContext(HangOn.HangState hangState) {
    }

    @Override
    public boolean canContinue() {
        return getDoingTick() <= MAX_TICK;
    }

    @Nullable
    public Vec3 getWallVec(float partial) {
        var direction = propertyDirection.get();
        if (direction == null) return null;
        return direction.asVec();
    }
}
