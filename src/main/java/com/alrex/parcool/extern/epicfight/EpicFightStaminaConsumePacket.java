package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.common.network.IHandler;
import com.alrex.parcool.extern.AdditionalMods;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record EpicFightStaminaConsumePacket(float value) {
    public static final IHandler<EpicFightStaminaConsumePacket> HANDLER = new IHandler<>() {
        @Override
        public void encode(EpicFightStaminaConsumePacket staminaPacket, FriendlyByteBuf packet) {
            packet.writeFloat(staminaPacket.value);
        }

        @Override
        public EpicFightStaminaConsumePacket decode(FriendlyByteBuf packet) {
            return new EpicFightStaminaConsumePacket(packet.readFloat());
        }

        @OnlyIn(Dist.DEDICATED_SERVER)
        @Override
        public void handleInPhysicalServer(EpicFightStaminaConsumePacket staminaPacket, Supplier<NetworkEvent.Context> contextSupplier) {
            var player = contextSupplier.get().getSender();
            if (player == null) return;
            var patch = AdditionalMods.epicFight().getPlayerPatch(player);
            if (patch == null) return;
            patch.resetActionTick();
            patch.setStamina(patch.getStamina() - staminaPacket.value);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void handleInPhysicalClient(EpicFightStaminaConsumePacket staminaPacket, Supplier<NetworkEvent.Context> contextSupplier) {
            var player = contextSupplier.get().getSender();
            if (player == null) return;
            var patch = AdditionalMods.epicFight().getPlayerPatch(player);
            if (patch == null) return;
            patch.resetActionTick();
            patch.setStamina(patch.getStamina() - staminaPacket.value);
        }
    };
}
