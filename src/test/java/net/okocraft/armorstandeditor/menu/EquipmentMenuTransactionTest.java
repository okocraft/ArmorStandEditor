package net.okocraft.armorstandeditor.menu;

import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

import static net.okocraft.armorstandeditor.testsupport.TestIds.ARMOR_STAND_UUID;

class EquipmentMenuTransactionTest {

    @Test
    void testSurvivalClickSwapsCursorAndEquipment() {
        UUID armorStandUuid = ARMOR_STAND_UUID;
        Inventory inventory = Mockito.mock(Inventory.class);
        Player viewer = authorizedViewer(GameMode.SURVIVAL);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EntityEquipment equipment = equipment(ItemStack.of(Material.DIAMOND_HELMET));
        ItemStack cursor = ItemStack.of(Material.APPLE);
        Mockito.when(armorStand.getEquipment()).thenReturn(equipment);
        Mockito.when(viewer.getItemOnCursor()).thenReturn(cursor);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            EquipmentMenu menu = createMenu(bukkit, armorStandUuid, inventory);
            stubEditableArmorStand(bukkit, armorStandUuid, armorStand, viewer);
            InventoryClickEvent event = equipmentClick(viewer, inventory, ItemStack.of(Material.DIAMOND_HELMET));
            Mockito.when(event.getClick()).thenReturn(ClickType.LEFT);

            menu.onClick(event);
        }

