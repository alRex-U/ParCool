package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.SynchronizedDataHolder;
import com.alrex.parcool.api.action.SynchronizedProperty;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.EntityUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;

public class TrickJump extends Action implements ActionExtension.JumpListener {
    public enum Type {
        FORWARD, BACK, STRIDE
    }

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Type> propertyTrickType;
    private boolean jumped;
    private boolean strideMirror;

    public TrickJump(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(ParCoolActions.DIVE));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyTrickType = SynchronizedProperty.newEnum(Type.class)
        );
    }

    @Override
    public boolean canStart() {
        if (!jumped) return false;
        jumped = false;

        var duration = ParCoolKeyBinds.getMovementInput(LogicalMovement.BACKWARD).getPressedDuration();
        if (0 <= duration && duration < 5) {
            propertyTrickType.set(Type.BACK);
            return true;
        }
        if (parkourability.get(ParCoolActions.FAST_RUN).isDoing()) {
            var player = parkourability.player();
            var vec = EntityUtil.getHorizontalLookAngle(player).scale(3.5);
            var collideVec = Entity.collideBoundingBox(player, vec, player.getBoundingBox().deflate(0.1), player.level, Collections.emptyList());
            if (Math.abs(vec.x - collideVec.x) > 1e-4 || Math.abs(vec.z - collideVec.z) > 1e-4) return false;
            if (parkourability.getAdditionalProperties().getOnGroundDurations().lastDurationDoing() < 3) {
                propertyTrickType.set(Type.STRIDE);
                return true;
            }
            if (parkourability.player().getRandom().nextInt(8) != 0) {
                propertyTrickType.set(Type.FORWARD);
                return true;
            }
        }
        duration = ParCoolKeyBinds.getMovementInput(LogicalMovement.FORWARD).getPressedDuration();
        if (0 <= duration && duration < 5) {
            propertyTrickType.set(Type.FORWARD);
            return true;
        }
        return false;
    }

    @Override
    public void onJump() {
        jumped = true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        var type = propertyTrickType.get();
        if (type == null) return;
        if (type == Type.STRIDE) {
            strideMirror = !strideMirror;
            PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(
                    AnimationRegistries.get().animations().STRIDE_JUMP, strideMirror
            );
        } else {
            PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(
                    type == Type.FORWARD
                            ? AnimationRegistries.get().animations().TRICK_JUMP_FORWARD
                            : AnimationRegistries.get().animations().TRICK_JUMP_BACK
            );
        }
    }
}
