package com.alrex.parcool.forge.server.command;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.forge.server.command.args.ActionArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.event.RegisterCommandsEvent;

public class CommandRegistry {
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(ParCool.MOD_ID).then(ActionCapabilitiesCommand.getBuilder())
        );
    }

    public static void registerArgumentTypes() {
        ArgumentTypeInfos.registerByClass(ActionArgumentType.class, SingletonArgumentInfo.contextFree(ActionArgumentType::action));
    }
}
