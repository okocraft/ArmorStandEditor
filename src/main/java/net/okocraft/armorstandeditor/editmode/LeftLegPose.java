package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.util.AngleCalculator;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class LeftLegPose extends AbstractEditMode {

    LeftLegPose() {
        super("left-leg-pose");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        armorStand.setLeftLegPose(
                AngleCalculator.calculate(
                        armorStand.getLeftLegPose(),
                        editor.getAngleChangeQuantity(),
                        editor.getAxis(),
                        reverse
                )
        );
    }
}
