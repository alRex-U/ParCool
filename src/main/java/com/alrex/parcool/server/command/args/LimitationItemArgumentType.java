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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LimitationItemArgumentType implements ArgumentType<Object> {
	private final List<String> paths;
	private final Object[] enumConstants;
	private final Class<?> clazz;

	@Override
	public Object parse(StringReader reader) throws CommandSyntaxException {
		String name = reader.readUnquotedString();
		int index = paths.indexOf(name);
		if (index == -1) {
			Message message = Component.translatable("parcool.command.message.invalidConfigName", name);
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
		return enumConstants[index];
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		String remain = builder.getRemaining();
		for (String name : paths.stream().filter(it -> it.startsWith(remain)).toList()) {
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

	public static class Info implements ArgumentTypeInfo<LimitationItemArgumentType, Info.Template> {
		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
			buffer.writeUtf(template.clazz.getTypeName());
		}

		@Nonnull
		@Override
		public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			String typeName = buffer.readUtf();
			if (typeName.equals(ParCoolConfig.Server.Booleans.class.getTypeName())) {
				return booleansTemplate();
			} else if (typeName.equals(ParCoolConfig.Server.Integers.class.getTypeName())) {
				return integersTemplate();
			} else if (typeName.equals(ParCoolConfig.Server.Doubles.class.getTypeName())) {
				return doublesTemplate();
			} else {
				throw new IllegalArgumentException(String.format("No such Limitation type[%s]", typeName));
			}
		}

		@Override
		public void serializeToJson(Template template, JsonObject json) {
			json.addProperty("type", template.clazz.getTypeName());
		}

		@Nonnull
		@Override
		public Template unpack(LimitationItemArgumentType instance) {
			return new Template(instance.clazz);
		}

		Template booleansTemplate() {
			return new Template(ParCoolConfig.Server.Booleans.class);
		}

		Template integersTemplate() {
			return new Template(ParCoolConfig.Server.Integers.class);
		}

		Template doublesTemplate() {
			return new Template(ParCoolConfig.Server.Doubles.class);
		}

		public class Template implements ArgumentTypeInfo.Template<LimitationItemArgumentType> {
			Template(Class<?> clazz) {
				this.clazz = clazz;
			}

			private final Class<?> clazz;

			@Override
			public LimitationItemArgumentType instantiate(CommandBuildContext context) {
				return new LimitationItemArgumentType(clazz);
			}

			@Override
			public ArgumentTypeInfo<LimitationItemArgumentType, ?> type() {
				return Info.this;
			}
		}
	}
}
