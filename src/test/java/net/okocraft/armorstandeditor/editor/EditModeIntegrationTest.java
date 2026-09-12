package net.okocraft.armorstandeditor.editor;

import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.menu.EquipmentMenuProvider;
import net.okocraft.armorstandeditor.util.ArmorStandRemover;
import net.okocraft.armorstandeditor.util.FoliaSyncTeleporter;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class EditModeIntegrationTest {

    @Test
    void testNoneModeOpensSelectionMenu() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Inventory inventory = Mockito.mock(Inventory.class);
        PlayerEditor editor = new PlayerEditor(player);
        Mockito.when(player.hasPermission(EditMode.NONE.getPermission())).thenReturn(true);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(
                Mockito.<InventoryHolder>any(),
                Mockito.eq(54),
                Mockito.any(Component.class)
            )).thenReturn(inventory);

            EditMode.NONE.edit(editor, armorStand, false);
        }

        Mockito.verify(player).openInventory(inventory);
    }

    @Test
    void testEquipmentModeOpensEquipmentMenu() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);
        Mockito.when(player.hasPermission(EditMode.EQUIPMENT.getPermission())).thenReturn(true);

        try (MockedStatic<EquipmentMenuProvider> provider = Mockito.mockStatic(EquipmentMenuProvider.class)) {
            EditMode.EQUIPMENT.edit(editor, armorStand, false);
            provider.verify(() -> EquipmentMenuProvider.openMenu(armorStand, player));
        }
    }

    @Test
    void testMovementUsesBukkitTeleportOutsideFolia() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);
        editor.setAxis(Axis.X);
        editor.setMovingDistance(0.5);
        Mockito.when(player.hasPermission(EditMode.MOVEMENT.getPermission())).thenReturn(true);
        Mockito.when(armorStand.getLocation()).thenReturn(new Location(null, 1, 2, 3));

        try (MockedStatic<FoliaSyncTeleporter> teleporter = Mockito.mockStatic(FoliaSyncTeleporter.class)) {
            teleporter.when(FoliaSyncTeleporter::isFolia).thenReturn(false);
            EditMode.MOVEMENT.edit(editor, armorStand, false);
        }

        Mockito.verify(armorStand).teleport(Mockito.argThat(location ->
            location.getX() == 1.5 && location.getY() == 2 && location.getZ() == 3
        ));
    }

    @Test
    void testMovementUsesSynchronousTeleporterOnFolia() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);
        editor.setAxis(Axis.Z);
        editor.setMovingDistance(0.25);
        Mockito.when(player.hasPermission(EditMode.MOVEMENT.getPermission())).thenReturn(true);
        Mockito.when(armorStand.getLocation()).thenReturn(new Location(null, 1, 2, 3));

        try (MockedStatic<FoliaSyncTeleporter> teleporter = Mockito.mockStatic(FoliaSyncTeleporter.class)) {
            teleporter.when(FoliaSyncTeleporter::isFolia).thenReturn(true);

            EditMode.MOVEMENT.edit(editor, armorStand, true);

            teleporter.verify(() -> FoliaSyncTeleporter.teleport(
                Mockito.same(armorStand),
                Mockito.argThat(location -> location.getX() == 1 && location.getY() == 2 && location.getZ() == 2.75)
            ));
        }

        Mockito.verify(armorStand, Mockito.never()).teleport(Mockito.any(Location.class));
    }

    @Test
    void testRemovalClosesEquipmentMenuAndRemovesArmorStand() {
        Player player = Mockito.mock(Player.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        PlayerEditor editor = new PlayerEditor(player);
        Mockito.when(player.hasPermission(EditMode.REMOVAL.getPermission())).thenReturn(true);

        try (MockedStatic<EquipmentMenuProvider> provider = Mockito.mockStatic(EquipmentMenuProvider.class);
             MockedStatic<ArmorStandRemover> remover = Mockito.mockStatic(ArmorStandRemover.class)) {
            EditMode.REMOVAL.edit(editor, armorStand, false);

            provider.verify(() -> EquipmentMenuProvider.closeMenu(armorStand));
            remover.verify(() -> ArmorStandRemover.remove(editor, armorStand, false));
        }
    }
}
