package com.alrex.parcool.common.handlers;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.payload.ClientInformationPayload;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class EnableOrDisableParCoolHandler {
    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {

        if (KeyBindings.getKeyBindEnable().consumeClick()) {
            boolean currentStatus = !ParCoolConfig.Client.Booleans.ParCoolIsActive.get();
            ParCoolConfig.Client.Booleans.ParCoolIsActive.set(currentStatus);
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return;
            parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
            PacketDistributor.sendToServer(new ClientInformationPayload(player.getUUID(), false, parkourability.getClientInfo()));
            player.displayClientMessage(Component.translatable(currentStatus ? "parcool.message.enabled" : "parcool.message.disabled"), true);
            if (currentStatus) {
                player.playSound(SoundEvents.PARCOOL_ENABLE.get(), 1.0f, 1.0f);
            } else {
                player.playSound(SoundEvents.PARCOOL_DISABLE.get(), 1.0f, 1.0f);
            }
        }
    }
}
