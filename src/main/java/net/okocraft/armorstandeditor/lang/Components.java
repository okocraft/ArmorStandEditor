package net.okocraft.armorstandeditor.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.okocraft.armorstandeditor.editor.EditMode;
import net.okocraft.armorstandeditor.editor.PlayerEditor;

import java.util.function.Function;

public final class Components {

    public static final String KEY_PREFIX = "armorstandeditor.";

    public static final TranslatableComponent PREFIX =
            Component.translatable("armorstandeditor.prefix").color(NamedTextColor.GRAY);

    public static final Component EQUIPMENT_MENU_TITLE =
            Component.translatable(KEY_PREFIX + "menu.equipment.title").color(NamedTextColor.BLACK);

    public static final Component SELECTION_MENU_TITLE =
            Component.translatable(KEY_PREFIX + "menu.selection.title").color(NamedTextColor.BLACK);

    public static final Function<PlayerEditor.Axis, Component> AXIS_NAME =
            axis -> Component.translatable(KEY_PREFIX + "axis." + axis.getName(), NamedTextColor.AQUA);

    public static final Function<EditMode, Component> MODE_NAME =
            mode -> Component.translatable(KEY_PREFIX + "mode." + mode.getName(), NamedTextColor.AQUA);

    public static final Component ADJUSTMENT_MODE_FINE =
            Component.translatable(KEY_PREFIX + "adjustment-mode.fine", NamedTextColor.AQUA);

    public static final Component ADJUSTMENT_MODE_COARSE =
            Component.translatable(KEY_PREFIX + "adjustment-mode.coarse", NamedTextColor.AQUA);

    public static final Component WIKI_LINK =
            Component.translatable(KEY_PREFIX + "wiki-link", NamedTextColor.AQUA);

    private Components() {
        throw new UnsupportedOperationException();
    }
}
