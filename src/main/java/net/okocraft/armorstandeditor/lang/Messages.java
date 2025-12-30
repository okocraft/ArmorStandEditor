package net.okocraft.armorstandeditor.lang;

import dev.siroshun.mcmsgdef.MessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.okocraft.armorstandeditor.editor.EditMode;
import org.bukkit.Axis;

public final class Messages {

    private Messages() {
        throw new UnsupportedOperationException();
    }

    public static final MessageKey EDIT_NO_PERMISSION = MessageKey.key("armorstandeditor.edit.no-permission");

    public static final MessageKey EDIT_MODE_NO_PERMISSION = MessageKey.key("armorstandeditor.edit.mode-no-permission");

    public static final MessageKey RENAME_NO_PERMISSION = MessageKey.key("armorstandeditor.rename.no-permission");

    public static final MessageKey COMMAND_ARGUMENT_NOT_ENOUGH = MessageKey.key("armorstandeditor.command.argument-not-enough");

    public static final MessageKey COMMAND_EQUIPMENT_ARMOR_STAND_NOT_FOUND = MessageKey.key("armorstandeditor.command.equipment.armor-stand-not-found");

    public static final MessageKey COMMAND_RELOAD_SUCCESS = MessageKey.key("armorstandeditor.command.reload.success");

    public static final MessageKey COMMAND_RELOAD_FAILURE = MessageKey.key("armorstandeditor.command.reload.failure");

    public static final MessageKey.Arg1<String> COMMAND_AXIS_INVALID_ARGUMENT = MessageKey.arg1("armorstandeditor.command.axis.invalid-argument", axis -> Argument.string("axis", axis));

    public static final MessageKey.Arg1<Axis> COMMAND_AXIS_TOOLTIP = MessageKey.arg1("armorstandeditor.command.axis.tooltip", Placeholders.AXIS_PLACEHOLDER);

    public static final MessageKey.Arg1<Axis> COMMAND_AXIS_CHANGE = MessageKey.arg1("armorstandeditor.command.axis.change", Placeholders.AXIS_PLACEHOLDER);

    public static final MessageKey.Arg1<String> COMMAND_MODE_INVALID_ARGUMENT = MessageKey.arg1("armorstandeditor.command.mode.invalid-argument", mode -> Argument.string("mode", mode));

    public static final MessageKey.Arg1<EditMode> COMMAND_MODE_TOOLTIP = MessageKey.arg1("armorstandeditor.command.mode.tooltip", Placeholders.EDIT_MODE_PLACEHOLDER);

    public static final MessageKey.Arg1<EditMode> COMMAND_MODE_CHANGE = MessageKey.arg1("armorstandeditor.command.mode.change", Placeholders.EDIT_MODE_PLACEHOLDER);

    public static final MessageKey COMMAND_MODE_NO_PERMISSION = MessageKey.key("armorstandeditor.command.mode.no-permission");

    public static final MessageKey COMMAND_ITEM_SUCCESS = MessageKey.key("armorstandeditor.command.item.success");

    public static final MessageKey COMMAND_ITEM_FAILURE = MessageKey.key("armorstandeditor.command.item.failure");

    public static final MessageKey EDIT_ARMS_ON = MessageKey.key("armorstandeditor.edit.arms.on");

    public static final MessageKey EDIT_ARMS_OFF = MessageKey.key("armorstandeditor.edit.arms.off");

    public static final MessageKey EDIT_BASE_PLATE_ON = MessageKey.key("armorstandeditor.edit.base-plate.on");

    public static final MessageKey EDIT_BASE_PLATE_OFF = MessageKey.key("armorstandeditor.edit.base-plate.off");

    public static final MessageKey.Arg1<Integer> EDIT_COPY = MessageKey.arg1("armorstandeditor.edit.copy", Placeholders.SLOT_PLACEHOLDER);

    public static final MessageKey EDIT_GRAVITY_ON = MessageKey.key("armorstandeditor.edit.gravity.on");

    public static final MessageKey EDIT_LOCK = MessageKey.key("armorstandeditor.edit.lock.locked");

    public static final MessageKey EDIT_UNLOCK = MessageKey.key("armorstandeditor.edit.lock.unlocked");

    public static final MessageKey EDIT_GRAVITY_OFF = MessageKey.key("armorstandeditor.edit.gravity.off");

    public static final  MessageKey.Arg1<Integer> EDIT_PASTE = MessageKey.arg1("armorstandeditor.edit.paste", Placeholders.SLOT_PLACEHOLDER);

    public static final MessageKey EDIT_REMOVAL = MessageKey.key("armorstandeditor.edit.removal");

    public static final MessageKey EDIT_RESET_POSE = MessageKey.key("armorstandeditor.edit.reset-pose");

    public static final MessageKey EDIT_SIZE_SMALL = MessageKey.key("armorstandeditor.edit.size.small");

    public static final MessageKey EDIT_SIZE_NORMAL = MessageKey.key("armorstandeditor.edit.size.normal");

    public static final MessageKey EDIT_VISIBLE_ON = MessageKey.key("armorstandeditor.edit.visible.on");

    public static final MessageKey EDIT_VISIBLE_OFF = MessageKey.key("armorstandeditor.edit.visible.off");

    public static final MessageKey EDIT_CUSTOM_NAME_VISIBLE_ON = MessageKey.key("armorstandeditor.edit.custom-name-visible.on");

    public static final MessageKey EDIT_CUSTOM_NAME_VISIBLE_OFF = MessageKey.key("armorstandeditor.edit.custom-name-visible.off");

    public static final MessageKey.Arg1<Axis> MENU_CHANGE_AXIS = MessageKey.arg1("armorstandeditor.menu.selection.change.axis", Placeholders.AXIS_PLACEHOLDER);

    public static final MessageKey.Arg1<Component> MENU_CHANGE_ADJUSTMENT_MODE = MessageKey.arg1("armorstandeditor.menu.selection.change.adjustment-mode", component -> Argument.component("mode", component));

    public static final MessageKey.Arg1<Integer> MENU_CHANGE_COPY_SLOT =MessageKey.arg1("armorstandeditor.menu.selection.change.copy-slot", Placeholders.SLOT_PLACEHOLDER);

    public static final MessageKey.Arg1<EditMode> MENU_CHANGE_MODE =MessageKey.arg1("armorstandeditor.menu.selection.change.mode", Placeholders.EDIT_MODE_PLACEHOLDER);

    public static final MessageKey MENU_HELP =MessageKey.key("armorstandeditor.menu.selection.help");
}
