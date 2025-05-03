package com.alrex.parcool.compatibility;

import java.lang.ref.WeakReference;
import java.util.Random;
import javax.annotation.Nonnull;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.potion.Effect;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.ForgeMod;

public class LivingEntityWrapper extends EntityWrapper {

    private WeakReference<LivingEntity> entityRef;
    private static final WeakCache<LivingEntity, LivingEntityWrapper> cache = new WeakCache<>();

    protected LivingEntityWrapper(LivingEntity entity) {
        super(entity);
        this.entityRef = new WeakReference<>(entity);
    }
    
    // All get methods grouped together
    @Override
    public LivingEntity getInstance() {
        return entityRef.get();
    }
    
    public ModifiableAttributeInstance getAttribute(Attribute attribute) {
        return entityRef.get().getAttribute(attribute);
    }
    
    public double getAttributeValue(Attribute attribute) {
        return entityRef.get().getAttributeValue(attribute);
    }
    
    public AxisAlignedBB getBoundingBox() {
        return entityRef.get().getBoundingBox();
    }
    
    public double getGravity() {
        return entityRef.get().getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
    }
    
    public HandSide getMainArm() {
        return entityRef.get().getMainArm();
    }
    
    public Random getRandom() {
        return entityRef.get().getRandom();
    }
    
    public Vec3Wrapper getRotatedBodyAngle(PlayerModelRotator rotator) {
        LivingEntity entity = entityRef.get();
        return new Vec3Wrapper(VectorUtil.fromYawDegree(MathUtil.lerp(entity.yBodyRotO, entity.yBodyRot, rotator.getPartialTick())));
    }
    
    public double getUpdatedYRotDifference() {
        LivingEntity entity = entityRef.get();
        Vec3Wrapper currentAngle = VectorUtil.fromYawDegree(entity.yBodyRot);
		Vec3Wrapper oldAngle = VectorUtil.fromYawDegree(entity.yBodyRotO);
		return Math.atan(
				(oldAngle.x() * currentAngle.z() - currentAngle.x() * oldAngle.z())
						/ (currentAngle.x() * oldAngle.x() + currentAngle.z() * oldAngle.z())
		);
    }
    
    public Vec3Wrapper getVectorYBodyRot() {
        return new Vec3Wrapper(VectorUtil.fromYawDegree(entityRef.get().yBodyRot));
    }
    
    public float getYBodyRot() {
        return entityRef.get().yBodyRot;
    }

    // Static get methods
    protected static LivingEntityWrapper get(LivingEntity entity) {
        return cache.get(entity, () -> new LivingEntityWrapper(entity));
    }

    public static LivingEntityWrapper get(Entity entity) {
        return get((LivingEntity)entity);
    }
    
    // Has/is boolean methods
    public boolean hasEffect(Effect effect) {
        return entityRef.get().hasEffect(effect);
    }
    
    public boolean isFallFlying() {
        return entityRef.get().isFallFlying();
    }
    
    public boolean isSpectator() {
        return entityRef.get().isSpectator();
    }
    
    public boolean isSwingingMainHand() {
        return entityRef.get().swingingArm == Hand.MAIN_HAND;
    }

    // Update methods
    public void updateBodyRot() {
        LivingEntity entity = entityRef.get();
        entity.yBodyRot = entity.yRot;
        entity.yBodyRotO = entity.yRotO;
    }
}
