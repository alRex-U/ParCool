package com.alrex.parcool.server.command.args;

import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LimitationItemArgumentType<V, T extends Enum<T> & ParCoolConfig.Item<V>> implements ArgumentType<T> {
	private final List<String> paths;
	private final T[] enumConstants;

	@Override
	public T parse(StringReader reader) throws CommandSyntaxException {
		String name = reader.readUnquotedString();
		int index = paths.indexOf(name);
		if (index == -1) {
			Message message = new TranslatableComponent("parcool.command.message.invalidConfigName", name);
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
		return enumConstants[index];
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String remain = builder.getRemaining();
		for (String name : paths.stream().filter(it -> it.startsWith(remain)).collect(Collectors.toList())) {
			builder.suggest(name);
		}
		return builder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return paths;
	}

	LimitationItemArgumentType(Class<T> clazz) {
		enumConstants = clazz.getEnumConstants();
		paths = new ArrayList<>(enumConstants.length);
		for (T enumConstant : enumConstants) {
			paths.add(enumConstant.getPath());
		}
	}

	public static LimitationItemArgumentType<Boolean, ParCoolConfig.Server.Booleans> booleans() {
		return new LimitationItemArgumentType<>(ParCoolConfig.Server.Booleans.class);
	}

	public static LimitationItemArgumentType<Integer, ParCoolConfig.Server.Integers> integers() {
		return new LimitationItemArgumentType<>(ParCoolConfig.Server.Integers.class);
	}

	public static LimitationItemArgumentType<Double, ParCoolConfig.Server.Doubles> doubles() {
		return new LimitationItemArgumentType<>(ParCoolConfig.Server.Doubles.class);
	}

	public static ParCoolConfig.Server.Booleans getBool(CommandContext<?> context, String name) {
		return context.getArgument(name, ParCoolConfig.Server.Booleans.class);
	}

	public static ParCoolConfig.Server.Integers getInt(CommandContext<?> context, String name) {
		return context.getArgument(name, ParCoolConfig.Server.Integers.class);
	}

	public static ParCoolConfig.Server.Doubles getDouble(CommandContext<?> context, String name) {
		return context.getArgument(name, ParCoolConfig.Server.Doubles.class);
	}
}
