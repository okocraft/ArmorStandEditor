package net.okocraft.armorstandeditor.util;

import com.mojang.brigadier.Message;
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

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public final class EnumArgumentType<E extends Enum<E>> extends CustomArgumentType.Converted<E, String> {

    private final Class<E> enumClass;
    private final Function<String, Component> messageForInvalidInput;
    private final Message tooltip;
    private final boolean lowercase;

    public EnumArgumentType(@NotNull Class<E> enumClass, @NotNull Function<String, Component> messageForInvalidInput, @NotNull Component tooltip, boolean lowercase) {
        super(StringArgumentType.word());
        this.enumClass = enumClass;
        this.messageForInvalidInput = messageForInvalidInput;
        this.tooltip = MessageComponentSerializer.message().serialize(tooltip);
        this.lowercase = lowercase;
    }

    @Override
    public @NotNull E convert(@NotNull String argument) throws CommandSyntaxException {
        try {
            return Enum.valueOf(this.enumClass, argument.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            var message = MessageComponentSerializer.message().serialize(this.messageForInvalidInput.apply(argument));
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        for (var value : this.enumClass.getEnumConstants()) {
            builder.suggest(
                    this.lowercase ? value.name().toLowerCase(Locale.ENGLISH) : value.name(),
                    this.tooltip
            );
        }

        return CompletableFuture.completedFuture(builder.build());
    }
}
