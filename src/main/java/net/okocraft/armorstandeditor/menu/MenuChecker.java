package net.okocraft.armorstandeditor.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

final class MenuChecker {

    private static final Class<?> CUSTOM_INVENTORY_CLASS;

    static {
        CUSTOM_INVENTORY_CLASS = Bukkit.createInventory(null, 54, Component.empty()).getClass();
    }

    static <T> @Nullable T fromInventory(Inventory inventory, Class<T> expectedMenuClass) {
        if (!CUSTOM_INVENTORY_CLASS.isInstance(inventory)) {
            return null;
        }

        var holder = inventory.getHolder();
        return expectedMenuClass.isInstance(holder) ? expectedMenuClass.cast(holder) : null;
    }
}
