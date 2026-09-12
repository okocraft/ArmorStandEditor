package net.okocraft.armorstandeditor.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

    @Test
    void testParseReturnsMatchingValue() throws CommandSyntaxException {
        Assertions.assertEquals("beta", argumentType().parse(new StringReader("beta")));
    }

    @Test
    void testParseRejectsUnknownValue() {
        Assertions.assertThrows(
            CommandSyntaxException.class,
            () -> argumentType().parse(new StringReader("unknown"))
        );
    }

    @Test
    void testSuggestionsReturnAllNamesForEmptyInput() {
        var suggestions = argumentType().listSuggestions(
            Mockito.mock(CommandContext.class),
            new SuggestionsBuilder("", 0)
        ).join();

        Assertions.assertEquals(
            List.of("alpha", "beta", "bravo"),
            suggestions.getList().stream().map(suggestion -> suggestion.getText()).toList()
        );
    }

    @Test
    void testSuggestionsFilterByPrefixCaseInsensitively() {
        var suggestions = argumentType().listSuggestions(
            Mockito.mock(CommandContext.class),
            new SuggestionsBuilder("B", 0)
        ).join();

        Assertions.assertEquals(
            List.of("beta", "bravo"),
            suggestions.getList().stream().map(suggestion -> suggestion.getText()).toList()
        );
    }

    @Test
    void testSuggestionsReturnNothingForUnknownPrefix() {
        var suggestions = argumentType().listSuggestions(
            Mockito.mock(CommandContext.class),
            new SuggestionsBuilder("z", 0)
        ).join();

        Assertions.assertTrue(suggestions.isEmpty());
    }
}
