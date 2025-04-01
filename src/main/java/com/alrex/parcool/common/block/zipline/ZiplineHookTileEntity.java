package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ZiplineHookTileEntity extends TileEntity implements ITickableTileEntity {
    public ZiplineHookTileEntity(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    private static class ConnectionInfo {
        public ConnectionInfo(int color) {
            this.color = color;
        }

        private final int color;

        public int getColor() {
            return color;
        }
    }

    private final TreeMap<BlockPos, ConnectionInfo> connections = new TreeMap<>();

    //OnlyIn Logical Server
    private final TreeMap<BlockPos, ZiplineRopeEntity> connectionEntities = new TreeMap<>();

    public Set<BlockPos> getConnectionPoints() {
        return connections.keySet();
    }

    private TreeMap<BlockPos, ConnectionInfo> getConnectionInfo() {
        return connections;
    }

    public List<ItemStack> removeAllConnection() {
        if (level == null) return Collections.EMPTY_LIST;
        getConnectionPoints().stream()
                .filter(level::isLoaded)
                .map(level::getBlockEntity)
                .map(it -> it instanceof ZiplineHookTileEntity ? (ZiplineHookTileEntity) it : null)
                .filter(Objects::nonNull)
                .forEach(it -> it.onPairHookRegistrationRemoved(this));
        List<ItemStack> itemStacks = Collections.EMPTY_LIST;
        if (!level.isClientSide()) {
            connectionEntities.values().forEach(ZiplineRopeEntity::remove);
            itemStacks = connections.values().stream().map(it -> {
                ItemStack stack = new ItemStack(Items.ZIPLINE_ROPE::get);
                ZiplineRopeItem.setColor(stack, it.getColor());
                return stack;
            }).collect(Collectors.toList());
        }
        connectionEntities.clear();
        return itemStacks;
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
            getConnectionPoints().stream()
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

    public void connectTo(ZiplineHookTileEntity target, int color) {
        if (this == target) return;

        if (level != null && !level.isClientSide()) {
            if (this.getConnectionPoints().stream().anyMatch(target.getBlockPos()::equals)) {
                return;
            }
            ConnectionInfo info = new ConnectionInfo(color);
            ZiplineRopeEntity ropeEntity = spawnRope(level, target, info);
            if (ropeEntity != null) {
                this.getConnectionInfo().put(target.getBlockPos(), info);
                target.getConnectionInfo().put(this.getBlockPos(), info);
            }
        }
    }

    @Nullable
    private ZiplineRopeEntity spawnRope(World level, ZiplineHookTileEntity target, ConnectionInfo info) {
        if (level.isClientSide()) return null;
        if (target.connectionEntities.containsKey(this.getBlockPos())) return null;

        ZiplineRopeEntity entity = new ZiplineRopeEntity(level, getBlockPos(), target.getBlockPos(), info.getColor());
        boolean result = level.addFreshEntity(entity);
        if (result) {
            this.connectionEntities.put(target.getBlockPos(), entity);
            target.connectionEntities.put(this.getBlockPos(), entity);
        }
        return result ? entity : null;
    }

    private void saveTo(CompoundNBT nbt) {
        int[] points = new int[getConnectionPoints().size() * 3];
        int[] colors = new int[getConnectionPoints().size()];
        int i = 0;
        int j = 0;
        for (BlockPos connectPoint : getConnectionPoints()) {
            points[i++] = connectPoint.getX();
            points[i++] = connectPoint.getY();
            points[i++] = connectPoint.getZ();

            colors[j++] = getConnectionInfo().getOrDefault(connectPoint, new ConnectionInfo(ZiplineRopeItem.DEFAULT_COLOR)).getColor();
        }
        nbt.putIntArray("Connect_Points", points);
        nbt.putIntArray("Colors", colors);
    }

    private void restoreFrom(CompoundNBT nbt) {
        int[] points = nbt.getIntArray("Connect_Points");
        int[] colors = nbt.getIntArray("Colors");
        getConnectionInfo().clear();
        int j = 0;
        for (int i = 0; i + 2 < points.length; i += 3) {
            BlockPos pos = new BlockPos(points[i], points[i + 1], points[i + 2]);
            getConnectionInfo().put(pos, new ConnectionInfo(j < colors.length ? colors[j++] : ZiplineRopeItem.DEFAULT_COLOR));
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

    @Nonnull
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

        if (level != null && !level.isClientSide() && connectionEntities.size() < getConnectionPoints().size()) {
            List<ZiplineHookTileEntity> tileEntities = getConnectionPoints()
                    .stream()
                    .filter(it -> !connectionEntities.containsKey(it))
                    .filter(level::isLoaded)
                    .map(level::getBlockEntity)
                    .map(it -> it instanceof ZiplineHookTileEntity ? (ZiplineHookTileEntity) it : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            tileEntities.forEach(it -> {
                if (it.getConnectionPoints().contains(this.getBlockPos())) {
                    this.spawnRope(level, it, getConnectionInfo().get(it.getBlockPos()));
                } else {
                    this.getConnectionPoints().remove(it.getBlockPos());
                }
            });
        }
    }
}
