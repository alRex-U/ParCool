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
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
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
    @Nullable
    Vector3d hidingPoint = null;
    @Nullable
    Tuple<BlockPos, BlockPos> hidingArea = null;
    @Nullable
    Vector3d enterPoint = null;
    @Nullable
    Vector3d lookDirection = null;

    @Nullable
    public Vector3d getLookDirection() {
        return lookDirection;
    }

    @Override
    public boolean canStart(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startInfo) {
        if (player.isSprinting()
                || player.noPhysics
                || !player.isOnGround()
                || player.isInWater()
                || player.isPassenger()
                || player.isVisuallySwimming()
                || parkourability.isDoingNothing()
                || !KeyBindings.getKeyBindHideInBlock().isDown()
                || getNotDoingTick() < 6
                || player.hurtTime > 0
        ) {
            return false;
        }
        RayTraceResult result = Minecraft.getInstance().hitResult;
        if (result instanceof BlockRayTraceResult) {
            BlockPos lookingBlock = ((BlockRayTraceResult) result).getBlockPos();
            Tuple<BlockPos, BlockPos> hideArea = WorldUtil.getHideAbleSpace(player, lookingBlock);
            if (hideArea == null) return false;
            Vector3d hidePoint = new Vector3d(
                    0.5 + (hideArea.getA().getX() + hideArea.getB().getX()) / 2.,
                    (hideArea.getA().getY() + hideArea.getB().getY()) / 2.,
                    0.5 + (hideArea.getA().getZ() + hideArea.getB().getZ()) / 2.
            );
            if (!player.position().closerThan(hidePoint, 1.8)) return false;
            boolean zLonger = Math.abs(hideArea.getA().getZ() - hideArea.getB().getZ()) > Math.abs(hideArea.getA().getX() - hideArea.getB().getX());
            Vector3d direction = zLonger ?
                    new Vector3d(0, 0, player.getLookAngle().z() > 0 ? 1 : -1) :
                    new Vector3d(player.getLookAngle().x() > 0 ? 1 : -1, 0, 0);
            BufferUtil.wrap(startInfo)
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
        return player.hurtTime <= 0 && (getDoingTick() < 6 || KeyBindings.getKeyBindHideInBlock().isDown());
    }

    @Override
    public void onStart(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
        hidingArea = new Tuple<>(BufferUtil.getBlockPos(startData), BufferUtil.getBlockPos(startData));
        hidingPoint = BufferUtil.getVector3d(startData);
        enterPoint = BufferUtil.getVector3d(startData);
        lookDirection = BufferUtil.getVector3d(startData);
        parkourability.getBehaviorEnforcer().setMarkerEnforcePosition(
                this::isDoing,
                () -> {
                    if (getDoingTick() == 0)
                        return hidingPoint.subtract(enterPoint).scale(0.75).add(enterPoint);
                    return hidingPoint;
                }
        );
        player.noPhysics = true;
        player.playSound(player.level.getBlockState(new BlockPos(hidingPoint.add(0, 0.2, 0))).getSoundType().getBreakSound(), 1, 1);
    }

    @Override
    public void onStartInLocalClient(PlayerEntity player, Parkourability parkourability, IStamina stamina, ByteBuffer startData) {
        RenderBehaviorEnforcer.serMarkerEnforceCameraType(this::isDoing, () -> PointOfView.THIRD_PERSON_BACK);
        parkourability.getBehaviorEnforcer().addMarkerCancellingShowName(ID_SHOW_NAME, this::isDoing);
        spawnOnHideParticles(player);
        Animation animation = Animation.get(player);
        if (animation == null) return;
        animation.setAnimator(new HideInBlockAnimator());
    }

    @Override
    public void onStartInOtherClient(PlayerEntity player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation = Animation.get(player);
        parkourability.getBehaviorEnforcer().addMarkerCancellingShowName(ID_SHOW_NAME, this::isDoing);
        spawnOnHideParticles(player);
        if (animation == null) return;
        animation.setAnimator(new HideInBlockAnimator());
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
        int minX = Math.min(hidingArea.getA().getX(), hidingArea.getB().getX());
        int maxX = Math.max(hidingArea.getA().getX(), hidingArea.getB().getX());
        int minY = Math.min(hidingArea.getA().getY(), hidingArea.getB().getY());
        int maxY = Math.max(hidingArea.getA().getY(), hidingArea.getB().getY());
        int minZ = Math.min(hidingArea.getA().getZ(), hidingArea.getB().getZ());
        int maxZ = Math.max(hidingArea.getA().getZ(), hidingArea.getB().getZ());
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
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.None;
    }
}
