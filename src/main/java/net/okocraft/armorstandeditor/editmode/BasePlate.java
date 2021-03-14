package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class BasePlate extends AbstractEditMode {

    BasePlate() {
        super("base-plate");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        var current = armorStand.hasBasePlate();

        armorStand.setBasePlate(!current);

        var message = armorStand.hasBasePlate() ? Messages.EDIT_BASE_PLATE_ON : Messages.EDIT_BASE_PLATE_OFF;
        editor.getPlayer().sendActionBar(message);
    }
}
