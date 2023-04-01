package net.okocraft.armorstandeditor.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.okocraft.armorstandeditor.lang.Components.KEY_PREFIX;

public final class Messages {

    private Messages() {
        throw new UnsupportedOperationException();
    }

    public static final Component EDIT_NO_PERMISSION =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "edit.no-permission", RED));

    public static final Component EDIT_MODE_NO_PERMISSION =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "edit.mode-no-permission", RED));

    public static final Component RENAME_NO_PERMISSION =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "rename.no-permission", RED));

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
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.reload.failure", RED));

    public static final TranslatableComponent COMMAND_AXIS_INVALID_ARGUMENT =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.axis.invalid-argument", RED));

    public static final TranslatableComponent COMMAND_AXIS_CHANGE =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.axis.change", GRAY));

    public static final TranslatableComponent COMMAND_MODE_INVALID_ARGUMENT =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.mode.invalid-argument", RED));

    public static final TranslatableComponent COMMAND_MODE_CHANGE =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.mode.change", GRAY));

    public static final Component COMMAND_MODE_NO_PERMISSION =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.mode.no-permission", GRAY));

    public static final Component COMMAND_ITEM_SUCCESS =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.item.success", GRAY));

    public static final Component COMMAND_ITEM_FAILURE =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "command.item.failure", RED));

    public static final Component EDIT_ARMS_ON = Component.translatable(KEY_PREFIX + "edit.arms.on", YELLOW);

    public static final Component EDIT_ARMS_OFF = Component.translatable(KEY_PREFIX + "edit.arms.off", YELLOW);

    public static final Component EDIT_BASE_PLATE_ON = Component.translatable(KEY_PREFIX + "edit.base-plate.on", YELLOW);

    public static final Component EDIT_BASE_PLATE_OFF = Component.translatable(KEY_PREFIX + "edit.base-plate.off", YELLOW);

    public static final TranslatableComponent EDIT_COPY = Component.translatable(KEY_PREFIX + "edit.copy", YELLOW);

    public static final Component EDIT_GRAVITY_ON = Component.translatable(KEY_PREFIX + "edit.gravity.on", YELLOW);

    public static final Component EDIT_LOCK = Component.translatable(KEY_PREFIX + "edit.lock.locked", YELLOW);

    public static final Component EDIT_UNLOCK = Component.translatable(KEY_PREFIX + "edit.lock.unlocked", YELLOW);

    public static final Component EDIT_GRAVITY_OFF = Component.translatable(KEY_PREFIX + "edit.gravity.off", YELLOW);

    public static final TranslatableComponent EDIT_PASTE = Component.translatable(KEY_PREFIX + "edit.paste", YELLOW);

    public static final Component EDIT_REMOVAL = Component.translatable(KEY_PREFIX + "edit.removal", YELLOW);

    public static final Component EDIT_RESET_POSE = Component.translatable(KEY_PREFIX + "edit.reset-pose", YELLOW);

    public static final Component EDIT_SIZE_SMALL = Component.translatable(KEY_PREFIX + "edit.size.small", YELLOW);

    public static final Component EDIT_SIZE_NORMAL = Component.translatable(KEY_PREFIX + "edit.size.normal", YELLOW);

    public static final Component EDIT_VISIBLE_ON = Component.translatable(KEY_PREFIX + "edit.visible.on", YELLOW);

    public static final Component EDIT_VISIBLE_OFF = Component.translatable(KEY_PREFIX + "edit.visible.off", YELLOW);

    public static final Component EDIT_CUSTOM_NAME_VISIBLE_ON = Component.translatable(KEY_PREFIX + "edit.custom-name-visible.on", YELLOW);

    public static final Component EDIT_CUSTOM_NAME_VISIBLE_OFF = Component.translatable(KEY_PREFIX + "edit.custom-name-visible.off", YELLOW);

    public static final TranslatableComponent MENU_CHANGE_AXIS =
            Component.translatable(KEY_PREFIX + "menu.selection.change.axis", YELLOW);

    public static final TranslatableComponent MENU_CHANGE_ADJUSTMENT_MODE =
            Component.translatable(KEY_PREFIX + "menu.selection.change.adjustment-mode", YELLOW);

    public static final TranslatableComponent MENU_CHANGE_COPY_SLOT =
            Component.translatable(KEY_PREFIX + "menu.selection.change.copy-slot", YELLOW);

    public static final TranslatableComponent MENU_CHANGE_MODE =
            Component.translatable(KEY_PREFIX + "menu.selection.change.mode", YELLOW);

    public static final TranslatableComponent MENU_HELP =
            Components.PREFIX.append(Component.translatable(KEY_PREFIX + "menu.selection.help", GRAY));
}
