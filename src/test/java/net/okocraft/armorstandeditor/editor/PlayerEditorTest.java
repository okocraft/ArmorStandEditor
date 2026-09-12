package net.okocraft.armorstandeditor.editor;

import org.bukkit.Axis;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static net.okocraft.armorstandeditor.testsupport.TestIds.ARMOR_STAND_UUID;

class PlayerEditorTest {

    @Test
    void testInitialState() {
        Player player = Mockito.mock(Player.class);
        PlayerEditor editor = new PlayerEditor(player);

        Assertions.assertSame(player, editor.getPlayer());
        Assertions.assertEquals(Axis.X, editor.getAxis());
        Assertions.assertSame(EditMode.NONE, editor.getMode());
        Assertions.assertEquals(1, editor.getSelectedCopySlot());
        Assertions.assertEquals(12, editor.getAngleChangeQuantity());
        Assertions.assertEquals(1, editor.getMovingDistance());
        Assertions.assertNull(editor.getSelectedArmorStand());
    }

    @Test
    void testSettingsCanBeChanged() {
        PlayerEditor editor = new PlayerEditor(Mockito.mock(Player.class));

        editor.setAxis(Axis.Z);
        editor.setMode(EditMode.HEAD_POSE);
        editor.setSelectedCopySlot(3);
        editor.setAngleChangeQuantity(2.5);
        editor.setMovingDistance(0.25);

        Assertions.assertEquals(Axis.Z, editor.getAxis());
        Assertions.assertSame(EditMode.HEAD_POSE, editor.getMode());
        Assertions.assertEquals(3, editor.getSelectedCopySlot());
        Assertions.assertEquals(2.5, editor.getAngleChangeQuantity());
        Assertions.assertEquals(0.25, editor.getMovingDistance());
    }

    @Test
    void testArmorStandCanBeLockedAndUnlocked() {
        PlayerEditor editor = new PlayerEditor(Mockito.mock(Player.class));
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Mockito.when(armorStand.getUniqueId()).thenReturn(ARMOR_STAND_UUID);

        Assertions.assertFalse(editor.isLocked(armorStand));

        editor.lock(armorStand);
        Assertions.assertTrue(editor.isLocked(armorStand));

        editor.unlock(armorStand);
        Assertions.assertFalse(editor.isLocked(armorStand));
    }

    @Test
    void testCopiedArmorStandIsStoredInSelectedSlot() {
        PlayerEditor editor = new PlayerEditor(Mockito.mock(Player.class));
        ArmorStand armorStand = createCopySource();

        editor.copy(armorStand);
        Assertions.assertNotNull(editor.getSelectedArmorStand());

        editor.setSelectedCopySlot(2);
        Assertions.assertNull(editor.getSelectedArmorStand());

        editor.copy(armorStand);
        Assertions.assertNotNull(editor.getSelectedArmorStand());

        editor.setSelectedCopySlot(1);
        Assertions.assertNotNull(editor.getSelectedArmorStand());
    }

    private static ArmorStand createCopySource() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EulerAngle angle = new EulerAngle(0, 0, 0);
        ItemStack item = Mockito.mock(ItemStack.class);

        Mockito.when(armorStand.getHeadPose()).thenReturn(angle);
        Mockito.when(armorStand.getBodyPose()).thenReturn(angle);
        Mockito.when(armorStand.getRightArmPose()).thenReturn(angle);
        Mockito.when(armorStand.getLeftArmPose()).thenReturn(angle);
        Mockito.when(armorStand.getRightLegPose()).thenReturn(angle);
        Mockito.when(armorStand.getLeftLegPose()).thenReturn(angle);
        Mockito.when(armorStand.getItem(Mockito.any(EquipmentSlot.class))).thenReturn(item);
        Mockito.when(item.clone()).thenReturn(item);

        return armorStand;
    }
}
