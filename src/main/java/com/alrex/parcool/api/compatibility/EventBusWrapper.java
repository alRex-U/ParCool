package com.alrex.parcool.api.compatibility;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.Action;

import net.minecraftforge.common.MinecraftForge;

public class EventBusWrapper {
    public static boolean tryToStartEvent(PlayerWrapper player, Action action) {
        return MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStartEvent(player, action));
    }
    
    public static boolean tryToContinueEvent(PlayerWrapper player, Action action) {
        return MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.TryToContinueEvent(player, action));
    }
    
    public static boolean startEvent(PlayerWrapper player, Action action) {
        return MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.StartEvent(player, action));
    }
    
    public static boolean stopEvent(PlayerWrapper player, Action action) {
        return MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.StopEvent(player, action));
    }
}
