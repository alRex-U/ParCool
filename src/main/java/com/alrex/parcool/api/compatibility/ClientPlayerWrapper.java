package com.alrex.parcool.api.compatibility;

import java.lang.ref.WeakReference;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ClientPlayerWrapper extends AbstractClientPlayerWrapper {
    private static final Minecraft mc = Minecraft.getInstance();
    private WeakReference<ClientPlayerEntity> playerRef;
    private static final WeakCache<ClientPlayerEntity, ClientPlayerWrapper> cache = new WeakCache<>();

    public ClientPlayerWrapper(ClientPlayerEntity player) {
        super(player);
        this.playerRef = new WeakReference<>(player);
    }

    // All static get methods grouped together
    public static ClientPlayerWrapper get(ClientPlayerEntity player) {
        return cache.get(player, () -> new ClientPlayerWrapper(player));
    }
    
    public static ClientPlayerWrapper get(PlayerEntity player) {
        return get((ClientPlayerEntity)player);
    }

    public static ClientPlayerWrapper get(PlayerWrapper player) {
        ClientPlayerEntity clientPlayer = (ClientPlayerEntity) player.getInstance();
        return cache.get(clientPlayer, () -> new ClientPlayerWrapper(clientPlayer));
    }

    @Nullable
    public static ClientPlayerWrapper get() {
        return mc.player == null ? null : get(mc.player);
    }
    
    public static ClientPlayerWrapper get(AbstractClientPlayerEntity player) {
        return get((ClientPlayerEntity)player);
    }

    public static ClientPlayerWrapper get(LivingEntity entity) {
        return get((ClientPlayerEntity)entity);
    }
    
    // All static getOrDefault methods grouped together
    @Nullable
    public static ClientPlayerWrapper getOrDefault(LivingEntity entity) {
        return entity instanceof ClientPlayerEntity ? get(entity) : null;
    }

    @Nullable
    public static ClientPlayerWrapper getOrDefault(LivingEntityWrapper entityWrapper) {
        return getOrDefault(entityWrapper.getInstance());
    }
    
    // All static is methods grouped together
    public static boolean is(EntityWrapper playerWrapper) {
        return playerWrapper.getInstance() instanceof ClientPlayerEntity;
    }

    // All get methods grouped together
    @Override
    public ClientPlayerEntity getInstance() {
        return playerRef.get();
    }

    public double getLeftImpulse() {
        return playerRef.get().input.leftImpulse;
    }

    public double getForwardImpulse() {
        return playerRef.get().input.forwardImpulse;
    }
    
    public long getGameTime() {
        return playerRef.get().level.getGameTime();
    }

    // All is/has methods grouped together
    public boolean isSprinting() {
        return playerRef.get().isSprinting();
    }

    public boolean isAnyMoveKeyDown() {
        ClientPlayerEntity player = playerRef.get();
        return player.input.up
                || player.input.down
                || player.input.left
                || player.input.right;
    }
    
    public boolean hasForwardImpulse() {
        return playerRef.get().input.hasForwardImpulse();
    }
}
