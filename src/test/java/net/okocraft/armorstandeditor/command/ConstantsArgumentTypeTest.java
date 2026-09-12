package net.okocraft.armorstandeditor.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

class ConstantsArgumentTypeTest {

    private static ConstantsArgumentType<String> argumentType() {
        List<String> names = List.of("alpha", "beta", "bravo");
        return new ConstantsArgumentType<>(
            names,
            input -> names.contains(input) ? input : null,
            input -> Component.text("invalid: " + input),
            value -> Component.text("tooltip: " + value)
        );
    }

    @SuppressWarnings("unchecked")
    private static CommandContext<Object> commandContext() {
        return Mockito.mock(CommandContext.class);
    }

    private static MockedStatic<MessageComponentSerializer> mockMessageSerializer() {
        MessageComponentSerializer serializer = Mockito.mock(MessageComponentSerializer.class);
        Message message = Mockito.mock(Message.class);
        Mockito.when(serializer.serialize(Mockito.any(Component.class))).thenReturn(message);

        MockedStatic<MessageComponentSerializer> mocked = Mockito.mockStatic(MessageComponentSerializer.class);
        mocked.when(MessageComponentSerializer::message).thenReturn(serializer);
        return mocked;
    }

    @Test
    void testParseReturnsMatchingValue() throws CommandSyntaxException {
        Assertions.assertEquals("beta", argumentType().parse(new StringReader("beta")));
    }

    @Test
    void testParseRejectsUnknownValue() {
        try (var ignored = mockMessageSerializer()) {
            Assertions.assertThrows(
                CommandSyntaxException.class,
                () -> argumentType().parse(new StringReader("unknown"))
            );
        }
    }

    @Test
    void testSuggestionsReturnAllNamesForEmptyInput() {
        try (var ignored = mockMessageSerializer()) {
            var suggestions = argumentType().listSuggestions(
                commandContext(),
                new SuggestionsBuilder("", 0)
            ).join();

            Assertions.assertEquals(
                List.of("alpha", "beta", "bravo"),
                suggestions.getList().stream().map(suggestion -> suggestion.getText()).toList()
            );
        }
    }

    @Test
    void testSuggestionsFilterByPrefixCaseInsensitively() {
        try (var ignored = mockMessageSerializer()) {
            var suggestions = argumentType().listSuggestions(
                commandContext(),
                new SuggestionsBuilder("B", 0)
            ).join();

            Assertions.assertEquals(
                List.of("beta", "bravo"),
                suggestions.getList().stream().map(suggestion -> suggestion.getText()).toList()
            );
        }
    }

    @Test
    void testSuggestionsReturnNothingForUnknownPrefix() {
        var suggestions = argumentType().listSuggestions(
            commandContext(),
            new SuggestionsBuilder("z", 0)
        ).join();

        Assertions.assertTrue(suggestions.isEmpty());
    }
}
