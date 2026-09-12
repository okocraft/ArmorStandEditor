package net.okocraft.armorstandeditor.listener;

import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.item.EditToolItem;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class PlayerSelectionMenuTest {

    @Test
    void testAuthorizedToolClickWithoutArmorStandTargetOpensSelectionMenu() {
        ArmorStandEditorPlugin plugin = Mockito.mock(ArmorStandEditorPlugin.class);
        EditToolItem editToolItem = Mockito.mock(EditToolItem.class);
        Player player = Mockito.mock(Player.class);
        ItemStack item = Mockito.mock(ItemStack.class);
        Inventory menuInventory = Mockito.mock(Inventory.class);
        PlayerInteractEvent event = Mockito.mock(PlayerInteractEvent.class);

        Mockito.when(plugin.getEditToolItem()).thenReturn(editToolItem);
        Mockito.when(editToolItem.check(item)).thenReturn(true);
        Mockito.when(player.hasPermission(Permissions.ARMOR_STAND_EDIT)).thenReturn(true);
        Mockito.when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        Mockito.when(event.getAction()).thenReturn(Action.RIGHT_CLICK_AIR);
        Mockito.when(event.getItem()).thenReturn(item);
        Mockito.when(event.getPlayer()).thenReturn(player);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.createInventory(
                Mockito.<InventoryHolder>any(),
                Mockito.eq(54),
                Mockito.any(Component.class)
            )).thenReturn(menuInventory);

            new PlayerListener(plugin).onClick(event);
        }

        Mockito.verify(event).setCancelled(true);
        Mockito.verify(player).getTargetEntity(5);
        Mockito.verify(player).openInventory(menuInventory);
    }
}
