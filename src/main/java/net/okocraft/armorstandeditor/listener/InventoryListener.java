package net.okocraft.armorstandeditor.listener;

import net.okocraft.armorstandeditor.menu.ArmorStandEditorMenu;
import net.okocraft.armorstandeditor.menu.EquipmentMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        var menu = ArmorStandEditorMenu.getMenuFromInventory(event.getView().getTopInventory(), ArmorStandEditorMenu.class);
        if (menu != null) {
            menu.onClick(event);
        }
    }

    @EventHandler
    public void onDrag(@NotNull InventoryDragEvent event) {
        var menu = ArmorStandEditorMenu.getMenuFromInventory(event.getView().getTopInventory(), EquipmentMenu.class);
        if (menu != null) {
            menu.onDrag(event);
        }
    }
}
