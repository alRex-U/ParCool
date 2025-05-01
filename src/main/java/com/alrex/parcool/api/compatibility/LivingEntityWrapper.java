package com.alrex.parcool.api.compatibility;

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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class LivingEntityWrapper extends EntityWrapper {

    private LivingEntity entity;
    private static final WeakCache<LivingEntity, LivingEntityWrapper> cache = new WeakCache<>();

    protected LivingEntityWrapper(LivingEntity entity) {
        super(entity);
        this.entity = entity;
    }
    
    // All get methods grouped together
    @Override
    public LivingEntity getInstance() {
        return entity;
    }
    
    public ModifiableAttributeInstance getAttribute(Attribute attribute) {
        return entity.getAttribute(attribute);
    }
    
    public double getAttributeValue(Attribute attribute) {
        return entity.getAttributeValue(attribute);
    }
    
    public AxisAlignedBB getBoundingBox() {
        return entity.getBoundingBox();
    }
    
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap) {
        return entity.getCapability(cap);
    }
    
    public double getGravity() {
        return entity.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
    }
    
    public HandSide getMainArm() {
        return entity.getMainArm();
    }
    
    public Random getRandom() {
        return entity.getRandom();
    }
    
    public Vector3d getRotatedBodyAngle(PlayerModelRotator rotator) {
        return VectorUtil.fromYawDegree(MathUtil.lerp(entity.yBodyRotO, entity.yBodyRot, rotator.getPartialTick()));
    }
    
    public double getUpdatedYRotDifference() {
        Vector3d currentAngle = VectorUtil.fromYawDegree(entity.yBodyRot);
		Vector3d oldAngle = VectorUtil.fromYawDegree(entity.yBodyRotO);
		return Math.atan(
				(oldAngle.x() * currentAngle.z() - currentAngle.x() * oldAngle.z())
						/ (currentAngle.x() * oldAngle.x() + currentAngle.z() * oldAngle.z())
		);
    }
    
    public Vector3d getVectorYBodyRot() {
        return VectorUtil.fromYawDegree(entity.yBodyRot);
    }
    
    public float getYBodyRot() {
        return entity.yBodyRot;
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
        return entity.hasEffect(effect);
    }
    
    public boolean isFallFlying() {
        return entity.isFallFlying();
    }
    
    public boolean isSpectator() {
        return entity.isSpectator();
    }
    
    public boolean isSwingingMainHand() {
        return entity.swingingArm == Hand.MAIN_HAND;
    }

    // Update methods
    public void updateBodyRot() {
        entity.yBodyRot = entity.yRot;
        entity.yBodyRotO = entity.yRotO;
    }
}
