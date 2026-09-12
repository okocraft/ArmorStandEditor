package net.okocraft.armorstandeditor.menu;

import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Map;
import java.util.UUID;

class EquipmentMenuTest {

    @Test
    void testConstructorInitializesMenuIcons() {
        Inventory inventory = Mockito.mock(Inventory.class);

        createMenu(UUID.randomUUID(), inventory);

        Mockito.verify(inventory).setItem(Mockito.eq(0), Mockito.argThat(item -> item.getType() == Material.LEATHER_HELMET));
        Mockito.verify(inventory).setItem(Mockito.eq(1), Mockito.argThat(item -> item.getType() == Material.LEATHER_CHESTPLATE));
        Mockito.verify(inventory).setItem(Mockito.eq(2), Mockito.argThat(item -> item.getType() == Material.LEATHER_LEGGINGS));
        Mockito.verify(inventory).setItem(Mockito.eq(3), Mockito.argThat(item -> item.getType() == Material.LEATHER_BOOTS));
        Mockito.verify(inventory).setItem(Mockito.eq(6), Mockito.argThat(item -> item.getType() == Material.WOODEN_SWORD));
        Mockito.verify(inventory).setItem(Mockito.eq(7), Mockito.argThat(item -> item.getType() == Material.SHIELD));
        Mockito.verify(inventory).setItem(Mockito.eq(8), Mockito.argThat(item -> item.getType() == Material.GRAY_STAINED_GLASS_PANE));
    }

    @Test
    void testOpenRejectsDifferentArmorStand() {
        UUID menuUuid = UUID.randomUUID();
        Inventory inventory = Mockito.mock(Inventory.class);
        EquipmentMenu menu = createMenu(menuUuid, inventory);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Player viewer = Mockito.mock(Player.class);
        Mockito.when(armorStand.getUniqueId()).thenReturn(UUID.randomUUID());

        Assertions.assertFalse(menu.open(armorStand, viewer));

        Mockito.verify(armorStand, Mockito.never()).getEquipment();
        Mockito.verify(viewer, Mockito.never()).openInventory(Mockito.any(Inventory.class));
    }

    @Test
    void testOpenRendersEquipmentAndOpensInventory() {
        UUID armorStandUuid = UUID.randomUUID();
        Inventory inventory = Mockito.mock(Inventory.class);
        EquipmentMenu menu = createMenu(armorStandUuid, inventory);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EntityEquipment equipment = equipment();
        Player viewer = Mockito.mock(Player.class);
        Mockito.when(armorStand.getUniqueId()).thenReturn(armorStandUuid);
        Mockito.when(armorStand.getEquipment()).thenReturn(equipment);
        Mockito.when(viewer.openInventory(inventory)).thenReturn(Mockito.mock(InventoryView.class));
        Mockito.clearInvocations(inventory);

        Assertions.assertTrue(menu.open(armorStand, viewer));

        Mockito.verify(inventory).setItem(9, ItemStack.of(Material.DIAMOND_HELMET));
        Mockito.verify(inventory).setItem(10, ItemStack.of(Material.DIAMOND_CHESTPLATE));
        Mockito.verify(inventory).setItem(11, ItemStack.of(Material.DIAMOND_LEGGINGS));
        Mockito.verify(inventory).setItem(12, ItemStack.of(Material.DIAMOND_BOOTS));
        Mockito.verify(inventory).setItem(15, ItemStack.of(Material.DIAMOND_SWORD));
        Mockito.verify(inventory).setItem(16, ItemStack.of(Material.SHIELD));
        Mockito.verify(viewer).openInventory(inventory);
    }

    @Test
    void testUnsafeClickFromPlayerInventoryIsCancelled() {
        Player viewer = authorizedViewer();
        Inventory inventory = Mockito.mock(Inventory.class);
        EquipmentMenu menu = createMenu(UUID.randomUUID(), inventory);
        InventoryClickEvent event = Mockito.mock(InventoryClickEvent.class);
        Mockito.when(event.getWhoClicked()).thenReturn(viewer);
        Mockito.when(event.getClickedInventory()).thenReturn(Mockito.mock(Inventory.class));
        Mockito.when(event.getAction()).thenReturn(InventoryAction.MOVE_TO_OTHER_INVENTORY);

        menu.onClick(event);

        Mockito.verify(event).setCancelled(true);
    }

