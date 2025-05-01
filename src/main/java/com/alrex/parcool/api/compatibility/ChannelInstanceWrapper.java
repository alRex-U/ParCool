package com.alrex.parcool.api.compatibility;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.network.StartBreakfallMessage;
import net.minecraftforge.fml.network.PacketDistributor;

public class ChannelInstanceWrapper {
    public static void send(ServerPlayerWrapper player, StartBreakfallMessage message) {
        ParCool.CHANNEL_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player.getInstance()), message);
    }
}
