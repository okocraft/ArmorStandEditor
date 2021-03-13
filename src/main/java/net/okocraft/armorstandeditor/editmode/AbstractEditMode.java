package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.permission.Permissions;
import org.jetbrains.annotations.NotNull;

abstract class AbstractEditMode implements EditMode {

    private final String name;
    private final String permission;

    protected AbstractEditMode(@NotNull String name) {
        this.name = name;
        this.permission = Permissions.MODE_PREFIX + name;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getPermission() {
        return permission;
    }
}
