package com.alrex.parcool.common.handlers;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.payload.ClientInformationPayload;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.fabric.PacketDistributor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class EnableOrDisableParCoolHandler {
    public static void onTick() {

        if (KeyBindings.getKeyBindEnable().consumeClick()) {
            boolean currentStatus = !ParCoolConfig.Client.Booleans.ParCoolIsActive.get();
            ParCoolConfig.Client.Booleans.ParCoolIsActive.set(currentStatus);
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return;
            parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
            PacketDistributor.sendToServer(
                    new ClientInformationPayload(
                            player.getUUID(), false, parkourability.getClientInfo()));
            player.displayClientMessage(
                    Component.translatable(
                            currentStatus ? "parcool.message.enabled" : "parcool.message.disabled"),
                    true);
            if (currentStatus) {
                player.playSound(SoundEvents.PARCOOL_ENABLE.get(), 1.0f, 1.0f);
            } else {
                player.playSound(SoundEvents.PARCOOL_DISABLE.get(), 1.0f, 1.0f);
            }
        }
    }
}
