package net.okocraft.armorstandeditor.menu;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

final class MenuChecker {

    static <T> @Nullable T fromInventory(Inventory inventory, Class<T> expectedMenuClass) {
        var holder = inventory.getHolder();
        return expectedMenuClass.isInstance(holder) ? expectedMenuClass.cast(holder) : null;
    }
}
