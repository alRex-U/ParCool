package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import com.alrex.parcool.compatibility.BlockStateWrapper;
import com.alrex.parcool.compatibility.LevelWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
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
    private LevelWrapper levelWrapper;

    public ZiplineHookTileEntity(TileEntityType<?> entityType) {
        super(entityType);
        setLevelWrapper();
    }

    private void setLevelWrapper() {
        levelWrapper = level == null ? null : LevelWrapper.get(level);
    }

    @Override
    public void setLevelAndPosition(World level, BlockPos position) {
        super.setLevelAndPosition(level, position);
        setLevelWrapper();
    }

    private final TreeMap<BlockPos, ZiplineInfo> connections = new TreeMap<>();

    //OnlyIn Logical Server
    private final TreeMap<BlockPos, ZiplineRopeEntity> connectionEntities = new TreeMap<>();

    public Set<BlockPos> getConnectionPoints() {
        return connections.keySet();
    }

    private TreeMap<BlockPos, ZiplineInfo> getConnectionInfo() {
        return connections;
    }

    public List<ItemStack> removeAllConnection() {
        if (levelWrapper == null) return Collections.EMPTY_LIST;
        getConnectionPoints().stream()
                .filter(levelWrapper::isLoaded)
                .map(levelWrapper::getBlockEntity)
                .map(it -> it.is(ZiplineHookTileEntity.class) ? (ZiplineHookTileEntity) it.getInstance() : null)
                .filter(Objects::nonNull)
                .forEach(it -> it.onPairHookRegistrationRemoved(this));
        List<ItemStack> itemStacks = Collections.EMPTY_LIST;
        if (!levelWrapper.isClientSide()) {
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
        if (levelWrapper != null) {
            getConnectionPoints().stream()
                    .filter(levelWrapper::isLoaded)
                    .map(levelWrapper::getBlockEntity)
                    .map(it -> it.is(ZiplineHookTileEntity.class) ? (ZiplineHookTileEntity) it.getInstance() : null)
                    .filter(Objects::nonNull)
                    .forEach(it -> it.onPairHookUnloaded(this));
            if (!levelWrapper.isClientSide()) {
                connectionEntities.values().forEach(ZiplineRopeEntity::remove);
            }
            connectionEntities.clear();
        }
    }

    public Vec3Wrapper getActualZiplinePoint(@Nullable BlockPos connected) {
        if (levelWrapper == null)
            new Vec3Wrapper(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
        BlockStateWrapper state = levelWrapper.getBlockState(this.getBlockPos());
        Block block = state.getBlock();
        if (block instanceof ZiplineHookBlock) {
            return ((ZiplineHookBlock) block).getActualZiplinePoint(this.getBlockPos(), state);
        }
        return new Vec3Wrapper(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
    }

    public boolean connectTo(ZiplineHookTileEntity target, ZiplineInfo info) {
        if (this == target) return false;

        if (levelWrapper != null && !levelWrapper.isClientSide()) {
            if (this.getConnectionPoints().stream().anyMatch(target.getBlockPos()::equals)) {
                return false;
            }
            ZiplineRopeEntity ropeEntity = spawnRope(levelWrapper, target, info);
            if (ropeEntity != null) {
                this.getConnectionInfo().put(target.getBlockPos(), info);
                target.getConnectionInfo().put(this.getBlockPos(), info);
                return true;
            }
        }
        return false;
    }

    @Nullable
    private ZiplineRopeEntity spawnRope(LevelWrapper levelWrapper, ZiplineHookTileEntity target, ZiplineInfo info) {
        if (levelWrapper.isClientSide()) return null;
        if (target.connectionEntities.containsKey(this.getBlockPos())) return null;

        ZiplineRopeEntity entity = new ZiplineRopeEntity(levelWrapper, getBlockPos(), target.getBlockPos(), info);
        boolean result = levelWrapper.addFreshEntity(entity);
        if (result) {
            this.connectionEntities.put(target.getBlockPos(), entity);
            target.connectionEntities.put(this.getBlockPos(), entity);
        }
        return result ? entity : null;
    }

    private void saveTo(CompoundNBT nbt) {
        ListNBT connections = new ListNBT();
        for (Map.Entry<BlockPos, ZiplineInfo> infoEntry : getConnectionInfo().entrySet()) {
            CompoundNBT entryTag = new CompoundNBT();
            entryTag.putInt("X", infoEntry.getKey().getX());
            entryTag.putInt("Y", infoEntry.getKey().getY());
            entryTag.putInt("Z", infoEntry.getKey().getZ());
            entryTag.put("Info", infoEntry.getValue().save());
            connections.add(entryTag);
        }
        nbt.put("Connection", connections);
    }

    private void restoreFrom(CompoundNBT nbt) {
        INBT connections = nbt.get("Connection");
        if (!(connections instanceof ListNBT)) {
            return;
        }
        ListNBT listConnections = (ListNBT) connections;
        getConnectionInfo().clear();

        for (INBT entry : listConnections) {
            if (!(entry instanceof CompoundNBT))
                continue;

            CompoundNBT cTag = (CompoundNBT) entry;
            if (!(cTag.contains("X") && cTag.contains("Y") && cTag.contains("Z")))
                continue;
            BlockPos pos = new BlockPos(cTag.getInt("X"), cTag.getInt("Y"), cTag.getInt("Z"));
            ZiplineInfo info = ZiplineInfo.load(cTag.get("Info"));
            getConnectionInfo().put(pos, info);
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

        if (levelWrapper != null && !levelWrapper.isClientSide() && connectionEntities.size() < getConnectionPoints().size()) {
            List<ZiplineHookTileEntity> tileEntities = getConnectionPoints()
                    .stream()
                    .filter(it -> !connectionEntities.containsKey(it))
                    .filter(levelWrapper::isLoaded)
                    .map(levelWrapper::getBlockEntity)
                    .map(it -> it.is(ZiplineHookTileEntity.class) ? (ZiplineHookTileEntity) it.getInstance() : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            tileEntities.forEach(it -> {
                if (it.getConnectionPoints().contains(this.getBlockPos())) {
                    this.spawnRope(levelWrapper, it, getConnectionInfo().get(it.getBlockPos()));
                } else {
                    this.getConnectionPoints().remove(it.getBlockPos());
                }
            });
        }
    }
}
