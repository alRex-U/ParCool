package com.alrex.parcool.proxy;

import com.alrex.parcool.common.handlers.AddAttributesHandler;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.world.entity.player.Player;

public abstract class CommonProxy {
    public abstract void registerMessages();

    public void init() {
        LifecycleEvent.SETUP.register(AddAttributesHandler::addAttributes);
    }

    public void openSkillTreeGui(Player player) {
    }

    public void openGuideGui() {
    }
}
