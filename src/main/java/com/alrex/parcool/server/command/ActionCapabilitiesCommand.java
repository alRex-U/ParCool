package com.alrex.parcool.server.command;

import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.server.command.args.ActionArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

public class ActionCapabilitiesCommand {
    private static final String ARG_NAME_ACTION = "action_id";
    private static final String ARG_NAME_PLAYERS = "players";
    private static final String ARG_NAME_PLAYER = "player";

    public static ArgumentBuilder<CommandSourceStack, ?> getBuilder() {
        return Commands.literal("action")
                .then(Commands.literal("unlock").requires(commandSourceStack -> commandSourceStack.hasPermission(2)).then(
                        Commands.argument(ARG_NAME_PLAYERS, EntityArgument.players())
                                .then(Commands.argument(ARG_NAME_ACTION, ActionArgumentType.action()).executes(ActionCapabilitiesCommand::unlock))
                                .then(Commands.literal("all").executes(ActionCapabilitiesCommand::unlockAll))
                        )
                ).then(Commands.literal("lock").requires(commandSourceStack -> commandSourceStack.hasPermission(2)).then(
                        Commands.argument(ARG_NAME_PLAYERS, EntityArgument.players())
                                .then(Commands.argument(ARG_NAME_ACTION, ActionArgumentType.action()).executes(ActionCapabilitiesCommand::lock))
                                .then(Commands.literal("all").executes(ActionCapabilitiesCommand::lockAll))
                        )
                ).then(Commands.literal("status")
                        .then(Commands.literal("enabled")
                                .then(Commands.argument(ARG_NAME_PLAYER, EntityArgument.players())
                                        .then(Commands.argument(ARG_NAME_ACTION, ActionArgumentType.action()).executes(ActionCapabilitiesCommand::getEnabledState))
                                )
                        )
                        .then(Commands.literal("unlocked")
                                .then(Commands.argument(ARG_NAME_PLAYER, EntityArgument.players())
                                        .then(Commands.argument(ARG_NAME_ACTION, ActionArgumentType.action()).executes(ActionCapabilitiesCommand::getUnlockedState))
                                )
                        )
                );
    }

    private static int getUnlockedState(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var action = ActionArgumentType.getAction(context, ARG_NAME_ACTION);
        var player = EntityArgument.getPlayer(context, ARG_NAME_PLAYER);
        var unlocked = Parkourability.get(player).learned(action);
        context.getSource().sendSuccess(
                () -> Component.translatable("parcool.command.success.status.unlocked",
                        player.getDisplayName(),
                        action.id().toString(),
                        Boolean.toString(unlocked)
                ),
                false
        );
        return unlocked ? 1 : 0;
    }

    private static int getEnabledState(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var action = ActionArgumentType.getAction(context, ARG_NAME_ACTION);
        var player = EntityArgument.getPlayer(context, ARG_NAME_PLAYER);
        var enabled = Parkourability.get(player).isEnabledByPlayer(action);
        context.getSource().sendSuccess(
                () -> Component.translatable("parcool.command.success.status.enabled",
                        player.getDisplayName(),
                        action.id().toString(),
                        Boolean.toString(enabled)
                ),
                false
        );
        return enabled ? 1 : 0;
    }

    private static int unlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var action = ActionArgumentType.getAction(context, ARG_NAME_ACTION);
        var players = EntityArgument.getPlayers(context, ARG_NAME_PLAYERS);

        for (var player : players) {
            Parkourability.get(player).getCapabilities().set(action, true);
        }
        context.getSource().sendSuccess(() -> Component.translatable("parcool.command.success.unlock.action", players.size(), action.id()), false);
        return players.size();
    }

    private static int unlockAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var players = EntityArgument.getPlayers(context, ARG_NAME_PLAYERS);

        for (var player : players) {
            Parkourability.get(player).getCapabilities().setAll(true);
        }
        context.getSource().sendSuccess(() -> Component.translatable("parcool.command.success.unlock.all", players.size()), false);
        return players.size();
    }

    private static int lock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var action = ActionArgumentType.getAction(context, ARG_NAME_ACTION);
        var players = EntityArgument.getPlayers(context, ARG_NAME_PLAYERS);

        for (var player : players) {
            Parkourability.get(player).getCapabilities().set(action, false);
        }
        context.getSource().sendSuccess(() -> Component.translatable("parcool.command.success.lock.action", players.size(), action.id()), false);
        return players.size();
    }

    private static int lockAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var players = EntityArgument.getPlayers(context, ARG_NAME_PLAYERS);

        for (var player : players) {
            Parkourability.get(player).getCapabilities().setAll(false);
        }
        context.getSource().sendSuccess(() -> Component.translatable("parcool.command.success.lock.all", players.size()), false);
        return players.size();
    }
}
