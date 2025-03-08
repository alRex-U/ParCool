package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ZiplineHookTileEntity extends TileEntity implements ITickableTileEntity {
    public ZiplineHookTileEntity(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    private final TreeSet<BlockPos> connectionPoints = new TreeSet<>();

    //OnlyIn Logical Server
    private final TreeMap<BlockPos, ZiplineRopeEntity> connectionEntities = new TreeMap<>();

    public TreeSet<BlockPos> getConnectionPoints() {
        return connectionPoints;
    }

    public void removeAllConnection() {
        if (level == null) return;
        connectionPoints.stream()
                .filter(level::isLoaded)
                .map(level::getBlockEntity)
                .map(it -> it instanceof ZiplineHookTileEntity ? (ZiplineHookTileEntity) it : null)
                .filter(Objects::nonNull)
                .forEach(it -> it.onPairHookRegistrationRemoved(this));
        if (!level.isClientSide()) {
            connectionEntities.values().forEach(ZiplineRopeEntity::remove);
        }
        connectionEntities.clear();
    }

    private void onPairHookRegistrationRemoved(ZiplineHookTileEntity removedPair) {
        getConnectionPoints().remove(removedPair.getBlockPos());
        connectionEntities.remove(removedPair.getBlockPos());
    }

    private void onPairHookUnloaded(ZiplineHookTileEntity removedPair) {
        connectionEntities.remove(removedPair.getBlockPos());
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (level != null) {
            connectionPoints.stream()
                    .filter(level::isLoaded)
                    .map(level::getBlockEntity)
                    .map(it -> it instanceof ZiplineHookTileEntity ? (ZiplineHookTileEntity) it : null)
                    .filter(Objects::nonNull)
                    .forEach(it -> it.onPairHookUnloaded(this));
            if (!level.isClientSide()) {
                connectionEntities.values().forEach(ZiplineRopeEntity::remove);
            }
            connectionEntities.clear();
        }
    }

    public void connectTo(ZiplineHookTileEntity target) {
        if (this == target) return;

        if (level != null && !level.isClientSide()) {
            if (this.getConnectionPoints().stream().anyMatch(target.getBlockPos()::equals)) {
                return;
            }
            ZiplineRopeEntity ropeEntity = spawnRope(level, target);
            if (ropeEntity != null) {
                this.getConnectionPoints().add(target.getBlockPos());
                target.getConnectionPoints().add(this.getBlockPos());
            }
        }
    }

    @Nullable
    private ZiplineRopeEntity spawnRope(World level, ZiplineHookTileEntity target) {
        if (level.isClientSide()) return null;
        if (target.connectionEntities.containsKey(this.getBlockPos())) return null;

        ZiplineRopeEntity entity = new ZiplineRopeEntity(level, getBlockPos(), target.getBlockPos());
        boolean result = level.addFreshEntity(entity);
        if (result) {
            this.connectionEntities.put(target.getBlockPos(), entity);
            target.connectionEntities.put(this.getBlockPos(), entity);
        }
        return result ? entity : null;
    }

    private void saveTo(CompoundNBT nbt) {
        int[] points = new int[connectionPoints.size() * 3];
        int i = 0;
        for (BlockPos connectPoint : connectionPoints) {
            points[i++] = connectPoint.getX();
            points[i++] = connectPoint.getY();
            points[i++] = connectPoint.getZ();
        }
        nbt.putIntArray("Connect_Points", points);
    }

    private void restoreFrom(CompoundNBT nbt) {
        int[] points = nbt.getIntArray("Connect_Points");
        connectionPoints.clear();
        for (int i = 0; i + 2 < points.length; i += 3) {
            connectionPoints.add(new BlockPos(points[i], points[i + 1], points[i + 2]));
        }
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT p_189515_1_) {
        CompoundNBT nbt = super.save(p_189515_1_);
        saveTo(nbt);
        return nbt;
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.load(state, nbt);
        restoreFrom(nbt);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        saveTo(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        restoreFrom(tag);
    }

    @Override
    public void tick() {

        if (level != null && !level.isClientSide() && connectionEntities.size() < connectionPoints.size()) {
            List<ZiplineHookTileEntity> tileEntities = connectionPoints.stream()
                    .filter(it -> !connectionEntities.containsKey(it))
                    .filter(level::isLoaded)
                    .map(level::getBlockEntity)
                    .map(it -> it instanceof ZiplineHookTileEntity ? (ZiplineHookTileEntity) it : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            tileEntities.forEach(it -> {
                if (it.getConnectionPoints().contains(this.getBlockPos())) {
                    this.spawnRope(level, it);
                } else {
                    this.getConnectionPoints().remove(it.getBlockPos());
                }
            });
        }
    }
}
