package com.alrex.parcool.server.command.args;

import com.alrex.parcool.common.stamina.StaminaType;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class StaminaTypeArgumentType implements ArgumentType<StaminaType> {
    @Override
    public StaminaType parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        try {
            return StaminaType.valueOf(name);
        } catch (IllegalArgumentException e) {
            Message message = Component.translatable("parcool.command.message.invalidStaminaType", name);
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remain = builder.getRemaining();
        for (var type : Arrays.stream(StaminaType.values()).filter(it -> it.name().startsWith(remain)).toList()) {
            builder.suggest(type.name());
        }
        return builder.buildFuture();
    }

    private static final Collection<String> EXAMPLES = Arrays.stream(StaminaType.values()).map(Enum::name).toList();

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static StaminaTypeArgumentType type() {
        return new StaminaTypeArgumentType();
    }

    public static StaminaType getStamina(final CommandContext<?> context, final String name) {
        return context.getArgument(name, StaminaType.class);
    }
}
