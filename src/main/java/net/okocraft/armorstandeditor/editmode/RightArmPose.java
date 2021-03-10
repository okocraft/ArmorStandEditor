package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.util.AngleCalculator;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class RightArmPose extends AbstractEditMode {

    RightArmPose() {
        super("right-arm-pose");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        armorStand.setRightArmPose(
                AngleCalculator.calculate(
                        armorStand.getRightArmPose(),
                        editor.getAngleChangeQuantity(),
                        editor.getAxis(),
                        reverse
                )
        );
    }
}
