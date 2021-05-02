package com.alrex.parcool.common.command.args;

import com.alrex.parcool.constants.TranslateKeys;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BooleanArgument implements ArgumentType<Boolean> {
	public static final List<String> examples = Arrays.asList("true", "false");
	private static final DynamicCommandExceptionType VALUE_INVALID = new DynamicCommandExceptionType((value) -> new TranslationTextComponent(TranslateKeys.COMMAND_ERROR_BOOLEAN_INVALID, value.toString()));

	@Override
	public Boolean parse(StringReader stringReader) throws CommandSyntaxException {
		String str = stringReader.readUnquotedString();
		if (str.equals("true")) return Boolean.TRUE;
		if (str.equals("false")) return Boolean.FALSE;

		throw VALUE_INVALID.create(str);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return builder.suggest("true").suggest("false").buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return examples;
	}
}
