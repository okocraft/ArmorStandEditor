package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

import static net.okocraft.armorstandeditor.util.AngleCalculator.INITIAL_ANGLE;

class ResetPose extends AbstractEditMode {

    ResetPose() {
        super("reset-pose");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        armorStand.setHeadPose(INITIAL_ANGLE);
        armorStand.setBodyPose(INITIAL_ANGLE);
        armorStand.setLeftArmPose(INITIAL_ANGLE);
        armorStand.setRightArmPose(INITIAL_ANGLE);
        armorStand.setLeftLegPose(INITIAL_ANGLE);
        armorStand.setRightLegPose(INITIAL_ANGLE);
    }
}
