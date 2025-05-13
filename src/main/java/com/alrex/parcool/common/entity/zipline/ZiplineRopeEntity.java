package com.alrex.parcool.common.entity.zipline;

import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.block.zipline.ZiplineInfo;
import com.alrex.parcool.common.entity.EntityTypes;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class ZiplineRopeEntity extends net.minecraft.world.entity.Entity {
    private static final EntityDataAccessor<BlockPos> DATA_START_POS;
    private static final EntityDataAccessor<BlockPos> DATA_END_POS;
    private static final EntityDataAccessor<Integer> DATA_COLOR;
    private static final EntityDataAccessor<Integer> DATA_ZIP_TYPE;

    static {
        DATA_START_POS = SynchedEntityData.defineId(ZiplineRopeEntity.class, EntityDataSerializers.BLOCK_POS);
        DATA_END_POS = SynchedEntityData.defineId(ZiplineRopeEntity.class, EntityDataSerializers.BLOCK_POS);
        DATA_COLOR = SynchedEntityData.defineId(ZiplineRopeEntity.class, EntityDataSerializers.INT);
        DATA_ZIP_TYPE = SynchedEntityData.defineId(ZiplineRopeEntity.class, EntityDataSerializers.INT);
    }

    private EntityDimensions size;

    public ZiplineRopeEntity(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    public ZiplineRopeEntity(Level world, BlockPos start, BlockPos end, ZiplineInfo info) {
        super(EntityTypes.ZIPLINE_ROPE.get(), world);
        setStartPos(start);
        setEndPos(end);
        setColor(info.getColor());
        setZiplineType(info.getType());
        setPos((end.getX() + start.getX()) / 2.0 + 0.5, Math.min(end.getY(), start.getY()), (end.getZ() + start.getZ()) / 2.0 + 0.5);
        noPhysics = true;
    }

    private BlockPos zipline_start;
    private BlockPos zipline_end;
    private ZiplineType zip_type;
    private Zipline zipline;

    public Zipline getZipline() {
        BlockPos start = getStartPos();
        BlockPos end = getEndPos();
        ZiplineType type = getZiplineType();
        if (zipline == null
                || !start.equals(zipline_start)
                || !end.equals(zipline_end)
                || !type.equals(zip_type)
        ) {
            zipline_start = start;
            zipline_end = end;
            zip_type = type;
            Vec3 startPos;
            Vec3 endPos;
            BlockEntity startEntity = level().getBlockEntity(start);
            BlockEntity endEntity = level().getBlockEntity(end);
            boolean delayInit = false;
            if (startEntity instanceof ZiplineHookTileEntity) {
                startPos = ((ZiplineHookTileEntity) startEntity).getActualZiplinePoint(end);
            } else {
                startPos = new Vec3(start.getX() + 0.5, start.getY() + 0.5, start.getZ() + 0.5);
                delayInit = true;
            }
            if (endEntity instanceof ZiplineHookTileEntity) {
                endPos = ((ZiplineHookTileEntity) endEntity).getActualZiplinePoint(start);
            } else {
                endPos = new Vec3(end.getX() + 0.5, end.getY() + 0.5, end.getZ() + 0.5);
                delayInit = true;
            }
            if (delayInit) {
                return type.getZipline(startPos, endPos);
            } else {
                zipline = type.getZipline(startPos, endPos);
            }

        }
        return zipline;
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
        Vec3 mostNearPoint = new Vec3(xOffset * t + start.getX(), yOffset * t + start.getY(), zOffset * t + start.getZ());
        distanceSqr = mostNearPoint.distanceToSqr(x, y, z);
        return distanceSqr < Zipline.MAXIMUM_HORIZONTAL_DISTANCE * Zipline.MAXIMUM_HORIZONTAL_DISTANCE;
    }

    @Nonnull
    @Override
    public EntityDimensions getDimensions(@Nonnull Pose p_213305_1_) {
        if (size == null) {
            return EntityDimensions.fixed(0.1f, 0.1f);
        }

        return size;
    }

    @Override
    public void move(MoverType p_19973_, Vec3 p_19974_) {
    }

    /*
    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        BlockPos end = getEndPos();
        BlockPos start = getStartPos();
        if (end != BlockPos.ZERO || start != BlockPos.ZERO) {
            size = EntityDimensions.fixed(Math.max(Math.abs(end.getX() - start.getX()), Math.abs(end.getZ() - start.getZ())) + 0.3f, Math.abs(end.getY() - start.getY()) + 0.3f);
            double d0 = size.width() / 2.0;
            this.setBoundingBox(new AABB(this.getX() - d0, this.getY(), this.getZ() - d0, this.getX() + d0, this.getY() + size.height(), this.getZ() + d0));
        }
    }

    @Override
    public void onSyncedDataUpdated(@Nonnull EntityDataAccessor<?> param) {
        if (param.equals(DATA_START_POS) || param.equals(DATA_END_POS)) {
            refreshDimensions();
        }
    }
    */

    public BlockPos getStartPos() {
        return getEntityData().get(DATA_START_POS);
    }

    public BlockPos getEndPos() {
        return getEntityData().get(DATA_END_POS);
    }

    private void setStartPos(BlockPos start) {
        getEntityData().set(DATA_START_POS, start);
    }

    private void setEndPos(BlockPos end) {
        getEntityData().set(DATA_END_POS, end);
    }

    public int getColor() {
        return getEntityData().get(DATA_COLOR);
    }

    private void setColor(int color) {
        getEntityData().set(DATA_COLOR, color);
    }

    public ZiplineType getZiplineType() {
        return ZiplineType.values()[getEntityData().get(DATA_ZIP_TYPE) % ZiplineType.values().length];
    }

    private void setZiplineType(ZiplineType type) {
        getEntityData().set(DATA_ZIP_TYPE, type.ordinal());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_START_POS, BlockPos.ZERO);
        builder.define(DATA_END_POS, BlockPos.ZERO);
        builder.define(DATA_COLOR, ZiplineRopeItem.DEFAULT_COLOR);
        builder.define(DATA_ZIP_TYPE, ZiplineType.STANDARD.ordinal());
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float v) {
        return false;
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compoundNBT) {
        setStartPos(new BlockPos(compoundNBT.getInt("Tile1_X"), compoundNBT.getInt("Tile1_Y"), compoundNBT.getInt("Tile1_Z")));
        setEndPos(new BlockPos(compoundNBT.getInt("Tile2_X"), compoundNBT.getInt("Tile2_Y"), compoundNBT.getInt("Tile2_Z")));
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compoundNBT) {
        BlockPos startPos = getStartPos();
        BlockPos endPos = getEndPos();
        compoundNBT.putInt("Tile1_X", startPos.getX());
        compoundNBT.putInt("Tile1_X", startPos.getY());
        compoundNBT.putInt("Tile1_X", startPos.getZ());
        compoundNBT.putInt("Tile2_X", endPos.getX());
        compoundNBT.putInt("Tile2_X", endPos.getY());
        compoundNBT.putInt("Tile2_X", endPos.getZ());
    }
}
