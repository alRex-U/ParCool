package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.SynchronizedDataHolder;
import com.alrex.parcool.api.action.SynchronizedProperty;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.math.MathUtil;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.VectorUtil;

import java.util.List;

public class LongJump extends Action {
    private static final int INPUT_ACCEPTANCE_TICK = 10;
    private static final int COOLDOWN = 30;

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Float> propertyJumpDirectionYaw;
    private int cooldownTick = 0;

    public LongJump(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(
                ParCoolActions.HIDE_IN_BLOCK
        ));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyJumpDirectionYaw = SynchronizedProperty.newFloat()
        );
    }

    @Override
    public void onTickInLocalClient() {
        if (cooldownTick > 0) cooldownTick--;
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canStart() {
        if (cooldownTick > 0) return false;
        var shiftKey = ParCoolKeyBinds.SHIFT;
        if (shiftKey.state().isJustReleased()
                && shiftKey.state().getPreviousPressedDurationTick() < INPUT_ACCEPTANCE_TICK
                && parkourability.get(ParCoolActions.FAST_RUN).getNotDoingTick() < INPUT_ACCEPTANCE_TICK
        ) {
            var deltaMove = parkourability.player().getDeltaMovement();
            if (deltaMove.lengthSqr() < 1e-5) return false;
            var deltaMoveYaw = (float) VectorUtil.toYawDegree(deltaMove);
            var lookAngleYaw = parkourability.player().getYRot();
            var rot = MathUtil.rotLerp(
                    shiftKey.state().getPreviousPressedDurationTick() / (float) INPUT_ACCEPTANCE_TICK,
                    deltaMoveYaw, lookAngleYaw
            );
            propertyJumpDirectionYaw.set(rot);
            return true;
        }
        return false;
    }

    @Override
    public void onStartInLocalClient() {
        var player = parkourability.player();
        var jumpDirectionYaw = propertyJumpDirectionYaw.get();
        if (jumpDirectionYaw == null) return;
        var jumpDirection = VectorUtil.fromYawDegree(jumpDirectionYaw);
        player.jumpFromGround();
        player.setOnGround(false);
        player.setDeltaMovement(
                jumpDirection.x() * 0.7,
                player.getDeltaMovement().y() * 1.16667,
                jumpDirection.z() * 0.7
        );
        cooldownTick = COOLDOWN;
    }

    @Override
    public void onStartInClient() {
        if (parkourability.player() instanceof IPlayerAnimatorHolder holder) {
            holder.getParCoolPlayerAnimator().start(AnimationRegistries.get().animations().LONG_JUMP);
        }
    }
}
