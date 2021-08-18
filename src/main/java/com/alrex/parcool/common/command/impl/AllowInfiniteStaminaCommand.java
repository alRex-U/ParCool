package com.alrex.parcool.common.command.impl;

import com.alrex.parcool.common.command.args.BooleanArgument;
import com.alrex.parcool.common.network.DisableInfiniteStaminaMessage;
import com.alrex.parcool.constants.TranslateKeys;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

public class AllowInfiniteStaminaCommand {
	private static final String ARGS_NAME_TARGET = "target";
	private static final String ARGS_NAME_ALLOWED = "isAllowed";

	public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
		return Commands
				.literal("allowInfiniteStamina")
				.requires(commandSource -> commandSource.hasPermissionLevel(4))
				.then(Commands.argument(ARGS_NAME_TARGET, EntityArgument.players())
						.then(Commands.argument(ARGS_NAME_ALLOWED, new BooleanArgument())
								.executes((context -> {
									return allowInfiniteStamina(context, EntityArgument.getPlayers(context, ARGS_NAME_TARGET));
								}))));
	}

	public static int allowInfiniteStamina(CommandContext<CommandSource> commandContext, Collection<ServerPlayerEntity> players) {
		boolean isAllowed = commandContext.getArgument(ARGS_NAME_ALLOWED, Boolean.class);
		for (ServerPlayerEntity player : players) {
			DisableInfiniteStaminaMessage.send(player, isAllowed);
		}
		commandContext.getSource().sendFeedback(
				new TranslationTextComponent(isAllowed ? TranslateKeys.COMMAND_SERVER_ALLOW_INFINITE_STAMINA : TranslateKeys.COMMAND_SERVER_NOT_ALLOW_INFINITE_STAMINA)
				, false);
		return 0;
	}
}
