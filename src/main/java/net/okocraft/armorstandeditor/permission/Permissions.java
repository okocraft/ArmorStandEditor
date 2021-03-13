package net.okocraft.armorstandeditor.permission;

public final class Permissions {

    private Permissions() {
        throw new UnsupportedOperationException();
    }

    private static final String PREFIX = "armorstandeditor.";

    public static final String ARMOR_STAND_EDIT = PREFIX + "edit";

    public static final String ARMOR_STAND_RENAME = PREFIX + "rename";

    public static final String COMMAND = PREFIX + "command";

    public static final String COMMAND_PREFIX = COMMAND + '.';

    public static final String MODE_PREFIX = PREFIX + "mode.";

    public static final String ICON_PREFIX = PREFIX + "menu.icon.";

}
