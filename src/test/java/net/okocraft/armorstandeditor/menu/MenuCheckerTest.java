package net.okocraft.armorstandeditor.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class MenuCheckerTest {

    private static Inventory customInventory;

    @BeforeAll
    static void initializeMenuChecker() {
        customInventory = Mockito.mock(Inventory.class);
        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(null, 54, Component.empty())).thenReturn(customInventory);
            MenuChecker.initialize();
        }
    }

    @BeforeEach
    void resetInventory() {
        Mockito.reset(customInventory);
    }

    @Test
    void testExpectedHolderIsReturnedFromCustomInventory() {
        ArmorStandEditorMenu menu = Mockito.mock(ArmorStandEditorMenu.class);
        Mockito.when(customInventory.getHolder()).thenReturn(menu);

        assertSame(menu, MenuChecker.fromInventory(customInventory, ArmorStandEditorMenu.class));
    }

    @Test
    void testDifferentHolderTypeReturnsNull() {
        InventoryHolder holder = Mockito.mock(InventoryHolder.class);
        Mockito.when(customInventory.getHolder()).thenReturn(holder);

        assertNull(MenuChecker.fromInventory(customInventory, ArmorStandEditorMenu.class));
    }

    @Test
    void testNonCustomInventoryReturnsNullWithoutResolvingHolder() {
        PlayerInventory inventory = Mockito.mock(PlayerInventory.class);

        assertNull(MenuChecker.fromInventory(inventory, ArmorStandEditorMenu.class));
        Mockito.verify(inventory, Mockito.never()).getHolder();
    }
}
