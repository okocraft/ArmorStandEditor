package net.okocraft.armorstandeditor.menu;

import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.editor.EditMode;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Locale;
import java.util.UUID;

class SelectionMenuTest {

    @AfterEach
    void unloadEditors() {
        PlayerEditorProvider.unloadAll();
    }

    @Test
    void testVisibleIconsAreRenderedWithTheirMaterials() {
        Player player = Mockito.mock(Player.class);
        Inventory inventory = Mockito.mock(Inventory.class);
        Mockito.when(player.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(player.locale()).thenReturn(Locale.ENGLISH);
        Mockito.when(inventory.getSize()).thenReturn(54);

        createMenu(player, inventory);

        Mockito.verify(inventory).setItem(
            Mockito.eq(0),
            Mockito.argThat(item -> item.getType() == Material.RED_WOOL)
        );
        Mockito.verify(inventory).setItem(
            Mockito.eq(25),
            Mockito.argThat(item -> item.getType() == Material.POTION)
        );
        Mockito.verify(inventory).setItem(
            Mockito.eq(28),
            Mockito.argThat(item -> item.getType() == Material.CHEST)
        );
        Mockito.verify(inventory).setItem(
            Mockito.eq(53),
            Mockito.argThat(item -> item.getType() == Material.NETHER_STAR)
        );
    }

    @Test
    void testClickChangesAxis() {
        Player player = playerWithPermission("x-axis");
        Inventory inventory = Mockito.mock(Inventory.class);
        SelectionMenu menu = createMenu(player, inventory);
        var editor = PlayerEditorProvider.getEditor(player);
        editor.setAxis(Axis.Z);

        menu.onClick(clickEvent(player, inventory, 0));

        Assertions.assertEquals(Axis.X, editor.getAxis());
        Mockito.verify(player).closeInventory();
    }

    @Test
    void testClickChangesMode() {
        Player player = playerWithPermission("movement");
        Inventory inventory = Mockito.mock(Inventory.class);
        SelectionMenu menu = createMenu(player, inventory);
        var editor = PlayerEditorProvider.getEditor(player);

        menu.onClick(clickEvent(player, inventory, 8));

        Assertions.assertSame(EditMode.MOVEMENT, editor.getMode());
        Mockito.verify(player).closeInventory();
    }

    @Test
    void testClickChangesAdjustmentMode() {
        Player player = playerWithPermission("coarse");
        Inventory inventory = Mockito.mock(Inventory.class);
        SelectionMenu menu = createMenu(player, inventory);
        var editor = PlayerEditorProvider.getEditor(player);
        editor.setAngleChangeQuantity(2);
        editor.setMovingDistance(0.1);

        menu.onClick(clickEvent(player, inventory, 4));

        Assertions.assertEquals(20, editor.getAngleChangeQuantity());
        Assertions.assertEquals(1, editor.getMovingDistance());
        Mockito.verify(player).closeInventory();
    }

    @Test
    void testClickChangesCopySlot() {
        Player player = playerWithPermission("copy-slot-3");
        Inventory inventory = Mockito.mock(Inventory.class);
        SelectionMenu menu = createMenu(player, inventory);
        var editor = PlayerEditorProvider.getEditor(player);

        menu.onClick(clickEvent(player, inventory, 47));

        Assertions.assertEquals(3, editor.getSelectedCopySlot());
        Mockito.verify(player).closeInventory();
    }

    @Test
    void testClickWithoutIconPermissionDoesNotApplyAction() {
        Player player = Mockito.mock(Player.class);
        Mockito.when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        Inventory inventory = Mockito.mock(Inventory.class);
        SelectionMenu menu = createMenu(player, inventory);
        var editor = PlayerEditorProvider.getEditor(player);

        menu.onClick(clickEvent(player, inventory, 8));

        Assertions.assertSame(EditMode.NONE, editor.getMode());
        Mockito.verify(player).closeInventory();
    }

    @Test
    void testClickOutsideMenuOnlyCancelsEvent() {
        Player player = Mockito.mock(Player.class);
        Inventory inventory = Mockito.mock(Inventory.class);
        SelectionMenu menu = createMenu(player, inventory);
        InventoryClickEvent event = Mockito.mock(InventoryClickEvent.class);
        Mockito.when(event.getClickedInventory()).thenReturn(Mockito.mock(Inventory.class));

        menu.onClick(event);

        Mockito.verify(event).setCancelled(true);
        Mockito.verify(player, Mockito.never()).closeInventory();
    }

    private static Player playerWithPermission(String iconName) {
        Player player = Mockito.mock(Player.class);
        Mockito.when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        Mockito.when(player.hasPermission(Permissions.ICON_PREFIX + iconName)).thenReturn(true);
        return player;
    }

    private static InventoryClickEvent clickEvent(Player player, Inventory inventory, int slot) {
        InventoryClickEvent event = Mockito.mock(InventoryClickEvent.class);
        Mockito.when(event.getClickedInventory()).thenReturn(inventory);
        Mockito.when(event.getSlot()).thenReturn(slot);
        Mockito.when(event.getWhoClicked()).thenReturn(player);
        return event;
    }

    private static SelectionMenu createMenu(Player player, Inventory inventory) {
        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(
                Mockito.<InventoryHolder>any(),
                Mockito.eq(54),
                Mockito.any(Component.class)
            )).thenReturn(inventory);
            return new SelectionMenu(player);
        }
    }
}
