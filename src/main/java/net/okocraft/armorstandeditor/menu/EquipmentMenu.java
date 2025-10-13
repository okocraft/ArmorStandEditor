package net.okocraft.armorstandeditor.menu;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.lang.Components;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
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
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class EquipmentMenu implements ArmorStandEditorMenu {

    private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[]{
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
        EquipmentSlot.HAND, EquipmentSlot.OFF_HAND
    };
    private static final int[] MENU_EQUIPMENT_SLOT_INDEXES = Arrays.stream(EQUIPMENT_SLOTS).mapToInt(EquipmentMenu::toMenuIndex).toArray();
    private static final IntSet MODIFIABLE_SLOTS = IntSet.of(MENU_EQUIPMENT_SLOT_INDEXES);
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

        initMenu(this.inventory);
        this.renderItems(armorStand.getEquipment());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        if (this.inventory.equals(event.getClickedInventory()) && !MODIFIABLE_SLOTS.contains(event.getSlot())) {
            event.setCancelled(true);
        }

        this.processEvent(event);
    }

    public void onDrag(@NotNull InventoryDragEvent event) {
        for (var rawSlot : event.getNewItems().keySet()) {
            if (!this.inventory.equals(event.getView().getInventory(rawSlot))) {
                continue;
            }

            if (!MODIFIABLE_SLOTS.contains(event.getView().convertSlot(rawSlot))) {
                event.setCancelled(true);
                return;
            }
        }

        this.processEvent(event);
    }

    public void handleManipulateEvent(@NotNull PlayerArmorStandManipulateEvent event) {
        if (this.blockModifying.compareAndSet(false, true)) {
            ArmorStand armorStand = this.getArmorStand();
            if (armorStand != null) {
                armorStand.getScheduler().run(ArmorStandEditorPlugin.plugin(), ignored -> this.renderItemsIfArmorStandExist(armorStand), null);
                return;
            }
        }

        event.setCancelled(true);
    }

    public void handleDispenseArmorEvent(@NotNull BlockDispenseArmorEvent event) {
        if (this.blockModifying.compareAndSet(false, true)) {
            ArmorStand armorStand = this.getArmorStand();
            if (armorStand != null) {
                armorStand.getScheduler().run(ArmorStandEditorPlugin.plugin(), ignored -> this.renderItemsIfArmorStandExist(armorStand), null);
                return;
            }
        }

        event.setCancelled(true);
    }

    private void processEvent(@NotNull Cancellable event) {
        if (this.blockModifying.get()) {
            event.setCancelled(true);
            return;
        }

        var armorStand = this.getArmorStand();

        if (armorStand == null || armorStand.isDead()) {
            event.setCancelled(true);
            this.closeMenu();
            return;
        }

        this.blockModifying.set(true);

        var equipment = armorStand.getEquipment();

        if (this.hasUnknownEquipment(equipment)) {
            event.setCancelled(true);
            this.renderItems(equipment);
            this.blockModifying.set(false);
            return;
        }

        armorStand.getScheduler().run(ArmorStandEditorPlugin.plugin(), ignored -> this.applyEquipmentsIfModified(armorStand), null);
    }

    public void renderItems(@NotNull EntityEquipment equipment) {
        for (int i = 0; i < EQUIPMENT_SLOTS.length; i++) {
            var slot = EQUIPMENT_SLOTS[i];
            var item = equipment.getItem(slot).clone();

            this.knownEquipments[i] = item;
            this.inventory.setItem(MENU_EQUIPMENT_SLOT_INDEXES[i], item);
        }
    }

    public void closeMenu() {
        this.blockModifying.set(true);
        this.inventory.getViewers().forEach(viewer -> viewer.getScheduler().run(ArmorStandEditorPlugin.plugin(), ignored -> viewer.closeInventory(), null));
    }

    private void renderItemsIfArmorStandExist(@Nullable ArmorStand armorStand) {
        if (armorStand != null && !armorStand.isDead()) {
            this.renderItems(armorStand.getEquipment());
            this.blockModifying.set(false);
        }
    }

    private void applyEquipmentsIfModified(@Nullable ArmorStand armorStand) {
        if (armorStand == null || armorStand.isDead()) { // If this happens, duplicate/lost items may occur. However, there is nothing we can do.
            return;
        }

        var equipment = armorStand.getEquipment();

        for (int i = 0; i < EQUIPMENT_SLOTS.length; i++) {
            var inventoryItem = this.getInventoryItem(i).clone();
            var knownEquipment = this.getKnownEquipment(i);

            if (!inventoryItem.equals(knownEquipment)) {
                this.knownEquipments[i] = inventoryItem;
                equipment.setItem(EQUIPMENT_SLOTS[i], inventoryItem);
            }
        }

        this.blockModifying.set(false);
    }

    private @Nullable ArmorStand getArmorStand() {
        var world = Bukkit.getWorld(this.worldKey);
        return world != null && world.getEntity(this.armorStandUuid) instanceof ArmorStand armorStand ? armorStand : null;
    }

    private boolean hasUnknownEquipment(@NotNull EntityEquipment equipment) {
        for (int i = 0; i < EQUIPMENT_SLOTS.length; i++) {
            if (!equipment.getItem(EQUIPMENT_SLOTS[i]).equals(this.getKnownEquipment(i))) {
                return true;
            }
        }

        return false;
    }

    private @NotNull ItemStack getKnownEquipment(int i) {
        return Objects.requireNonNullElse(this.knownEquipments[i], AIR);
    }

    private @NotNull ItemStack getInventoryItem(int i) {
        return Objects.requireNonNullElse(this.inventory.getItem(MENU_EQUIPMENT_SLOT_INDEXES[i]), AIR);
    }

    private static int toMenuIndex(@NotNull EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 9;
            case CHEST -> 10;
            case LEGS -> 11;
            case FEET -> 12;
            case HAND -> 15;
            case OFF_HAND -> 16;
            default -> throw new AssertionError("No other EquipmentSlot should come here");
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
