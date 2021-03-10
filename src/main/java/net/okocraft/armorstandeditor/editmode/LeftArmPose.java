package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.util.AngleCalculator;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

class LeftArmPose extends AbstractEditMode {

    LeftArmPose() {
        super("left-arm-pose");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        armorStand.setLeftArmPose(
                AngleCalculator.calculate(
                        armorStand.getLeftArmPose(),
                        editor.getAngleChangeQuantity(),
                        editor.getAxis(),
                        reverse
                )
        );
    }
}
