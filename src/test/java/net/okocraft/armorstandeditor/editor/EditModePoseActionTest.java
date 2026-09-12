package net.okocraft.armorstandeditor.editor;

import org.bukkit.Axis;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EditModePoseActionTest {

    @Test
    void testPoseModesUpdateTheirOwnBodyPart() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EulerAngle current = new EulerAngle(1, 2, 3);
        EulerAngle expected = new EulerAngle(1, 2, 3 + Math.toRadians(10));
        PlayerEditor editor = new PlayerEditor(player);
        editor.setAxis(Axis.Z);
        editor.setAngleChangeQuantity(10);

        Mockito.when(player.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(armorStand.getBodyPose()).thenReturn(current);
        Mockito.when(armorStand.getLeftArmPose()).thenReturn(current);
        Mockito.when(armorStand.getRightArmPose()).thenReturn(current);
        Mockito.when(armorStand.getLeftLegPose()).thenReturn(current);
        Mockito.when(armorStand.getRightLegPose()).thenReturn(current);

        EditMode.BODY_POSE.edit(editor, armorStand, false);
        EditMode.LEFT_ARM_POSE.edit(editor, armorStand, false);
        EditMode.RIGHT_ARM_POSE.edit(editor, armorStand, false);
        EditMode.LEFT_LEG_POSE.edit(editor, armorStand, false);
        EditMode.RIGHT_LEG_POSE.edit(editor, armorStand, false);

        Mockito.verify(armorStand).setBodyPose(expected);
        Mockito.verify(armorStand).setLeftArmPose(expected);
        Mockito.verify(armorStand).setRightArmPose(expected);
        Mockito.verify(armorStand).setLeftLegPose(expected);
        Mockito.verify(armorStand).setRightLegPose(expected);
    }
}
