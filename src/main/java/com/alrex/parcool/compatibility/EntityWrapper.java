package com.alrex.parcool.compatibility;

import java.lang.ref.WeakReference;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.utilities.MathUtil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class EntityWrapper {

    private final WeakReference<Entity> entityRef;
    protected static final WeakCache<Entity, EntityWrapper> cache = new WeakCache<>();

    protected EntityWrapper(Entity entity) {
        this.entityRef = new WeakReference<>(entity);
    }

    // Static get methods
    public static EntityWrapper get(Entity entity) {
        return cache.get(entity, () -> new EntityWrapper(entity));
    }

    // add methods grouped together
    public void addParticle(BasicParticleType endRod, double x, double d, double z, double e, double f, double g) {
        entityRef.get().level.addParticle(endRod, x, d, z, e, f, g);
    }
    
    public void addToDeltaMovement(int i, double d, int j) {
        entityRef.get().setDeltaMovement(entityRef.get().getDeltaMovement().add(i, d, j));
    }
    
    public void addToDeltaMovement(Vec3Wrapper vec) {
        entityRef.get().setDeltaMovement(entityRef.get().getDeltaMovement().add(vec));
    }

    // get methods grouped together
    public BlockPos getAdjustedBlockPos(int xDirection, double minY, int zDirection) {
        Entity entity = entityRef.get();
        return new BlockPos(
            entity.getX() + xDirection,
            entity.getBoundingBox().minY + minY,
            entity.getZ() + zDirection
        );
    }
    
    public float getBbHeight() {
        return entityRef.get().getBbHeight();
    }
    
    public float getBbWidth() {
        return entityRef.get().getBbWidth();
    }
    
    public BlockStateWrapper getBlockState(BlockPos pos) {
        return new BlockStateWrapper(entityRef.get().level.getBlockState(pos));
    }

    @Nullable
    public <T> T getCapability(@Nonnull final Capability<T> cap) {
        LazyOptional<T> optional = entityRef.get().getCapability(cap);
        if (!optional.isPresent()) return null;
		return optional.orElse(null);
    }
 
    public Vec3Wrapper getDeltaMovement() {
        return new Vec3Wrapper(entityRef.get().getDeltaMovement());
    }
    
    public float getEyeHeight() {
        return entityRef.get().getEyeHeight();
    }
    
    public float getFallDistance() {
        return entityRef.get().fallDistance;
    }
    
    public LevelWrapper getLevel() {
        return LevelWrapper.get(entityRef.get().level);
    }
    
    public Vec3Wrapper getLookAngle() {
        return new Vec3Wrapper(entityRef.get().getLookAngle());
    }
    
    public float getMinSlipperiness(BlockPos ...blockPos) {
        float minSlipperiness = 1;
        Entity entity = entityRef.get();
        LevelWrapper level = getLevel();
        for (BlockPos pos : blockPos) {
            if (level.isLoaded(pos)) {
                float candidateSlipperiness = level.getBlockState(pos).getFriction(entity.level, pos, entity);
                minSlipperiness = Math.min(minSlipperiness, candidateSlipperiness);
            }
        }
        return minSlipperiness;
    }
    
    public float getRotatedYRot(PlayerModelRotator rotator) {
        return 180f + MathUtil.lerp(rotator.getPartialTick(), entityRef.get().yRotO, entityRef.get().yRot);
    }
    
    public int getTickCount() {
        return entityRef.get().tickCount;
    }
    
    public UUID getUUID() {
        return entityRef.get().getUUID();
    }
    
    public float getViewXRot(float renderPartialTicks) {
        return entityRef.get().getViewXRot(renderPartialTicks);
    }
    
    public float getYHeadRot() {
        return entityRef.get().getYHeadRot();
    }
    
    public float getYRot() {
        return entityRef.get().yRot;
    }
    
    public double getX() {
        return entityRef.get().getX();
    }
    
    public double getY() {
        return entityRef.get().getY();
    }
    
    public double getZ() {
        return entityRef.get().getZ();
    }
    
    public Entity getInstance() {
        return entityRef.get();
    }

    // is methods grouped together
    public boolean isEveryLoaded(BlockPos ...blockPos) {
        for (BlockPos pos : blockPos) {
            if (!entityRef.get().level.isLoaded(pos)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isInLava() {
        return entityRef.get().isInLava();
    }
    
    public boolean isInWaterOrBubble() {
        return entityRef.get().isInWaterOrBubble();
    }
    
    public boolean isOnGround() {
        return entityRef.get().isOnGround();
    }
    
    public boolean isVisuallyCrawling() {
        return entityRef.get().isVisuallyCrawling();
    }
    
    public boolean isVisuallySwimming() {
        return entityRef.get().isVisuallySwimming();
    }

    // Other methods
    public void multiplyDeltaMovement(double d, int i, double e) {
        Entity entity = entityRef.get();
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(d, i, e));
    }
    
    public boolean noCollision(AABBWrapper bb) {
        return entityRef.get().level.noCollision(bb);
    }
    
    public void playSound(SoundEvent soundEvent, float i, float j) {
        entityRef.get().playSound(soundEvent, i, j);
    }
    
    public Vec3Wrapper position() {
        return new Vec3Wrapper(entityRef.get().position());
    }
    
    public void resetFallDistance() {
        entityRef.get().fallDistance = 0;
    }
    
    public void setDeltaMovement(double x, double i, double z) {
        entityRef.get().setDeltaMovement(x, i, z);
    }
    
    public void setYBodyRot(float f) {
        entityRef.get().setYBodyRot(f);
    }
}
