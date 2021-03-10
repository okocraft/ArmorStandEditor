package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class Gravity extends AbstractEditMode {

    Gravity() {
        super("gravity");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        boolean current = armorStand.hasGravity();

        armorStand.setGravity(!current);
    }
}
