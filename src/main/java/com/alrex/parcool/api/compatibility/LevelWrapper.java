package com.alrex.parcool.api.compatibility;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class LevelWrapper {
   private WeakReference<World> level;
   private static final WeakCache<World, LevelWrapper> cache = new WeakCache<>();
   private static final Minecraft mc = Minecraft.getInstance();

   public LevelWrapper(World level) {
      this.level = new WeakReference<>(level);
   }

   public static LevelWrapper get(World level) {
      return cache.get(level, () -> new LevelWrapper(level));
   }

   public PlayerEntity getPlayerByUUID(UUID playerID) {
      return level.get().getPlayerByUUID(playerID);
   }

   public void addParticle(IParticleData drippingWater, double d, double e, double f, double x, double g,
         double z) {
      level.get().addParticle(drippingWater, d, e, f, x, g, z);
   }

   public boolean isLoaded(BlockPos blockpos) {
      return level.get().isLoaded(blockpos);
   }

   public BlockState getBlockState(BlockPos blockpos) {
      return level.get().getBlockState(blockpos);
   }

   public boolean isClientSide() {
      return level.get().isClientSide();
   }

   public BlockEntityWrapper getBlockEntity(BlockPos pos) {
      return new BlockEntityWrapper(level.get().getBlockEntity(pos));
   }

   public static LevelWrapper get() {
      return get(mc.level);
   }

   public boolean isCollisionShapeFullBlock(BlockState state, BlockPos pos) {
      return state.isCollisionShapeFullBlock(level.get(), pos);
   }

   public boolean noCollision(AxisAlignedBB expandTowards) {
      return level.get().noCollision(expandTowards);
   }

   public List<ZiplineRopeEntity> getEntitiesOfClass(Class<ZiplineRopeEntity> class1, AxisAlignedBB inflate) {
      return level.get().getEntitiesOfClass(class1, inflate);
   }

   public World getInstance() {
      return level.get();
   }
}
