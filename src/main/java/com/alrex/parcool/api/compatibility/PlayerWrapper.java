package com.alrex.parcool.api.compatibility;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.alrex.parcool.utilities.VectorUtil;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class PlayerWrapper extends LivingEntityWrapper {

    private PlayerEntity player;
    protected static final WeakCache<PlayerEntity, PlayerWrapper> cache = new WeakCache<>();

    public PlayerWrapper(PlayerEntity player) {
        super(player);
        this.player = player;
    }

    // All get methods grouped together
    @Override
    public PlayerEntity getInstance() {
        return player;
    }

    public BlockState getBelowBlockState() {
        return player.level.getBlockState(player.blockPosition().below());
    }
    
    public float getEyeHeight() {
        return player.getEyeHeight(Pose.STANDING);
    }
    
    public float getFallDistance() {
        return player.fallDistance;
    }
    
    public int getFoodLevel() {
        return player.getFoodData().getFoodLevel();
    }

    public ItemStack getItemInHand(Hand hand) {
        return player.getItemInHand(hand);
    }
    
    public String getName() {
        return player.getGameProfile().getName();
    }
    
    public Pose getPose() {
        return player.getPose();
    }
    
    public float getSlipperiness(BlockPos leanedBlock) {
        return player.level.getBlockState(leanedBlock).getSlipperiness(player.level, leanedBlock, player);
    }
    
    public int getTickCount() {
        return player.tickCount;
    }
    
    public Object getVehicle() {
        return player.getVehicle();
    }

    public Iterable<PlayerWrapper> getPlayersOnSameLevel() {
        return MinecraftServerWrapper.getPlayers(player.level.players());
    }

    // All static get methods grouped together
    public static PlayerWrapper get(PlayerEntity player) {
        return cache.get(player, () -> new PlayerWrapper(player));
    }

    public static PlayerWrapper get(PlayerTickEvent event) {
        return get(event.player);
    }
    
    public static PlayerWrapper get(AttachCapabilitiesEvent<Entity> event) {
        return get((PlayerEntity) event.getObject());
    }

    public static PlayerWrapper get(PlayerEvent event) {
        return get(event.getPlayer());
    }
    
    public static PlayerWrapper get(Entity entity) {
        return get((PlayerEntity)entity);
    }
    
    public static PlayerWrapper get(ItemUseContext context) {
        return get(context.getPlayer());
    }
    
    public static PlayerWrapper get(LivingEntity entity) {
        return get((PlayerEntity)entity);
    }

    public static PlayerWrapper get(LivingEntityWrapper entity) {
        return get((PlayerEntity)entity.getInstance());
    }
    
    public static PlayerWrapper get(CapabilityProvider<Entity> entityMixin) {
        return get((PlayerEntity)(Object)entityMixin);
    }
    
    public static PlayerWrapper get(Supplier<Context> contextSupplier) {
        return get(contextSupplier.get().getSender());
    }
    
    @Nullable
    public static PlayerWrapper get(LevelWrapper world, UUID playerID) {
        PlayerEntity playerEntity = world.getPlayerByUUID(playerID);
        if (playerEntity == null) return null;
        return PlayerWrapper.get(playerEntity);
    }

    // All getOrDefault methods grouped
    @Nullable
    public static PlayerWrapper getOrDefault(AttachCapabilitiesEvent<? extends Entity> event) {
        return getOrDefault(event.getObject());
    }

    @Nullable
    public static PlayerWrapper getOrDefault(Entity entity) {
        return entity instanceof PlayerEntity ? get(entity) : null;
    }
    
    public static PlayerWrapper getOrDefault(LivingEntityWrapper entity) {
        return getOrDefault(entity.getInstance());
    }

    // All getFrom methods grouped
    public static PlayerWrapper getOriginalPlayer(Clone event) {
        return get(event.getOriginal());
    }
    
    public static PlayerWrapper getFromLivingEntity(LivingEvent event) {
        return get((PlayerEntity)event.getEntityLiving());
    }

    public static PlayerWrapper getFromEntity(LivingEvent event) {
        return get((PlayerEntity)event.getEntity());
    }
    
    public static PlayerWrapper getFromEntityOrDefault(LivingFallEvent event) {
        Entity entity = event.getEntity();
        return entity instanceof PlayerEntity ? get((PlayerEntity)entity) : null;
    }
    
    public static PlayerWrapper getFromEntityOrDefault(LivingEvent event) {
        return getOrDefault(event.getEntity());
    }
    
    // All has/is boolean methods grouped
    
    public boolean hasHurtTime() {
        return player.hurtTime > 0;
    }
    
    public boolean hasNoPhysics() {
        return player.noPhysics;
    }
    
    public boolean hasSomeCollision() {
        return player.horizontalCollision || player.verticalCollision;
    }
    
    public boolean isCreative() {
        return player.isCreative();
    }
    
    public boolean isCrouching() {
        return player.isCrouching();
    }
    
    public boolean isFallFlying() {
        return player.isFallFlying();
    }
    
    public boolean isFlying() {
        return player.abilities.flying;
    }
    
    public boolean isImmortal() {
        return player.isSpectator() || player.isCreative();
    }
    
    public boolean isInWall() {
        return player.isInWall();
    }
    
    public boolean isInWater() {
        return player.isInWater();
    }
    
    public boolean isLevelClientSide() {
        return player.level.isClientSide;
    }
    
    public boolean isLocalPlayer() {
        return player.isLocalPlayer();
    }
    
    public boolean isOnGround() {
        return player.isOnGround();
    }
    
    public boolean isPassenger() {
        return player.isPassenger();
    }
    
    public boolean isShiftKeyDown() {
        return player.isShiftKeyDown();
    }
    
    public boolean isSprinting() {
        return player.isSprinting();
    }
    
    public boolean isSwimming() {
        return player.isSwimming();
    }
    
    public static boolean is(LivingEntityWrapper entity) {
        return entity.getInstance() instanceof PlayerEntity;
    }
    
    public boolean onClimbable() {
        return player.onClimbable();
    }

    // All set methods grouped
    public void setAllYBodyRot(float bodyYaw) {
        player.yBodyRot = bodyYaw;
        player.yBodyRotO = bodyYaw;
    }
    
    public void setDeltaMovement(Vec3Wrapper vec) {
        player.setDeltaMovement(vec);
    }
    
    public void setPos(Vec3Wrapper hidingPoint) {
        player.setPos(hidingPoint.x(), hidingPoint.y(), hidingPoint.z());
    }
    
    public void setSprinting(boolean b) {
        player.setSprinting(true);
    }
    
    public void setStandingPose() {
        player.setPose(Pose.STANDING);
    }
    
    public void setSwimming() {
        player.setSprinting(false);
        player.setPose(Pose.SWIMMING);
    }
    
    public void setYHeadRot(float yawDegree) {
        player.yHeadRot = yawDegree;
    }
    
    public void setYRot(double yawDegree) {
        player.yRot = (float)yawDegree;
    }

    // All hurt methods grouped
    public <T> void hurtAndBreakStack(int quantity, ItemStack stack) {
        hurtAndBreakStack(quantity, stack, (it) -> {
            // Do nothing
        });
    }
    
    public <T> void hurtAndBreakStack(int quantity, ItemStack stack, Consumer<PlayerWrapper> consumer) {
        stack.hurtAndBreak(quantity, player, (it) -> consumer.accept(get(it)));
    }

    // Remaining methods
    public void displayClientMessage(IFormattableTextComponent translationTextComponent, boolean b) {
        player.displayClientMessage(translationTextComponent, b);
    }

    public void disablePhysics() {
        player.noPhysics = true;
    }

    public void enablePhysics() {
        player.noPhysics = false;
    }

    public void forceDamage(DamageSource source, float f) {
        int invulnerableTime = player.invulnerableTime; // bypass invulnerableTime
        player.invulnerableTime = 0;
        player.hurt(source, f);
        player.invulnerableTime = invulnerableTime;
    }

    public void jumpFromGround() {
        player.jumpFromGround();
    }

    public void multiplyFallDistance(float multiplier) {
        player.fallDistance *= multiplier;
    }

    public void rotateBodyRot0(Vec3Wrapper direction, double d) {
        player.yBodyRotO = player.yBodyRot = (float) VectorUtil.toYawDegree(direction.yRot((float) d));
    }
}
