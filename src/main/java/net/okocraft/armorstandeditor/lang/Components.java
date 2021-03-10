package net.okocraft.armorstandeditor.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class Components {

    private Components() {
        throw new UnsupportedOperationException();
    }

    public static final Component PREFIX =
            Component.translatable("armorstandeditor.prefix").color(NamedTextColor.GRAY);

    public static final Component DISABLE_MANIPULATION_1 =
            PREFIX.append(Component.translatable("armorstandeditor.disable-manipulation.line-1"));

    public static final Component DISABLE_MANIPULATION_2 =
            PREFIX.append(Component.translatable("armorstandeditor.disable-manipulation.line-2"));

    public static final Component EQUIPMENT_MENU_TITLE =
            Component.translatable("armorstandeditor.menu.equipment.title").color(NamedTextColor.BLACK);

    public static final Component SELECTION_MENU_TITLE =
            Component.translatable("armorstandeditor.menu.selection.title").color(NamedTextColor.BLACK);
}
