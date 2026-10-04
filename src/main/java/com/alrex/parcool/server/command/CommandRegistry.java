package com.alrex.parcool.server.command;

import com.alrex.parcool.ParCool;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class CommandRegistry {
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(ParCool.MOD_ID)
                        .then(ActionCapabilitiesCommand.getBuilder())
        );
    }
}
