package com.alrex.parcool.common.event;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
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
            ParCoolConfig.Client.Booleans.ParCoolIsActive.set(!ParCoolConfig.Client.Booleans.ParCoolIsActive.get());
            boolean currentStatus = ParCool.isActive();
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                player.displayClientMessage(new TranslationTextComponent(currentStatus ? "key.parcool.enable.enabled" : "key.parcool.enable.disabled"), true);
            }
        }
    }
}
