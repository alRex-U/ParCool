package com.alrex.parcool.compatibility;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class LevelWrapper {
   private WeakReference<World> levelRef;
   private static final WeakCache<World, LevelWrapper> cache = new WeakCache<>();
   private static final Minecraft mc = Minecraft.getInstance();

   public LevelWrapper(World level) {
      this.levelRef = new WeakReference<>(level);
   }

   public static LevelWrapper get(World level) {
      return cache.get(level, () -> new LevelWrapper(level));
   }

   public PlayerEntity getPlayerByUUID(UUID playerID) {
      return levelRef.get().getPlayerByUUID(playerID);
   }

   public void addParticle(IParticleData drippingWater, double d, double e, double f, double x, double g,
         double z) {
      levelRef.get().addParticle(drippingWater, d, e, f, x, g, z);
   }

   public boolean isLoaded(BlockPos blockpos) {
      return levelRef.get().isLoaded(blockpos);
   }

   public BlockStateWrapper getBlockState(BlockPos blockpos) {
      return BlockStateWrapper.get(levelRef.get().getBlockState(blockpos));
   }

   public boolean isClientSide() {
      return levelRef.get().isClientSide();
   }

   public BlockEntityWrapper getBlockEntity(BlockPos pos) {
      return new BlockEntityWrapper(levelRef.get().getBlockEntity(pos));
   }

   public static LevelWrapper get() {
      return get(mc.level);
   }

   public boolean isCollisionShapeFullBlock(BlockStateWrapper state, BlockPos pos) {
      return state.getInstance().isCollisionShapeFullBlock(levelRef.get(), pos);
   }

   public boolean noCollision(AABBWrapper expandTowards) {
      return levelRef.get().noCollision(expandTowards);
   }

   public List<ZiplineRopeEntity> getEntitiesOfClass(Class<ZiplineRopeEntity> class1, AABBWrapper inflate) {
      return levelRef.get().getEntitiesOfClass(class1, inflate);
   }

   public World getInstance() {
      return levelRef.get();
   }

   public boolean isCollisionShapeFullBlock(BlockState state, BlockPos pos) {
      return state.isCollisionShapeFullBlock(levelRef.get(), pos);
   }

   public boolean addFreshEntity(ZiplineRopeEntity entity) {
      return levelRef.get().addFreshEntity(entity);
   }

   public boolean isAir(BlockPos block) {
      return getBlockState(block).isAir();
   }

   public float getSlipperiness(BlockPos leanedBlock, Entity entity) {
      World level = levelRef.get();
      return level.getBlockState(leanedBlock).getSlipperiness(level, leanedBlock, entity);
   }
}
