package net.okocraft.armorstandeditor.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class EquipmentMenuProviderTest {

    @Test
    void testOpenMenuRejectsInvalidRegionOwnershipAndDeadArmorStand() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Player viewer = Mockito.mock(Player.class);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            Assertions.assertFalse(EquipmentMenuProvider.openMenu(armorStand, viewer));

            bukkit.when(() -> Bukkit.isOwnedByCurrentRegion(viewer)).thenReturn(true);
            Assertions.assertFalse(EquipmentMenuProvider.openMenu(armorStand, viewer));

            bukkit.when(() -> Bukkit.isOwnedByCurrentRegion(armorStand)).thenReturn(true);
            Mockito.when(armorStand.isDead()).thenReturn(true);
            Assertions.assertFalse(EquipmentMenuProvider.openMenu(armorStand, viewer));
        }
    }

    @Test
    void testReleasedMenuCanBeOpenedByAnotherViewer() {
        UUID armorStandUuid = UUID.randomUUID();
        ArmorStand armorStand = armorStand(armorStandUuid);
        Player firstViewer = viewer(UUID.randomUUID());
        Player secondViewer = viewer(UUID.randomUUID());
        List<EquipmentMenu> menus = new ArrayList<>();

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            stubOwned(bukkit, armorStand, firstViewer, secondViewer);
            stubMenuInventories(bukkit, menus);

            Assertions.assertTrue(EquipmentMenuProvider.openMenu(armorStand, firstViewer));
            EquipmentMenuProvider.release(menus.getFirst(), firstViewer);
            Assertions.assertTrue(EquipmentMenuProvider.openMenu(armorStand, secondViewer));
            EquipmentMenuProvider.release(menus.getLast(), secondViewer);
        }

        Mockito.verify(firstViewer).openInventory(Mockito.any(Inventory.class));
        Mockito.verify(secondViewer).openInventory(Mockito.any(Inventory.class));
    }

    @Test
    void testInactiveViewerReservationIsReleasedBeforeRetry() {
        UUID armorStandUuid = UUID.randomUUID();
        ArmorStand armorStand = armorStand(armorStandUuid);
        Player firstViewer = viewer(UUID.randomUUID());
        Player secondViewer = viewer(UUID.randomUUID());
        List<EquipmentMenu> menus = new ArrayList<>();

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            stubOwned(bukkit, armorStand, firstViewer, secondViewer);
            stubMenuInventories(bukkit, menus);
            bukkit.when(() -> Bukkit.getPlayer(firstViewer.getUniqueId())).thenReturn(null);

            Assertions.assertTrue(EquipmentMenuProvider.openMenu(armorStand, firstViewer));
            Assertions.assertFalse(EquipmentMenuProvider.openMenu(armorStand, secondViewer));
            Assertions.assertTrue(EquipmentMenuProvider.openMenu(armorStand, secondViewer));
            EquipmentMenuProvider.release(menus.getLast(), secondViewer);
        }
    }

    @Test
    void testCloseMenuReleasesReservationWhenViewerIsOffline() {
        UUID armorStandUuid = UUID.randomUUID();
        ArmorStand armorStand = armorStand(armorStandUuid);
        Player firstViewer = viewer(UUID.randomUUID());
        Player secondViewer = viewer(UUID.randomUUID());
        List<EquipmentMenu> menus = new ArrayList<>();

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            stubOwned(bukkit, armorStand, firstViewer, secondViewer);
            stubMenuInventories(bukkit, menus);
            bukkit.when(() -> Bukkit.getPlayer(firstViewer.getUniqueId())).thenReturn(null);

            Assertions.assertTrue(EquipmentMenuProvider.openMenu(armorStand, firstViewer));
            EquipmentMenuProvider.closeMenu(armorStand);
            Assertions.assertTrue(EquipmentMenuProvider.openMenu(armorStand, secondViewer));
            EquipmentMenuProvider.release(menus.getLast(), secondViewer);
        }
    }

    private static ArmorStand armorStand(UUID uuid) {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EntityEquipment equipment = Mockito.mock(EntityEquipment.class);
        ItemStack item = ItemStack.of(Material.STONE);

        Mockito.when(armorStand.getUniqueId()).thenReturn(uuid);
        Mockito.when(armorStand.getEquipment()).thenReturn(equipment);
        Mockito.when(equipment.getItem(Mockito.any())).thenReturn(item);
        return armorStand;
    }

    private static Player viewer(UUID uuid) {
        Player viewer = Mockito.mock(Player.class);
        Mockito.when(viewer.getUniqueId()).thenReturn(uuid);
        Mockito.when(viewer.openInventory(Mockito.any(Inventory.class))).thenReturn(Mockito.mock(InventoryView.class));
        return viewer;
    }

    private static void stubOwned(MockedStatic<Bukkit> bukkit, ArmorStand armorStand, Player... viewers) {
        bukkit.when(() -> Bukkit.isOwnedByCurrentRegion(armorStand)).thenReturn(true);
        for (Player viewer : viewers) {
            bukkit.when(() -> Bukkit.isOwnedByCurrentRegion(viewer)).thenReturn(true);
        }
    }

    private static void stubMenuInventories(MockedStatic<Bukkit> bukkit, List<EquipmentMenu> menus) {
        bukkit.when(() -> Bukkit.createInventory(
            Mockito.<InventoryHolder>any(),
            Mockito.eq(18),
            Mockito.any(Component.class)
        )).thenAnswer(invocation -> {
            menus.add(invocation.getArgument(0));
            return Mockito.mock(Inventory.class);
        });
    }
}
