package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.client.sound.SlideDownSoundInstance;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.InteractingWallDirection;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.common.damage.DamageTypes;
import com.alrex.parcool.common.item.armor.TraceurGlovesItem;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;

import javax.annotation.Nullable;
import java.util.List;

public class SlideDown extends ContinuableAction implements ActionExtension.LeaveFromWallListener, ActionExtension.KeyMapTriggeredListener {
    private static final BehaviorEnforcer.ID ID_FALL_FLY_CANCEL = BehaviorEnforcer.newID();
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<InteractingWallDirection> propertyDirection;
    private final SynchronizedProperty<Float> propertyBeginningYSpeed;

    private AnimationData currentAnimData = AnimationData.NONE;
    private short tickSinceCanceled = 0;

    private byte damageCoolTime;
    private byte damageCount;

    public SlideDown(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(
                ParCoolActions.GRAPPLE,
                ParCoolActions.CLIMB_UP,
                ParCoolActions.VAULT,
                ParCoolActions.HANG_ON,
                ParCoolActions.POLE_CLIMB,
                ParCoolActions.DIVE,
                ParCoolActions.CASTAWAY
        ));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyDirection = SynchronizedProperty.newEnum(InteractingWallDirection.class),
                propertyBeginningYSpeed = SynchronizedProperty.newFloat()
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canContinue() {
        if (tickSinceCanceled < 3) {
            return false;
        }
        return canStart();
    }