        Mockito.verify(equipment).setItem(EquipmentSlot.HEAD, ItemStack.of(Material.APPLE));
        Mockito.verify(viewer).setItemOnCursor(ItemStack.of(Material.DIAMOND_HELMET));
    }

    @Test
    void testSpectatorClickDoesNotChangeEquipment() {
        UUID armorStandUuid = ARMOR_STAND_UUID;
        Inventory inventory = Mockito.mock(Inventory.class);
        Player viewer = authorizedViewer(GameMode.SPECTATOR);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EntityEquipment equipment = equipment(ItemStack.of(Material.DIAMOND_HELMET));
        Mockito.when(armorStand.getEquipment()).thenReturn(equipment);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            EquipmentMenu menu = createMenu(bukkit, armorStandUuid, inventory);
            stubEditableArmorStand(bukkit, armorStandUuid, armorStand, viewer);
            InventoryClickEvent event = equipmentClick(viewer, inventory, ItemStack.of(Material.DIAMOND_HELMET));
            Mockito.when(event.getClick()).thenReturn(ClickType.LEFT);

            menu.onClick(event);
        }

        Mockito.verify(equipment, Mockito.never()).setItem(Mockito.any(EquipmentSlot.class), Mockito.any(ItemStack.class));
        Mockito.verify(viewer, Mockito.never()).setItemOnCursor(Mockito.any(ItemStack.class));
    }

    @Test
    void testStaleDisplayedItemDoesNotChangeEquipment() {
        UUID armorStandUuid = ARMOR_STAND_UUID;
        Inventory inventory = Mockito.mock(Inventory.class);
        Player viewer = authorizedViewer(GameMode.SURVIVAL);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EntityEquipment equipment = equipment(ItemStack.of(Material.DIAMOND_HELMET));
        Mockito.when(armorStand.getEquipment()).thenReturn(equipment);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            EquipmentMenu menu = createMenu(bukkit, armorStandUuid, inventory);
            stubEditableArmorStand(bukkit, armorStandUuid, armorStand, viewer);
            InventoryClickEvent event = equipmentClick(viewer, inventory, ItemStack.of(Material.IRON_HELMET));
            Mockito.when(event.getClick()).thenReturn(ClickType.LEFT);

            menu.onClick(event);
        }

        Mockito.verify(equipment, Mockito.never()).setItem(Mockito.any(EquipmentSlot.class), Mockito.any(ItemStack.class));
        Mockito.verify(viewer, Mockito.never()).setItemOnCursor(Mockito.any(ItemStack.class));
    }

    @Test
    void testCreativeHotbarSwapExchangesItems() {
        UUID armorStandUuid = ARMOR_STAND_UUID;
        Inventory inventory = Mockito.mock(Inventory.class);
        Player viewer = authorizedViewer(GameMode.CREATIVE);
        PlayerInventory playerInventory = Mockito.mock(PlayerInventory.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EntityEquipment equipment = equipment(ItemStack.of(Material.DIAMOND_HELMET));
        Mockito.when(armorStand.getEquipment()).thenReturn(equipment);
        Mockito.when(viewer.getInventory()).thenReturn(playerInventory);
        Mockito.when(playerInventory.getItem(2)).thenReturn(ItemStack.of(Material.APPLE));

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            EquipmentMenu menu = createMenu(bukkit, armorStandUuid, inventory);
            stubEditableArmorStand(bukkit, armorStandUuid, armorStand, viewer);
            InventoryClickEvent event = equipmentClick(viewer, inventory, ItemStack.of(Material.DIAMOND_HELMET));
            Mockito.when(event.getAction()).thenReturn(InventoryAction.HOTBAR_SWAP);
            Mockito.when(event.getHotbarButton()).thenReturn(2);

            menu.onClick(event);
        }

        Mockito.verify(equipment).setItem(EquipmentSlot.HEAD, ItemStack.of(Material.APPLE));
        Mockito.verify(playerInventory).setItem(2, ItemStack.of(Material.DIAMOND_HELMET));
    }

    @Test
    void testCreativeOffhandSwapExchangesItems() {
        UUID armorStandUuid = ARMOR_STAND_UUID;
        Inventory inventory = Mockito.mock(Inventory.class);
        Player viewer = authorizedViewer(GameMode.CREATIVE);
        PlayerInventory playerInventory = Mockito.mock(PlayerInventory.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EntityEquipment equipment = equipment(ItemStack.of(Material.DIAMOND_HELMET));
        Mockito.when(armorStand.getEquipment()).thenReturn(equipment);
        Mockito.when(viewer.getInventory()).thenReturn(playerInventory);
        Mockito.when(playerInventory.getItemInOffHand()).thenReturn(ItemStack.of(Material.SHIELD));

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            EquipmentMenu menu = createMenu(bukkit, armorStandUuid, inventory);
            stubEditableArmorStand(bukkit, armorStandUuid, armorStand, viewer);
            InventoryClickEvent event = equipmentClick(viewer, inventory, ItemStack.of(Material.DIAMOND_HELMET));
            Mockito.when(event.getAction()).thenReturn(InventoryAction.HOTBAR_SWAP);
            Mockito.when(event.getHotbarButton()).thenReturn(-1);
            Mockito.when(event.getClick()).thenReturn(ClickType.SWAP_OFFHAND);

            menu.onClick(event);
        }

        Mockito.verify(equipment).setItem(EquipmentSlot.HEAD, ItemStack.of(Material.SHIELD));
        Mockito.verify(playerInventory).setItemInOffHand(ItemStack.of(Material.DIAMOND_HELMET));
    }

    @Test
    void testCreativeCloneFillsItemToMaximumStackSize() {
        UUID armorStandUuid = ARMOR_STAND_UUID;
        Inventory inventory = Mockito.mock(Inventory.class);
        Player viewer = authorizedViewer(GameMode.CREATIVE);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EntityEquipment equipment = equipment(ItemStack.of(Material.STONE));
        Mockito.when(armorStand.getEquipment()).thenReturn(equipment);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            EquipmentMenu menu = createMenu(bukkit, armorStandUuid, inventory);
            stubEditableArmorStand(bukkit, armorStandUuid, armorStand, viewer);
            InventoryClickEvent event = equipmentClick(viewer, inventory, ItemStack.of(Material.STONE));
            Mockito.when(event.getAction()).thenReturn(InventoryAction.CLONE_STACK);

            menu.onClick(event);
        }

        Mockito.verify(viewer).setItemOnCursor(Mockito.argThat(item ->
            item.getType() == Material.STONE && item.getAmount() == item.getMaxStackSize()
        ));
        Mockito.verify(equipment, Mockito.never()).setItem(Mockito.any(EquipmentSlot.class), Mockito.any(ItemStack.class));
    }

    private static Player authorizedViewer(GameMode gameMode) {
        Player viewer = Mockito.mock(Player.class);
        Mockito.when(viewer.hasPermission(Permissions.COMMAND)).thenReturn(true);
        Mockito.when(viewer.hasPermission(Permissions.COMMAND_EQUIPMENT)).thenReturn(true);
        Mockito.when(viewer.getGameMode()).thenReturn(gameMode);
        return viewer;
    }

    private static EntityEquipment equipment(ItemStack headItem) {
        EntityEquipment equipment = Mockito.mock(EntityEquipment.class);
        Mockito.when(equipment.getItem(Mockito.any(EquipmentSlot.class))).thenReturn(ItemStack.of(Material.STONE));
        Mockito.when(equipment.getItem(EquipmentSlot.HEAD)).thenReturn(headItem);
        return equipment;
    }

    private static InventoryClickEvent equipmentClick(Player viewer, Inventory inventory, ItemStack currentItem) {
        InventoryClickEvent event = Mockito.mock(InventoryClickEvent.class);
        Mockito.when(event.getWhoClicked()).thenReturn(viewer);
        Mockito.when(event.getClickedInventory()).thenReturn(inventory);
        Mockito.when(event.getSlot()).thenReturn(9);
        Mockito.when(event.getCurrentItem()).thenReturn(currentItem);
        return event;
    }

    private static EquipmentMenu createMenu(MockedStatic<Bukkit> bukkit, UUID armorStandUuid, Inventory inventory) {
        bukkit.when(() -> Bukkit.createInventory(
            Mockito.<InventoryHolder>any(),
            Mockito.eq(18),
            Mockito.any(Component.class)
        )).thenReturn(inventory);
        return new EquipmentMenu(armorStandUuid);
    }

    private static void stubEditableArmorStand(
        MockedStatic<Bukkit> bukkit,
        UUID armorStandUuid,
        ArmorStand armorStand,
        Player viewer
    ) {
        bukkit.when(() -> Bukkit.getEntity(armorStandUuid)).thenReturn(armorStand);
        bukkit.when(() -> Bukkit.isOwnedByCurrentRegion(viewer)).thenReturn(true);
        bukkit.when(() -> Bukkit.isOwnedByCurrentRegion(armorStand)).thenReturn(true);
    }
}
