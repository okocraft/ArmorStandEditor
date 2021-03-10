package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

public enum Mode {
    NONE(new None()),
    BASE_PLATE(new BasePlate()),
    BODY_POSE(new BodyPose()),
    COPY(new Copy()),
    EQUIPMENT(new Equipment()),
    GRAVITY(new Gravity()),
    HEAD_POSE(new HeadPose()),
    LEFT_ARM_POSE(new LeftArmPose()),
    LEFT_LEG_POSE(new LeftLegPose()),
    MOVEMENT(new Movement()),
    PASTE(new Paste()),
    REMOVAL(new Removal()),
    RESET_POSE(new ResetPose()),
    RIGHT_ARM_POSE(new RightArmPose()),
    RIGHT_LEG_POSE(new RightLegPose()),
    ROTATION(new Rotation()),
    SHOW_ARMS(new ShowArms()),
    SIZE(new Size()),
    VISIBLE(new Visible());

    private final EditMode editMode;

    Mode(@NotNull EditMode editMode) {
        this.editMode = editMode;
    }

    public @NotNull String getName() {
        return editMode.getName();
    }

    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        editMode.edit(editor, armorStand, reverse);
    }
}
