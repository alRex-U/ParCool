package com.alrex.parcool.common.network;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.stamina.ReadonlyStamina;
import com.alrex.parcool.util.NetworkUtil;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.Env;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record StaminaPacket(UUID playerID, boolean fromClient, ReadonlyStamina stamina) {
    private record Handler(ResourceLocation id) implements IHandler<StaminaPacket> {
        @Override
        public void encode(StaminaPacket staminaPacket, FriendlyByteBuf packet) {
            packet.writeUUID(staminaPacket.playerID);
            packet.writeBoolean(staminaPacket.fromClient);
            packet.writeDouble(staminaPacket.stamina.value());
            packet.writeDouble(staminaPacket.stamina.max());
            packet.writeBoolean(staminaPacket.stamina.isExhausted());
            packet.writeBoolean(staminaPacket.stamina.imposePenalty());
        }

        @Override
        public StaminaPacket decode(FriendlyByteBuf packet) {
            return new StaminaPacket(
                    packet.readUUID(),
                    packet.readBoolean(),
                    new ReadonlyStamina(packet.readDouble(), packet.readDouble(), packet.readBoolean(), packet.readBoolean())
            );
        }

        @Environment(EnvType.SERVER)
        @Override
        public void handleInPhysicalServer(StaminaPacket staminaPacket, NetworkManager.PacketContext context) {
            var player = NetworkUtil.getPlayerInPhysicalServer(staminaPacket.playerID, context);
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.updateStaminaInRemote(staminaPacket.stamina);

            ParCool.getActionProcessor().getStaminaSyncDepot().requestSync(player.getUUID(), staminaPacket.stamina);
        }

        @Environment(EnvType.CLIENT)
        @Override
        public void handleInPhysicalClient(StaminaPacket staminaPacket, NetworkManager.PacketContext context) {
            var player = NetworkUtil.getPlayerInPhysicalClient(staminaPacket.playerID, context, staminaPacket.fromClient);
            if (player == null) return;
            var parkourability = Parkourability.get(player);
            parkourability.updateStaminaInRemote(staminaPacket.stamina);

            if (context.getEnvironment() == Env.SERVER) {
                ParCool.getActionProcessor().getStaminaSyncDepot().requestSync(player.getUUID(), staminaPacket.stamina);
            }
        }
    }

    public static final IHandler<StaminaPacket> HANDLER = new Handler(ParCool.resourceLocation("stamina"));
}
