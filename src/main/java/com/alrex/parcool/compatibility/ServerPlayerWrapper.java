package com.alrex.parcool.compatibility;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ServerPlayerWrapper extends PlayerWrapper {

    private ServerPlayerEntity player;
    private static final WeakCache<ServerPlayerEntity, ServerPlayerWrapper> cache = new WeakCache<ServerPlayerEntity, ServerPlayerWrapper>();

    private ServerPlayerWrapper(ServerPlayerEntity player) {
        super(player);
        this.player = player;
    }

    // All static get methods grouped together
    public static ServerPlayerWrapper get(ServerPlayerEntity player) {
        return cache.get(player, () -> new ServerPlayerWrapper(player));
    }

    public static ServerPlayerWrapper get(Supplier<Context> contextSupplier) {
        return new ServerPlayerWrapper(contextSupplier.get().getSender());
    }
    
    public static ServerPlayerWrapper get(LivingFallEvent event) {
        return get((ServerPlayerEntity)event.getEntityLiving());
    }
    
    public static ServerPlayerWrapper get(Entity entity) {
        return get((ServerPlayerEntity)entity);
    }
    
    @Nullable
    public static ServerPlayerWrapper getPlayer(CommandContext<CommandSource> context, String arg)
    throws CommandSyntaxException
    {
        @Nullable ServerPlayerEntity player = EntityArgument.getPlayer(context, arg);
        return player == null ? null : new ServerPlayerWrapper(player);
    }

    // All getFrom methods grouped together
    public static ServerPlayerWrapper getFromEntity(LivingEvent event) {
        return get((ServerPlayerEntity)event.getEntity());
    }
    
    public static ServerPlayerWrapper getFromEntityOrDefault(LivingFallEvent event) {
        Entity entity = event.getEntity();
        return entity instanceof ServerPlayerEntity
                ? get((ServerPlayerEntity)entity)
                : null;
    }

    // All getOrDefault methods grouped together
    @Nullable
    public static ServerPlayerWrapper getOrDefault(Entity entity) {
        return entity instanceof ServerPlayerEntity ? get(entity) : null;
    }

    public static ServerPlayerWrapper getOrDefault(Clone event) {
        return getOrDefault(event.getPlayer());
    }

    // All is methods grouped together
    public static boolean is(PlayerWrapper player) {
        return player.getInstance() instanceof ServerPlayerEntity;
    }

    // Instance methods
    @Override
    public ServerPlayerEntity getInstance() {
        return player;
    }

    public void causeFoodExhaustion(float f) {
        player.causeFoodExhaustion(f);
    }
}
