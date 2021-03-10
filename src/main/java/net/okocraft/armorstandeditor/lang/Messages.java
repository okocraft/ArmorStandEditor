package net.okocraft.armorstandeditor.lang;

import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.okocraft.armorstandeditor.lang.Components.KEY_PREFIX;

public final class Messages {

    private Messages() {
        throw new UnsupportedOperationException();
    }

    public static final Component COMMAND_NO_PERMISSION =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.no-permission", RED));


}
