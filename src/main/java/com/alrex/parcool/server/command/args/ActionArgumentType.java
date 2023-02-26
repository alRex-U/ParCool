package com.alrex.parcool.server.command.args;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.ActionList;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ActionArgumentType implements ArgumentType<Class<? extends Action>> {
	@Override
	public Class<? extends Action> parse(StringReader reader) throws CommandSyntaxException {
		String name = reader.readUnquotedString();
		Class<? extends Action> result = ActionList.getByName(name);
		if (result == null) {
			Message message = new TranslatableComponent("parcool.command.message.invalidActionName", name);
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
		return result;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String remain = builder.getRemaining();
		for (String name : ActionList.NAMES.stream().filter(it -> it.startsWith(remain)).toList()) {
			builder.suggest(name);
		}
		return builder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return ActionList.NAMES;
	}

	public static ActionArgumentType action() {
		return new ActionArgumentType();
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends Action> getAction(final CommandContext<?> context, final String name) {
		return context.getArgument(name, Class.class);
	}
}
