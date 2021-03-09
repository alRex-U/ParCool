package com.alrex.parcool.client.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.common.Mod;


@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KeyRecorder {
    public static KeyState keyJumpState=new KeyState();
    public static KeyState keySprintState=new KeyState();
    public static KeyState keyCrawlState=new KeyState();
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){
        if (event.phase!= TickEvent.Phase.START)return;

        record(KeyBindings.getKeyJump(),keyJumpState);
        record(KeyBindings.getKeySprint(),keySprintState);
        record(KeyBindings.getKeyCrawl(),keyCrawlState);
    }
    private static void record(KeyBinding keyBinding,KeyState state){
        state.pressed= (keyBinding.isKeyDown() && state.tickKeyDown == 0);
        if (keyBinding.isKeyDown()){ state.tickKeyDown++; }else { state.tickKeyDown=0; }
    }

    public static class KeyState{
        private boolean pressed=false;
        private int tickKeyDown=0;

        public boolean isPressed() { return pressed; }
        public int getTickKeyDown() { return tickKeyDown; }
    }
}
