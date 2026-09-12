package net.okocraft.armorstandeditor.editor;

import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Axis;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

class EditModeTest {

    private static final double DELTA = 1.0E-12;

    @Test
    void testModesAreRegisteredByName() {
        Assertions.assertSame(EditMode.ROTATION, EditMode.byName("rotation"));
        Assertions.assertSame(EditMode.HEAD_POSE, EditMode.byName("head-pose"));
        Assertions.assertNull(EditMode.byName("unknown"));

        Set<String> names = EditMode.names().collect(Collectors.toSet());
        Assertions.assertTrue(names.contains("none"));
        Assertions.assertTrue(names.contains("movement"));
        Assertions.assertTrue(names.contains("equipment"));
        Assertions.assertTrue(names.contains("removal"));
    }

    @Test
    void testPermissionIsDerivedFromModeName() {
        Assertions.assertEquals(Permissions.MODE_PREFIX + "rotation", EditMode.ROTATION.getPermission());
        Assertions.assertEquals(Permissions.MODE_PREFIX + "head-pose", EditMode.HEAD_POSE.getPermission());
    }

    @Test
    void testEditRequiresModePermission() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);

        EditMode.BASE_PLATE.edit(editor, armorStand, false);

        Mockito.verify(armorStand, Mockito.never()).setBasePlate(Mockito.anyBoolean());
        Mockito.verify(player).sendMessage(Messages.EDIT_MODE_NO_PERMISSION);
    }

    @Test
    void testLockedArmorStandIsNotEdited() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Mockito.when(armorStand.getUniqueId()).thenReturn(UUID.randomUUID());
        Mockito.when(player.hasPermission(EditMode.BASE_PLATE.getPermission())).thenReturn(true);
        PlayerEditor editor = new PlayerEditor(player);
        editor.lock(armorStand);

        EditMode.BASE_PLATE.edit(editor, armorStand, false);

        Mockito.verify(armorStand, Mockito.never()).setBasePlate(Mockito.anyBoolean());
    }

    @Test
    void testLockModeCanUnlockLockedArmorStand() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Mockito.when(armorStand.getUniqueId()).thenReturn(UUID.randomUUID());
        Mockito.when(player.hasPermission(EditMode.LOCK.getPermission())).thenReturn(true);
        PlayerEditor editor = new PlayerEditor(player);
        editor.lock(armorStand);

        EditMode.LOCK.edit(editor, armorStand, false);

        Assertions.assertFalse(editor.isLocked(armorStand));
        Mockito.verify(player).sendActionBar(Messages.EDIT_UNLOCK);
    }

    @Test
    void testBooleanModeTogglesCurrentState() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Mockito.when(player.hasPermission(EditMode.BASE_PLATE.getPermission())).thenReturn(true);
        Mockito.when(armorStand.hasBasePlate()).thenReturn(true);
        PlayerEditor editor = new PlayerEditor(player);

        EditMode.BASE_PLATE.edit(editor, armorStand, false);

        Mockito.verify(armorStand).setBasePlate(false);
        Mockito.verify(player).sendActionBar(Messages.EDIT_BASE_PLATE_OFF);
    }

    @Test
    void testPoseModeChangesSelectedAxis() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Mockito.when(player.hasPermission(EditMode.HEAD_POSE.getPermission())).thenReturn(true);
        Mockito.when(armorStand.getHeadPose()).thenReturn(new EulerAngle(1, 2, 3));
        PlayerEditor editor = new PlayerEditor(player);
        editor.setAxis(Axis.Y);
        editor.setAngleChangeQuantity(12);

        EditMode.HEAD_POSE.edit(editor, armorStand, false);

        ArgumentCaptor<EulerAngle> angle = ArgumentCaptor.forClass(EulerAngle.class);
        Mockito.verify(armorStand).setHeadPose(angle.capture());
        Assertions.assertEquals(1, angle.getValue().getX(), DELTA);
        Assertions.assertEquals(2 + Math.toRadians(12), angle.getValue().getY(), DELTA);
        Assertions.assertEquals(3, angle.getValue().getZ(), DELTA);
    }

    @Test
    void testRotationUsesDirectionAndConfiguredQuantity() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Mockito.when(player.hasPermission(EditMode.ROTATION.getPermission())).thenReturn(true);
        Mockito.when(armorStand.getYaw()).thenReturn(30.0F);
        Mockito.when(armorStand.getPitch()).thenReturn(10.0F);
        PlayerEditor editor = new PlayerEditor(player);
        editor.setAngleChangeQuantity(12);

        EditMode.ROTATION.edit(editor, armorStand, true);

        Mockito.verify(armorStand).setRotation(18.0F, 10.0F);
    }

    @Test
    void testResetPoseResetsEveryBodyPart() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Mockito.when(player.hasPermission(EditMode.RESET_POSE.getPermission())).thenReturn(true);
        PlayerEditor editor = new PlayerEditor(player);

        EditMode.RESET_POSE.edit(editor, armorStand, false);

        Mockito.verify(armorStand).setHeadPose(AngleCalculator.INITIAL_ANGLE);
        Mockito.verify(armorStand).setBodyPose(AngleCalculator.INITIAL_ANGLE);
        Mockito.verify(armorStand).setLeftArmPose(AngleCalculator.INITIAL_ANGLE);
        Mockito.verify(armorStand).setRightArmPose(AngleCalculator.INITIAL_ANGLE);
        Mockito.verify(armorStand).setLeftLegPose(AngleCalculator.INITIAL_ANGLE);
        Mockito.verify(armorStand).setRightLegPose(AngleCalculator.INITIAL_ANGLE);
        Mockito.verify(player).sendActionBar(Messages.EDIT_RESET_POSE);
    }
}
