package com.alrex.parcool.registrar;

import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.common.handlers.AddAttributesHandler;
import com.alrex.parcool.common.handlers.PlayerEventHandler;
import com.alrex.parcool.common.network.NetworkRegistrar;
import com.alrex.parcool.common.potion.ParCoolBrewingRecipe;
import com.alrex.parcool.common.stamina.StaminaTypes;
import com.alrex.parcool.server.command.CommandRegistry;
import net.neoforged.bus.api.IEventBus;

public class CommonRegistrar {
    public static void registerModLoadingEvent(IEventBus bus) {
        bus.register(AddAttributesHandler.class);
        bus.register(ParCoolActions.class);
        bus.register(StaminaTypes.class);
        bus.register(NetworkRegistrar.class);
        bus.addListener(CommandRegistry::registerArgumentTypes);
    }

    public static void registerGameEvent(IEventBus bus) {
        bus.register(PlayerEventHandler.class);
        bus.register(ParCoolBrewingRecipe.class);
        bus.addListener(CommandRegistry::onRegisterCommand);
    }
}
