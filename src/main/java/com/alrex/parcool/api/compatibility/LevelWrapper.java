package com.alrex.parcool.api.compatibility;

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
   private World level;
   private static final WeakCache<World, LevelWrapper> cache = new WeakCache<>();
   private static final Minecraft mc = Minecraft.getInstance();

   public LevelWrapper(World level) {
      this.level = level;
   }

   public static LevelWrapper get(World level) {
      return cache.get(level, () -> new LevelWrapper(level));
   }

   public PlayerEntity getPlayerByUUID(UUID playerID) {
      return level.getPlayerByUUID(playerID);
   }

   public void addParticle(IParticleData drippingWater, double d, double e, double f, double x, double g,
         double z) {
      level.addParticle(drippingWater, d, e, f, x, g, z);
   }

   public boolean isLoaded(BlockPos blockpos) {
      return level.isLoaded(blockpos);
   }

   public BlockState getBlockState(BlockPos blockpos) {
      return level.getBlockState(blockpos);
   }

   public boolean isClientSide() {
      return level.isClientSide();
   }

   public BlockEntityWrapper getBlockEntity(BlockPos pos) {
      return new BlockEntityWrapper(level.getBlockEntity(pos));
   }

   public static LevelWrapper get() {
      return get(mc.level);
   }

   public boolean isCollisionShapeFullBlock(BlockState state, BlockPos pos) {
      return state.isCollisionShapeFullBlock(level, pos);
   }

   public boolean noCollision(AxisAlignedBB expandTowards) {
      return level.noCollision(expandTowards);
   }

   public List<ZiplineRopeEntity> getEntitiesOfClass(Class<ZiplineRopeEntity> class1, AxisAlignedBB inflate) {
      return level.getEntitiesOfClass(class1, inflate);
   }

   public World getInstance() {
      return level;
   }
}
