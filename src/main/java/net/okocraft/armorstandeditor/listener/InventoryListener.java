package net.okocraft.armorstandeditor.listener;

import net.okocraft.armorstandeditor.menu.ArmorStandEditorMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        var inventory = event.getClickedInventory();

        if (inventory == null) {
            return;
        }

        var holder = inventory.getHolder();

        if (holder instanceof ArmorStandEditorMenu) {
            ((ArmorStandEditorMenu) holder).onClick(event);
        }
    }

}
