package com.alrex.parcool.server.command;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.server.command.args.ActionArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

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

    public static void registerArgumentTypes(FMLCommonSetupEvent event) {
        ArgumentTypeInfos.registerByClass(ActionArgumentType.class, SingletonArgumentInfo.contextFree(ActionArgumentType::action));
    }
}
