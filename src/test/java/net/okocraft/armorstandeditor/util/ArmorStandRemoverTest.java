package net.okocraft.armorstandeditor.util;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;

class ArmorStandRemoverTest {

    @Test
    void testArmorStandAndEquipmentAreReturnedBeforeRemoval() {
        Player player = Mockito.mock(Player.class);
        PlayerInventory inventory = Mockito.mock(PlayerInventory.class);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EntityEquipment equipment = Mockito.mock(EntityEquipment.class);
        Mockito.when(player.getInventory()).thenReturn(inventory);
        Mockito.when(armorStand.getEquipment()).thenReturn(equipment);
        Mockito.when(equipment.getHelmet()).thenReturn(ItemStack.of(Material.DIAMOND_HELMET));
        Mockito.when(equipment.getChestplate()).thenReturn(null);
        Mockito.when(equipment.getLeggings()).thenReturn(ItemStack.of(Material.DIAMOND_LEGGINGS));
        Mockito.when(equipment.getBoots()).thenReturn(null);
        Mockito.when(equipment.getItemInMainHand()).thenReturn(ItemStack.of(Material.DIAMOND_SWORD));
        Mockito.when(equipment.getItemInOffHand()).thenReturn(ItemStack.of(Material.SHIELD));
        Mockito.when(inventory.addItem(Mockito.any(ItemStack[].class))).thenReturn(new HashMap<>());

        ArmorStandRemover.remove(new PlayerEditor(player), armorStand, false);

        ArgumentCaptor<ItemStack[]> itemsCaptor = ArgumentCaptor.forClass(ItemStack[].class);
        Mockito.verify(inventory).addItem(itemsCaptor.capture());
        ItemStack[] items = itemsCaptor.getValue();
        Assertions.assertEquals(7, items.length);
        Assertions.assertEquals(Material.ARMOR_STAND, items[0].getType());
        Assertions.assertEquals(Material.DIAMOND_HELMET, items[1].getType());
        Assertions.assertTrue(items[2].getType().isAir());
        Assertions.assertEquals(Material.DIAMOND_LEGGINGS, items[3].getType());
        Assertions.assertTrue(items[4].getType().isAir());
        Assertions.assertEquals(Material.DIAMOND_SWORD, items[5].getType());
        Assertions.assertEquals(Material.SHIELD, items[6].getType());
        Mockito.verify(armorStand).remove();
        Mockito.verify(player).sendActionBar(Messages.EDIT_REMOVAL);
    }

    @Test
    void testItemsThatDoNotFitAreDroppedAtPlayerLocation() {
        Player player = Mockito.mock(Player.class);
        PlayerInventory inventory = Mockito.mock(PlayerInventory.class);
        World world = Mockito.mock(World.class);
        Location location = new Location(world, 1, 2, 3);
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EntityEquipment equipment = Mockito.mock(EntityEquipment.class);
        ItemStack overflow = ItemStack.of(Material.DIAMOND_CHESTPLATE);
        ItemStack air = new ItemStack(Material.AIR);
        HashMap<Integer, ItemStack> returned = new HashMap<>();
        returned.put(0, overflow);
        returned.put(1, air);

        Mockito.when(player.getInventory()).thenReturn(inventory);
        Mockito.when(player.getWorld()).thenReturn(world);
        Mockito.when(player.getLocation()).thenReturn(location);
        Mockito.when(armorStand.getEquipment()).thenReturn(equipment);
        Mockito.when(equipment.getItemInMainHand()).thenReturn(new ItemStack(Material.AIR));
        Mockito.when(equipment.getItemInOffHand()).thenReturn(new ItemStack(Material.AIR));
        Mockito.when(inventory.addItem(Mockito.any(ItemStack[].class))).thenReturn(returned);

        ArmorStandRemover.remove(new PlayerEditor(player), armorStand, false);

        Mockito.verify(world).dropItem(location, overflow);
        Mockito.verify(world, Mockito.never()).dropItem(location, air);
        Mockito.verify(armorStand).remove();
    }
}
