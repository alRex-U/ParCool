package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.util.EntityUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Vault extends ContinuableAction {
    private enum Type {
        FORWARD, LEFT, RIGHT
    }

    private static final int MAX_DURATION = 9;
    private static final double MOVE_DURATION_SCALE = 0.35;

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Byte> propertyDuration;
    private final SynchronizedProperty<Type> propertyVaultType;

    // only for local client
    private float vaultHeight;
    private Vec3 obstacleDistance;

    public Vault(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyDuration = SynchronizedProperty.newByte(),
                propertyVaultType = SynchronizedProperty.newEnum(Type.class)
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public void onStartInClient() {
        switch (propertyVaultType.getOrDefaultIfNull(Type.FORWARD)) {
            case FORWARD:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().VAULT_FORWARD);
                break;
            case LEFT:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().VAULT_SIDE);
                break;
            case RIGHT:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().VAULT_SIDE, true);
                break;
        }
        parkourability.player().playSound(ParCoolSoundEvents.VAULT.get());
    }

    @Override
    public void onStartInLocalClient() {
        final int duration = propertyDuration.getOrDefaultIfNull((byte) MAX_DURATION);
        final int jumpDuration = Mth.ceil(duration * MOVE_DURATION_SCALE);
        final float moveHeight = this.vaultHeight;
        final var moveVec = this.obstacleDistance.scale(1.5);
        final var startPos = parkourability.player().position();
        parkourability.getBehaviorEnforcer().setMarkerEnforcingMovePoint(this::isDoing,
                () -> {
                    var tick = getDoingTick() + 1;
                    var moveProgress = tick / (float) jumpDuration;
                    if (tick <= jumpDuration + 3) {
                        return startPos.add(
                                moveVec.x * moveProgress,
                                moveHeight * EasingFunctions.QUAD.easeOut(Math.min(1f, moveProgress * 1.5f)),
                                moveVec.z * moveProgress
                        );
                    }
                    var postProgress = 0.5f * (tick - (jumpDuration + 3)) / (float) (duration - (jumpDuration + 3));
                    return startPos.add(
                            moveVec.x * moveProgress,
                            moveHeight * (1f - EasingFunctions.QUAD.easeIn(postProgress)),
                            moveVec.z * moveProgress
                    );
                });
    }

    @Override
    public boolean canContinue() {
        var duration = propertyDuration.get();
        return duration != null && getDoingTick() < duration;
    }

    @Override
    public boolean canStart() {
        var player = parkourability.player();
        var pos = player.position();
        double maxDeltaMove = EntityUtil.getHorizontalMaximumDeltaMovementValue(player);
        var deltaMovementH = new Vec3(pos.x - player.xo, 0, pos.z - player.zo);
        if (Mth.square(maxDeltaMove * 0.625) > deltaMovementH.lengthSqr()) return false;
        deltaMovementH = deltaMovementH.normalize().scale(maxDeltaMove * 1.8);

        var speedScale = Math.max(1., player.getSpeed() / 0.7);
        var duration = Math.max(5, (int) (MAX_DURATION / speedScale));
        var vaultMovement = deltaMovementH.scale(duration * 0.4);
        var baseBB = player.getBoundingBox();
        baseBB = baseBB.deflate(baseBB.getXsize() * 0.25, 0, baseBB.getZsize() * 0.25);

        var obstacleCollision = getCollisionVec(player, vaultMovement, baseBB.move(0, player.getStepHeight(), 0));
        if (!checkHVecDifferent(obstacleCollision, vaultMovement)) return false; // No obstacles to get over
        var vaultMaxMovement = deltaMovementH.scale(duration);

        // try to get over full block
        var vaultingHeight = baseBB.getYsize() * ((1. + 0.01) / 1.8);
        if (checkCollision(player, vaultMaxMovement, baseBB.move(0, vaultingHeight, 0))) {
            // try to get over fence
            vaultingHeight = baseBB.getYsize() * ((1.5 + 0.01) / 1.8);
            if (checkCollision(player, vaultMaxMovement, baseBB.move(0, vaultingHeight, 0))) {
                return false;
            }
        }
        this.vaultHeight = (float) vaultingHeight;
        this.obstacleDistance = obstacleCollision;
        if (this.obstacleDistance.lengthSqr() < Mth.square(player.getBbWidth() * 1.5)) {
            this.obstacleDistance = obstacleDistance.normalize().scale(player.getBbWidth() * 1.5);
        }
        this.propertyDuration.set((byte) duration);
        this.propertyVaultType.set(getVaultType(vaultMovement, player.level.random));
        return true;
    }

    private static Type getVaultType(Vec3 movementVec, RandomSource randomSource) {
        movementVec = movementVec.normalize();
        var xAbs = Math.abs(movementVec.x);
        if ((xAbs > 0.9848 || xAbs < 0.1736) && randomSource.nextInt(8) != 0) { // 0.9848 is cos(10 degrees), 0.1736 is cos(80 degrees)
            return Type.FORWARD;
        }
        if (0 < movementVec.z) {
            return movementVec.z < movementVec.x || (movementVec.x < 0 && -movementVec.x < movementVec.z)
                    ? Type.RIGHT : Type.LEFT;
        }
        return movementVec.x < movementVec.z || (movementVec.x > 0 && -movementVec.x > movementVec.z)
                ? Type.RIGHT : Type.LEFT;
    }

    private static boolean checkHVecDifferent(Vec3 a, Vec3 b) {
        return Math.abs(a.x - b.x) > 1e-4 || Math.abs(a.z - b.z) > 1e-4;
    }

    private static boolean checkCollision(Player player, Vec3 movement, AABB boundingBox) {
        var collision = getCollisionVec(player, movement, boundingBox);
        return checkHVecDifferent(collision, movement);
    }

    private static Vec3 getCollisionVec(Player player, Vec3 movement, AABB boundingBox) {
        if (Math.abs(movement.lengthSqr()) < 1e-4) return Vec3.ZERO;
        return Entity.collideBoundingBox(player, movement, boundingBox, player.level, player.level.getEntityCollisions(player, boundingBox.expandTowards(movement)));
    }
}
