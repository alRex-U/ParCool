package com.alrex.parcool.common.handlers;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class EnableOrDisableParCoolHandler {
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        if (KeyBindings.getKeyBindEnable().consumeClick()) {
            ParCoolConfig.Client.Booleans.ParCoolIsActive.set(!ParCoolConfig.Client.Booleans.ParCoolIsActive.get());
            boolean currentStatus = ParCool.isActive();
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) return;
            parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
            SyncClientInformationMessage.sync(player, false);
            player.displayClientMessage(new TranslatableComponent(currentStatus ? "parcool.message.enabled" : "parcool.message.disabled"), true);
            if (currentStatus) {
                player.playSound(SoundEvents.PARCOOL_ENABLE.get(), 1.0f, 1.0f);
            } else {
                player.playSound(SoundEvents.PARCOOL_DISABLE.get(), 1.0f, 1.0f);
            }
        }
    }
}
