package net.okocraft.armorstandeditor.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class Components {

    public static final Component EQUIPMENT_MENU_TITLE =
            Component.translatable("armorstandeditor.menu.equipment.title").color(NamedTextColor.BLACK);

    public static final Component SELECTION_MENU_TITLE =
            Component.translatable("armorstandeditor.menu.selection.title").color(NamedTextColor.BLACK);

    public static final Component ADJUSTMENT_MODE_FINE =
            Component.translatable("armorstandeditor.adjustment-mode.fine", NamedTextColor.AQUA);

    public static final Component ADJUSTMENT_MODE_COARSE =
            Component.translatable("armorstandeditor.adjustment-mode.coarse", NamedTextColor.AQUA);

    private Components() {
        throw new UnsupportedOperationException();
    }
}
