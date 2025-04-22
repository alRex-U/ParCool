package com.alrex.parcool.server.command.impl;

import com.alrex.parcool.api.unstable.Limitation;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.command.args.ActionArgumentType;
import com.alrex.parcool.server.command.args.LimitationIDArgumentType;
import com.alrex.parcool.server.command.args.LimitationItemArgumentType;
import com.alrex.parcool.server.limitation.Limitations;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ControlLimitationCommand {
    private static final String ARGS_NAME_PLAYERS = "targets";
    private static final String ARGS_NAME_PLAYER = "target";
    private static final String ARGS_NAME_ACTION = "action";
    private static final String ARGS_NAME_STAMINA_CONSUMPTION = "stamina_consumption";
    private static final String ARGS_NAME_POSSIBILITY = "possibility";
    private static final String ARGS_NAME_VALUE = "value";
    private static final String ARGS_NAME_CONFIG_ITEM = "limitation_name";
    private static final String ARGS_NAME_LIMITATION_ID = "limitation_id";

    private static ArgumentBuilder<CommandSource, ?> limitationGetCoreCommands(ArgumentBuilder<CommandSource, ?> builder, boolean hasID, boolean hasPlayer) {
        return builder
                .then(Commands
                        .literal("boolean")
                        .then(Commands
                                .argument(ARGS_NAME_CONFIG_ITEM, LimitationItemArgumentType.booleans())
                                .executes((context) -> getBoolLimitation(context, hasID, hasPlayer))
                        )
                )
                .then(Commands
                        .literal("integer")
                        .then(Commands
                                .argument(ARGS_NAME_CONFIG_ITEM, LimitationItemArgumentType.integers())
                                .executes((context) -> getIntLimitation(context, hasID, hasPlayer))
                        )
                )
                .then(Commands
                        .literal("reals")
                        .then(Commands
                                .argument(ARGS_NAME_CONFIG_ITEM, LimitationItemArgumentType.doubles())
                                .executes((context) -> getDoubleLimitation(context, hasID, hasPlayer))
                        )
                )
                .then(Commands
                        .literal("possibility")
                        .then(Commands
                                .argument(ARGS_NAME_ACTION, ActionArgumentType.action())
                                .executes((context) -> getActionPossibility(context, hasID, hasPlayer))
                        )
                )
                .then(Commands
                        .literal("least_stamina_consumption")
                        .then(Commands
                                .argument(ARGS_NAME_ACTION, ActionArgumentType.action())
                                .executes((context) -> getLeastStaminaConsumption(context, hasID, hasPlayer))
                        )
                );
    }

    private static ArgumentBuilder<CommandSource, ?> limitationSetCoreCommands(ArgumentBuilder<CommandSource, ?> builder, boolean hasID, boolean hasPlayer) {
        return builder
                .then(Commands
                        .literal("to_default")
                        .executes((context) -> setLimitationDefault(context, hasID, hasPlayer))
                )
                .then(Commands
                        .literal("boolean")
                        .then(Commands
                                .argument(ARGS_NAME_CONFIG_ITEM, LimitationItemArgumentType.booleans())
                                .then(Commands
                                        .argument(ARGS_NAME_VALUE, BoolArgumentType.bool())
                                        .executes((context) -> setBoolLimitation(context, hasID, hasPlayer))
                                )
                        )
                )
                .then(Commands
                        .literal("integer")
                        .then(Commands
                                .argument(ARGS_NAME_CONFIG_ITEM, LimitationItemArgumentType.integers())
                                .then(Commands
                                        .argument(ARGS_NAME_VALUE, IntegerArgumentType.integer())
                                        .executes((context) -> setIntLimitation(context, hasID, hasPlayer))
                                )
                        )
                )
                .then(Commands
                        .literal("reals")
                        .then(Commands
                                .argument(ARGS_NAME_CONFIG_ITEM, LimitationItemArgumentType.doubles())
                                .then(Commands
                                        .argument(ARGS_NAME_VALUE, DoubleArgumentType.doubleArg())
                                        .executes((context) -> setDoubleLimitation(context, hasID, hasPlayer))
                                )
                        )
                )
                .then(Commands
                        .literal("possibility")
                        .then(Commands
                                .argument(ARGS_NAME_ACTION, ActionArgumentType.action())
                                .then(Commands
                                        .argument(ARGS_NAME_POSSIBILITY, BoolArgumentType.bool())
                                        .executes((context) -> changePossibilityOfAction(context, hasID, hasPlayer))
                                )
                        )
                )
                .then(Commands
                        .literal("least_stamina_consumption")
                        .then(Commands
                                .argument(ARGS_NAME_ACTION, ActionArgumentType.action())
                                .then(Commands
                                        .argument(ARGS_NAME_STAMINA_CONSUMPTION, IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes((context) -> changeStaminaConsumption(context, hasID, hasPlayer))
                                )
                        )
                );
    }

    private static ArgumentBuilder<CommandSource, ?> getLimitationByNameCommands(boolean multiPlayer, Function<RequiredArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> afterCommand) {
        if (multiPlayer) {
            return Commands.argument(ARGS_NAME_LIMITATION_ID, LimitationIDArgumentType.limitation())
                    .then(Commands
                            .literal("of")
                            .then(
                                    afterCommand.apply(Commands.argument(ARGS_NAME_PLAYERS, EntityArgument.players()))
                            )
                    );
        } else {
            return Commands.argument(ARGS_NAME_LIMITATION_ID, LimitationIDArgumentType.limitation())
                    .then(Commands
                            .literal("of")
                            .then(
                                    afterCommand.apply(Commands.argument(ARGS_NAME_PLAYER, EntityArgument.player()))
                            )
                    );
        }
    }

    private static ArgumentBuilder<CommandSource, ?> getIndividualLimitationCommands(boolean multiPlayer, Function<RequiredArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> afterCommand) {
        if (multiPlayer) {
            return Commands.literal("individual")
                    .then(Commands.literal("of")
                            .then(
                                    afterCommand.apply(Commands.argument(ARGS_NAME_PLAYERS, EntityArgument.players()))
                            )
                    );
        } else {
            return Commands.literal("individual")
                    .then(Commands.literal("of")
                            .then(
                                    afterCommand.apply(Commands.argument(ARGS_NAME_PLAYER, EntityArgument.player()))
                            )
                    );
        }
    }

    private static ArgumentBuilder<CommandSource, ?> getGlobalLimitationCommands(Function<LiteralArgumentBuilder<CommandSource>, ArgumentBuilder<CommandSource, ?>> afterCommand) {
        return afterCommand.apply(Commands.literal("global"));
    }

    public static ArgumentBuilder<CommandSource, ?> getBuilder() {
        return Commands
                .literal("limitation")
                .then(Commands
                        .literal("get")
                        .then(
                                getLimitationByNameCommands(false, (it) -> {
                                    it.executes((context) -> getLimitationInfo(context, true, true));
                                    limitationGetCoreCommands(it, true, true);
                                    return it;
                                })
                        )
                        .then(
                                getIndividualLimitationCommands(false, (it) -> {
                                    it.executes((context) -> getLimitationInfo(context, false, true));
                                    limitationGetCoreCommands(it, false, true);
                                    return it;
                                })
                        )
                        .then(
                                getGlobalLimitationCommands((it) -> {
                                    it.executes((context) -> getLimitationInfo(context, false, false));
                                    limitationGetCoreCommands(it, false, false);
                                    return it;
                                })
                        )
                )
                .then(Commands
                        .literal("set")
                        .requires(commandSource -> commandSource.hasPermission(2))
                        .then(
                                getLimitationByNameCommands(true, (it) -> {
                                    limitationSetCoreCommands(it, true, true);
                                    return it;
                                })
                        )
                        .then(
                                getIndividualLimitationCommands(true, (it) -> {
                                    limitationSetCoreCommands(it, false, true);
                                    return it;
                                })
                        )
                )
                .then(Commands
                        .literal("enable")
                        .requires(commandSource -> commandSource.hasPermission(2))
                        .then(
                                getLimitationByNameCommands(true, (it) -> {
                                    it
                                            .executes((context) -> enableLimitation(context, true, true));
                                    return it;
                                })
                        )
                        .then(
                                getIndividualLimitationCommands(true, (it) -> {
                                    it
                                            .requires(commandSource -> commandSource.hasPermission(2))
                                            .executes((context) -> enableLimitation(context, false, true));
                                    return it;
                                })
                        )
                )
                .then(Commands
                        .literal("disable")
                        .requires(commandSource -> commandSource.hasPermission(2))
                        .then(
                                getLimitationByNameCommands(true, (it) -> {
                                    it
                                            .executes((context) -> disableLimitation(context, true, true));
                                    return it;
                                })
                        )
                        .then(
                                getIndividualLimitationCommands(true, (it) -> {
                                    it
                                            .executes((context) -> disableLimitation(context, false, true));
                                    return it;
                                })
                        )
                )
                .then(Commands
                        .literal("delete")
                        .requires(commandSource -> commandSource.hasPermission(2))
                        .then(Commands
                                .argument(ARGS_NAME_LIMITATION_ID, LimitationIDArgumentType.limitation())
                                .executes(ControlLimitationCommand::deleteLimitation)
                        )
                );
    }

    private static List<Limitation> getLimitationInstance(Collection<ServerPlayerEntity> players, @Nullable Limitation.ID id, @Nullable MinecraftServer server) {
        if (players.isEmpty()) {
            if (server != null) {// global limitation
                return Collections.singletonList(Limitation.getGlobal(server));
            } else if (id == null) {
                // limitation for all players
                // not implemented
            }
        } else if (id != null) {// limitation
            LinkedList<Limitation> list = new LinkedList<>();
            for (ServerPlayerEntity player : players) {
                list.add(Limitation.get(player, id));
            }
            return list;
        } else {//individual limitation
            LinkedList<Limitation> list = new LinkedList<>();
            for (ServerPlayerEntity player : players) {
                list.add(Limitation.getIndividual(player));
            }
            return list;
        }
        return Collections.emptyList();
    }

    private static int getBoolLimitation(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, ARGS_NAME_PLAYER)) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        ParCoolConfig.Server.Booleans item = LimitationItemArgumentType.getBool(context, ARGS_NAME_CONFIG_ITEM);
        context.getSource().sendSuccess(
                new StringTextComponent(
                        Boolean.toString(limitations.get(0).get(item))
                ),
                false
        );
        return 0;
    }

    private static int getIntLimitation(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, ARGS_NAME_PLAYER)) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        ParCoolConfig.Server.Integers item = LimitationItemArgumentType.getInt(context, ARGS_NAME_CONFIG_ITEM);
        context.getSource().sendSuccess(
                new StringTextComponent(
                        Integer.toString(limitations.get(0).get(item))
                ),
                false
        );
        return 0;
    }

    private static int getDoubleLimitation(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, ARGS_NAME_PLAYER)) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        ParCoolConfig.Server.Doubles item = LimitationItemArgumentType.getDouble(context, ARGS_NAME_CONFIG_ITEM);
        context.getSource().sendSuccess(
                new StringTextComponent(
                        Double.toString(limitations.get(0).get(item))
                ),
                false
        );
        return 0;
    }

    private static int getActionPossibility(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, ARGS_NAME_PLAYER)) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
        context.getSource().sendSuccess(
                new StringTextComponent(
                        Boolean.toString(limitations.get(0).isPermitted(action))
                ),
                false
        );
        return 0;
    }

    private static int getLimitationInfo(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, ARGS_NAME_PLAYER)) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        Limitation limitation = limitations.get(0);
        StringBuilder builder = new StringBuilder();
        builder.append("- Limitation Info -\n");
        builder.append("Enabled : ").append(limitation.isEnabled()).append('\n');
        for (Class<? extends Action> action : ActionList.ACTIONS) {
            builder.append("  ").append(action.getSimpleName()).append(" : ").append('\n')
                    .append("    ").append("permitted : ").append(limitation.isPermitted(action)).append('\n')
                    .append("    ").append("stamina consumption : ").append(limitation.getLeastStaminaConsumption(action)).append('\n');
        }
        for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
            builder.append("  ").append(item.getPath()).append(" : ").append(limitation.get(item)).append('\n');
        }
        for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
            builder.append("  ").append(item.getPath()).append(" : ").append(limitation.get(item)).append('\n');
        }
        for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
            builder.append("  ").append(item.getPath()).append(" : ").append(limitation.get(item)).append('\n');
        }
        builder.append("----------");
        context.getSource().sendSuccess(
                new StringTextComponent(
                        builder.toString()
                ),
                false
        );
        return 0;
    }

    private static int getLeastStaminaConsumption(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, ARGS_NAME_PLAYER)) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
        context.getSource().sendSuccess(
                new StringTextComponent(
                        Integer.toString(limitations.get(0).getLeastStaminaConsumption(action))
                ),
                false
        );
        return 0;
    }

    private static int setLimitationDefault(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.setDefault().apply();
            limitation.save();
            num++;
        }
        context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.setLimitationToDefault", num), true);
        return 0;
    }

    private static int setBoolLimitation(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        ParCoolConfig.Server.Booleans item = LimitationItemArgumentType.getBool(context, ARGS_NAME_CONFIG_ITEM);
        boolean value = BoolArgumentType.getBool(context, ARGS_NAME_VALUE);
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.set(item, value).apply();
            limitation.save();
            num++;
        }
        context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.set", num, item.getPath(), Boolean.toString(value)), true);
        return 0;
    }

    private static int setIntLimitation(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        ParCoolConfig.Server.Integers item = LimitationItemArgumentType.getInt(context, ARGS_NAME_CONFIG_ITEM);
        int value = IntegerArgumentType.getInteger(context, ARGS_NAME_VALUE);
        if (value < item.Min) {
            value = item.Min;
        }
        if (value > item.Max) {
            value = item.Max;
        }
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.set(item, value).apply();
            limitation.save();
            num++;
        }
        context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.set", num, item.getPath(), Integer.toString(value)), true);
        return 0;
    }

    private static int setDoubleLimitation(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        ParCoolConfig.Server.Doubles item = LimitationItemArgumentType.getDouble(context, ARGS_NAME_CONFIG_ITEM);
        double value = DoubleArgumentType.getDouble(context, ARGS_NAME_VALUE);
        if (value < item.Min) {
            value = item.Min;
        }
        if (value > item.Max) {
            value = item.Max;
        }
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.set(item, value).apply();
            limitation.save();
            num++;
        }
        context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.set", num, item.getPath(), Double.toString(value)), true);
        return 0;
    }

    private static int enableLimitation(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.enable().apply();
            limitation.save();
            num++;
        }
        context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.enableLimitation", num), true);
        return 0;
    }

    private static int disableLimitation(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.disable().apply();
            limitation.save();
            num++;
        }
        context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.disableLimitation", num), true);
        return 0;
    }

    private static int deleteLimitation(CommandContext<CommandSource> context) throws CommandSyntaxException {
        Limitation.ID limitation = LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID);
        if (Limitation.delete(limitation)) {
            for (ServerPlayerEntity player : context.getSource().getServer().getPlayerList().getPlayers()) {
                Limitations.updateOnlyLimitation(player);
            }
            context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.deleteLimitation", limitation.toString()), true);
        } else {
            context.getSource().sendFailure(new StringTextComponent("Error:deleting folder failed"));
        }
        return 0;
    }

    private static int changeStaminaConsumption(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
        int newValue = IntegerArgumentType.getInteger(context, ARGS_NAME_STAMINA_CONSUMPTION);
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.setLeastStaminaConsumption(action, newValue).apply();
            limitation.save();
            num++;
        }
        context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.setStaminaConsumption", num, action.getSimpleName(), newValue), true);
        return 0;
    }

    private static int changePossibilityOfAction(CommandContext<CommandSource> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = getLimitationInstance(
                hasPlayer ? EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS) : Collections.emptyList(),
                hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null,
                context.getSource().getServer()
        );
        Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
        boolean newValue = BoolArgumentType.getBool(context, ARGS_NAME_POSSIBILITY);
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.permit(action, newValue).apply();
            limitation.save();
            num++;
        }
        context.getSource().sendSuccess(new TranslationTextComponent("parcool.command.message.success.setPermissionOfAction", num, action.getSimpleName(), newValue), true);
        return 0;
    }
}
