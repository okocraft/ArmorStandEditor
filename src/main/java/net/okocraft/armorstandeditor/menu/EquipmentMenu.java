package net.okocraft.armorstandeditor.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.okocraft.armorstandeditor.lang.Components;
import net.okocraft.armorstandeditor.util.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class EquipmentMenu implements ArmorStandEditorMenu {

    private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();
    private static final int[] MENU_EQUIPMENT_SLOT_INDEXES = Arrays.stream(EQUIPMENT_SLOTS).mapToInt(EquipmentMenu::toMenuIndex).toArray();
    private static final Set<Integer> MODIFIABLE_SLOTS = Arrays.stream(MENU_EQUIPMENT_SLOT_INDEXES).boxed().collect(Collectors.toUnmodifiableSet());
    private static final ItemStack AIR = new ItemStack(Material.AIR);
    private static final ItemStack HELMET = new ItemStack(Material.LEATHER_HELMET);
    private static final ItemStack CHEST_PLATE = new ItemStack(Material.LEATHER_CHESTPLATE);
    private static final ItemStack LEGGINGS = new ItemStack(Material.LEATHER_LEGGINGS);
    private static final ItemStack BOOTS = new ItemStack(Material.LEATHER_BOOTS);
    private static final ItemStack RIGHT_HAND = new ItemStack(Material.WOODEN_SWORD);
    private static final ItemStack LEFT_HAND = new ItemStack(Material.SHIELD);
    private static final ItemStack DISABLED = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

    static {
        setItemName(HELMET, "Helmet");
        setItemName(CHEST_PLATE, "Chest-plate");
        setItemName(LEGGINGS, "Leggings");
        setItemName(BOOTS, "Boots");
        setItemName(RIGHT_HAND, "Right-hand");
        setItemName(LEFT_HAND, "Left-hand");
        setItemName(DISABLED, "");
    }

    private final Inventory inventory;
    private final UUID armorStandUuid;
    private final NamespacedKey worldKey;
    private final ItemStack[] knownEquipments = new ItemStack[EQUIPMENT_SLOTS.length];
    private final AtomicBoolean blockModifying = new AtomicBoolean();

    public EquipmentMenu(@NotNull ArmorStand armorStand) {
        this.inventory = Bukkit.createInventory(this, 18, Components.EQUIPMENT_MENU_TITLE);
        this.armorStandUuid = armorStand.getUniqueId();
        this.worldKey = armorStand.getWorld().getKey();

        initMenu(inventory);
        renderItems(armorStand.getEquipment());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (inventory.equals(event.getClickedInventory()) && !MODIFIABLE_SLOTS.contains(event.getSlot())) {
            event.setCancelled(true);
        }

        processEvent(event);
    }

    public void onDrag(@NotNull InventoryDragEvent event) {
        for (var rawSlot : event.getNewItems().keySet()) {
            if (!inventory.equals(event.getView().getInventory(rawSlot))) {
                continue;
            }

            if (!MODIFIABLE_SLOTS.contains(event.getView().convertSlot(rawSlot))) {
                event.setCancelled(true);
                return;
            }
        }

        processEvent(event);
    }

    public void handleManipulateEvent(@NotNull PlayerArmorStandManipulateEvent event) {
        if (blockModifying.compareAndSet(false, true)) {
            TaskScheduler.runNextTick(this::renderItemsIfArmorStandExist);
        } else {
            event.setCancelled(true);
        }
    }

    public void handleDispenseArmorEvent(@NotNull BlockDispenseArmorEvent event) {
        if (blockModifying.compareAndSet(false, true)) {
            TaskScheduler.runNextTick(this::renderItemsIfArmorStandExist);
        } else {
            event.setCancelled(true);
        }
    }

    private void processEvent(@NotNull Cancellable event) {
        if (blockModifying.get()) {
            event.setCancelled(true);
            return;
        }

        var armorStand = getArmorStand();

        if (armorStand == null || armorStand.isDead()) {
            event.setCancelled(true);
            TaskScheduler.runNextTick(this::closeMenu);
            return;
        }

        var equipment = armorStand.getEquipment();

        if (hasUnknownEquipment(equipment)) {
            event.setCancelled(true);
            renderItems(equipment);
            return;
        }

        blockModifying.set(true);
        TaskScheduler.runNextTick(this::applyEquipmentsIfModified);
    }

    public void renderItems(@NotNull EntityEquipment equipment) {
        for (int i = 0; i < EQUIPMENT_SLOTS.length; i++) {
            var slot = EQUIPMENT_SLOTS[i];
            var item = equipment.getItem(slot).clone();

            knownEquipments[i] = item;
            inventory.setItem(MENU_EQUIPMENT_SLOT_INDEXES[i], item);
        }
    }

    public void closeMenu() {
        List.copyOf(inventory.getViewers()).forEach(HumanEntity::closeInventory);
    }

    private void renderItemsIfArmorStandExist() {
        var armorStand = getArmorStand();

        if (armorStand == null || armorStand.isDead()) {
            return;
        }

        renderItems(armorStand.getEquipment());
        blockModifying.set(false);
    }

    private void applyEquipmentsIfModified() {
        var armorStand = getArmorStand();

        if (armorStand == null || armorStand.isDead()) { // If this happens, duplicate/lost items may occur. However, there is nothing we can do.
            return;
        }

        var equipment = armorStand.getEquipment();

        for (int i = 0; i < EQUIPMENT_SLOTS.length; i++) {
            var inventoryItem = getInventoryItem(i).clone();
            var knownEquipment = getKnownEquipment(i);

            if (!inventoryItem.equals(knownEquipment)) {
                knownEquipments[i] = inventoryItem;
                equipment.setItem(EQUIPMENT_SLOTS[i], inventoryItem);
            }
        }

        blockModifying.set(false);
    }

    private @Nullable ArmorStand getArmorStand() {
        var world = Bukkit.getWorld(worldKey);

        if (world == null) {
            return null;
        }

        return world.getEntity(armorStandUuid) instanceof ArmorStand armorStand ? armorStand : null;
    }

    private boolean hasUnknownEquipment(@NotNull EntityEquipment equipment) {
        for (int i = 0; i < EQUIPMENT_SLOTS.length; i++) {
            if (!equipment.getItem(EQUIPMENT_SLOTS[i]).equals(getKnownEquipment(i))) {
                return true;
            }
        }

        return false;
    }

    private @NotNull ItemStack getKnownEquipment(int i) {
        return Objects.requireNonNullElse(knownEquipments[i], AIR);
    }

    private @NotNull ItemStack getInventoryItem(int i) {
        return Objects.requireNonNullElse(inventory.getItem(MENU_EQUIPMENT_SLOT_INDEXES[i]), AIR);
    }

    private static int toMenuIndex(@NotNull EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 9;
            case CHEST -> 10;
            case LEGS -> 11;
            case FEET -> 12;
            case HAND -> 15;
            case OFF_HAND -> 16;
        };
    }

    private static void initMenu(@NotNull Inventory inventory) {
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

    private static void setItemName(@NotNull ItemStack item, @NotNull String name) {
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
