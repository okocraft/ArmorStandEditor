package net.okocraft.armorstandeditor.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface ArmorStandEditorMenu extends InventoryHolder {

    void onOpen(@NotNull InventoryOpenEvent event);

    void onClick(@NotNull InventoryClickEvent event);

    void onClose(@NotNull InventoryCloseEvent event);
}
