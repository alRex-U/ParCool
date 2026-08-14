package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import javax.annotation.Nullable;
import java.util.List;

public class Dodge extends ContinuableAction implements ActionExtension.AttackedListener {
    private static final BehaviorEnforcer.ID ID_CANCEL_GET_OFF_BLOCK = BehaviorEnforcer.newID();

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<AnimationType> propertyAnimationType;
    private final SynchronizedProperty<Float> propertyStartedYRot;

    public Dodge(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(ParCoolActions.FAST_RUN));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyAnimationType = SynchronizedProperty.newEnum(AnimationType.class),
                propertyStartedYRot = SynchronizedProperty.newFloat()
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canStart() {
        if (ParCoolKeyBinds.DODGE.state().isJustPressed()) {
            AnimationType type = null;
            if (ParCoolKeyBinds.getMovementInput(LogicalMovement.BACKWARD).isDown()) {
                type = AnimationType.BACK;
            } else if (ParCoolKeyBinds.getMovementInput(LogicalMovement.FORWARD).isDown()) {
                type = AnimationType.FRONT;
            } else if (ParCoolKeyBinds.getMovementInput(LogicalMovement.RIGHT).isDown()) {
                type = AnimationType.RIGHT;
            } else if (ParCoolKeyBinds.getMovementInput(LogicalMovement.LEFT).isDown()) {
                type = AnimationType.LEFT;
            }
            if (type == null) return false;
            propertyAnimationType.set(type);
            propertyStartedYRot.set(parkourability.player().getYRot());
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        return getDoingTick() < 15;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        switch (propertyAnimationType.getOrDefaultIfNull(AnimationType.FRONT)) {
            case BACK:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().DODGE_BACK);
                break;
            case FRONT:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().DODGE_FRONT);
                break;
            case RIGHT:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().DODGE_RIGHT);
                break;
            case LEFT:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().DODGE_RIGHT, true);
                break;
        }
        parkourability.player().playSound(ParCoolSoundEvents.DODGE.get());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInLocalClient() {
        var player = parkourability.player();
        var moveDirection = EntityUtil.getHorizontalLookAngle(player);
        var speed = EntityUtil.getHorizontalMaximumSpeed(player);
        moveDirection = switch (propertyAnimationType.getOrDefaultIfNull(AnimationType.FRONT)) {
            case FRONT -> moveDirection;
            case BACK -> moveDirection.reverse();
            case LEFT -> moveDirection.yRot(Mth.HALF_PI);
            case RIGHT -> moveDirection.yRot(-Mth.HALF_PI);
        };
        var moveVec = moveDirection.scale(speed);
        parkourability.getBehaviorEnforcer().setMarkerEnforcingDeltaMovement(this::isDoing, () -> new Vec3(moveVec.x, player.getDeltaMovement().y, moveVec.z));
        parkourability.getBehaviorEnforcer().addMarkerEnforcingNoDescendingFromEdge(ID_CANCEL_GET_OFF_BLOCK, this::isDoing);
    }

    @Override
    public void onAttacked(LivingAttackEvent event) {
        if (!isDoing()) return;
        if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) return;
        if (getDoingTick() < 7) {
            event.setCanceled(true);
        }
    }

    @Nullable
    public Vec3 getFacingVec() {
        var rot = propertyStartedYRot.get();
        return rot != null ? VectorUtil.fromYawDegree(rot) : null;
    }

    private enum AnimationType {
        BACK, RIGHT, LEFT, FRONT;
    }
}
