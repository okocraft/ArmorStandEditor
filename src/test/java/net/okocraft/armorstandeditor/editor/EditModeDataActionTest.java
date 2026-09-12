package net.okocraft.armorstandeditor.editor;

import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EditModeDataActionTest {

    @Test
    void testCopyStoresCurrentArmorStandAndReportsSelectedSlot() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = copySource();
        PlayerEditor editor = new PlayerEditor(player);
        editor.setSelectedCopySlot(3);
        Mockito.when(player.hasPermission(EditMode.COPY.getPermission())).thenReturn(true);

        EditMode.COPY.edit(editor, armorStand, false);

        Assertions.assertNotNull(editor.getSelectedArmorStand());
        Mockito.verify(player).sendActionBar(Messages.EDIT_COPY.apply(3));
    }

    @Test
    void testPasteWithoutCopiedDataDoesNothing() {
        Player player = Mockito.mock(Player.class);
        ArmorStand target = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);
        Mockito.when(player.hasPermission(EditMode.PASTE.getPermission())).thenReturn(true);

        EditMode.PASTE.edit(editor, target, false);

        Mockito.verify(target, Mockito.never()).setSmall(Mockito.anyBoolean());
        Mockito.verify(target, Mockito.never()).setItem(Mockito.any(EquipmentSlot.class), Mockito.any(ItemStack.class));
        Mockito.verify(player, Mockito.never()).sendActionBar(Messages.EDIT_PASTE.apply(1));
    }

    @Test
    void testPasteInSurvivalAppliesNonCreativeState() {
        Player player = Mockito.mock(Player.class);
        ArmorStand source = copySource();
        ArmorStand target = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);
        editor.copy(source);
        Mockito.when(player.hasPermission(EditMode.PASTE.getPermission())).thenReturn(true);
        Mockito.when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);

        EditMode.PASTE.edit(editor, target, false);

        Mockito.verify(target).setSmall(true);
        Mockito.verify(target).setVisible(false);
        Mockito.verify(target, Mockito.never()).customName(Mockito.any());
        Mockito.verify(target, Mockito.never()).setItem(Mockito.any(EquipmentSlot.class), Mockito.any(ItemStack.class));
        Mockito.verify(player).sendActionBar(Messages.EDIT_PASTE.apply(1));
    }

    @Test
    void testPasteInCreativeAlsoAppliesNameAndEquipment() {
        Player player = Mockito.mock(Player.class);
        ArmorStand source = copySource();
        ArmorStand target = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);
        editor.copy(source);
        Mockito.when(player.hasPermission(EditMode.PASTE.getPermission())).thenReturn(true);
        Mockito.when(player.getGameMode()).thenReturn(GameMode.CREATIVE);

        EditMode.PASTE.edit(editor, target, false);

        Mockito.verify(target).customName(Component.text("source"));
        Mockito.verify(target).setCustomNameVisible(true);
        Mockito.verify(target).setItem(EquipmentSlot.HEAD, ItemStack.of(Material.DIAMOND_HELMET));
        Mockito.verify(player).sendActionBar(Messages.EDIT_PASTE.apply(1));
    }

    private static ArmorStand copySource() {
        ArmorStand source = Mockito.mock(ArmorStand.class);
        EulerAngle angle = new EulerAngle(0.1, 0.2, 0.3);
        Mockito.when(source.getHeadPose()).thenReturn(angle);
        Mockito.when(source.getBodyPose()).thenReturn(angle);
        Mockito.when(source.getRightArmPose()).thenReturn(angle);
        Mockito.when(source.getLeftArmPose()).thenReturn(angle);
        Mockito.when(source.getRightLegPose()).thenReturn(angle);
        Mockito.when(source.getLeftLegPose()).thenReturn(angle);
        Mockito.when(source.isSmall()).thenReturn(true);
        Mockito.when(source.isVisible()).thenReturn(false);
        Mockito.when(source.isGlowing()).thenReturn(true);
        Mockito.when(source.hasGravity()).thenReturn(false);
        Mockito.when(source.hasArms()).thenReturn(true);
        Mockito.when(source.hasBasePlate()).thenReturn(false);
        Mockito.when(source.isCustomNameVisible()).thenReturn(true);
        Mockito.when(source.customName()).thenReturn(Component.text("source"));
        Mockito.when(source.getItem(Mockito.any(EquipmentSlot.class))).thenAnswer(invocation -> switch ((EquipmentSlot) invocation.getArgument(0)) {
            case HEAD -> ItemStack.of(Material.DIAMOND_HELMET);
            case CHEST -> ItemStack.of(Material.DIAMOND_CHESTPLATE);
            case LEGS -> ItemStack.of(Material.DIAMOND_LEGGINGS);
            case FEET -> ItemStack.of(Material.DIAMOND_BOOTS);
            case HAND -> ItemStack.of(Material.DIAMOND_SWORD);
            case OFF_HAND -> ItemStack.of(Material.SHIELD);
            default -> ItemStack.empty();
        });
        return source;
    }
}
