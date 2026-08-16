package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.client.sound.GrappleSwingSoundInstance;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.common.grapple.GrapplePhase;
import com.alrex.parcool.common.grapple.GrapplePhysics;
import com.alrex.parcool.common.grapple.GrappleTargeting;
import com.alrex.parcool.common.grapple.RopeState;
import com.alrex.parcool.common.item.misc.GrapplingHookItem;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class Grapple extends ContinuableAction {
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();
    private static final BehaviorEnforcer.ID ID_FALL_FLY_CANCEL = BehaviorEnforcer.newID();

    private static final int ASSIST_RINGS = 3;
    private static final int ASSIST_SAMPLES_PER_RING = 12;
    private static final double MIN_TARGET_DISTANCE = 3.0;
    private static final int PREVIEW_ASSIST_INTERVAL = 3;

    private static final int MIN_FLIGHT_TICKS = 2;
    private static final int MAX_FLIGHT_TICKS = 12;

    private static final float RETRACT_SPEED_FACTOR = 2f;

    private static final double ATTACH_SLACK = 0.05;

    private static final int CATCH_SOFTEN_TICKS = 3;
    private static final double CATCH_COMPLIANCE = 0.02;

    private static final double MAX_SLACK = 2.0;

    private static final double DESYNC_TOLERANCE = 2.0;

    private static final double VANILLA_AIR_FRICTION = 0.91;

    private static final double OVERSTRETCH_FACTOR = 1.2;

    private static final int CONTACT_SEARCH_STEPS = 8;

    private static final double WOBBLE_DRIVE = 0.7;
    private static final double WOBBLE_STIFFNESS = 0.40;
    private static final double WOBBLE_DAMPING = 0.82;
    private static final double WOBBLE_MAX = 0.08;

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<GrapplePhase> propertyPhase;
    private final SynchronizedProperty<Vec3> propertyAnchor;
    private final SynchronizedProperty<Vec3> propertyPivot;
    private final SynchronizedProperty<Float> propertyRopeLength;
    private final SynchronizedProperty<Byte> propertyFlightDuration;

    @Nullable
    private RopeState rope;
    @Nullable
    private GrappleTargeting.Result pendingTarget;
    private Vec3 velocity = Vec3.ZERO;
    private Vec3 previousPosition = Vec3.ZERO;
    private Vec3 plannedDelta = Vec3.ZERO;
    private Vec3 plannedPosition = Vec3.ZERO;
    private int attachedTicks = 0;
    private int flightDuration = MIN_FLIGHT_TICKS;
    private boolean releaseWithBoost = false;
    private int momentumTicksLeft = 0;
    private double momentumSpeedCap = 0;
    private int lastRopeReportTick = Integer.MIN_VALUE;
    private double peakTension = 0;

    @Nullable
    private Vec3 previewTarget = null;
    private int previewCooldown = 0;

    private boolean pulling = false;
    private int unresolvedContacts = 0;

    private Vec3 ropeWobble = Vec3.ZERO;
    private Vec3 ropeWobbleVelocity = Vec3.ZERO;
    private Vec3 previousRenderMovement = Vec3.ZERO;

    @Nullable
    private Vec3 renderFrameSide = null;
    @Nullable
    private Vec3 renderFrameTangent = null;

    public Grapple(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(
                ParCoolActions.HANG_ON,
                ParCoolActions.HANG_DOWN,
                ParCoolActions.CLIMB_UP,
                ParCoolActions.POLE_CLIMB,
                ParCoolActions.SLIDE_DOWN,
                ParCoolActions.CASTAWAY,
                ParCoolActions.RIDE_ZIPLINE,
                ParCoolActions.HORIZONTAL_WALL_RUN
        ));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyPhase = SynchronizedProperty.newEnum(GrapplePhase.class, this::onPhaseChanged),
                propertyAnchor = SynchronizedProperty.newVec3(),
                propertyPivot = SynchronizedProperty.newVec3(),
                propertyRopeLength = SynchronizedProperty.newFloat(),
                propertyFlightDuration = SynchronizedProperty.newByte()
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    private static ParCoolConfig.Server.GrapplingHook config() {
        return ParCool.getConfig().server().grapplingHook;
    }

    public GrapplePhase getPhase() {
        return propertyPhase.getOrDefaultIfNull(GrapplePhase.RETRACTING);
    }

    public boolean isAttached() {
        return isDoing() && getPhase() == GrapplePhase.ATTACHED;
    }

    @Nullable
    public Vec3 getAnchor() {
        return propertyAnchor.get();
    }

    @Nullable
    public Vec3 getPivot() {
        Vec3 pivot = propertyPivot.get();
        return pivot != null ? pivot : propertyAnchor.get();
    }

    public float getRopeLength() {
        return propertyRopeLength.getOrDefaultIfNull(0f);
    }

    public List<Vec3> getBends() {
        return rope != null ? rope.bends() : Collections.emptyList();
    }

    public float getFlightProgress(float partialTick) {
        int duration = Math.max(1, propertyFlightDuration.getOrDefaultIfNull((byte) MIN_FLIGHT_TICKS));
        float elapsed = getDoingTick() + partialTick;
        return switch (getPhase()) {
            case FLYING -> Mth.clamp(elapsed / duration, 0f, 1f);
            case ATTACHED -> 1f;
            case RETRACTING -> Mth.clamp(1f - (elapsed - duration) * RETRACT_SPEED_FACTOR / duration, 0f, 1f);
        };
    }

    public Vec3 getVelocity() {
        return velocity;
    }

    @Nullable
    public Vec3 getPreviewTarget() {
        return previewTarget;
    }

    public Vec3 getRopeWobble() {
        return ropeWobble;
    }

    @Nullable
    public Vec3 getRenderFrameSide() {
        return renderFrameSide;
    }

    @Nullable
    public Vec3 getRenderFrameTangent() {
        return renderFrameTangent;
    }

    public void setRenderFrame(Vec3 side, Vec3 tangent) {
        this.renderFrameSide = side;
        this.renderFrameTangent = tangent;
    }

    public boolean isMomentumActive() {
        return momentumTicksLeft > 0;
    }

    @Override
    public boolean canStart() {
        var player = parkourability.player();
        if (!GrapplingHookItem.isHeld(player)) return false;
        if (!ParCoolKeyBinds.ATTACK.state().isJustPressed()) return false;
        prepareThrow(player);
        return true;
    }

    @Override
    public boolean canContinue() {
        var player = parkourability.player();
        if (!GrapplingHookItem.isHeld(player)) return false;
        if (ParCoolKeyBinds.JUMP.state().isJustPressed()) {
            releaseWithBoost = true;
            return false;
        }

        if (ParCoolKeyBinds.ATTACK.state().isJustPressed()) return false;

        return switch (getPhase()) {
            case FLYING -> true;
            case RETRACTING -> getDoingTick() < flightDuration * (1 + 1 / RETRACT_SPEED_FACTOR);
            case ATTACHED -> canStayAttached(player);
        };
    }

    private boolean canStayAttached(Player player) {
        if (rope == null) return false;
        if (player.isPassenger() || player.isFallFlying()) return false;

        if (pendingTarget != null && player.level.getBlockState(pendingTarget.blockPos()).isAir()) return false;

        var config = config();

        if (rope.anchor().distanceTo(GrapplePhysics.attachmentOf(player.position()))
                > config.maxRange().get() * OVERSTRETCH_FACTOR) return false;
        double tensionLimit = config.maxTension().get();
        if (tensionLimit > 0 && peakTension > tensionLimit) return false;

        if (player.horizontalCollision || player.verticalCollision) return true;
        return GrapplePhysics.attachmentOf(player.position()).distanceToSqr(plannedPosition)
                <= DESYNC_TOLERANCE * DESYNC_TOLERANCE;
    }

    private void prepareThrow(Player player) {
        var config = config();
        double maxRange = config.maxRange().get();

        pendingTarget = GrappleTargeting.find(
                player,
                maxRange,
                config.aimAssistAngle().get(),
                ASSIST_RINGS,
                ASSIST_SAMPLES_PER_RING,
                MIN_TARGET_DISTANCE
        );

        Vec3 origin = player.getEyePosition();
        Vec3 endpoint = pendingTarget != null
                ? pendingTarget.point()
                : origin.add(player.getLookAngle().scale(maxRange));

        flightDuration = Mth.clamp(
                Mth.ceil(origin.distanceTo(endpoint) / config.hookTravelSpeed().get()),
                MIN_FLIGHT_TICKS,
                MAX_FLIGHT_TICKS
        );

        rope = null;
        attachedTicks = 0;
        peakTension = 0;
        releaseWithBoost = false;
        momentumTicksLeft = 0;

        propertyPhase.set(GrapplePhase.FLYING);
        propertyAnchor.set(endpoint);
        propertyPivot.set(endpoint);
        propertyRopeLength.set(0f);
        propertyFlightDuration.set((byte) flightDuration);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        renderFrameSide = null;
        renderFrameTangent = null;

        playSound(parkourability.player(), SoundEvents.CROSSBOW_SHOOT, 0.7f, 1.5f);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInLocalClient() {
        if (!(parkourability.player() instanceof LocalPlayer player)) return;
        parkourability.getBehaviorEnforcer().addMarkerEnforcingNoFallFlying(ID_FALL_FLY_CANCEL, this::isDoing);
        Minecraft.getInstance().getSoundManager().play(new GrappleSwingSoundInstance(player, this));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStopInClient() {
        playSound(
                parkourability.player(),
                getPhase() == GrapplePhase.ATTACHED ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.FISHING_BOBBER_RETRIEVE,
                0.4f,
                getPhase() == GrapplePhase.ATTACHED ? 1.6f : 1.3f
        );
    }

    private void onPhaseChanged(GrapplePhase phase, @Nullable GrapplePhase previous) {
        if (phase != GrapplePhase.ATTACHED || previous == GrapplePhase.ATTACHED) return;
        var player = parkourability.player();
        if (!player.level.isClientSide()) return;
        playSound(player, SoundEvents.CHAIN_PLACE, 0.9f, 1.4f);
        spawnAttachParticles(player, propertyAnchor.get());
    }

    private static void spawnAttachParticles(Player player, @Nullable Vec3 anchor) {
        if (anchor == null) return;
        BlockState state = anchoredState(player.level, anchor);
        if (state.isAir()) return;
        for (int i = 0; i < 8; i++) {
            player.level.addParticle(
                    new BlockParticleOption(ParticleTypes.BLOCK, state),
                    anchor.x, anchor.y, anchor.z,
                    (player.getRandom().nextDouble() - 0.5) * 0.2,
                    (player.getRandom().nextDouble() - 0.5) * 0.2,
                    (player.getRandom().nextDouble() - 0.5) * 0.2
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStopInLocalClient() {
        if (!(parkourability.player() instanceof LocalPlayer player)) return;
        var config = config();

        if (getPhase() == GrapplePhase.ATTACHED) {
            Vec3 exitVelocity = velocity;
            if (releaseWithBoost) {
                exitVelocity = exitVelocity.add(0, config.releaseBoost().get(), 0);
            }
            exitVelocity = GrapplePhysics.clampSpeed(exitVelocity, config.maxSpeed().get());
            player.setDeltaMovement(exitVelocity);

            momentumTicksLeft = config.momentumKeepTicks().get();
            momentumSpeedCap = Math.sqrt(exitVelocity.x * exitVelocity.x + exitVelocity.z * exitVelocity.z);
        }

        rope = null;
        pendingTarget = null;
        plannedDelta = Vec3.ZERO;
        ropeWobble = ropeWobbleVelocity = previousRenderMovement = Vec3.ZERO;
        releaseWithBoost = false;
    }

    @Override
    public void onWorkingTick() {
        if (getPhase() == GrapplePhase.ATTACHED) {
            parkourability.player().fallDistance = 0;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onWorkingTickInClient() {
        if (getPhase() != GrapplePhase.ATTACHED) {
            ropeWobble = ropeWobbleVelocity = Vec3.ZERO;
            return;
        }
        var player = parkourability.player();
        Vec3 pivot = getPivot();
        if (pivot == null) return;

        Vec3 movement = new Vec3(player.getX() - player.xo, player.getY() - player.yo, player.getZ() - player.zo);
        Vec3 ropeDirection = GrapplePhysics.ropeDirection(pivot, GrapplePhysics.attachmentOf(player.position()));

        Vec3 impulse = GrapplePhysics.tangential(movement.subtract(previousRenderMovement), ropeDirection);
        previousRenderMovement = movement;

        ropeWobbleVelocity = ropeWobbleVelocity
                .add(impulse.scale(WOBBLE_DRIVE))
                .subtract(ropeWobble.scale(WOBBLE_STIFFNESS))
                .scale(WOBBLE_DAMPING);
        ropeWobble = GrapplePhysics.tangential(ropeWobble.add(ropeWobbleVelocity), ropeDirection);
        double magnitude = ropeWobble.length();
        if (magnitude > WOBBLE_MAX) ropeWobble = ropeWobble.scale(WOBBLE_MAX / magnitude);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onWorkingTickInLocalClient() {
        if (!(parkourability.player() instanceof LocalPlayer player)) return;
        switch (getPhase()) {
            case FLYING -> tickFlight(player);
            case ATTACHED -> tickSwing(player);
            case RETRACTING -> {
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickFlight(LocalPlayer player) {
        if (getDoingTick() < flightDuration) return;
        if (pendingTarget == null) {
            propertyPhase.set(GrapplePhase.RETRACTING);
            return;
        }
        attach(player);
    }

    @OnlyIn(Dist.CLIENT)
    private void attach(LocalPlayer player) {
        var config = config();
        Vec3 anchor = pendingTarget.point();
        Vec3 position = GrapplePhysics.attachmentOf(player.position());

        double length = Math.min(position.distanceTo(anchor) + ATTACH_SLACK, config.maxRange().get());
        rope = new RopeState(anchor, length);

        velocity = player.getDeltaMovement();
        peakTension = 0;
        previousPosition = position;
        plannedDelta = Vec3.ZERO;
        plannedPosition = position;
        attachedTicks = 0;

        parkourability.getBehaviorEnforcer().setMarkerEnforcingDeltaMovement(this::isAttached, () -> plannedDelta);
        propertyPhase.set(GrapplePhase.ATTACHED);
        propertyPivot.set(anchor);
        propertyRopeLength.set((float) length);

        tickSwing(player);
    }

    @OnlyIn(Dist.CLIENT)
    private void tickSwing(LocalPlayer player) {
        if (rope == null) return;
        var config = config();
        Vec3 position = GrapplePhysics.attachmentOf(player.position());
        parkourability.getBehaviorEnforcer().setMarkerEnforcingDeltaMovement(this::isAttached, () -> plannedDelta);

        if (attachedTicks > 0) {
            velocity = reconcile(velocity, plannedDelta, position.subtract(previousPosition));
        }
        previousPosition = position;

        int substeps = Math.max(1, config.physicsSubsteps().get());
        double dt = 1.0 / substeps;
        double dragPerSubstep = Math.pow(config.ropeDrag().get(), dt);
        double gravity = player.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
        double airResistance = config.airResistance().get();
        double maxSpeed = config.maxSpeed().get();
        int maxBends = config.maxRopeBends().get();
        boolean allowWrap = config.allowRopeWrapping().get();
        double compliance = config.ropeCompliance().get()
                + CATCH_COMPLIANCE * Mth.clamp(1 - attachedTicks / (double) CATCH_SOFTEN_TICKS, 0, 1);

        peakTension = 0;
        pulling = false;
        Vec3 current = position;
        Vec3 currentVelocity = velocity;
        Vec3 pivot = rope.pivot();

        for (int i = 0; i < substeps; i++) {
            rope.update(player.level, player, current, maxBends, allowWrap, i == 0);
            pivot = rope.pivot();
            Vec3 ropeDirection = GrapplePhysics.ropeDirection(pivot, current);

            if (i == 0) {
                double damping = config.swingDamping().get();
                if (damping > 0) {
                    currentVelocity = currentVelocity.subtract(
                            GrapplePhysics.tangential(currentVelocity, ropeDirection).scale(damping));
                }
            }

            Vec3 acceleration = new Vec3(0, -gravity, 0)
                    .add(steering(player, config, ropeDirection, currentVelocity))
                    .add(winch(config, ropeDirection, current, dt, gravity));

            if (airResistance > 0) {
                acceleration = acceleration.subtract(currentVelocity.scale(airResistance * currentVelocity.length()));
            }

            var solved = GrapplePhysics.substep(
                    current, currentVelocity, pivot, rope.freeLength(),
                    acceleration, dragPerSubstep, dt, compliance
            );
            peakTension = Math.max(peakTension, solved.tension());

            Vec3 next = solved.position();
            Vec3 nextVelocity = solved.velocity();

            if (!RopeState.isSegmentClear(player.level, player, rope.pivot(), next)) {
                Vec3[] boundary = contactBoundary(player, rope.pivot(), current, next);
                rope.update(player.level, player, boundary[1], maxBends, allowWrap, false);
                if (!RopeState.isSegmentClear(player.level, player, rope.pivot(), next)) {
                    unresolvedContacts++;
                }
            }

            current = next;
            currentVelocity = nextVelocity;

            if (pulling) {
                Vec3 outward = GrapplePhysics.ropeDirection(rope.pivot(), current);
                double inward = -currentVelocity.dot(outward);
                double limit = config.pullSpeedLimit().get();
                if (inward > limit) {
                    currentVelocity = currentVelocity.add(outward.scale(inward - limit));
                }
            }
        }

        Vec3 delta = current.subtract(position);
        double distance = delta.length();
        if (distance > maxSpeed) delta = delta.scale(maxSpeed / distance);

        velocity = GrapplePhysics.clampSpeed(currentVelocity, maxSpeed);
        plannedDelta = delta;
        plannedPosition = position.add(delta);
        attachedTicks++;

        propertyPivot.set(pivot);
        propertyRopeLength.set((float) rope.freeLength());
        if (ParCool.getConfig().client().debugRope.get()) reportRopeProblems(player, current);
        unresolvedContacts = 0;
    }

    @OnlyIn(Dist.CLIENT)
    private Vec3 winch(ParCoolConfig.Server.GrapplingHook config, Vec3 ropeDirection, Vec3 position, double dt, double gravity) {
        if (rope == null) return Vec3.ZERO;
        double distance = position.distanceTo(rope.pivot());

        if (ParCoolKeyBinds.SHIFT.state().isDown()) {
            double limit = Math.min(config.maxRange().get() - rope.wrappedLength(), distance + MAX_SLACK);

            rope.setFreeLength(Math.min(limit, rope.freeLength() + config.reelOutSpeed().get() * dt));
            return Vec3.ZERO;
        }

        double minLength = config.minRopeLength().get();
        if (!ParCoolKeyBinds.USE_ITEM.state().isDown() || distance <= minLength) return Vec3.ZERO;

        rope.setFreeLength(Math.max(minLength, Math.min(rope.freeLength(), distance)));
        pulling = true;

        Vec3 pullDirection = ropeDirection.reverse();
        double againstGravity = Math.max(0, gravity * pullDirection.y);
        return pullDirection.scale(config.pullStrength().get() + againstGravity);
    }

    @OnlyIn(Dist.CLIENT)
    private Vec3 steering(LocalPlayer player, ParCoolConfig.Server.GrapplingHook config, Vec3 ropeDirection, Vec3 currentVelocity) {
        Vec3 forward = EntityUtil.getHorizontalLookAngle(player);
        Vec3 input = forward.scale(player.input.forwardImpulse)
                .add(forward.yRot(Mth.HALF_PI).scale(player.input.leftImpulse));
        double inputStrength = input.length();
        if (inputStrength < 0.1) return Vec3.ZERO;
        if (inputStrength > 1) {
            input = input.scale(1 / inputStrength);
            inputStrength = 1;
        }

        double force = config.swingControlForce().get();
        Vec3 steer = input.scale(force);

        double assist = config.swingAssist().get();
        if (assist > 0) {
            double verticality = Mth.clamp(-ropeDirection.y, 0, 1);
            Vec3 along = GrapplePhysics.tangential(currentVelocity, ropeDirection);
            double speed = along.length();
            if (verticality > 0 && speed > 1.0e-4) {
                double headroom = Mth.clamp(1 - speed / config.maxSpeed().get(), 0, 1);
                steer = steer.add(along.scale(force * assist * inputStrength * verticality * headroom / speed));
            }
        }
        return GrapplePhysics.tangential(steer, ropeDirection);
    }

    @OnlyIn(Dist.CLIENT)
    private Vec3[] contactBoundary(LocalPlayer player, Vec3 pivot, Vec3 from, Vec3 to) {
        if (!RopeState.isSegmentClear(player.level, player, pivot, from)) return new Vec3[]{from, to};
        double clear = 0;
        double blocked = 1;
        for (int i = 0; i < CONTACT_SEARCH_STEPS; i++) {
            double middle = (clear + blocked) * 0.5;
            if (RopeState.isSegmentClear(player.level, player, pivot, from.add(to.subtract(from).scale(middle)))) {
                clear = middle;
            } else {
                blocked = middle;
            }
        }
        Vec3 span = to.subtract(from);
        return new Vec3[]{from.add(span.scale(clear)), from.add(span.scale(blocked))};
    }

    @OnlyIn(Dist.CLIENT)
    private void reportRopeProblems(LocalPlayer player, Vec3 attachment) {
        if (rope == null) return;
        List<Vec3> path = new java.util.ArrayList<>(rope.bends().size() + 2);
        path.add(rope.anchor());
        path.addAll(rope.bends());
        path.add(attachment);

        for (int i = 0; i < path.size() - 1; i++) {
            Vec3 from = path.get(i);
            Vec3 to = path.get(i + 1);
            if (RopeState.isSegmentClear(player.level, player, from, to)) continue;
            if (player.tickCount - lastRopeReportTick < 20) return;
            lastRopeReportTick = player.tickCount;
            LOGGER.warn(
                    "[ParCool grapple] rope segment {} of {} passes through the world: {} -> {} ({} contacts, free {}, wrapped {}, {} corners the wrap could not resolve this tick)",
                    i, path.size() - 1,
                    String.format("%.2f,%.2f,%.2f", from.x, from.y, from.z),
                    String.format("%.2f,%.2f,%.2f", to.x, to.y, to.z),
                    rope.contactCount(),
                    String.format("%.2f", rope.freeLength()),
                    String.format("%.2f", rope.wrappedLength()),
                    unresolvedContacts
            );
            player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                    "\u00a7cGrapple: rope segment " + i + "/" + (path.size() - 1)
                            + " through world, " + rope.contactCount() + " contacts"), true);
            return;
        }
    }

    private static Vec3 reconcile(Vec3 velocity, Vec3 planned, Vec3 achieved) {
        return new Vec3(
                reconcileAxis(velocity.x, planned.x, achieved.x),
                reconcileAxis(velocity.y, planned.y, achieved.y),
                reconcileAxis(velocity.z, planned.z, achieved.z)
        );
    }

    private static double reconcileAxis(double velocity, double planned, double achieved) {
        return Math.abs(achieved) < Math.abs(planned) - 1.0e-5 ? achieved : velocity;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onTickInLocalClient() {
        updatePreviewTarget();
        if (momentumTicksLeft <= 0) return;
        if (!(parkourability.player() instanceof LocalPlayer player)) return;
        if (isDoing() || player.isOnGround() || player.horizontalCollision
                || player.isInFluidType() || player.isFallFlying() || player.getAbilities().flying) {
            momentumTicksLeft = 0;
            return;
        }
        momentumTicksLeft--;

        var config = config();
        double keep = config.momentumDrag().get();
        Vec3 movement = player.getDeltaMovement();
        double speed = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
        if (speed < 1.0e-4) return;

        momentumSpeedCap *= keep;
        double factor = Math.min(keep / VANILLA_AIR_FRICTION, momentumSpeedCap / speed);
        if (factor <= 1.0) return;
        player.setDeltaMovement(movement.x * factor, movement.y, movement.z * factor);
    }

    private static BlockState anchoredState(Level level, Vec3 anchor) {
        BlockPos base = new BlockPos(anchor);
        BlockState state = level.getBlockState(base);
        if (!state.isAir()) return state;
        for (Direction direction : Direction.values()) {
            BlockState neighbour = level.getBlockState(base.relative(direction));
            if (!neighbour.isAir()) return neighbour;
        }
        return state;
    }

    @OnlyIn(Dist.CLIENT)
    private void updatePreviewTarget() {
        var player = parkourability.player();
        if (isDoing()
                || !ParCool.getConfig().client().showTargetIndicator.get()
                || !GrapplingHookItem.isHeld(player)
                || player.getAbilities().flying
                || player.isSpectator()) {
            previewTarget = null;
            return;
        }
        var config = config();
        double maxRange = config.maxRange().get();

        var direct = GrappleTargeting.findDirect(player, maxRange, MIN_TARGET_DISTANCE);
        if (direct != null) {
            previewTarget = direct.point();
            previewCooldown = 0;
            return;
        }

        if (previewCooldown > 0) {
            previewCooldown--;
            return;
        }
        previewCooldown = PREVIEW_ASSIST_INTERVAL;

        var target = GrappleTargeting.find(
                player,
                maxRange,
                config.aimAssistAngle().get(),
                ASSIST_RINGS,
                ASSIST_SAMPLES_PER_RING,
                MIN_TARGET_DISTANCE
        );
        previewTarget = target != null ? target.point() : null;
    }

    private static void playSound(Player player, SoundEvent sound, float volume, float pitch) {
        if (!ParCool.getConfig().client().enableActionSounds.get()) return;
        player.level.playLocalSound(player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, volume, pitch, false);
    }
}
