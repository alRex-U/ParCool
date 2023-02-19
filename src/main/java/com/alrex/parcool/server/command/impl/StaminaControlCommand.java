package com.alrex.parcool.server.command.impl;

import com.alrex.parcool.common.network.StaminaControlMessage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class StaminaControlCommand {
	private static final String ARGS_NAME_VALUE = "value";
	private static final String ARGS_NAME_PLAYER = "target";

	public static ArgumentBuilder<CommandSource, ?> getBuilder() {
		return (Commands
				.literal("stamina")
				.requires(commandSource -> commandSource.hasPermission(2))
				.then(Commands.literal("set")
						.then(Commands.argument(ARGS_NAME_PLAYER, EntityArgument.player()).then(Commands.argument(ARGS_NAME_VALUE, IntegerArgumentType.integer(1, 99999)).executes(context -> {
							ServerPlayerEntity player = EntityArgument.getPlayer(context, ARGS_NAME_PLAYER);
							StaminaControlMessage.sync(
									player,
									IntegerArgumentType.getInteger(context, ARGS_NAME_VALUE),
									false
							);
							context.getSource().sendSuccess(new StringTextComponent("Set-Stamina operation is requested to " + player.getDisplayName()), false);
							return 0;
						})))
				)
				.then(Commands.literal("add")
						.then(Commands.argument(ARGS_NAME_PLAYER, EntityArgument.player()).then(Commands.argument(ARGS_NAME_VALUE, IntegerArgumentType.integer(1, 99999)).executes(context -> {
							ServerPlayerEntity player = EntityArgument.getPlayer(context, ARGS_NAME_PLAYER);
							StaminaControlMessage.sync(
									player,
									IntegerArgumentType.getInteger(context, ARGS_NAME_VALUE),
									true
							);
							context.getSource().sendSuccess(new StringTextComponent("Add-Stamina operation is requested to " + player.getDisplayName()), false);
							return 0;
						})))
				)
		);
	}

}
