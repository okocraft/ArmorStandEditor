package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.util.AngleCalculator;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class RightLegPose extends AbstractEditMode {

    RightLegPose() {
        super("right-leg-pose");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        armorStand.setRightLegPose(
                AngleCalculator.calculate(
                        armorStand.getRightLegPose(),
                        editor.getAngleChangeQuantity(),
                        editor.getAxis(),
                        reverse
                )
        );
    }
}
