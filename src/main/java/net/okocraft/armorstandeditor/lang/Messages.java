package net.okocraft.armorstandeditor.lang;

import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.okocraft.armorstandeditor.lang.Components.KEY_PREFIX;

public final class Messages {

    private Messages() {
        throw new UnsupportedOperationException();
    }

    public static final Component COMMAND_NO_PERMISSION =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.no-permission", RED));

    public static final Component COMMAND_ONLY_PLAYER =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.only-player", RED));

    public static final Component COMMAND_ARGUMENT_NOT_ENOUGH =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.argument-not-enough", RED));

    public static final Component COMMAND_SUB_COMMAND_NOT_FOUND =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.sub-command-not-found", RED));

    public static final Component COMMAND_EQUIPMENT_ARMOR_STAND_NOT_FOUND =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.equipment.armor-stand-not-found", RED));

    public static final Component COMMAND_RELOAD_SUCCESS =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.reload.success", GRAY));

    public static final Component COMMAND_RELOAD_FAILURE =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.reload.failure", GRAY));

    public static final Component COMMAND_AXIS_INVALID_ARGUMENT =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.axis.invalid-argument", RED));

    public static final Component COMMAND_AXIS_CHANGE =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.axis.change", GRAY));

    public static final Component COMMAND_MODE_INVALID_ARGUMENT =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.mode.invalid-argument", RED));

    public static final Component COMMAND_MODE_CHANGE =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.mode.change", GRAY));

    public static final Component COMMAND_ITEM_SUCCESS =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.item.success", GRAY));

    public static final Component COMMAND_ITEM_FAILURE =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.item.failure", GRAY));
}
