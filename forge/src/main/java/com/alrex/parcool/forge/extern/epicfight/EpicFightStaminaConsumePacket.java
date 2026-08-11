package com.alrex.parcool.forge.extern.epicfight;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.network.IHandler;
import com.alrex.parcool.forge.extern.AdditionalMods;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record EpicFightStaminaConsumePacket(float value) {
    private record Handler(ResourceLocation id) implements IHandler<EpicFightStaminaConsumePacket> {
        @Override
        public void encode(EpicFightStaminaConsumePacket staminaPacket, FriendlyByteBuf packet) {
            packet.writeFloat(staminaPacket.value);
        }

        @Override
        public EpicFightStaminaConsumePacket decode(FriendlyByteBuf packet) {
            return new EpicFightStaminaConsumePacket(packet.readFloat());
        }

        @Environment(EnvType.SERVER)
        @Override
        public void handleInPhysicalServer(EpicFightStaminaConsumePacket staminaPacket, NetworkManager.PacketContext context) {
            var player = context.getPlayer();
            if (player == null) return;
            var patch = AdditionalMods.epicFight().getPlayerPatch(player);
            if (patch == null) return;
            patch.resetActionTick();
            patch.setStamina(patch.getStamina() - staminaPacket.value);
        }

        @Environment(EnvType.CLIENT)
        @Override
        public void handleInPhysicalClient(EpicFightStaminaConsumePacket staminaPacket, NetworkManager.PacketContext context) {
            var player = context.getPlayer();
            if (player == null) return;
            var patch = AdditionalMods.epicFight().getPlayerPatch(player);
            if (patch == null) return;
            patch.resetActionTick();
            patch.setStamina(patch.getStamina() - staminaPacket.value);
        }
    }

    public static final IHandler<EpicFightStaminaConsumePacket> HANDLER = new Handler(ParCool.resourceLocation("epicfight.stamina"));
}
