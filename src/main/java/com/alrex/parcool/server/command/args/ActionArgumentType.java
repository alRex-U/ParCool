package com.alrex.parcool.server.command.args;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.action.ActionEntry;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ActionArgumentType implements ArgumentType<ActionEntry<?>> {
    @Override
    public ActionEntry<?> parse(StringReader reader) throws CommandSyntaxException {
        var id = ResourceLocation.read(reader);
        var result = ParCool.getActionRegistry().get(id);
        if (result != null) return result;
        var message = Component.translatable("parcool.command.message.action_not_found", id);
        throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remain = builder.getRemaining();
        for (var name : ParCool.getActionRegistry().getRegisteredActions().keySet().stream().map(Objects::toString).filter(it -> it.startsWith(remain)).toList()) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return ParCool.getActionRegistry().getRegisteredActions().keySet().stream().map(Objects::toString).toList();
    }

    public static ActionArgumentType action() {
        return new ActionArgumentType();
    }

    public static ActionEntry<?> getAction(final CommandContext<?> context, final String name) {
        return context.getArgument(name, ActionEntry.class);
    }
}
