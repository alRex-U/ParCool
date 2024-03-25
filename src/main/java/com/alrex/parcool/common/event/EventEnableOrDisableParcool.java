package com.alrex.parcool.common.event;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.network.SyncClientInformationMessage;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class EventEnableOrDisableParcool {
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        if (KeyBindings.getKeyBindEnable().consumeClick()) {
            boolean currentStatus = !ParCoolConfig.Client.Booleans.ParCoolIsActive.get();
            ParCoolConfig.Client.Booleans.ParCoolIsActive.set(currentStatus);
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.displayClientMessage(Component.translatable(currentStatus ? "parcool.message.enabled" : "parcool.message.disabled"), true);
                SyncClientInformationMessage.sync(player, false);
                if (currentStatus) {
                    player.playSound(SoundEvents.PARCOOL_ENABLE.get(), 1.0f, 1.0f);
                } else {
                    player.playSound(SoundEvents.PARCOOL_DISABLE.get(), 1.0f, 1.0f);
                }
            }
        }
    }
}
