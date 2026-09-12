package net.okocraft.armorstandeditor.listener;

import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.menu.ArmorStandEditorMenu;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class InventoryListenerTest {

    @BeforeAll
    static void initializeMenuChecker() {
        Inventory inventory = Mockito.mock(Inventory.class);
        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(null, 54, Component.empty())).thenReturn(inventory);
            ArmorStandEditorMenu.initialize();
        }
    }

    @Test
    void testClickIsDelegatedToArmorStandEditorMenu() {
        Inventory inventory = Mockito.mock(Inventory.class);
        ArmorStandEditorMenu menu = Mockito.mock(ArmorStandEditorMenu.class);
        InventoryView view = Mockito.mock(InventoryView.class);
        InventoryClickEvent event = Mockito.mock(InventoryClickEvent.class);
        Mockito.when(inventory.getHolder()).thenReturn(menu);
        Mockito.when(view.getTopInventory()).thenReturn(inventory);
        Mockito.when(event.getView()).thenReturn(view);

        new InventoryListener().onClick(event);

        Mockito.verify(menu).onClick(event);
    }

    @Test
    void testClickOnOtherInventoryIsIgnored() {
        PlayerInventory inventory = Mockito.mock(PlayerInventory.class);
        InventoryView view = Mockito.mock(InventoryView.class);
        InventoryClickEvent event = Mockito.mock(InventoryClickEvent.class);
        Mockito.when(view.getTopInventory()).thenReturn(inventory);
        Mockito.when(event.getView()).thenReturn(view);

        new InventoryListener().onClick(event);

        Mockito.verify(inventory, Mockito.never()).getHolder();
    }
}
