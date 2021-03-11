package net.okocraft.armorstandeditor.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.function.Function;

public final class Placeholders {

    private Placeholders() {
        throw new UnsupportedOperationException();
    }

    public static final Function<String, TextReplacementConfig> AXIS =
            s -> TextReplacementConfig.builder()
                    .matchLiteral("%axis%")
                    .replacement(Component.text(s).color(NamedTextColor.AQUA))
                    .build();

    public static final Function<String, TextReplacementConfig> MODE =
            s -> TextReplacementConfig.builder()
                    .matchLiteral("%mode%")
                    .replacement(Component.text(s).color(NamedTextColor.AQUA))
                    .build();
}
