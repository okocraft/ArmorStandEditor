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
        var location = armorStand.getLocation().clone();

        float yaw;

        if (!reverse) {
            yaw = (float) (location.getYaw() + editor.getAngleChangeQuantity());
        } else {
            yaw = (float) (location.getYaw() - editor.getAngleChangeQuantity());
        }

        location.setYaw(yaw);

        armorStand.teleport(location);
    }
}
