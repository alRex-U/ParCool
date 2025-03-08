package com.alrex.parcool.common.entity.zipline;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class ZiplineRopeEntity extends Entity {
    private static final DataParameter<BlockPos> DATA_START_POS;
    private static final DataParameter<BlockPos> DATA_END_POS;

    static {
        DATA_START_POS = EntityDataManager.defineId(ZiplineRopeEntity.class, DataSerializers.BLOCK_POS);
        DATA_END_POS = EntityDataManager.defineId(ZiplineRopeEntity.class, DataSerializers.BLOCK_POS);
    }

    private EntitySize size;

    public ZiplineRopeEntity(EntityType<?> p_i48580_1_, World p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    public ZiplineRopeEntity(World world, BlockPos start, BlockPos end) {
        super(com.alrex.parcool.common.entity.EntityType.ZIPLINE_ROPE.get(), world);
        setStartPos(start);
        setEndPos(end);
        setPos((end.getX() + start.getX()) / 2.0 + 0.5, Math.min(end.getY(), start.getY()), (end.getZ() + start.getZ()) / 2.0 + 0.5);
        noPhysics = true;
        forcedLoading = true;
        size = EntitySize.fixed(Math.max(Math.abs(end.getX() - start.getX()), Math.abs(end.getZ() - start.getZ())) + 0.3f, Math.abs(end.getY() - start.getY()) + 0.3f);
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        BlockPos start = getStartPos();
        BlockPos end = getEndPos();
        if (start == BlockPos.ZERO && end == BlockPos.ZERO) return false;
        // calculating the distance from rope straight line to the position
        double distanceSqr;

        double xOffset = end.getX() - start.getX();
        double yOffset = end.getY() - start.getY();
        double zOffset = end.getZ() - start.getZ();
        double baseXOffset = start.getX() - x;
        double baseYOffset = start.getY() - y;
        double baseZOffset = start.getZ() - z;
        double t = -(xOffset * baseXOffset + yOffset * baseYOffset + zOffset * baseZOffset) / (xOffset * xOffset + yOffset * yOffset + zOffset * zOffset);
        Vector3d mostNearPoint = new Vector3d(xOffset * t + start.getX(), yOffset * t + start.getY(), zOffset * t + start.getZ());
        distanceSqr = mostNearPoint.distanceToSqr(x, y, z);
        return distanceSqr < 900;
    }

    @Nonnull
    @Override
    public EntitySize getDimensions(@Nonnull Pose p_213305_1_) {
        if (size == null) {
            return EntitySize.fixed(0.1f, 0.1f);
        }

        return size;
    }

    @Override
    public void refreshDimensions() {
        if (size != null) {
            BlockPos end = getEndPos();
            BlockPos start = getStartPos();
            if (end != BlockPos.ZERO || start != BlockPos.ZERO) {
                size = EntitySize.fixed(Math.max(Math.abs(end.getX() - start.getX()), Math.abs(end.getZ() - start.getZ())) + 0.3f, Math.abs(end.getY() - start.getY()) + 0.3f);
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(@Nonnull DataParameter<?> param) {
        if (param.equals(DATA_END_POS)) {
            refreshDimensions();
        }
    }

    public BlockPos getStartPos() {
        return getEntityData().get(DATA_START_POS);
    }

    public BlockPos getEndPos() {
        return getEntityData().get(DATA_END_POS);
    }

    public void setStartPos(BlockPos start) {
        getEntityData().set(DATA_START_POS, start);
    }

    public void setEndPos(BlockPos end) {
        getEntityData().set(DATA_END_POS, end);
    }

    @Nonnull
    @Override
    public ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
        return ActionResultType.sidedSuccess(p_184230_1_.level.isClientSide());
    }

    @Override
    protected void defineSynchedData() {
        getEntityData().define(DATA_START_POS, BlockPos.ZERO);
        getEntityData().define(DATA_END_POS, BlockPos.ZERO);
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT compoundNBT) {
        setStartPos(new BlockPos(compoundNBT.getInt("Tile1_X"), compoundNBT.getInt("Tile1_Y"), compoundNBT.getInt("Tile1_Z")));
        setEndPos(new BlockPos(compoundNBT.getInt("Tile2_X"), compoundNBT.getInt("Tile2_Y"), compoundNBT.getInt("Tile2_Z")));
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundNBT compoundNBT) {
        BlockPos startPos = getStartPos();
        BlockPos endPos = getEndPos();
        compoundNBT.putInt("Tile1_X", startPos.getX());
        compoundNBT.putInt("Tile1_X", startPos.getY());
        compoundNBT.putInt("Tile1_X", startPos.getZ());
        compoundNBT.putInt("Tile2_X", endPos.getX());
        compoundNBT.putInt("Tile2_X", endPos.getY());
        compoundNBT.putInt("Tile2_X", endPos.getZ());
    }

    @Nonnull
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
