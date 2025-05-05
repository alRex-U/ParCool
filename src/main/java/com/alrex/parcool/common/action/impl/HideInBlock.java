package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.RenderBehaviorEnforcer;
import com.alrex.parcool.client.animation.impl.HideInBlockAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

public class HideInBlock extends Action {
    private static final BehaviorEnforcer.ID ID_SHOW_NAME = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_SNEAK = BehaviorEnforcer.newID();
    @Nullable
    Vec3 hidingPoint = null;
    @Nullable
    Tuple<BlockPos, BlockPos> hidingArea = null;
    @Nullable
    Vec3 enterPoint = null;
    @Nullable
    Vec3 lookDirection = null;
    boolean hidingBlockChanged = false;
    boolean keyPressed;
    boolean startedFromDiving;

    @Nullable
    public Vec3 getLookDirection() {
        return lookDirection;
    }

    public boolean isStandbyInAir(Parkourability parkourability) {
        if (!keyPressed) return false;
        Dive dive = parkourability.get(Dive.class);
        return (dive.isDoing() || dive.getNotDoingTick() < 2)
                && (parkourability.getAdditionalProperties().getLandingTick() <= 1);
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        if (player.isSprinting()
                || player.noPhysics
                || !player.onGround()
                || player.isInWater()
                || player.isPassenger()
                || player.isVisuallySwimming()
                || getNotDoingTick() < 6
                || parkourability.get(Crawl.class).isDoing()
        ) {
            return false;
        }

        BlockPos hideBaseBlockPos = null;
        boolean startFromDiving = false;
        if (isStandbyInAir(parkourability)) {
            hideBaseBlockPos = player.blockPosition().below();
            startFromDiving = true;
        } else if (KeyBindings.getKeyHideInBlock().isDown() && (!ParCoolConfig.Client.Booleans.HideInBlockSneakNeeded.get() || player.getPose() == Pose.CROUCHING)) {
            HitResult result = Minecraft.getInstance().hitResult;
            if (result instanceof BlockHitResult && parkourability.isDoingNothing()) {
                hideBaseBlockPos = ((BlockHitResult) result).getBlockPos();
            }
        }
        if (!startFromDiving && player.hurtTime > 0) {
            return false;
        }

        if (hideBaseBlockPos != null) {
            Tuple<BlockPos, BlockPos> hideArea = WorldUtil.getHideAbleSpace(player, hideBaseBlockPos);
            if (hideArea == null) return false;
            Vec3 hidePoint = new Vec3(
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
            Vec3 direction;
            boolean stand = player.getBbHeight() < (hideArea.getB().getY() - hideArea.getA().getY() + 1);
            if (stand && startFromDiving) return false;
            if (stand) {
                Vec3 lookAngle = player.getLookAngle();
                direction = Math.abs(lookAngle.x()) > Math.abs(lookAngle.z()) ?
                        new Vec3(lookAngle.x() > 0 ? 1 : -1, 0, 0) :
                        new Vec3(0, 0, lookAngle.z() > 0 ? 1 : -1);
            } else {
                boolean zLonger = Math.abs(hideArea.getA().getZ() - hideArea.getB().getZ()) > Math.abs(hideArea.getA().getX() - hideArea.getB().getX());
                direction = zLonger ?
                        new Vec3(0, 0, player.getLookAngle().z() > 0 ? 1 : -1) :
                        new Vec3(player.getLookAngle().x() > 0 ? 1 : -1, 0, 0);
            }
            BufferUtil.wrap(startInfo)
                    .putBoolean(stand)
                    .putBoolean(startFromDiving)
                    .putBlockPos(hideArea.getA())
                    .putBlockPos(hideArea.getB())
                    .putVec3(hidePoint)
                    .putVec3(player.position())
                    .putVec3(direction);
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        if (hidingBlockChanged) {
            return hidingBlockChanged = false;
        }
        return (player.hurtTime <= 0 || (startedFromDiving && getDoingTick() < 10))
                && player.getPose() == Pose.STANDING
                && (getDoingTick() < 6 || KeyBindings.getKeyHideInBlock().isDown() || KeyBindings.getKeySneak().isDown());
    }

    @Override
    public void onStart(Player player, Parkourability parkourability, ByteBuffer startData) {
        boolean _stand = BufferUtil.getBoolean(startData);
        startedFromDiving = BufferUtil.getBoolean(startData);
        hidingArea = new Tuple<>(BufferUtil.getBlockPos(startData), BufferUtil.getBlockPos(startData));
        hidingPoint = BufferUtil.getVec3(startData);
        enterPoint = BufferUtil.getVec3(startData);
        lookDirection = BufferUtil.getVec3(startData);
        if (startedFromDiving) {
            parkourability.getBehaviorEnforcer().setMarkerEnforcePosition(
                    this::isDoing,
                    () -> hidingPoint
            );
        } else {
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
        player.playSound(player.level()
                        .getBlockState(
                                new BlockPos(
                                        (int) Math.floor(hidingPoint.x()),
                                        (int) Math.floor(hidingPoint.y() + 0.2),
                                        (int) Math.floor(hidingPoint.z())
                                )
                        )
                        .getSoundType().getBreakSound(),
                1, 1
        );
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        boolean stand = BufferUtil.getBoolean(startData);
        RenderBehaviorEnforcer.serMarkerEnforceCameraType(this::isDoing, () -> CameraType.THIRD_PERSON_BACK);
        parkourability.getBehaviorEnforcer().addMarkerCancellingShowName(ID_SHOW_NAME, this::isDoing);
        spawnOnHideParticles(player);
        Animation animation = Animation.get(player);
        animation.setAnimator(new HideInBlockAnimator(stand, startedFromDiving));
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        boolean stand = BufferUtil.getBoolean(startData);
        parkourability.getBehaviorEnforcer().addMarkerCancellingShowName(ID_SHOW_NAME, this::isDoing);
        spawnOnHideParticles(player);
        Animation animation = Animation.get(player);
        animation.setAnimator(new HideInBlockAnimator(stand, startedFromDiving));
    }


    @Override
    public void onWorkingTickInServer(Player player, Parkourability parkourability) {
        if (hidingPoint == null) return;
        player.setPos(hidingPoint.x(), hidingPoint.y(), hidingPoint.z());
    }

    @Override
    public void onWorkingTick(Player player, Parkourability parkourability) {
        player.setDeltaMovement(Vec3.ZERO);
        player.noPhysics = true;
        player.setSprinting(false);
        player.setPose(Pose.STANDING);
    }

    @Override
    public void onStopInLocalClient(Player player) {
        final Vec3 hidePos = hidingPoint;
        final Vec3 entPos = enterPoint;
        Parkourability parkourability = Parkourability.get(player);
        parkourability.getBehaviorEnforcer().setMarkerEnforcePosition(
                () -> this.getNotDoingTick() <= 1,
                () -> {
                    if (getNotDoingTick() == 0)
                        return entPos.subtract(hidePos).scale(0.65).add(hidePos);
                    return entPos;
                }
        );
        spawnOnHideParticles(player);
        player.playSound(player.level()
                        .getBlockState(
                                new BlockPos(
                                        (int) Math.floor(hidingPoint.x()),
                                        (int) Math.floor(hidingPoint.y() + 0.2),
                                        (int) Math.floor(hidingPoint.z())
                                )
                        )
                        .getSoundType().getBreakSound(),
                1, 1
        );
    }

    @Override
    public void onTick(Player player, Parkourability parkourability) {
        if (!isDoing() && getNotDoingTick() <= 1) {
            player.noPhysics = true;
        }
    }

    @Override
    public void onStopInOtherClient(Player player) {
        spawnOnHideParticles(player);
        player.playSound(player.level()
                        .getBlockState(
                                new BlockPos(
                                        (int) Math.floor(hidingPoint.x()),
                                        (int) Math.floor(hidingPoint.y() + 0.2),
                                        (int) Math.floor(hidingPoint.z())
                                )
                        )
                        .getSoundType().getBreakSound(),
                1, 1
        );
    }

    @Override
    public void onStop(Player player) {
        hidingPoint = null;
        enterPoint = null;
        hidingArea = null;
        lookDirection = null;
        player.noPhysics = false;
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnOnHideParticles(Player player) {
        if (hidingArea == null) return;
        Level world = player.level();
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
    public void onClientTick(Player player, Parkourability parkourability) {
        keyPressed = KeyBindings.getKeyHideInBlock().isDown();
    }

    @Override
    public void saveSynchronizedState(ByteBuffer buffer) {
        BufferUtil.wrap(buffer).putBoolean(keyPressed);
    }

    @Override
    public void restoreSynchronizedState(ByteBuffer buffer) {
        keyPressed = BufferUtil.getBoolean(buffer);
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
