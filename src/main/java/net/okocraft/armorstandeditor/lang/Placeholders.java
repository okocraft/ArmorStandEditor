package net.okocraft.armorstandeditor.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.okocraft.armorstandeditor.editmode.Mode;

import java.util.function.Function;

public final class Placeholders {

    private static final String MODE_KEY_PREFIX = "armorstandeditor.mode.";

    private Placeholders() {
        throw new UnsupportedOperationException();
    }

    public static final Function<String, TextReplacementConfig> AXIS =
            s -> TextReplacementConfig.builder()
                    .matchLiteral("%axis%")
                    .replacement(Component.text(s).color(NamedTextColor.AQUA))
                    .build();

    public static final Function<String, TextReplacementConfig> MODE_STRING =
            s -> TextReplacementConfig.builder()
                    .matchLiteral("%mode%")
                    .replacement(Component.text(s).color(NamedTextColor.AQUA))
                    .build();

    public static final Function<Mode, TextReplacementConfig> MODE =
            mode -> TextReplacementConfig.builder()
                    .matchLiteral("%mode%")
                    .replacement(Component.translatable(MODE_KEY_PREFIX + mode.getName()).color(NamedTextColor.AQUA))
                    .build();
}
