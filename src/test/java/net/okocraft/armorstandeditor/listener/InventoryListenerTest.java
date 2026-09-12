package net.okocraft.armorstandeditor.listener;

import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.menu.ArmorStandEditorMenu;
import net.okocraft.armorstandeditor.menu.EquipmentMenu;
import net.okocraft.armorstandeditor.menu.EquipmentMenuProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
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
    void testDragIsDelegatedToEquipmentMenu() {
        Inventory inventory = Mockito.mock(Inventory.class);
        EquipmentMenu menu = Mockito.mock(EquipmentMenu.class);
        InventoryView view = Mockito.mock(InventoryView.class);
        InventoryDragEvent event = Mockito.mock(InventoryDragEvent.class);
        Mockito.when(inventory.getHolder()).thenReturn(menu);
        Mockito.when(view.getTopInventory()).thenReturn(inventory);
        Mockito.when(event.getView()).thenReturn(view);

        new InventoryListener().onDrag(event);

        Mockito.verify(menu).onDrag(event);
    }

    @Test
    void testCloseReleasesEquipmentMenu() {
        Inventory inventory = Mockito.mock(Inventory.class);
        EquipmentMenu menu = Mockito.mock(EquipmentMenu.class);
        HumanEntity viewer = Mockito.mock(HumanEntity.class);
        InventoryView view = Mockito.mock(InventoryView.class);
        InventoryCloseEvent event = Mockito.mock(InventoryCloseEvent.class);
        Mockito.when(inventory.getHolder()).thenReturn(menu);
        Mockito.when(view.getTopInventory()).thenReturn(inventory);
        Mockito.when(event.getView()).thenReturn(view);
        Mockito.when(event.getPlayer()).thenReturn(viewer);

        try (MockedStatic<EquipmentMenuProvider> provider = Mockito.mockStatic(EquipmentMenuProvider.class)) {
            new InventoryListener().onClose(event);
            provider.verify(() -> EquipmentMenuProvider.release(menu, viewer));
        }
    }
}
