package net.okocraft.armorstandeditor.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public final class ConstantsArgumentType<T> implements CustomArgumentType<T, String> {

    private final Function<String, Component> messageForInvalidInput;
    private final Function<T, Component> tooltipFunction;
    private final List<String> names;
    private final Function<String, T> finder;

    public ConstantsArgumentType(@NotNull List<String> names, @NotNull Function<String, T> finder, @NotNull Function<String, Component> messageForInvalidInput, @NotNull Function<T, Component> tooltipFunction) {
        this.names = names;
        this.finder = finder;
        this.messageForInvalidInput = messageForInvalidInput;
        this.tooltipFunction = tooltipFunction;
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public @NotNull T parse(@NotNull StringReader reader) throws CommandSyntaxException {
        var input = reader.readUnquotedString();
        var found = this.finder.apply(input);

        if (found != null) {
            return found;
        } else {
            var message = MessageComponentSerializer.message().serialize(this.messageForInvalidInput.apply(input));
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message, reader.getString(), reader.getCursor());
        }
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        var input = builder.getRemaining();
        boolean empty = input.isEmpty();

        for (var name : this.names) {
            if (empty || startsWith(name, input, input.length())) {
                builder.suggest(name, MessageComponentSerializer.message().serialize(this.tooltipFunction.apply(this.finder.apply(name))));
            }
        }

        return CompletableFuture.completedFuture(builder.build());
    }

    private static boolean startsWith(@NotNull String str, @NotNull String prefix, int prefixLength) {
        return prefixLength <= str.length() && str.regionMatches(true, 0, prefix, 0, prefixLength);
    }
}
