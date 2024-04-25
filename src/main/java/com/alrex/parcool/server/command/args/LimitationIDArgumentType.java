package com.alrex.parcool.server.command.args;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.unstable.Limitation;
import com.alrex.parcool.server.limitation.Limitations;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.TextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class LimitationIDArgumentType implements ArgumentType<Limitation.ID> {
    private static final Collection<String> EXAMPLES = Arrays.asList("{Your ID}:{Limitation ID}", "{parcool:example_limitation}");

    private String read(StringReader reader) {
        int start = reader.getCursor();

        while (reader.canRead() && (reader.peek() == ':' || StringReader.isAllowedInUnquotedString(reader.peek()))) {
            reader.skip();
        }

        return reader.getString().substring(start, reader.getCursor());
    }

    @Override
    public Limitation.ID parse(StringReader reader) throws CommandSyntaxException {
        String value = read(reader);
        String[] split = value.split(":");
        if (split.length != 2) {
            Message message = new TextComponent("Too many or less separators(':')");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }
        return new Limitation.ID(split[0], split[1]);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remain = builder.getRemaining();
        for (com.alrex.parcool.server.limitation.Limitation.ID id : Limitations.getRegisteredIDs()) {
            if (id.getGroup().equals(ParCool.MOD_ID)) continue;
            String suggestion = id.getGroup() + ":" + id.getName();
            if (suggestion.startsWith(remain))
                builder.suggest(suggestion);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static LimitationIDArgumentType limitation() {
        return new LimitationIDArgumentType();
    }

    @SuppressWarnings("unchecked")
    public static Limitation.ID getLimitationID(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Limitation.ID.class);
    }
}
