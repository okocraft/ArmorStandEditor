package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

interface EditMode {

    @NotNull String getName();

    void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse);
}
