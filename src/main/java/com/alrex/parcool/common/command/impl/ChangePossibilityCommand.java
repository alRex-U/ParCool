package com.alrex.parcool.common.command.impl;

import com.alrex.parcool.common.command.args.ActionNameArgument;
import com.alrex.parcool.common.command.args.BooleanArgument;
import com.alrex.parcool.common.network.SetActionPossibilityMessage;
import com.alrex.parcool.constants.ActionsEnum;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentUtils;

public class ChangePossibilityCommand implements Command<CommandSource> {
	private static final ChangePossibilityCommand CMD = new ChangePossibilityCommand();
	private static final String ARGS_NAME_ACTION = "targetAction";
	private static final String ARGS_NAME_POSSIBILITY = "possibility";

	public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
		return Commands
				.literal("changeActionPossibility")
				.requires(commandSource -> commandSource.hasPermissionLevel(0))
				.then(Commands.argument(ARGS_NAME_ACTION, new ActionNameArgument()).then(Commands.argument(ARGS_NAME_POSSIBILITY, new BooleanArgument()).executes(CMD)));
	}

	@Override
	public int run(CommandContext<CommandSource> commandContext) throws CommandSyntaxException {
		boolean possibility = commandContext.getArgument(ARGS_NAME_POSSIBILITY, Boolean.class);
		ActionsEnum actionsEnum = commandContext.getArgument(ARGS_NAME_ACTION, ActionsEnum.class);
		SetActionPossibilityMessage.send(commandContext.getSource().asPlayer(), actionsEnum, possibility);
		commandContext.getSource().sendFeedback(TextComponentUtils.toTextComponent((Message) () -> String.format("%s : %b", actionsEnum.name(), possibility)), false);
		return 0;
	}
}
