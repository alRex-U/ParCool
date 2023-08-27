package com.alrex.parcool.server.command.args;

import com.alrex.parcool.config.ParCoolConfig;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LimitationItemArgumentType implements ArgumentType<Object> {
	private final List<String> paths;
	private final Object[] enumConstants;
	private final Class<?> clazz;

	@Override
	public Object parse(StringReader reader) throws CommandSyntaxException {
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

	LimitationItemArgumentType(Class<?> clazz) {
		this.clazz = clazz;
		enumConstants = clazz.getEnumConstants();
		paths = new ArrayList<>(enumConstants.length);
		for (Object enumConstant : enumConstants) {
			ParCoolConfig.Item<?> item = (ParCoolConfig.Item<?>) enumConstant;
			paths.add(item.getPath());
		}
	}

	public static LimitationItemArgumentType booleans() {
		return new LimitationItemArgumentType(ParCoolConfig.Server.Booleans.class);
	}

	public static LimitationItemArgumentType integers() {
		return new LimitationItemArgumentType(ParCoolConfig.Server.Integers.class);
	}

	public static LimitationItemArgumentType doubles() {
		return new LimitationItemArgumentType(ParCoolConfig.Server.Doubles.class);
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

	public static class Serializer implements ArgumentSerializer<LimitationItemArgumentType> {
		@Nonnull
		@Override
		public LimitationItemArgumentType deserializeFromNetwork(FriendlyByteBuf buffer) {
			String typeName = buffer.readUtf();
			if (typeName.equals(ParCoolConfig.Server.Booleans.class.getTypeName())) {
				return LimitationItemArgumentType.booleans();
			} else if (typeName.equals(ParCoolConfig.Server.Integers.class.getTypeName())) {
				return LimitationItemArgumentType.integers();
			} else if (typeName.equals(ParCoolConfig.Server.Doubles.class.getTypeName())) {
				return LimitationItemArgumentType.doubles();
			} else {
				throw new IllegalArgumentException(String.format("No such Limitation type[%s]", typeName));
			}
		}

		@Override
		public void serializeToNetwork(LimitationItemArgumentType instance, FriendlyByteBuf buffer) {
			buffer.writeUtf(instance.clazz.getTypeName());
		}

		@Override
		public void serializeToJson(LimitationItemArgumentType instance, JsonObject json) {
			json.addProperty("type", instance.clazz.getTypeName());
		}
	}
}
