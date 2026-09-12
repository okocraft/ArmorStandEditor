package net.okocraft.armorstandeditor.editor;

import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static net.okocraft.armorstandeditor.testsupport.TestIds.ARMOR_STAND_UUID;

class EditModeStateActionTest {

    @Test
    void testShowArmsEnablesArms() {
        Player player = playerWithPermission(EditMode.SHOW_ARMS);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);

        EditMode.SHOW_ARMS.edit(editor, armorStand, false);

        Mockito.verify(armorStand).setArms(true);
        Mockito.verify(player).sendActionBar(Messages.EDIT_ARMS_ON);
    }

    @Test
    void testSizeMakesNormalArmorStandSmall() {
        Player player = playerWithPermission(EditMode.SIZE);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);

        EditMode.SIZE.edit(editor, armorStand, false);

        Mockito.verify(armorStand).setSmall(true);
        Mockito.verify(player).sendActionBar(Messages.EDIT_SIZE_SMALL);
    }

    @Test
    void testVisibilityMakesInvisibleArmorStandVisible() {
        Player player = playerWithPermission(EditMode.VISIBLE);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);

        EditMode.VISIBLE.edit(editor, armorStand, false);

        Mockito.verify(armorStand).setVisible(true);
        Mockito.verify(player).sendActionBar(Messages.EDIT_VISIBLE_ON);
    }

    @Test
    void testGravityEnablesGravity() {
        Player player = playerWithPermission(EditMode.GRAVITY);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);

        EditMode.GRAVITY.edit(editor, armorStand, false);

        Mockito.verify(armorStand).setGravity(true);
        Mockito.verify(player).sendActionBar(Messages.EDIT_GRAVITY_ON);
    }

    @Test
    void testCustomNameVisibilityMakesNameVisible() {
        Player player = playerWithPermission(EditMode.CUSTOM_NAME_VISIBLE);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);

        EditMode.CUSTOM_NAME_VISIBLE.edit(editor, armorStand, false);

        Mockito.verify(armorStand).setCustomNameVisible(true);
        Mockito.verify(player).sendActionBar(Messages.EDIT_CUSTOM_NAME_VISIBLE_ON);
    }

    @Test
    void testLockModeLocksUnlockedArmorStand() {
        Player player = playerWithPermission(EditMode.LOCK);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Mockito.when(armorStand.getUniqueId()).thenReturn(ARMOR_STAND_UUID);
        PlayerEditor editor = new PlayerEditor(player);

        EditMode.LOCK.edit(editor, armorStand, false);

        Assertions.assertTrue(editor.isLocked(armorStand));
        Mockito.verify(player).sendActionBar(Messages.EDIT_LOCK);
    }

    private static Player playerWithPermission(EditMode mode) {
        Player player = Mockito.mock(Player.class);
        Mockito.when(player.hasPermission(mode.getPermission())).thenReturn(true);
        return player;
    }
}
