package net.okocraft.armorstandeditor.permission;

public final class Permissions {

    private Permissions() {
        throw new UnsupportedOperationException();
    }

    private static final String PREFIX = "armorstandeditor.";

    public static final String ARMOR_STAND_EDIT = PREFIX + "edit";

    public static final String ARMOR_STAND_RENAME = PREFIX + "rename";

    public static final String COMMAND = PREFIX + "command";

    private static final String COMMAND_PREFIX = COMMAND + '.';

    public static final String COMMAND_AXIS = COMMAND_PREFIX + "axis";

    public static final String COMMAND_EQUIPMENT = COMMAND_PREFIX + "equipment";

    public static final String COMMAND_ITEM = COMMAND_PREFIX + "item";

    public static final String COMMAND_MODE = COMMAND_PREFIX + "mode";

    public static final String COMMAND_RELOAD = COMMAND_PREFIX + "reload";

    public static final String MODE_PREFIX = PREFIX + "mode.";

    public static final String ICON_PREFIX = PREFIX + "menu.icon.";

}
