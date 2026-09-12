package net.okocraft.armorstandeditor.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

final class MenuChecker {

    private static volatile Class<?> customInventoryClass;

    static synchronized void initialize() {
        if (customInventoryClass == null) {
            customInventoryClass = Bukkit.createInventory(null, 54, Component.empty()).getClass();
        }
    }

    static <T> @Nullable T fromInventory(Inventory inventory, Class<T> expectedMenuClass) {
        var customInventoryClass = MenuChecker.customInventoryClass;
        if (customInventoryClass == null || !customInventoryClass.isInstance(inventory)) {
            return null;
        }

        var holder = inventory.getHolder();
        return expectedMenuClass.isInstance(holder) ? expectedMenuClass.cast(holder) : null;
    }
}
