package com.alrex.parcool.common.processor;

import com.alrex.parcool.common.capability.IStamina;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StaminaLogic {
    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event){
        if (event.phase != TickEvent.Phase.END || event.player != Minecraft.getInstance().player)return;
        ClientPlayerEntity player = Minecraft.getInstance().player;

        IStamina stamina;
        {
            LazyOptional<IStamina> staminaOptional = player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
            if (!staminaOptional.isPresent()) return;
            stamina = staminaOptional.resolve().get();
        }

        if (stamina.getRecoveryCoolTime()<=0)stamina.recover(stamina.getMaxStamina()/100);
        stamina.updateRecoveryCoolTime();

        if (stamina.isExhausted()){
            player.setSprinting(false);
        }
    }
}
