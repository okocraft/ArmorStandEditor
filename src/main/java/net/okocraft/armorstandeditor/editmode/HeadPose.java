package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.util.AngleCalculator;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class HeadPose extends AbstractEditMode {

    HeadPose() {
        super("head-pose");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        armorStand.setHeadPose(
                AngleCalculator.calculate(
                        armorStand.getHeadPose(),
                        editor.getAngleChangeQuantity(),
                        editor.getAxis(),
                        reverse
                )
        );
    }
}
