package net.okocraft.armorstandeditor.menu;

import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.editor.EditMode;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Map;
import java.util.UUID;

class EquipmentMenuAccessTest {

    @Test
    void testEditModePermissionsAuthorizeViewer() {
        Player viewer = Mockito.mock(Player.class);
        Inventory inventory = Mockito.mock(Inventory.class);
        InventoryClickEvent event = Mockito.mock(InventoryClickEvent.class);
        Mockito.when(viewer.hasPermission(Permissions.ARMOR_STAND_EDIT)).thenReturn(true);
        Mockito.when(viewer.hasPermission(EditMode.EQUIPMENT.getPermission())).thenReturn(true);
        Mockito.when(event.getWhoClicked()).thenReturn(viewer);
        Mockito.when(event.getClickedInventory()).thenReturn(Mockito.mock(Inventory.class));

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            EquipmentMenu menu = createMenu(bukkit, UUID.randomUUID(), inventory);
            menu.onClick(event);
        }

        Mockito.verify(event, Mockito.never()).setCancelled(true);
    }

    @Test
    void testUnauthorizedClickIsCancelledAndSchedulesClose() {
        Player viewer = Mockito.mock(Player.class);
        EntityScheduler scheduler = Mockito.mock(EntityScheduler.class);
        Inventory inventory = Mockito.mock(Inventory.class);
        InventoryClickEvent event = Mockito.mock(InventoryClickEvent.class);
        ArmorStandEditorPlugin plugin = Mockito.mock(ArmorStandEditorPlugin.class);
        Mockito.when(viewer.getScheduler()).thenReturn(scheduler);
        Mockito.when(event.getWhoClicked()).thenReturn(viewer);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class);
             MockedStatic<ArmorStandEditorPlugin> pluginClass = Mockito.mockStatic(ArmorStandEditorPlugin.class)) {
            pluginClass.when(ArmorStandEditorPlugin::plugin).thenReturn(plugin);
            EquipmentMenu menu = createMenu(bukkit, UUID.randomUUID(), inventory);
            menu.onClick(event);
        }

        Mockito.verify(event).setCancelled(true);
        Mockito.verify(scheduler).run(Mockito.same(plugin), Mockito.any(), Mockito.isNull());
    }

    @Test
    void testUnauthorizedDragIsCancelledAndSchedulesClose() {
        Player viewer = Mockito.mock(Player.class);
        EntityScheduler scheduler = Mockito.mock(EntityScheduler.class);
        Inventory inventory = Mockito.mock(Inventory.class);
        InventoryDragEvent event = Mockito.mock(InventoryDragEvent.class);
        ArmorStandEditorPlugin plugin = Mockito.mock(ArmorStandEditorPlugin.class);
        Mockito.when(viewer.getScheduler()).thenReturn(scheduler);
        Mockito.when(event.getWhoClicked()).thenReturn(viewer);
        Mockito.when(event.getNewItems()).thenReturn(Map.of());

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class);
             MockedStatic<ArmorStandEditorPlugin> pluginClass = Mockito.mockStatic(ArmorStandEditorPlugin.class)) {
            pluginClass.when(ArmorStandEditorPlugin::plugin).thenReturn(plugin);
            EquipmentMenu menu = createMenu(bukkit, UUID.randomUUID(), inventory);
            menu.onDrag(event);
        }

        Mockito.verify(event).setCancelled(true);
        Mockito.verify(scheduler).run(Mockito.same(plugin), Mockito.any(), Mockito.isNull());
    }

    @Test
    void testMissingArmorStandSchedulesClose() {
        UUID armorStandUuid = UUID.randomUUID();
        Player viewer = authorizedViewer();
        EntityScheduler scheduler = Mockito.mock(EntityScheduler.class);
        Inventory inventory = Mockito.mock(Inventory.class);
        InventoryClickEvent event = equipmentClick(viewer, inventory);
        ArmorStandEditorPlugin plugin = Mockito.mock(ArmorStandEditorPlugin.class);
        Mockito.when(viewer.getScheduler()).thenReturn(scheduler);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class);
             MockedStatic<ArmorStandEditorPlugin> pluginClass = Mockito.mockStatic(ArmorStandEditorPlugin.class)) {
            pluginClass.when(ArmorStandEditorPlugin::plugin).thenReturn(plugin);
            EquipmentMenu menu = createMenu(bukkit, armorStandUuid, inventory);
            menu.onClick(event);
        }

        Mockito.verify(event).setCancelled(true);
        Mockito.verify(scheduler).run(Mockito.same(plugin), Mockito.any(), Mockito.isNull());
    }

    @Test
    void testViewerOutsideCurrentRegionSchedulesClose() {
        UUID armorStandUuid = UUID.randomUUID();
        Player viewer = authorizedViewer();
        EntityScheduler scheduler = Mockito.mock(EntityScheduler.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Inventory inventory = Mockito.mock(Inventory.class);
        InventoryClickEvent event = equipmentClick(viewer, inventory);
        ArmorStandEditorPlugin plugin = Mockito.mock(ArmorStandEditorPlugin.class);
        Mockito.when(viewer.getScheduler()).thenReturn(scheduler);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class);
             MockedStatic<ArmorStandEditorPlugin> pluginClass = Mockito.mockStatic(ArmorStandEditorPlugin.class)) {
            pluginClass.when(ArmorStandEditorPlugin::plugin).thenReturn(plugin);
            bukkit.when(() -> Bukkit.getEntity(armorStandUuid)).thenReturn(armorStand);
            EquipmentMenu menu = createMenu(bukkit, armorStandUuid, inventory);
            menu.onClick(event);
        }

        Mockito.verify(scheduler).run(Mockito.same(plugin), Mockito.any(), Mockito.isNull());
    }

    @Test
    void testArmorStandOutsideCurrentRegionSchedulesClose() {
        UUID armorStandUuid = UUID.randomUUID();
        Player viewer = authorizedViewer();
        EntityScheduler scheduler = Mockito.mock(EntityScheduler.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Inventory inventory = Mockito.mock(Inventory.class);
        InventoryClickEvent event = equipmentClick(viewer, inventory);
        ArmorStandEditorPlugin plugin = Mockito.mock(ArmorStandEditorPlugin.class);
        Mockito.when(viewer.getScheduler()).thenReturn(scheduler);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class);
             MockedStatic<ArmorStandEditorPlugin> pluginClass = Mockito.mockStatic(ArmorStandEditorPlugin.class)) {
            pluginClass.when(ArmorStandEditorPlugin::plugin).thenReturn(plugin);
            bukkit.when(() -> Bukkit.getEntity(armorStandUuid)).thenReturn(armorStand);
            bukkit.when(() -> Bukkit.isOwnedByCurrentRegion(viewer)).thenReturn(true);
            EquipmentMenu menu = createMenu(bukkit, armorStandUuid, inventory);
            menu.onClick(event);
        }

        Mockito.verify(scheduler).run(Mockito.same(plugin), Mockito.any(), Mockito.isNull());
    }

    @Test
    void testDeadArmorStandSchedulesClose() {
        UUID armorStandUuid = UUID.randomUUID();
        Player viewer = authorizedViewer();
        EntityScheduler scheduler = Mockito.mock(EntityScheduler.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        Inventory inventory = Mockito.mock(Inventory.class);
        InventoryClickEvent event = equipmentClick(viewer, inventory);
        ArmorStandEditorPlugin plugin = Mockito.mock(ArmorStandEditorPlugin.class);
        Mockito.when(viewer.getScheduler()).thenReturn(scheduler);
        Mockito.when(armorStand.isDead()).thenReturn(true);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class);
             MockedStatic<ArmorStandEditorPlugin> pluginClass = Mockito.mockStatic(ArmorStandEditorPlugin.class)) {
            pluginClass.when(ArmorStandEditorPlugin::plugin).thenReturn(plugin);
            bukkit.when(() -> Bukkit.getEntity(armorStandUuid)).thenReturn(armorStand);
            bukkit.when(() -> Bukkit.isOwnedByCurrentRegion(viewer)).thenReturn(true);
            bukkit.when(() -> Bukkit.isOwnedByCurrentRegion(armorStand)).thenReturn(true);
            EquipmentMenu menu = createMenu(bukkit, armorStandUuid, inventory);
            menu.onClick(event);
        }

        Mockito.verify(scheduler).run(Mockito.same(plugin), Mockito.any(), Mockito.isNull());
    }

    private static Player authorizedViewer() {
        Player viewer = Mockito.mock(Player.class);
        Mockito.when(viewer.hasPermission(Permissions.COMMAND)).thenReturn(true);
        Mockito.when(viewer.hasPermission(Permissions.COMMAND_EQUIPMENT)).thenReturn(true);
        return viewer;
    }

    private static InventoryClickEvent equipmentClick(Player viewer, Inventory inventory) {
        InventoryClickEvent event = Mockito.mock(InventoryClickEvent.class);
        Mockito.when(event.getWhoClicked()).thenReturn(viewer);
        Mockito.when(event.getClickedInventory()).thenReturn(inventory);
        Mockito.when(event.getSlot()).thenReturn(9);
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
}
