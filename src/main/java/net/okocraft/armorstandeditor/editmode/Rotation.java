package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class Rotation extends AbstractEditMode {

    Rotation() {
        super("rotation");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        double diff = editor.getAngleChangeQuantity() * (reverse ? -1 : 1);
        armorStand.setRotation((float) (armorStand.getYaw() + diff), armorStand.getPitch());
    }
}
