package com.alrex.parcool.server.command.impl;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.Limitations;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.command.args.ActionArgumentType;
import com.alrex.parcool.server.command.args.LimitationItemArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

public class ChangeIndividualLimitationCommand {
	private static final String ARGS_NAME_PLAYERS = "targets";
	private static final String ARGS_NAME_PLAYER = "target";
	private static final String ARGS_NAME_ACTION = "action";
	private static final String ARGS_NAME_STAMINA_CONSUMPTION = "stamina_consumption";
	private static final String ARGS_NAME_POSSIBILITY = "possibility";
	private static final String ARGS_NAME_VALUE = "value";
	private static final String ARGS_NAME_CONFIG_ITEM = "limitation_name";

	public static ArgumentBuilder<CommandSource, ?> getBuilder() {
		return Commands
				.literal("limitation")
				.requires(commandSource -> commandSource.hasPermission(2))
				.then(Commands
						.literal("enable")
						.then(Commands
								.argument(ARGS_NAME_PLAYERS, EntityArgument.players())
								.executes(ChangeIndividualLimitationCommand::enableLimitation))
				)
				.then(Commands
						.literal("disable")
						.then(Commands
								.argument(ARGS_NAME_PLAYERS, EntityArgument.players())
								.executes(ChangeIndividualLimitationCommand::disableLimitation))
				)
				.then(Commands
						.literal("set")
						.then(Commands
								.argument(ARGS_NAME_PLAYERS, EntityArgument.players())
								.then(Commands
										.literal("to_default")
										.executes(ChangeIndividualLimitationCommand::setLimitationDefault)
								)
								.then(Commands
										.literal("boolean")
										.then(Commands
												.argument(ARGS_NAME_CONFIG_ITEM, LimitationItemArgumentType.booleans())
												.then(Commands
														.argument(ARGS_NAME_VALUE, BoolArgumentType.bool())
														.executes(ChangeIndividualLimitationCommand::setBoolLimitation)
												)
										)
								)
								.then(Commands
										.literal("integer")
										.then(Commands
												.argument(ARGS_NAME_CONFIG_ITEM, LimitationItemArgumentType.integers())
												.then(Commands
														.argument(ARGS_NAME_VALUE, IntegerArgumentType.integer())
														.executes(ChangeIndividualLimitationCommand::setIntLimitation)
												)
										)
								)
								.then(Commands
										.literal("reals")
										.then(Commands
												.argument(ARGS_NAME_CONFIG_ITEM, LimitationItemArgumentType.doubles())
												.then(Commands
														.argument(ARGS_NAME_VALUE, DoubleArgumentType.doubleArg())
														.executes(ChangeIndividualLimitationCommand::setDoubleLimitation)
												)
										)
								)
								.then(Commands
										.literal("possibility")
										.then(Commands
												.argument(ARGS_NAME_ACTION, ActionArgumentType.action())
												.then(Commands
														.argument(ARGS_NAME_POSSIBILITY, BoolArgumentType.bool())
														.executes(ChangeIndividualLimitationCommand::changePossibilityOfAction)
												)
										)
								)
								.then(Commands
										.literal("least_stamina_consumption")
										.then(Commands
												.argument(ARGS_NAME_ACTION, ActionArgumentType.action())
												.then(Commands
														.argument(ARGS_NAME_STAMINA_CONSUMPTION, IntegerArgumentType.integer(0, Integer.MAX_VALUE))
														.executes(ChangeIndividualLimitationCommand::changeStaminaConsumption)
												)
										)
								)
						)
				)
				.then(Commands
						.literal("get")
						.then(Commands
								.argument(ARGS_NAME_PLAYER, EntityArgument.player())
								.then(Commands
										.literal("max_stamina")
										.executes((it) -> ChangeIndividualLimitationCommand.getLimitationValue(it, 0))
								)
								.then(Commands
										.literal("possibility")
										.then(Commands
												.literal("infinite_stamina")
												.executes((it) -> ChangeIndividualLimitationCommand.getLimitationValue(it, 1))
										)
										.then(Commands
												.argument(ARGS_NAME_ACTION, ActionArgumentType.action())
												.executes((it) -> ChangeIndividualLimitationCommand.getLimitationValue(it, 2))
										)
								)
								.then(Commands
										.literal("least_stamina_consumption")
										.then(Commands
												.argument(ARGS_NAME_ACTION, ActionArgumentType.action())
												.executes((it) -> ChangeIndividualLimitationCommand.getLimitationValue(it, 3))
										)
								)
						)
				);
	}

