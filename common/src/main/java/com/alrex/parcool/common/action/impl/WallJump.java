package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.SynchronizedDataHolder;
import com.alrex.parcool.api.action.SynchronizedProperty;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.MathUtil;
import com.alrex.parcool.util.VectorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class WallJump extends Action {
    private enum Type {
        LEFT, RIGHT
    }

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Vec3> propertyJumpVec;
    private final SynchronizedProperty<Type> propertyJumpType;

    public WallJump(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyJumpVec = SynchronizedProperty.newVec3(),
                propertyJumpType = SynchronizedProperty.newEnum(Type.class)
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onStartInClient() {
        switch (propertyJumpType.getOrDefaultIfNull(Type.LEFT)) {
            case LEFT:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().WALL_JUMP);
                break;
            case RIGHT:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().WALL_JUMP, true);
                break;
        }
        parkourability.player().playSound(ParCoolSoundEvents.WALL_JUMP.get());
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onStartInLocalClient() {
        var deltaMovement = parkourability.player().getDeltaMovement();
        var jumpVec = propertyJumpVec.getOrDefaultIfNull(Vec3.ZERO);
        parkourability.player().setDeltaMovement(
                deltaMovement.x + jumpVec.x,
                deltaMovement.y > jumpVec.y ? deltaMovement.y + jumpVec.y : jumpVec.y,
                deltaMovement.z + jumpVec.z
        );
        for (var listener : parkourability.getActions().getExtensionListeners(ActionExtension.LeaveFromWallListener.class)) {
            listener.onLeaveFromWall();
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onStartInServer() {
        parkourability.player().fallDistance = 0;
    }

    @Override
    public boolean canStart() {
        if (parkourability.getAdditionalProperties().getOnGroundDurations().durationNotDoing() < 4) return false;
        if (!ParCoolKeyBinds.JUMP.state().isJustPressed()) return false;
        var player = parkourability.player();
        var wall = parkourability.getAdditionalProperties().getDefaultWallInteraction();
        if (wall == null) return false;
        var lookVec = EntityUtil.getHorizontalLookAngle(player);
        var dot = lookVec.dot(wall.asVec());
        if (dot > 0.7071) { // 0.7071 is 1/sqrt(2)
            return false;
        }
        var yRot = player.getYHeadRot();
        var xRot = Mth.clamp(MathUtil.mapLinear(player.getXRot(), -35, 0, -80, -70), -80f, -70f);
        var jumpPower = player.getJumpPower() + player.getJumpBoostPower();
        var jumpVec = VectorUtil.calculateViewVector(xRot, yRot).scale(jumpPower);
        if (dot > 0) {
            jumpVec = VectorUtil.calculateReflectVector(jumpVec, wall.asVec().reverse());
        }
        jumpVec = jumpVec.add(wall.asVec().reverse().scale(0.12));
        propertyJumpVec.set(jumpVec);
        propertyJumpType.set(lookVec.cross(wall.asVec()).y < 0 ? Type.LEFT : Type.RIGHT);

        return true;
    }
}
