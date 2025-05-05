package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ZiplineHookTileEntity extends BlockEntity {

    private final TreeMap<BlockPos, ZiplineInfo> connections = new TreeMap<>();

    //OnlyIn Logical Server
    private final TreeMap<BlockPos, ZiplineRopeEntity> connectionEntities = new TreeMap<>();

    public ZiplineHookTileEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, net.minecraft.world.level.block.state.BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public Set<BlockPos> getConnectionPoints() {
        return connections.keySet();
    }

    private TreeMap<BlockPos, ZiplineInfo> getConnectionInfo() {
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
            connectionEntities.values().forEach((it) -> it.remove(Entity.RemovalReason.DISCARDED));
            itemStacks = getConnectionInfo().values().stream().map(it -> {
                ItemStack stack = new ItemStack(Items.ZIPLINE_ROPE::get);
                ZiplineRopeItem.setColor(stack, ARGB.color(0xFF, it.getColor()));
                return stack;
            }).collect(Collectors.toList());
        }
        connectionEntities.clear();
        getConnectionInfo().clear();
        setChanged();
        return itemStacks;
    }

    private void onPairHookRegistrationRemoved(ZiplineHookTileEntity removedPair) {
        getConnectionPoints().remove(removedPair.getBlockPos());
        connectionEntities.remove(removedPair.getBlockPos());
        setChanged();
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
                connectionEntities.values().forEach((it) -> it.remove(Entity.RemovalReason.DISCARDED));
            }
            connectionEntities.clear();
        }
    }

    public Vec3 getActualZiplinePoint(@Nullable BlockPos connected) {
        if (level == null)
            new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
        BlockState state = level.getBlockState(this.getBlockPos());
        Block block = state.getBlock();
        if (block instanceof ZiplineHookBlock) {
            return ((ZiplineHookBlock) block).getActualZiplinePoint(this.getBlockPos(), state);
        }
        return new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
    }

    public boolean connectTo(ZiplineHookTileEntity target, ZiplineInfo info) {
        if (this == target) return false;

        if (level != null && !level.isClientSide()) {
            if (this.getConnectionPoints().stream().anyMatch(target.getBlockPos()::equals)) {
                return false;
            }
            ZiplineRopeEntity ropeEntity = spawnRope(level, target, info);
            if (ropeEntity != null) {
                this.getConnectionInfo().put(target.getBlockPos(), info);
                this.setChanged();
                target.getConnectionInfo().put(this.getBlockPos(), info);
                target.setChanged();

                return true;
            }
        }
        return false;
    }

    @Nullable
    private ZiplineRopeEntity spawnRope(Level level, ZiplineHookTileEntity target, ZiplineInfo info) {
        if (level.isClientSide()) return null;
        if (target.connectionEntities.containsKey(this.getBlockPos())) return null;

        ZiplineRopeEntity entity = new ZiplineRopeEntity(level, getBlockPos(), target.getBlockPos(), info);
        boolean result = level.addFreshEntity(entity);
        if (result) {
            this.connectionEntities.put(target.getBlockPos(), entity);
            target.connectionEntities.put(this.getBlockPos(), entity);
        }
        return result ? entity : null;
    }

    private void saveTo(CompoundTag nbt) {
        var connections = new ListTag();
        for (Map.Entry<BlockPos, ZiplineInfo> infoEntry : getConnectionInfo().entrySet()) {
            var entryTag = new CompoundTag();
            entryTag.putInt("X", infoEntry.getKey().getX());
            entryTag.putInt("Y", infoEntry.getKey().getY());
            entryTag.putInt("Z", infoEntry.getKey().getZ());
            entryTag.put("Info", infoEntry.getValue().save());
            connections.add(entryTag);
        }
        nbt.put("Connection", connections);
    }

    private void restoreFrom(CompoundTag nbt) {
        Tag connections = nbt.get("Connection");
        if (!(connections instanceof ListTag)) {
            return;
        }
        var listConnections = (ListTag) connections;
        getConnectionInfo().clear();

        for (Tag entry : listConnections) {
            if (!(entry instanceof CompoundTag cTag))
                continue;

            var x = cTag.getInt("X");
            var y = cTag.getInt("Y");
            var z = cTag.getInt("Z");

            x.ifPresent(x_ -> y.ifPresent(y_ -> z.ifPresent(z_ -> {
                BlockPos pos = new BlockPos(x_, y_, z_);
                ZiplineInfo info = ZiplineInfo.load(cTag.get("Info"));
                getConnectionInfo().put(pos, info);
            })));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveTo(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        restoreFrom(tag);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        var nbt = super.getUpdateTag(registries);
        saveTo(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        restoreFrom(tag);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity entity) {
        if (!(entity instanceof ZiplineHookTileEntity self)) return;

        if (level != null && !level.isClientSide() && self.connectionEntities.size() < self.getConnectionPoints().size()) {
            List<ZiplineHookTileEntity> tileEntities = self.getConnectionPoints()
                    .stream()
                    .filter(it -> !self.connectionEntities.containsKey(it))
                    .filter(level::isLoaded)
                    .map(level::getBlockEntity)
                    .map(it -> it instanceof ZiplineHookTileEntity ? (ZiplineHookTileEntity) it : null)
                    .filter(Objects::nonNull)
                    .toList();
            tileEntities.forEach(it -> {
                if (it.getConnectionPoints().contains(self.getBlockPos())) {
                    self.spawnRope(level, it, self.getConnectionInfo().get(it.getBlockPos()));
                } else {
                    self.getConnectionPoints().remove(it.getBlockPos());
                    self.setChanged();
                }
            });
        }
    }
}
