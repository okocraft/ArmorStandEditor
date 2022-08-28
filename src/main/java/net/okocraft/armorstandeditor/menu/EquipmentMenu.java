package net.okocraft.armorstandeditor.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.okocraft.armorstandeditor.lang.Components;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EquipmentMenu implements ArmorStandEditorMenu {

    private static final Set<Integer> EDITABLE_SLOTS = Set.of(9, 10, 11, 12, 15, 16);
    //  private static final String ICON_KEY_PREFIX = "armorstandeditor.menu.equipment.icon.";

    private static final ItemStack AIR = new ItemStack(Material.AIR);
    private static final ItemStack HELMET = new ItemStack(Material.LEATHER_HELMET);
    private static final ItemStack CHEST_PLATE = new ItemStack(Material.LEATHER_CHESTPLATE);
    private static final ItemStack LEGGINGS = new ItemStack(Material.LEATHER_LEGGINGS);
    private static final ItemStack BOOTS = new ItemStack(Material.LEATHER_BOOTS);
    private static final ItemStack RIGHT_HAND = new ItemStack(Material.WOODEN_SWORD);
    private static final ItemStack LEFT_HAND = new ItemStack(Material.SHIELD);
    private static final ItemStack DISABLED = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

    static {
        setName(HELMET, "Helmet");
        setName(CHEST_PLATE, "Chest-plate");
        setName(LEGGINGS, "Leggings");
        setName(BOOTS, "Boots");
        setName(RIGHT_HAND, "Right-hand");
        setName(LEFT_HAND, "Left-hand");
        setName(DISABLED, "");
    }

    private final ArmorStand armorStand;
    private final Inventory inventory;

    public EquipmentMenu(@NotNull ArmorStand armorStand) {
        this.armorStand = armorStand;
        this.inventory = Bukkit.createInventory(this, 18, Components.EQUIPMENT_MENU_TITLE);

        setIcons();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {
        if (armorStand.isDead()) {
            inventory.setItem(9, null);
            inventory.setItem(10, null);
            inventory.setItem(11, null);
            inventory.setItem(12, null);
            inventory.setItem(15, null);
            inventory.setItem(16, null);
        } else {
            EntityEquipment equipment = armorStand.getEquipment();

            inventory.setItem(9, getOrAir(equipment.getHelmet()));
            inventory.setItem(10, getOrAir(equipment.getChestplate()));
            inventory.setItem(11, getOrAir(equipment.getLeggings()));
            inventory.setItem(12, getOrAir(equipment.getBoots()));
            inventory.setItem(15, equipment.getItemInMainHand());
            inventory.setItem(16, equipment.getItemInOffHand());
        }
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (!EDITABLE_SLOTS.contains(event.getSlot())) {
            event.setCancelled(true);
            return;
        }

        if (armorStand.isDead()) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        }
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
        if (armorStand.isDead()) {
            return;
        }

        EntityEquipment equipment = armorStand.getEquipment();

        equipment.setHelmet(inventory.getItem(9));
        equipment.setChestplate(inventory.getItem(10));
        equipment.setLeggings(inventory.getItem(11));
        equipment.setBoots(inventory.getItem(12));
        equipment.setItemInMainHand(inventory.getItem(15));
        equipment.setItemInOffHand(inventory.getItem(16));
    }

    private void setIcons() {
        inventory.setItem(0, HELMET);
        inventory.setItem(1, CHEST_PLATE);
        inventory.setItem(2, LEGGINGS);
        inventory.setItem(3, BOOTS);
        inventory.setItem(4, DISABLED);
        inventory.setItem(5, DISABLED);
        inventory.setItem(6, RIGHT_HAND);
        inventory.setItem(7, LEFT_HAND);
        inventory.setItem(8, DISABLED);
        inventory.setItem(13, DISABLED);
        inventory.setItem(14, DISABLED);
        inventory.setItem(17, DISABLED);
    }

    private static @NotNull ItemStack getOrAir(@Nullable ItemStack item) {
        return item != null ? item : AIR;
    }

    private static void setName(@NotNull ItemStack item, @NotNull String name) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        Component component;

        if (name.isEmpty()) {
            component = Component.empty();
        } else {
            component = Component.text(name).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false);
        }

        meta.displayName(component);

        item.setItemMeta(meta);
    }
}
