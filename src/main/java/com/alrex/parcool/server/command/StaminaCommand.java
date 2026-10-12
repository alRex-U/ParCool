package com.alrex.parcool.server.command;

import com.alrex.parcool.common.Parkourability;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

public class StaminaCommand {
    private static final String ARG_NAME_PLAYER = "player";

    public static ArgumentBuilder<CommandSourceStack, ?> getBuilder() {
        return Commands.literal("stamina")
                .then(Commands.literal("get").requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(Commands.literal("value")
                                .then(Commands.argument(ARG_NAME_PLAYER, EntityArgument.players()).executes(StaminaCommand::getValue))
                        )
                        .then(Commands.literal("depletion")
                                .then(Commands.argument(ARG_NAME_PLAYER, EntityArgument.players()).executes(StaminaCommand::getDepleted))
                        )
                );
    }

    private static int getValue(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, ARG_NAME_PLAYER);
        var value = Parkourability.get(player).getStamina().value();
        context.getSource().sendSuccess(Component.translatable("parcool.command.success.stamina.get", player.getDisplayName(), value), false);
        return (int) value;
    }

    private static int getDepleted(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(context, ARG_NAME_PLAYER);
        var value = Parkourability.get(player).getStamina().isExhausted();
        context.getSource().sendSuccess(Component.translatable("parcool.command.success.stamina.depletion", player.getDisplayName(), Boolean.toString(value)), false);
        return value ? 1 : 0;
    }
}
