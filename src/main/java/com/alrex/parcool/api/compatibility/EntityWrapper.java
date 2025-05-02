package com.alrex.parcool.api.compatibility;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class EntityWrapper {

    private Entity entity;
    protected static final WeakCache<Entity, EntityWrapper> cache = new WeakCache<>();

    protected EntityWrapper(Entity entity) {
        this.entity = entity;
    }

    // Static get methods
    public static EntityWrapper get(Entity entity) {
        return cache.get(entity, () -> new EntityWrapper(entity));
    }

    // add methods grouped together
    public void addParticle(BasicParticleType endRod, double x, double d, double z, double e, double f, double g) {
        entity.level.addParticle(endRod, x, d, z, e, f, g);
    }
    
    public void addToDeltaMovement(int i, double d, int j) {
        entity.setDeltaMovement(entity.getDeltaMovement().add(i, d, j));
    }
    
    public void addToDeltaMovement(Vec3Wrapper vec) {
        entity.setDeltaMovement(entity.getDeltaMovement().add(vec));
    }

    // get methods grouped together
    public BlockPos getAdjustedBlockPos(int xDirection, double minY, int zDirection) {
        return new BlockPos(
            entity.getX() + xDirection,
            entity.getBoundingBox().minY + minY,
            entity.getZ() + zDirection
        );
    }
    
    public float getBbHeight() {
        return entity.getBbHeight();
    }
    
    public float getBbWidth() {
        return entity.getBbWidth();
    }
    
    public BlockState getBlockState(BlockPos pos) {
        return entity.level.getBlockState(pos);
    }

    @Nullable
    public <T> T getCapability(@Nonnull final Capability<T> cap) {
        LazyOptional<T> optional = entity.getCapability(cap);
        if (!optional.isPresent()) return null;
		return optional.orElse(null);
    }
 
    public Vec3Wrapper getDeltaMovement() {
        return new Vec3Wrapper(entity.getDeltaMovement());
    }
    
    public float getEyeHeight() {
        return entity.getEyeHeight();
    }
    
    public float getFallDistance() {
        return entity.fallDistance;
    }
    
    public LevelWrapper getLevel() {
        return LevelWrapper.get(entity.level);
    }
    
    public Vec3Wrapper getLookAngle() {
        return new Vec3Wrapper(entity.getLookAngle());
    }
    
    public float getMinSlipperiness(BlockPos ...blockPos) {
        float minSlipperiness = 1;
        for (BlockPos pos : blockPos) {
            if (entity.level.isLoaded(pos)) {
                float candidateSlipperiness = entity.level.getBlockState(pos).getSlipperiness(entity.level, pos, entity);
                minSlipperiness = Math.min(minSlipperiness, candidateSlipperiness);
            }
        }
        return minSlipperiness;
    }
    
    public float getRotatedYRot(PlayerModelRotator rotator) {
        return 180f + MathHelper.lerp(rotator.getPartialTick(), entity.yRotO, entity.yRot);
    }
    
    public int getTickCount() {
        return entity.tickCount;
    }
    
    public UUID getUUID() {
        return entity.getUUID();
    }
    
    public float getViewXRot(float renderPartialTicks) {
        return entity.getViewXRot(renderPartialTicks);
    }
    
    public float getYHeadRot() {
        return entity.getYHeadRot();
    }
    
    public float getYRot() {
        return entity.yRot;
    }
    
    public double getX() {
        return entity.getX();
    }
    
    public double getY() {
        return entity.getY();
    }
    
    public double getZ() {
        return entity.getZ();
    }
    
    public Entity getInstance() {
        return entity;
    }

    // is methods grouped together
    public boolean isEveryLoaded(BlockPos ...blockPos) {
        for (BlockPos pos : blockPos) {
            if (!entity.level.isLoaded(pos)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isInLava() {
        return entity.isInLava();
    }
    
    public boolean isInWaterOrBubble() {
        return entity.isInWaterOrBubble();
    }
    
    public boolean isOnGround() {
        return entity.isOnGround();
    }
    
    public boolean isVisuallyCrawling() {
        return entity.isVisuallyCrawling();
    }
    
    public boolean isVisuallySwimming() {
        return entity.isVisuallySwimming();
    }

    // Other methods
    public void multiplyDeltaMovement(double d, int i, double e) {
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(d, i, e));
    }
    
    public boolean noCollision(AxisAlignedBB bb) {
        return entity.level.noCollision(bb);
    }
    
    public void playSound(SoundEvent soundEvent, float i, float j) {
        entity.playSound(soundEvent, i, j);
    }
    
    public Vec3Wrapper position() {
        return new Vec3Wrapper(entity.position());
    }
    
    public void resetFallDistance() {
        entity.fallDistance = 0;
    }
    
    public void setDeltaMovement(double x, double i, double z) {
        entity.setDeltaMovement(x, i, z);
    }
    
    public void setYBodyRot(float f) {
        entity.setYBodyRot(f);
    }
}