    @Override
    public boolean canStart() {
        if (tickSinceCanceled < 3 || parkourability.player().getDeltaMovement().y >= -1e-4) {
            return false;
        }
        if (ParCoolKeyBinds.SLIDE_DOWN.key().isDown()) {
            var direction = parkourability.getAdditionalProperties().getDefaultWallInteraction();
            if (direction == null) return false;
            propertyDirection.set(direction);
            propertyBeginningYSpeed.set((float) parkourability.player().getDeltaMovement().y);
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onWorkingTickInClient() {
        currentAnimData = AnimationData.get(this, parkourability.player());

        var direction = propertyDirection.get();
        if (direction == null) return;
        var player = parkourability.player();
        var pos = player.position();
        var blockPos = new BlockPos(
                Mth.floor(pos.x + direction.asVec().x),
                Mth.floor(pos.y),
                Mth.floor(pos.z + direction.asVec().z)
        );
        var blockState = player.level().getBlockState(blockPos);
        if (blockState.isAir()) return;
        var random = player.level().random;
        var particleMove = com.alrex.parcool.client.animation.system.util.EntityUtil.getPositionDifference(player).reverse().scale(0.1).add(direction.asVec().reverse().scale(0.3));
        var particlePos = pos.add(0, player.getBbHeight() + 0.5, 0).add(direction.asVec().scale(player.getBbWidth() * 0.5));

        player.level().addParticle(
                new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                particlePos.x + (random.nextDouble() - 0.5) * 0.35,
                particlePos.y,
                particlePos.z + (random.nextDouble() - 0.5) * 0.35,
                particleMove.x, particleMove.y, particleMove.z
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInLocalClient() {
        if (!(parkourability.player() instanceof LocalPlayer player)) return;
        parkourability.getBehaviorEnforcer().addMarkerEnforcingNoFallFlying(ID_FALL_FLY_CANCEL, this::isDoing);
        parkourability.getBehaviorEnforcer().setMarkerEnforcingMovePoint(
                this::isDoing, () -> {
                    var direction = propertyDirection.get();
                    if (direction == null) return null;

                    var speed = player.getSpeed() * 0.2f;
                    var moveVec = player.input.getMoveVector().scale(speed);
                    var actualMoveVec = new Vec3(moveVec.x, 0, moveVec.y).yRot((float) Math.toRadians(-player.getYRot()));
                    if (direction.isProtrusion()) {
                        return player.position()
                                .add(new Vec3(
                                        direction.getSignX() > 0 ? Math.max(0, actualMoveVec.x) : Math.min(0, actualMoveVec.x),
                                        player.getDeltaMovement().y,
                                        direction.getSignZ() > 0 ? Math.max(0, actualMoveVec.z) : Math.min(0, actualMoveVec.z)
                                ));
                    } else if (direction.isOblique()) {
                        var directionVec = direction.asVec().yRot(Mth.HALF_PI);
                        return player.position()
                                .add(0, player.getDeltaMovement().y, 0)
                                .add(directionVec.scale(directionVec.dot(actualMoveVec)));
                    } else {
                        return player.position()
                                .add(direction.getSignX() * 0.2, player.getDeltaMovement().y, direction.getSignZ() * 0.2)
                                .add(actualMoveVec);
                    }
                }
        );
        Minecraft.getInstance().getSoundManager().play(new SlideDownSoundInstance(player, this));
    }

    @Override
    public void onStartInServer() {
        damageCount = (byte) Mth.clamp(5.5 * (-propertyBeginningYSpeed.getOrDefaultIfNull(0f) - 1.) / parkourability.player().getBbHeight(), 0, Byte.MAX_VALUE);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onTickInLocalClient() {
        if (tickSinceCanceled < 255) tickSinceCanceled++;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().SLIDE_DOWN);
    }

    @Nullable
    public Vec3 getWallVec(float partial) {
        var wallVec = propertyDirection.get();
        if (wallVec == null) return null;
        return wallVec.asVec();
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

    @Override
    public void onWorkingTickInLocalClient() {
        var player = parkourability.player();
        var attr = player.getAttribute(ParCoolAttributes.SLIDE_DOWN_DECELERATION.get());
        if (attr == null) return;
        parkourability.player().setDeltaMovement(parkourability.player().getDeltaMovement().multiply(0, 1. - attr.getValue(), 0));
    }

    @Override
    public void onWorkingTickInServer() {
        var player = parkourability.player();
        var attr = player.getAttribute(ParCoolAttributes.SLIDE_DOWN_DECELERATION.get());
        if (attr == null) return;
        player.fallDistance *= (float) (1. - attr.getValue());

        if (damageCoolTime <= 0) {
            if (damageCount <= 0) return;
            damageCount--;
            if (!ParCool.getConfig().server().damageWithoutGlove.get()) return;
            if (TraceurGlovesItem.isEquipped(player)) return;
            int invulnerableTime = player.invulnerableTime; // bypass invulnerableTime
            damageCoolTime = 1;
            player.invulnerableTime = 0;
            player.hurt(player.level().damageSources().source(DamageTypes.FRICTION), 0.3f);
            player.invulnerableTime = invulnerableTime;
        } else {
            damageCoolTime--;
        }
    }

    @Override
    public void onLeaveFromWall() {
        tickSinceCanceled = 0;
    }

    @Override
    public void onInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (isDoing() && (event.isAttack() || event.isUseItem())) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }

    private record AnimationData(
            float blendFactorRightToWall,
            float blendFactorLeftToWall,
            float blendFactorBackToWall
    ) {
        static final AnimationData NONE = new AnimationData(0, 0, 0);

        public static AnimationData get(SlideDown slideDown, Player player) {
            var direction = slideDown.propertyDirection.get();
            if (direction == null) return NONE;
            var wallVec = direction.asVec();
            var horizontalLookVec = EntityUtil.getHorizontalLookAngle(player);
            var dotOfWallVecLookVec = (float) horizontalLookVec.dot(wallVec);

            return new AnimationData(
                    getBlendFactorRightToWall(horizontalLookVec, wallVec, dotOfWallVecLookVec),
                    getBlendFactorLeftToWall(horizontalLookVec, wallVec, dotOfWallVecLookVec),
                    getBlendFactorBackToWall(horizontalLookVec, wallVec, dotOfWallVecLookVec)
            );
        }

        private static float getBlendFactorLeftToWall(Vec3 horizontalLookVec, Vec3 wallVec, float dotOfWallVecLookVec) {
            if (wallVec.yRot(Mth.HALF_PI).dot(horizontalLookVec) < 0) return 0;
            return MathUtil.mapLinear(
                    -dotOfWallVecLookVec, -1, -0.7071f /*-cos(pi/4)*/, 0, 1
            );
        }

        private static float getBlendFactorRightToWall(Vec3 horizontalLookVec, Vec3 wallVec, float dotOfWallVecLookVec) {
            if (wallVec.yRot(Mth.HALF_PI).dot(horizontalLookVec) > 0) return 0;
            return MathUtil.mapLinear(
                    -dotOfWallVecLookVec, -1, -0.7071f /*-cos(pi/4)*/, 0, 1
            );
        }

        private static float getBlendFactorBackToWall(Vec3 horizontalLookVec, Vec3 wallVec, float dotOfWallVecLookVec) {
            return MathUtil.mapLinear(
                    -dotOfWallVecLookVec, 0.866f /*cos(pi/3)*/, 1, 0, 1
            );
        }
    }
}
