package net.okocraft.armorstandeditor.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface ArmorStandEditorMenu extends InventoryHolder {

    void onClick(@NotNull InventoryClickEvent event);

}
