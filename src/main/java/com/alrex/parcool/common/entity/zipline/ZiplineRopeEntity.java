package com.alrex.parcool.common.entity.zipline;

import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.block.zipline.ZiplineInfo;
import com.alrex.parcool.common.entity.ParcoolEntityType;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineType;
import com.alrex.parcool.compatibility.LevelWrapper;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;

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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class ZiplineRopeEntity extends Entity {
    private static final DataParameter<BlockPos> DATA_START_POS;
    private static final DataParameter<BlockPos> DATA_END_POS;
    private static final DataParameter<Integer> DATA_COLOR;
    private static final DataParameter<Integer> DATA_ZIP_TYPE;
    private Object levelWrapper;

    static {
        DATA_START_POS = EntityDataManager.defineId(ZiplineRopeEntity.class, DataSerializers.BLOCK_POS);
        DATA_END_POS = EntityDataManager.defineId(ZiplineRopeEntity.class, DataSerializers.BLOCK_POS);
        DATA_COLOR = EntityDataManager.defineId(ZiplineRopeEntity.class, DataSerializers.INT);
        DATA_ZIP_TYPE = EntityDataManager.defineId(ZiplineRopeEntity.class, DataSerializers.INT);
    }

    private EntitySize size;

    public ZiplineRopeEntity(EntityType<?> entityType, World level) {
        super(entityType, level);
        setLevelWrapper();
    }

    public ZiplineRopeEntity(EntityType<?> entityType, LevelWrapper level) {
        super(entityType, level.getInstance());
        levelWrapper = level;
    }

    private void setLevelWrapper() {
        levelWrapper = level == null ? null : LevelWrapper.get(level);
    }

    @Override
    public void setLevel(World level) {
        super.setLevel(level);
        setLevelWrapper();
    }

    public ZiplineRopeEntity(World world, BlockPos start, BlockPos end, ZiplineInfo info) {
        super(ParcoolEntityType.ZIPLINE_ROPE.get(), world);
        setStartPos(start);
        setEndPos(end);
        setColor(info.getColor());
        setZiplineType(info.getType());
        setPos((end.getX() + start.getX()) / 2.0 + 0.5, Math.min(end.getY(), start.getY()), (end.getZ() + start.getZ()) / 2.0 + 0.5);
        noPhysics = true;
        forcedLoading = true;
        size = EntitySize.fixed(Math.max(Math.abs(end.getX() - start.getX()), Math.abs(end.getZ() - start.getZ())) + 0.3f, Math.abs(end.getY() - start.getY()) + 0.3f);
        setLevelWrapper();
    }

    public ZiplineRopeEntity(LevelWrapper level, BlockPos start, BlockPos end, ZiplineInfo info) {
        this(level.getInstance(), start, end, info);
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
            Vec3Wrapper startPos;
            Vec3Wrapper endPos;
            TileEntity startEntity = level.getBlockEntity(start);
            TileEntity endEntity = level.getBlockEntity(end);
            if (startEntity instanceof ZiplineHookTileEntity) {
                startPos = ((ZiplineHookTileEntity) startEntity).getActualZiplinePoint(end);
            } else {
                startPos = new Vec3Wrapper(start.getX() + 0.5, start.getY() + 0.7, start.getZ() + 0.5);
            }
            if (endEntity instanceof ZiplineHookTileEntity) {
                endPos = ((ZiplineHookTileEntity) endEntity).getActualZiplinePoint(start);
            } else {
                endPos = new Vec3Wrapper(end.getX() + 0.5, end.getY() + 0.7, end.getZ() + 0.5);
            }
            zipline = type.getZipline(startPos, endPos);
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
        Vec3Wrapper mostNearPoint = new Vec3Wrapper(xOffset * t + start.getX(), yOffset * t + start.getY(), zOffset * t + start.getZ());
        distanceSqr = mostNearPoint.distanceToSqr(x, y, z);
        return distanceSqr < Zipline.MAXIMUM_DISTANCE * Zipline.MAXIMUM_DISTANCE;
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

    @Nonnull
    @Override
    public ActionResultType interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
        return ActionResultType.sidedSuccess(PlayerWrapper.get(p_184230_1_).isLevelClientSide());
    }

    @Override
    protected void defineSynchedData() {
        getEntityData().define(DATA_START_POS, BlockPos.ZERO);
        getEntityData().define(DATA_END_POS, BlockPos.ZERO);
        getEntityData().define(DATA_COLOR, ZiplineRopeItem.DEFAULT_COLOR);
        getEntityData().define(DATA_ZIP_TYPE, ZiplineType.STANDARD.ordinal());
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
