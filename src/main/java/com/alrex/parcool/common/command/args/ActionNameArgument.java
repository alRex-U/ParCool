package com.alrex.parcool.common.command.args;

import com.alrex.parcool.constants.ActionsEnum;
import com.alrex.parcool.constants.TranslateKeys;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ActionNameArgument implements ArgumentType<ActionsEnum> {
	public static final List<String> suggests = Arrays.stream(ActionsEnum.values()).map(Enum::name).collect(Collectors.toList());
	public static final List<String> examples = Arrays.asList("Crawl", "Dodge");
	private static final DynamicCommandExceptionType ACTION_INVALID = new DynamicCommandExceptionType(action -> new TranslationTextComponent(TranslateKeys.COMMAND_ERROR_ACTIONNAME_INVALID, action.toString()));

	@Override
	public ActionsEnum parse(StringReader stringReader) throws CommandSyntaxException {
		String s = stringReader.readUnquotedString();
		try {
			return ActionsEnum.valueOf(s);
		} catch (IllegalArgumentException e) {
			throw ACTION_INVALID.create(s);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return ISuggestionProvider.suggest(suggests, builder);
	}

	@Override
	public Collection<String> getExamples() {
		return examples;
	}
}
