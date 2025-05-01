package com.alrex.parcool.api.compatibility;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;

public class PlayerPacketDistributor {
    public static PacketTarget with(ServerPlayerWrapper player) {
        ServerPlayerEntity instance = player.getInstance();
        return PacketDistributor.PLAYER.with(() -> instance);
    }
}
