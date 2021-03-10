package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class BasePlate extends AbstractEditMode {

    BasePlate() {
        super("base-plate");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        boolean current = armorStand.hasBasePlate();

        armorStand.setBasePlate(!current);
    }
}
