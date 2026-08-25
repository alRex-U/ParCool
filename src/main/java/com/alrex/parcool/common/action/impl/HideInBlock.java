package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolBlockTags;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.RenderBehaviorEnforcer;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.animation.system.math.EasingFunctions;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class HideInBlock
        extends ContinuableAction
        implements
        ActionExtension.VisibilityListener,
        ActionExtension.LandListener,
        ActionExtension.BlockChangedInClientListener,
        ActionExtension.KeyMapTriggeredListener {
    private static final BehaviorEnforcer.ID ID_CANCEL_SHOW_NAME = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_CANCEL_SNEAK = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_NO_PHYSICS = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_ENFORCE_IMMEDIATE_EYE_HEIGHT = BehaviorEnforcer.newID();

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Vec3> propertyHidingDirection;
    private final SynchronizedProperty<Vec3> propertyHidingStartedPoint;
    private final SynchronizedProperty<Vec3> propertyHidingPoint;
    private final SynchronizedProperty<Boolean> propertyStanding;
    private final SynchronizedProperty<Boolean> propertyDiving;
    private final SynchronizedProperty<BlockPos> propertyHidingAreaEdge1;
    private final SynchronizedProperty<BlockPos> propertyHidingAreaEdge2;
    private final SynchronizedProperty<Byte> propertyTransitionDuration;

    private boolean hidingBlockChanged = false;
    private boolean startingFromDive = false;

    public HideInBlock(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(ParCoolActions.CRAWL));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyHidingDirection = SynchronizedProperty.newVec3Horizontal(),
                propertyHidingStartedPoint = SynchronizedProperty.newVec3(),
                propertyHidingPoint = SynchronizedProperty.newVec3(),
                propertyHidingAreaEdge1 = SynchronizedProperty.newBlockPos(),
                propertyHidingAreaEdge2 = SynchronizedProperty.newBlockPos(),
                propertyStanding = SynchronizedProperty.newBoolean(),
                propertyDiving = SynchronizedProperty.newBoolean(),
                propertyTransitionDuration = SynchronizedProperty.newByte()
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInLocalClient() {
        RenderBehaviorEnforcer.getInstance().setMarkerEnforcingCameraType(this::isDoing, () -> CameraType.THIRD_PERSON_BACK);
        RenderBehaviorEnforcer.getInstance().addMarkerEnforcingImmediateEyeHeightChange(
                ID_ENFORCE_IMMEDIATE_EYE_HEIGHT, () -> this.isDoing() || getNotDoingTick() < 2
        );
        parkourability.getBehaviorEnforcer().noShowNameMarks.add(ID_CANCEL_SHOW_NAME, this::isDoing);

        var hidingPoint = propertyHidingPoint.get();
        if (hidingPoint == null) return;
        var hideStartedPoint = propertyHidingStartedPoint.get();
        if (hideStartedPoint == null) return;
        byte transitionDuration = propertyTransitionDuration.getOrDefaultIfNull((byte) 1);
        var player = parkourability.player();
        player.noPhysics = true;
        parkourability.getBehaviorEnforcer().setMarkerEnforcingPosition(
                this::isDoing,
                () -> {
                    var doingTick = getDoingTick();
                    if (doingTick < transitionDuration)
                        return VectorUtil.lerp(EasingFunctions.QUAD.easeInOut((doingTick + 1) / (float) (transitionDuration + 1)), hideStartedPoint, hidingPoint);
                    return hidingPoint;
                }
        );
        player.playSound(player.level().getBlockState(new BlockPos(
                Mth.floor(hidingPoint.x), Mth.floor(hidingPoint.y + 0.2), Mth.floor(hidingPoint.z)
        )).getSoundType().getBreakSound(), 1, 1);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(
                propertyStanding.getOrDefaultIfNull(Boolean.TRUE)
                        ? AnimationRegistries.get().animations().HIDE_IN_BLOCK_STANDING
                        : AnimationRegistries.get().animations().HIDE_IN_BLOCK_CRAWLING
        );
        var area1 = propertyHidingAreaEdge1.get();
        if (area1 == null) return;
        var area2 = propertyHidingAreaEdge2.get();
        if (area2 == null) return;
        spawnOnHideParticles(parkourability.player().level(), area1, area2);
    }

    @Override
    public void onStart() {
        parkourability.getBehaviorEnforcer().noPhysicsMarks.add(ID_NO_PHYSICS, this::isDoing);
        var area1 = propertyHidingAreaEdge1.get();
        if (area1 == null) return;
        var area2 = propertyHidingAreaEdge2.get();
        if (area2 == null) return;
        var areaHeight = Math.abs(area1.getY() - area2.getY()) + 1;
        var player = parkourability.player();
        var defaultEyeHeight = player.getDimensions(Pose.STANDING).height * 0.85;
        if (player.isLocalPlayer()) {
            player.setShiftKeyDown(false);
            parkourability.getBehaviorEnforcer().noSneakMarks.add(ID_CANCEL_SNEAK, this::isDoing);
        }
        if (defaultEyeHeight < areaHeight) {
            parkourability.getBehaviorEnforcer().setMarkerEnforcingEyeHeight(() -> this.isDoing() || this.getNotDoingTick() < 1, () -> areaHeight + 0.2f);
            player.refreshDimensions();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStopInLocalClient() {
        var hideStartedPoint = propertyHidingStartedPoint.get();
        if (hideStartedPoint == null) return;
        parkourability.getBehaviorEnforcer().setMarkerEnforcingPosition(
                () -> this.getNotDoingTick() == 0,
                () -> hideStartedPoint
        );
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStopInClient() {
        var area1 = propertyHidingAreaEdge1.get();
        if (area1 == null) return;
        var area2 = propertyHidingAreaEdge2.get();
        if (area2 == null) return;
        spawnOnHideParticles(parkourability.player().level(), area1, area2);
    }

    @Override
    public void onStop() {
        var player = parkourability.player();
        var hidingPoint = propertyHidingPoint.get();
        if (hidingPoint != null) {
            player.playSound(player.level().getBlockState(new BlockPos(
                    Mth.floor(hidingPoint.x), Mth.floor(hidingPoint.y + 0.2), Mth.floor(hidingPoint.z)
            )).getSoundType().getBreakSound(), 1, 1);
        }
    }

    @Override
    public void onTick() {
        if (getNotDoingTick() == 1) {
            parkourability.player().refreshDimensions();
        }
    }

    @Override
    public boolean canContinue() {
        if (hidingBlockChanged) return hidingBlockChanged = false;
        return (parkourability.player().hurtTime <= 0 || (propertyDiving.getOrDefaultIfNull(Boolean.FALSE) && getDoingTick() < 10))
                && (getDoingTick() < 6 || ParCoolKeyBinds.HIDE_IN_BLOCK.key().isDown());
    }

    @Override
    public boolean canStart() {
        var fromDive = startingFromDive;
        startingFromDive = false;
        var player = parkourability.player();
        if ((!fromDive && (!player.isShiftKeyDown() || player.hurtTime > 0)) || !ParCoolKeyBinds.HIDE_IN_BLOCK.key().isDown()) {
            return false;
        }

        var result = Minecraft.getInstance().hitResult;
        BlockPos hideBaseBlockPos;
        if (fromDive) {
            hideBaseBlockPos = player.getBlockPosBelowThatAffectsMyMovement();
        } else {
            if (!(result instanceof BlockHitResult blockHitResult)) {
                return false;
            }
            hideBaseBlockPos = blockHitResult.getBlockPos();
        }
        var hideArea = getHideAbleSpace(player, hideBaseBlockPos);
        if (hideArea == null) return false;
        hideArea = new Tuple<>(
                new BlockPos(
                        Math.min(hideArea.getA().getX(), hideArea.getB().getX()),
                        Math.min(hideArea.getA().getY(), hideArea.getB().getY()),
                        Math.min(hideArea.getA().getZ(), hideArea.getB().getZ())
                ),
                new BlockPos(
                        Math.max(hideArea.getA().getX(), hideArea.getB().getX()),
                        Math.max(hideArea.getA().getY(), hideArea.getB().getY()),
                        Math.max(hideArea.getA().getZ(), hideArea.getB().getZ())
                )
        );
        var hidePoint = new Vec3(
                0.5 + (hideArea.getA().getX() + hideArea.getB().getX()) / 2.,
                Math.min(hideArea.getA().getY(), hideArea.getB().getY()),
                0.5 + (hideArea.getA().getZ() + hideArea.getB().getZ()) / 2.
        );
        if (!player.position().closerThan(hidePoint, 1.8)) return false;
        boolean stand = player.getBbHeight() < (hideArea.getB().getY() - hideArea.getA().getY() + 1);

        propertyDiving.set(fromDive);
        propertyStanding.set(stand);
        propertyTransitionDuration.set((byte) 2);
        propertyHidingDirection.set(getHidingBodyDirection(stand, player, hideArea));
        propertyHidingAreaEdge1.set(hideArea.getA());
        propertyHidingAreaEdge2.set(hideArea.getB());
        propertyHidingStartedPoint.set(player.position());
        propertyHidingPoint.set(hidePoint);

        return true;
    }

    @Nullable
    public Vec3 getFacingVec() {
        return isDoing() ? propertyHidingDirection.get() : null;
    }

    @Nonnull
    private static Vec3 getHidingBodyDirection(boolean stand, Player player, Tuple<BlockPos, BlockPos> hideArea) {
        if (stand) {
            var lookAngle = player.getLookAngle();
            return Math.abs(lookAngle.x()) > Math.abs(lookAngle.z()) ?
                    new Vec3(lookAngle.x() > 0 ? 1 : -1, 0, 0) :
                    new Vec3(0, 0, lookAngle.z() > 0 ? 1 : -1);
        } else {
            boolean zLonger = Math.abs(hideArea.getA().getZ() - hideArea.getB().getZ()) > Math.abs(hideArea.getA().getX() - hideArea.getB().getX());
            return zLonger ?
                    new Vec3(0, 0, player.getLookAngle().z() > 0 ? 1 : -1) :
                    new Vec3(player.getLookAngle().x() > 0 ? 1 : -1, 0, 0);
        }
    }

    private static boolean isHideAblePoint(Level world, Block block, BlockPos pos) {
        return world.isLoaded(pos)
                && world.getBlockState(pos).is(block)
                && world.getBlockState(pos.above()).isAir();
    }

    @Nullable
    private static Tuple<BlockPos, BlockPos> getHideAbleSpace(Entity entity, BlockPos base) {
        var world = entity.level();
        if (!world.isLoaded(base)) return null;
        var state = world.getBlockState(base);
        var block = state.getBlock();
        if (state.getTags().noneMatch(ParCoolBlockTags.HIDE_ABLE::equals)) return null;
        if (!world.getBlockState(base.above()).isAir()) {
            if (isHideAblePoint(world, block, base.above())) {
                return new Tuple<>(base, base.above());
            }
            return null;
        }
        double entityWidth = entity.getBbWidth();
        double entityHeight = entity.getBbHeight();
        if (entityHeight >= 2 || entityWidth >= 1) return null;
        if (entityHeight < 1) return new Tuple<>(base, base);
        var lookAngle = EntityUtil.getHorizontalLookAngle(entity);
        if (Math.abs(lookAngle.z()) > Math.abs(lookAngle.x())) {
            if (lookAngle.z() > 0) {
                if (isHideAblePoint(world, block, base.south())) return new Tuple<>(base, base.south());
                if (lookAngle.x() > 0) {
                    if (isHideAblePoint(world, block, base.east())) return new Tuple<>(base, base.east());
                    if (isHideAblePoint(world, block, base.west())) return new Tuple<>(base, base.west());
                } else {
                    if (isHideAblePoint(world, block, base.west())) return new Tuple<>(base, base.west());
                    if (isHideAblePoint(world, block, base.east())) return new Tuple<>(base, base.east());
                }
                if (isHideAblePoint(world, block, base.north())) return new Tuple<>(base, base.north());
            } else {
                if (isHideAblePoint(world, block, base.north())) return new Tuple<>(base, base.north());
                if (lookAngle.x() > 0) {
                    if (isHideAblePoint(world, block, base.east())) return new Tuple<>(base, base.east());
                    if (isHideAblePoint(world, block, base.west())) return new Tuple<>(base, base.west());
                } else {
                    if (isHideAblePoint(world, block, base.west())) return new Tuple<>(base, base.west());
                    if (isHideAblePoint(world, block, base.east())) return new Tuple<>(base, base.east());
                }
                if (isHideAblePoint(world, block, base.south())) return new Tuple<>(base, base.south());
            }
        } else {
            if (lookAngle.x() > 0) {
                if (isHideAblePoint(world, block, base.east())) return new Tuple<>(base, base.east());
                if (lookAngle.z() > 0) {
                    if (isHideAblePoint(world, block, base.south())) return new Tuple<>(base, base.south());
                    if (isHideAblePoint(world, block, base.north())) return new Tuple<>(base, base.north());
                } else {
                    if (isHideAblePoint(world, block, base.north())) return new Tuple<>(base, base.north());
                    if (isHideAblePoint(world, block, base.south())) return new Tuple<>(base, base.south());
                }
                if (isHideAblePoint(world, block, base.west())) return new Tuple<>(base, base.west());
            } else {
                if (isHideAblePoint(world, block, base.west())) return new Tuple<>(base, base.west());
                if (lookAngle.z() > 0) {
                    if (isHideAblePoint(world, block, base.south())) return new Tuple<>(base, base.south());
                    if (isHideAblePoint(world, block, base.north())) return new Tuple<>(base, base.north());
                } else {
                    if (isHideAblePoint(world, block, base.north())) return new Tuple<>(base, base.north());
                    if (isHideAblePoint(world, block, base.south())) return new Tuple<>(base, base.south());
                }
                if (isHideAblePoint(world, block, base.east())) return new Tuple<>(base, base.east());
            }
        }
        if (world.getBlockState(base.below()).is(block) && Math.abs(entity.getY() - base.below().getY()) < 0.2) {
            return new Tuple<>(base.below(), base);
        }
        return null;
    }

    @Override
    public void onUpdateVisibility(LivingEvent.LivingVisibilityEvent event) {
        if (isDoing()) event.modifyVisibility(event.getVisibilityModifier() * 0.1);
    }

    @OnlyIn(Dist.CLIENT)
    private static void spawnOnHideParticles(Level world, BlockPos hidingArea1, BlockPos hidingArea2) {
        var particleEngine = Minecraft.getInstance().particleEngine;
        int minX = hidingArea1.getX();
        int minY = hidingArea1.getY();
        int minZ = hidingArea1.getZ();
        int maxX = hidingArea2.getX();
        int maxY = hidingArea2.getY();
        int maxZ = hidingArea2.getZ();
        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {
                    var pos = new BlockPos(x, y, z);
                    if (!world.isLoaded(pos)) break;
                    particleEngine.destroy(pos, world.getBlockState(pos));
                }
            }
        }
    }

    @Override
    public void onLand(LivingFallEvent event) {
        if (parkourability.get(ParCoolActions.DIVE).isDoing()) {
            startingFromDive = true;
        }
    }

    @Override
    public void onChangeBlock(BlockPos pos) {
        if (isDoing()) {
            var blockPos1 = propertyHidingAreaEdge1.get();
            var blockPos2 = propertyHidingAreaEdge2.get();
            if (blockPos1 == null || blockPos2 == null) return;
            if (blockPos1.getX() <= pos.getX() && pos.getX() <= blockPos2.getX()
                    && blockPos1.getY() <= pos.getY() && pos.getY() <= blockPos2.getY()
                    && blockPos1.getZ() <= pos.getZ() && pos.getZ() <= blockPos2.getZ()
            ) {
                hidingBlockChanged = true;
            }
        }
    }

    @Override
    public void onInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (isDoing() && (event.isAttack() || event.isUseItem())) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }
}
