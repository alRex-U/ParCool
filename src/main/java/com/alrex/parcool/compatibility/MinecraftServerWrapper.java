package com.alrex.parcool.compatibility;

import java.util.Iterator;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class MinecraftServerWrapper {
    private final MinecraftServer instance;
    private static Minecraft mc = Minecraft.getInstance();

    public MinecraftServerWrapper(MinecraftServer server) {
        this.instance = server;
    }

    // All get methods grouped together
    public static MinecraftServerWrapper get(CommandContext<CommandSource> context) {
        return new MinecraftServerWrapper(context.getSource().getServer());
    }
    
    @Nullable
    public static PlayerWrapper getPlayer(UUID playerID) {
        ClientWorld level = mc.level;
        if (level == null) return null;
        PlayerEntity playerEntity = level.getPlayerByUUID(playerID);
        if (playerEntity == null) return null;
        return PlayerWrapper.get(playerEntity);
    }

    // All getPlayers methods grouped together
    public Iterable<ServerPlayerWrapper> getServerPlayers() {
        return getServerPlayers(instance.getPlayerList().getPlayers());
    }
    
    public static Iterable<ServerPlayerWrapper> getPlayers(CommandContext<CommandSource> context) {
        return getServerPlayers(context.getSource().getServer().getPlayerList().getPlayers());
    }

    public static Iterable<ServerPlayerWrapper> getServerPlayers(Iterable<ServerPlayerEntity> iterable) {
        Iterator<ServerPlayerEntity> iterator = iterable.iterator();
        return new Iterable<ServerPlayerWrapper>() {
            @Override
            public Iterator<ServerPlayerWrapper> iterator() {
                return new Iterator<ServerPlayerWrapper>() {

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public ServerPlayerWrapper next() {
                        return ServerPlayerWrapper.get(iterator.next());
                    }
                };
            }
        };  
    }
    
    public static Iterable<PlayerWrapper> getPlayers(Iterable<? extends PlayerEntity> iterable) {
        Iterator<? extends PlayerEntity> iterator = iterable.iterator();
        return new Iterable<PlayerWrapper>() {
            @Override
            public Iterator<PlayerWrapper> iterator() {
                return new Iterator<PlayerWrapper>() {

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public PlayerWrapper next() {
                        return PlayerWrapper.get(iterator.next());
                    }
                };
            }
        };  
    }
}
