package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.RenderBehaviorEnforcer;
import com.alrex.parcool.client.animation.impl.HideInBlockAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class HideInBlock extends Action {
    private static final BehaviorEnforcer.ID ID_SHOW_NAME = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_SNEAK = BehaviorEnforcer.newID();
    @Nullable
    Vector3d hidingPoint = null;
    @Nullable
    Tuple<BlockPos, BlockPos> hidingArea = null;
    @Nullable
    Vector3d enterPoint = null;
    @Nullable
    Vector3d lookDirection = null;
    boolean hidingBlockChanged = false;
    boolean keyPressed;
    boolean startedFromDiving;

    @Nullable
    public Vector3d getLookDirection() {
        return lookDirection;
    }

    public boolean isStandbyInAir(Parkourability parkourability) {
        if (!keyPressed) return false;
        Dive dive = parkourability.get(Dive.class);
        return (dive.isDoing() || dive.getNotDoingTick() < 2)
                && (parkourability.getAdditionalProperties().getLandingTick() <= 1);
    }

    @Override
    public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
        if (player.isSprinting()
                || player.noPhysics
                || !player.isOnGround()
                || player.isInWater()
                || player.isPassenger()
                || player.isVisuallySwimming()
                || getNotDoingTick() < 6
                || player.hurtTime > 0
                || parkourability.get(Crawl.class).isDoing()
        ) {
            return false;
        }

        BlockPos hideBaseBlockPos=null;
        boolean startFromDiving=false;
        if (isStandbyInAir(parkourability)) {
            hideBaseBlockPos=player.blockPosition().below();
            startFromDiving=true;
        } else if (KeyBindings.getKeyHideInBlock().isDown() && (!ParCoolConfig.Client.Booleans.HideInBlockSneakNeeded.get() || player.getPose() == Pose.CROUCHING)) {
            RayTraceResult result = Minecraft.getInstance().hitResult;
            if (result instanceof BlockRayTraceResult && parkourability.isDoingNothing()) {
                hideBaseBlockPos = ((BlockRayTraceResult) result).getBlockPos();
            }
        }

        if(hideBaseBlockPos!= null){
            Tuple<BlockPos, BlockPos> hideArea = WorldUtil.getHideAbleSpace(player, hideBaseBlockPos);
            if (hideArea == null) return false;
            Vector3d hidePoint = new Vector3d(
                    0.5 + (hideArea.getA().getX() + hideArea.getB().getX()) / 2.,
                    Math.min(hideArea.getA().getY(), hideArea.getB().getY()),
                    0.5 + (hideArea.getA().getZ() + hideArea.getB().getZ()) / 2.
            );
            if (!player.position().closerThan(hidePoint, 1.8)) return false;
            {
                int minX = Math.min(hideArea.getA().getX(), hideArea.getB().getX());
                int maxX = Math.max(hideArea.getA().getX(), hideArea.getB().getX());
                int minY = Math.min(hideArea.getA().getY(), hideArea.getB().getY());
                int maxY = Math.max(hideArea.getA().getY(), hideArea.getB().getY());
                int minZ = Math.min(hideArea.getA().getZ(), hideArea.getB().getZ());
                int maxZ = Math.max(hideArea.getA().getZ(), hideArea.getB().getZ());
                hideArea = new Tuple<>(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
            }
            Vector3d direction;
            boolean stand = player.getBbHeight() < (hideArea.getB().getY() - hideArea.getA().getY() + 1);
            if (stand && startFromDiving)return false;
            if (stand) {
                Vector3d lookAngle = player.getLookAngle();
                direction = Math.abs(lookAngle.x()) > Math.abs(lookAngle.z()) ?
                        new Vector3d(lookAngle.x() > 0 ? 1 : -1, 0, 0) :
                        new Vector3d(0, 0, lookAngle.z() > 0 ? 1 : -1);
            } else {
                boolean zLonger = Math.abs(hideArea.getA().getZ() - hideArea.getB().getZ()) > Math.abs(hideArea.getA().getX() - hideArea.getB().getX());
                direction = zLonger ?
                        new Vector3d(0, 0, player.getLookAngle().z() > 0 ? 1 : -1) :
                        new Vector3d(player.getLookAngle().x() > 0 ? 1 : -1, 0, 0);
            }
            BufferUtil.wrap(startInfo)
                    .putBoolean(stand)
                    .putBoolean(startFromDiving)
                    .putBlockPos(hideArea.getA())
                    .putBlockPos(hideArea.getB())
                    .putVector3d(hidePoint)
                    .putVector3d(player.position())
                    .putVector3d(direction);
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinue(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        if (hidingBlockChanged) {
            return hidingBlockChanged = false;
        }
        return (player.hurtTime <= 0 || (startedFromDiving && getDoingTick()<10))
                && player.getPose() == Pose.STANDING
                && (getDoingTick() < 6 || KeyBindings.getKeyHideInBlock().isDown() || KeyBindings.getKeySneak().isDown());
    }

    @Override
    public void onStart(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
        boolean _stand = BufferUtil.getBoolean(startData);
        startedFromDiving=BufferUtil.getBoolean(startData);
        hidingArea = new Tuple<>(BufferUtil.getBlockPos(startData), BufferUtil.getBlockPos(startData));
        hidingPoint = BufferUtil.getVector3d(startData);
        enterPoint = BufferUtil.getVector3d(startData);
        lookDirection = BufferUtil.getVector3d(startData);
        if (startedFromDiving){
            parkourability.getBehaviorEnforcer().setMarkerEnforcePosition(
                    this::isDoing,
                    () -> hidingPoint
            );
        }else {
            parkourability.getBehaviorEnforcer().setMarkerEnforcePosition(
                    this::isDoing,
                    () -> {
                        if (getDoingTick() == 0)
                            return hidingPoint.subtract(enterPoint).scale(0.75).add(enterPoint);
                        return hidingPoint;
                    }
            );
        }
        parkourability.getBehaviorEnforcer().addMarkerCancellingSneak(ID_SNEAK, this::isDoing);
        player.setPose(Pose.STANDING);
        player.noPhysics = true;
        player.playSound(player.level.getBlockState(new BlockPos(hidingPoint.add(0, 0.2, 0))).getSoundType().getBreakSound(), 1, 1);
    }

    @Override
    public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
        boolean stand = BufferUtil.getBoolean(startData);
        RenderBehaviorEnforcer.serMarkerEnforceCameraType(this::isDoing, () -> PointOfView.THIRD_PERSON_BACK);
        parkourability.getBehaviorEnforcer().addMarkerCancellingShowName(ID_SHOW_NAME, this::isDoing);
        spawnOnHideParticles(player);
        Animation animation = Animation.get(player);
        if (animation == null) return;
        animation.setAnimator(new HideInBlockAnimator(stand,startedFromDiving));
    }

    @Override
    public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
        boolean stand = BufferUtil.getBoolean(startData);
        Animation animation = Animation.get(player);
        parkourability.getBehaviorEnforcer().addMarkerCancellingShowName(ID_SHOW_NAME, this::isDoing);
        spawnOnHideParticles(player);
        if (animation == null) return;
        animation.setAnimator(new HideInBlockAnimator(stand,startedFromDiving));
    }


    @Override
    public void onWorkingTickInServer(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        if (hidingPoint == null) return;
        player.setPos(hidingPoint.x(), hidingPoint.y(), hidingPoint.z());
    }

    @Override
    public void onWorkingTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        player.setDeltaMovement(Vector3d.ZERO);
        player.noPhysics = true;
        player.setSprinting(false);
        player.setPose(Pose.STANDING);
    }

    @Override
    public void onStopInLocalClient(PlayerEntity player) {
        final Vector3d hidePos = hidingPoint;
        final Vector3d entPos = enterPoint;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        parkourability.getBehaviorEnforcer().setMarkerEnforcePosition(
                () -> this.getNotDoingTick() <= 1,
                () -> {
                    if (getNotDoingTick() == 0)
                        return entPos.subtract(hidePos).scale(0.65).add(hidePos);
                    return entPos;
                }
        );
        spawnOnHideParticles(player);
        player.playSound(player.level.getBlockState(new BlockPos(hidingPoint.add(0, 0.2, 0))).getSoundType().getBreakSound(), 1, 1);
    }

    @Override
    public void onStopInOtherClient(PlayerEntity player) {
        spawnOnHideParticles(player);
        player.playSound(player.level.getBlockState(new BlockPos(hidingPoint.add(0, 0.2, 0))).getSoundType().getBreakSound(), 1, 1);
    }

    @Override
    public void onStop(PlayerEntity player) {
        hidingPoint = null;
        enterPoint = null;
        hidingArea = null;
        lookDirection = null;
        player.noPhysics = false;
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnOnHideParticles(PlayerEntity player) {
        if (hidingArea == null) return;
        World world = player.level;
        int minX = hidingArea.getA().getX();
        int minY = hidingArea.getA().getY();
        int minZ = hidingArea.getA().getZ();
        int maxX = hidingArea.getB().getX();
        int maxY = hidingArea.getB().getY();
        int maxZ = hidingArea.getB().getZ();
        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!world.isLoaded(pos)) break;
                    Minecraft.getInstance().particleEngine.destroy(pos, world.getBlockState(pos));
                }
            }
        }
    }

    @Override
    public void onClientTick(PlayerEntity player, Parkourability parkourability, IStamina stamina) {
        keyPressed=KeyBindings.getKeyHideInBlock().isDown();
    }

    @Override
    public void saveSynchronizedState(ByteBuffer buffer) {
        BufferUtil.wrap(buffer).putBoolean(keyPressed);
    }

    @Override
    public void restoreSynchronizedState(ByteBuffer buffer) {
        keyPressed=BufferUtil.getBoolean(buffer);
    }

    private boolean isHidingBlock(BlockPos pos) {
        if (hidingArea == null) {
            return false;
        }
        BlockPos posA = hidingArea.getA(), posB = hidingArea.getB();
        return (posA.getX() <= pos.getX() && pos.getX() <= posB.getX()
                && posA.getY() <= pos.getY() && pos.getY() <= posB.getY()
                && posA.getZ() <= pos.getZ() && pos.getZ() <= posB.getZ()
        );
    }

    @OnlyIn(Dist.CLIENT)
    public void notifyBlockChanged(BlockPos pos) {
        if (isHidingBlock(pos)) {
            hidingBlockChanged = true;
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.None;
    }

    @Nullable
    public Tuple<BlockPos, BlockPos> getHidingArea() {
        return hidingArea;
    }
}