	private static int getLimitationValue(CommandContext<CommandSource> context, int code) throws CommandSyntaxException {
		ServerPlayerEntity player = EntityArgument.getPlayer(context, ARGS_NAME_PLAYER);
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) {
			context.getSource().sendFailure(new StringTextComponent("§4[Internal Error] Parkourability is null"));
			return 1;
		}
		Class<? extends Action> action;
		switch (code) {
			case 0:
				context.getSource().sendSuccess(new StringTextComponent(Integer.toString(parkourability.getActionInfo().getIndividualLimitation().get(ParCoolConfig.Server.Integers.MaxStaminaLimit))), false);
				break;
			case 1:
				context.getSource().sendSuccess(new StringTextComponent(Boolean.toString(parkourability.getActionInfo().getIndividualLimitation().isInfiniteStaminaPermitted())), false);
				break;
			case 2:
				action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
				context.getSource().sendSuccess(new StringTextComponent(Boolean.toString(parkourability.getActionInfo().getIndividualLimitation().isPermitted(action))), false);
				break;
			case 3:
				action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
				context.getSource().sendSuccess(new StringTextComponent(Integer.toString(parkourability.getActionInfo().getIndividualLimitation().getLeastStaminaConsumption(action))), false);
				break;
			default:
				return 1;
		}
		return 0;
	}

	private static int setLimitationDefault(CommandContext<CommandSource> context) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		int num = 0;
		for (ServerPlayerEntity player : targets) {
			Limitations.Changer.get(player)
					.setAllDefault()
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.setLimitationToDefault", num), true);
		return 0;
	}

	private static int setBoolLimitation(CommandContext<CommandSource> context) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		ParCoolConfig.Server.Booleans item = LimitationItemArgumentType.getBool(context, ARGS_NAME_CONFIG_ITEM);
		boolean value = BoolArgumentType.getBool(context, ARGS_NAME_VALUE);
		int num = 0;
		for (ServerPlayerEntity player : targets) {
			Limitations.Changer.get(player)
					.set(item, value)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.set", num, item.getPath(), Boolean.toString(value)), true);
		return 0;
	}

	private static int setIntLimitation(CommandContext<CommandSource> context) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		ParCoolConfig.Server.Integers item = LimitationItemArgumentType.getInt(context, ARGS_NAME_CONFIG_ITEM);
		int value = IntegerArgumentType.getInteger(context, ARGS_NAME_VALUE);
		if (value < item.Min) {
			value = item.Min;
		}
		if (value > item.Max) {
			value = item.Max;
		}
		int num = 0;
		for (ServerPlayerEntity player : targets) {
			Limitations.Changer.get(player)
					.set(item, value)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.set", num, item.getPath(), Integer.toString(value)), true);
		return 0;
	}

	private static int setDoubleLimitation(CommandContext<CommandSource> context) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		ParCoolConfig.Server.Doubles item = LimitationItemArgumentType.getDouble(context, ARGS_NAME_CONFIG_ITEM);
		double value = DoubleArgumentType.getDouble(context, ARGS_NAME_VALUE);
		if (value < item.Min) {
			value = item.Min;
		}
		if (value > item.Max) {
			value = item.Max;
		}
		int num = 0;
		for (ServerPlayerEntity player : targets) {
			Limitations.Changer.get(player)
					.set(item, value)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.set", num, item.getPath(), Double.toString(value)), true);
		return 0;
	}

	private static int enableLimitation(CommandContext<CommandSource> context) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		int num = 0;
		for (ServerPlayerEntity player : targets) {
			Limitations.Changer.get(player)
					.setEnabled(true)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.enableLimitation", num), true);
		return 0;
	}

	private static int disableLimitation(CommandContext<CommandSource> context) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		int num = 0;
		for (ServerPlayerEntity player : targets) {
			Limitations.Changer.get(player)
					.setEnabled(false)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.disableLimitation", num), true);
		return 0;
	}

	private static int changeStaminaConsumption(CommandContext<CommandSource> context) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
		int newValue = IntegerArgumentType.getInteger(context, ARGS_NAME_STAMINA_CONSUMPTION);
		int num = 0;
		for (ServerPlayerEntity player : targets) {
			Limitations.Changer.get(player)
					.setLeastStaminaConsumption(action, newValue)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.setStaminaConsumption", num, action.getSimpleName(), newValue), true);
		return 0;
	}

	private static int changePossibilityOfAction(CommandContext<CommandSource> context) throws CommandSyntaxException {
		Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
		boolean newValue = BoolArgumentType.getBool(context, ARGS_NAME_POSSIBILITY);
		int num = 0;
		for (ServerPlayerEntity player : targets) {
			Limitations.Changer.get(player)
					.setPossibilityOf(action, newValue)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.setPermissionOfAction", num, action.getSimpleName(), newValue), true);
		return 0;
	}
}
