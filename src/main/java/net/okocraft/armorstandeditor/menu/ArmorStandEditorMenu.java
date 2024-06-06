package net.okocraft.armorstandeditor.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ArmorStandEditorMenu extends InventoryHolder {

    static <T> @Nullable T getMenuFromInventory(Inventory inventory, Class<T> expectedMenuClass) {
        return MenuChecker.fromInventory(inventory, expectedMenuClass);
    }

    static boolean isArmorStandEditorMenu(Inventory inventory) {
        return MenuChecker.fromInventory(inventory, ArmorStandEditorMenu.class) != null;
    }

    void onClick(@NotNull InventoryClickEvent event);

}
