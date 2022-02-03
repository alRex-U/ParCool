package com.alrex.parcool.common.command.impl;

import com.alrex.parcool.common.command.args.BooleanArgument;
import com.alrex.parcool.common.network.SetActionPossibilityMessage;
import com.alrex.parcool.constants.ActionsEnum;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class SetInfiniteStaminaCommand {
	private static final String ARGS_NAME_ENABLE = "enable";

	public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
		return Commands
				.literal("infiniteStamina")
				.requires(commandSource -> commandSource.hasPermissionLevel(0))
				.then(Commands.argument(ARGS_NAME_ENABLE, new BooleanArgument())
						.executes(SetInfiniteStaminaCommand::enableInfiniteStamina));
	}

	public static int enableInfiniteStamina(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
		boolean value = commandContext.getArgument(ARGS_NAME_ENABLE, Boolean.class);
		SetActionPossibilityMessage.send(commandContext.getSource().asPlayer(), ActionsEnum.InfiniteStamina, value);
		commandContext.getSource().sendFeedback(new StringTextComponent(String.format("Infinite Stamina : %b", value)), false);
		return 0;
	}

}
