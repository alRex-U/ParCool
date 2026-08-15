package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.item.misc.ZiplineRopeItem;
import com.alrex.parcool.common.zipline.ILoadedZiplineHolderProvider;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ZiplineHookTileEntity extends BlockEntity {
    @OnlyIn(Dist.CLIENT)
    private class RenderAbleZiplineIterator implements Iterator<Zipline> {
        private RenderAbleZiplineIterator() {
            this.iterator = connections.entrySet().iterator();
            this.nextItem = findNextItem();
        }

        private final Iterator<Map.Entry<BlockPos, ZiplineInfo>> iterator;
        @Nullable
        private Zipline nextItem;

        @Override
        public boolean hasNext() {
            return nextItem != null;
        }

        @Override
        public Zipline next() {
            var tmp = nextItem;
            nextItem = findNextItem();
            return tmp;
        }

        @Nullable
        private Zipline findNextItem() {
            var level = getLevel();
            if (level == null) return null;
            var thisIsPowered = getBlockState().getValue(ZiplineHookBlock.POWERED);
            while (iterator.hasNext()) {
                var item = iterator.next();
                var endPos = item.getKey();
                if (getBlockPos().compareTo(endPos) < 0) {
                    if (!level.isLoaded(endPos)) continue;
                    if (!(level.getBlockEntity(endPos) instanceof ZiplineHookTileEntity endHook)) continue;
                    var shape = item.getValue().type().getZipline(getHookPoint(), endHook.getHookPoint());
                    var ziplinePowered = thisIsPowered || level.getBlockState(endPos).getValue(ZiplineHookBlock.POWERED);
                    return new Zipline(shape, item.getValue(), getBlockPos(), endPos, ziplinePowered);
                }
            }
            return null;
        }
    }

    private final TreeMap<BlockPos, ZiplineInfo> connections = new TreeMap<>();

    public ZiplineHookTileEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @OnlyIn(Dist.CLIENT)
    public Iterator<Zipline> getRenderAbleZiplines() {
        return new RenderAbleZiplineIterator();
    }

    public Set<BlockPos> getConnectionPoints() {
        return connections.keySet();
    }

    public Map<BlockPos, ZiplineInfo> getConnections() {
        return connections;
    }

    public Vec3 getHookPoint() {
        if (level == null)
            return new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
        var state = level.getBlockState(this.getBlockPos());
        var block = state.getBlock();
        if (block instanceof ZiplineHookBlock ziplineHookBlock) {
            return ziplineHookBlock.getActualZiplinePoint(this.getBlockPos(), state);
        }
        return new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
    }

    protected void addConnection(BlockPos target, ZiplineInfo info) {
        if (level == null) return;
        var blockEntity = level.getBlockEntity(target);
        if (blockEntity instanceof ZiplineHookTileEntity) {
            this.connections.put(target, info);
            notifyBlockUpdated();
        }
    }

    public boolean connectTo(ZiplineHookTileEntity target, ZiplineInfo info) {
        if (connections.containsKey(target.getBlockPos())) return false;
        this.addConnection(target.getBlockPos(), info);
        target.addConnection(this.getBlockPos(), info);
        return true;
    }

    protected Optional<ZiplineInfo> removeConnection(BlockPos target) {
        var info = connections.remove(target);
        notifyBlockUpdated();
        return info != null ? Optional.of(info) : Optional.empty();
    }

    public Optional<ZiplineInfo> removeConnectionFrom(ZiplineHookTileEntity hookTileEntity) {
        if (this == hookTileEntity) return Optional.empty();
        var info = this.removeConnection(hookTileEntity.getBlockPos());
        hookTileEntity.removeConnection(this.getBlockPos());
        return info;
    }

    public List<ItemStack> removeAllConnections() {
        if (level == null) return Collections.emptyList();
        var itemStacks = new ArrayList<ItemStack>(connections.size());
        for (var entry : connections.entrySet()) {
            var entity = level.getBlockEntity(entry.getKey());
            if (entity instanceof ZiplineHookTileEntity hookTileEntity) {
                hookTileEntity.removeConnection(this.getBlockPos())
                        .map(ZiplineRopeItem::from)
                        .ifPresent(itemStacks::add);
            }
        }
        connections.clear();
        return itemStacks;
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag nbt, @Nonnull HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);

        var connections = new ListTag();
        for (Map.Entry<BlockPos, ZiplineInfo> infoEntry : getConnections().entrySet()) {
            var entryTag = new CompoundTag();
            var pos = getBlockPos();
            entryTag.putInt("rX", infoEntry.getKey().getX() - pos.getX());
            entryTag.putInt("rY", infoEntry.getKey().getY() - pos.getY());
            entryTag.putInt("rZ", infoEntry.getKey().getZ() - pos.getZ());
            entryTag.put("info", infoEntry.getValue().save());
            connections.add(entryTag);
        }
        nbt.put("connection", connections);
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag nbt, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        Tag connectionsTag = nbt.get("connection");
        if (!(connectionsTag instanceof ListTag listConnections)) return;

        connections.clear();

        for (Tag entry : listConnections) {
            if (!(entry instanceof CompoundTag cTag))
                continue;

            BlockPos pos;
            if (cTag.contains("rX") && cTag.contains("rY") && cTag.contains("rZ")) {
                pos = getBlockPos().offset(
                        cTag.getInt("rX"),
                        cTag.getInt("rY"),
                        cTag.getInt("rZ")
                );
            } else continue;
            var info = ZiplineInfo.load(cTag.get("info"));
            connections.put(pos, info);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        var nbt = super.getUpdateTag(registries);
        saveAdditional(nbt, registries);
        return nbt;
    }

    private void notifyBlockUpdated() {
        setChanged();
        if (level != null) level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 8);
    }


    @OnlyIn(Dist.CLIENT)
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        if (level instanceof ILoadedZiplineHolderProvider provider && blockEntity instanceof ZiplineHookTileEntity hookTileEntity) {
            var ziplineHolder = provider.getZiplineHolder();
            var removedItems = new LinkedList<BlockPos>();
            var thisIsPowered = blockState.getValue(ZiplineHookBlock.POWERED);
            for (var connection : hookTileEntity.connections.entrySet()) {
                var endPos = connection.getKey();
                if (!level.isLoaded(endPos)) continue;
                if (!(level.getBlockEntity(endPos) instanceof ZiplineHookTileEntity endHook)) {
                    removedItems.add(endPos);
                    continue;
                }
                if (hookTileEntity.getBlockPos().compareTo(endPos) < 0) {
                    var ziplinePowered = thisIsPowered || level.getBlockState(endPos).getValue(ZiplineHookBlock.POWERED);
                    var shape = connection.getValue().type().getZipline(hookTileEntity.getHookPoint(), endHook.getHookPoint());
                    ziplineHolder.notifyZiplineAlive(new Zipline(shape, connection.getValue(), hookTileEntity.getBlockPos(), endPos, ziplinePowered));
                }
            }
            for (var removedItem : removedItems) {
                hookTileEntity.connections.remove(removedItem);
            }
        }
    }
}
