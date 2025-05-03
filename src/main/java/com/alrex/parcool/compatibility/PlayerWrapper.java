package com.alrex.parcool.compatibility;

import java.lang.ref.WeakReference;
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

    private WeakReference<PlayerEntity> playerRef;
    protected static final WeakCache<PlayerEntity, PlayerWrapper> cache = new WeakCache<>();

    public PlayerWrapper(PlayerEntity player) {
        super(player);
        this.playerRef = new WeakReference<PlayerEntity>(player);
    }

    // All get methods grouped together
    @Override
    public PlayerEntity getInstance() {
        return playerRef.get();
    }

    public BlockStateWrapper getBelowBlockState() {
        PlayerEntity player = playerRef.get();
        return new BlockStateWrapper(player.level.getBlockState(player.blockPosition().below()));
    }
    
    public float getEyeHeight() {
        return playerRef.get().getEyeHeight(Pose.STANDING);
    }
    
    public float getFallDistance() {
        return playerRef.get().fallDistance;
    }
    
    public int getFoodLevel() {
        return playerRef.get().getFoodData().getFoodLevel();
    }

    public ItemStack getItemInHand(Hand hand) {
        return playerRef.get().getItemInHand(hand);
    }
    
    public String getName() {
        return playerRef.get().getGameProfile().getName();
    }
    
    public Pose getPose() {
        return playerRef.get().getPose();
    }
    
    public float getSlipperiness(BlockPos leanedBlock) {
        LevelWrapper level = getLevel();
        return level.getSlipperiness(leanedBlock, playerRef.get());
    }
    
    public int getTickCount() {
        return playerRef.get().tickCount;
    }
    
    public Object getVehicle() {
        return playerRef.get().getVehicle();
    }

    public Iterable<PlayerWrapper> getPlayersOnSameLevel() {
        return MinecraftServerWrapper.getPlayers(playerRef.get().level.players());
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
    
    public static PlayerWrapper get(Supplier<NetworkContextWrapper> contextSupplier) {
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
        return playerRef.get().hurtTime > 0;
    }
    
    public boolean hasNoPhysics() {
        return playerRef.get().noPhysics;
    }
    
    public boolean hasSomeCollision() {
        PlayerEntity player = playerRef.get();
        return player.horizontalCollision || player.verticalCollision;
    }
    
    public boolean isCreative() {
        return playerRef.get().isCreative();
    }
    
    public boolean isCrouching() {
        return playerRef.get().isCrouching();
    }
    
    public boolean isFallFlying() {
        return playerRef.get().isFallFlying();
    }
    
    public boolean isFlying() {
        return playerRef.get().abilities.flying;
    }
    
    public boolean isImmortal() {
        PlayerEntity player = playerRef.get();
        return player.isSpectator() || player.isCreative();
    }
    
    public boolean isInWall() {
        return playerRef.get().isInWall();
    }
    
    public boolean isInWater() {
        return playerRef.get().isInWater();
    }
    
    public boolean isLevelClientSide() {
        return playerRef.get().level.isClientSide;
    }
    
    public boolean isLocalPlayer() {
        return playerRef.get().isLocalPlayer();
    }
    
    public boolean isOnGround() {
        return playerRef.get().isOnGround();
    }
    
    public boolean isPassenger() {
        return playerRef.get().isPassenger();
    }
    
    public boolean isShiftKeyDown() {
        return playerRef.get().isShiftKeyDown();
    }
    
    public boolean isSprinting() {
        return playerRef.get().isSprinting();
    }
    
    public boolean isSwimming() {
        return playerRef.get().isSwimming();
    }
    
    public static boolean is(LivingEntityWrapper entity) {
        return entity.getInstance() instanceof PlayerEntity;
    }
    
    public boolean onClimbable() {
        return playerRef.get().onClimbable();
    }

    // All set methods grouped
    public void setAllYBodyRot(float bodyYaw) {
        PlayerEntity player = playerRef.get();
        player.yBodyRot = bodyYaw;
        player.yBodyRotO = bodyYaw;
    }
    
    public void setDeltaMovement(Vec3Wrapper vec) {
        playerRef.get().setDeltaMovement(vec);
    }
    
    public void setPos(Vec3Wrapper hidingPoint) {
        playerRef.get().setPos(hidingPoint.x(), hidingPoint.y(), hidingPoint.z());
    }
    
    public void setSprinting(boolean b) {
        playerRef.get().setSprinting(true);
    }
    
    public void setStandingPose() {
        playerRef.get().setPose(Pose.STANDING);
    }
    
    public void setSwimming() {
        playerRef.get().setSprinting(false);
        playerRef.get().setPose(Pose.SWIMMING);
    }
    
    public void setYHeadRot(float yawDegree) {
        playerRef.get().yHeadRot = yawDegree;
    }
    
    public void setYRot(double yawDegree) {
        playerRef.get().yRot = (float)yawDegree;
    }

    // All hurt methods grouped
    public <T> void hurtAndBreakStack(int quantity, ItemStack stack) {
        hurtAndBreakStack(quantity, stack, (it) -> {
            // Do nothing
        });
    }
    
    public <T> void hurtAndBreakStack(int quantity, ItemStack stack, Consumer<PlayerWrapper> consumer) {
        stack.hurtAndBreak(quantity, playerRef.get(), (it) -> consumer.accept(get(it)));
    }

    // Remaining methods
    public void displayClientMessage(IFormattableTextComponent translationTextComponent, boolean b) {
        playerRef.get().displayClientMessage(translationTextComponent, b);
    }

    public void disablePhysics() {
        playerRef.get().noPhysics = true;
    }

    public void enablePhysics() {
        playerRef.get().noPhysics = false;
    }

    public void forceDamage(DamageSource source, float f) {
        PlayerEntity player = playerRef.get();
        int invulnerableTime = player.invulnerableTime; // bypass invulnerableTime
        player.invulnerableTime = 0;
        player.hurt(source, f);
        player.invulnerableTime = invulnerableTime;
    }

    public void jumpFromGround() {
        playerRef.get().jumpFromGround();
    }

    public void multiplyFallDistance(float multiplier) {
        playerRef.get().fallDistance *= multiplier;
    }

    public void rotateBodyRot0(Vec3Wrapper direction, double d) {
        PlayerEntity player = playerRef.get();
        player.yBodyRotO = player.yBodyRot = (float) VectorUtil.toYawDegree(direction.yRot((float) d));
    }
}
