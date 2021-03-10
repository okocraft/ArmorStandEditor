package net.okocraft.armorstandeditor.editmode;

import org.jetbrains.annotations.NotNull;

abstract class AbstractEditMode implements EditMode {

    private final String name;

    protected AbstractEditMode(@NotNull String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }
}