    @Test
    void testRegularClickFromPlayerInventoryIsIgnored() {
        Player viewer = authorizedViewer();
        Inventory inventory = Mockito.mock(Inventory.class);
        EquipmentMenu menu = createMenu(UUID.randomUUID(), inventory);
        InventoryClickEvent event = Mockito.mock(InventoryClickEvent.class);
        Mockito.when(event.getWhoClicked()).thenReturn(viewer);
        Mockito.when(event.getClickedInventory()).thenReturn(Mockito.mock(Inventory.class));
        Mockito.when(event.getAction()).thenReturn(InventoryAction.PICKUP_ALL);

        menu.onClick(event);

        Mockito.verify(event, Mockito.never()).setCancelled(true);
    }

    @Test
    void testClickOnNonEquipmentMenuSlotIsCancelledWithoutEditingEquipment() {
        Player viewer = authorizedViewer();
        Inventory inventory = Mockito.mock(Inventory.class);
        EquipmentMenu menu = createMenu(UUID.randomUUID(), inventory);
        InventoryClickEvent event = Mockito.mock(InventoryClickEvent.class);
        Mockito.when(event.getWhoClicked()).thenReturn(viewer);
        Mockito.when(event.getClickedInventory()).thenReturn(inventory);
        Mockito.when(event.getSlot()).thenReturn(0);

        menu.onClick(event);

        Mockito.verify(event).setCancelled(true);
    }

    @Test
    void testDragIntoMenuIsCancelled() {
        Player viewer = authorizedViewer();
        Inventory inventory = Mockito.mock(Inventory.class);
        EquipmentMenu menu = createMenu(UUID.randomUUID(), inventory);
        InventoryDragEvent event = Mockito.mock(InventoryDragEvent.class);
        InventoryView view = Mockito.mock(InventoryView.class);
        Mockito.when(event.getWhoClicked()).thenReturn(viewer);
        Mockito.when(event.getNewItems()).thenReturn(Map.of(2, ItemStack.of(Material.STONE)));
        Mockito.when(event.getView()).thenReturn(view);
        Mockito.when(view.getInventory(2)).thenReturn(inventory);

        menu.onDrag(event);

        Mockito.verify(event).setCancelled(true);
    }

    @Test
    void testDragOutsideMenuIsAllowed() {
        Player viewer = authorizedViewer();
        Inventory inventory = Mockito.mock(Inventory.class);
        EquipmentMenu menu = createMenu(UUID.randomUUID(), inventory);
        InventoryDragEvent event = Mockito.mock(InventoryDragEvent.class);
        InventoryView view = Mockito.mock(InventoryView.class);
        Mockito.when(event.getWhoClicked()).thenReturn(viewer);
        Mockito.when(event.getNewItems()).thenReturn(Map.of(20, ItemStack.of(Material.STONE)));
        Mockito.when(event.getView()).thenReturn(view);
        Mockito.when(view.getInventory(20)).thenReturn(Mockito.mock(Inventory.class));

        menu.onDrag(event);

        Mockito.verify(event, Mockito.never()).setCancelled(true);
    }

    private static Player authorizedViewer() {
        Player viewer = Mockito.mock(Player.class);
        Mockito.when(viewer.hasPermission(Permissions.COMMAND)).thenReturn(true);
        Mockito.when(viewer.hasPermission(Permissions.COMMAND_EQUIPMENT)).thenReturn(true);
        return viewer;
    }

    private static EntityEquipment equipment() {
        EntityEquipment equipment = Mockito.mock(EntityEquipment.class);
        Mockito.when(equipment.getItem(EquipmentSlot.HEAD)).thenReturn(ItemStack.of(Material.DIAMOND_HELMET));
        Mockito.when(equipment.getItem(EquipmentSlot.CHEST)).thenReturn(ItemStack.of(Material.DIAMOND_CHESTPLATE));
        Mockito.when(equipment.getItem(EquipmentSlot.LEGS)).thenReturn(ItemStack.of(Material.DIAMOND_LEGGINGS));
        Mockito.when(equipment.getItem(EquipmentSlot.FEET)).thenReturn(ItemStack.of(Material.DIAMOND_BOOTS));
        Mockito.when(equipment.getItem(EquipmentSlot.HAND)).thenReturn(ItemStack.of(Material.DIAMOND_SWORD));
        Mockito.when(equipment.getItem(EquipmentSlot.OFF_HAND)).thenReturn(ItemStack.of(Material.SHIELD));
        return equipment;
    }

    private static EquipmentMenu createMenu(UUID armorStandUuid, Inventory inventory) {
        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(
                Mockito.<InventoryHolder>any(),
                Mockito.eq(18),
                Mockito.any(Component.class)
            )).thenReturn(inventory);
            return new EquipmentMenu(armorStandUuid);
        }
    }
}
