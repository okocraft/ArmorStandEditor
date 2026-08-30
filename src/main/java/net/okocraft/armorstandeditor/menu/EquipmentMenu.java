package net.okocraft.armorstandeditor.menu;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.editor.EditMode;
import net.okocraft.armorstandeditor.lang.Components;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EquipmentMenu implements ArmorStandEditorMenu {

    private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[]{
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
        EquipmentSlot.HAND, EquipmentSlot.OFF_HAND
    };
    private static final ItemStack AIR = new ItemStack(Material.AIR);
    private static final ItemStack HELMET = createIcon(ItemType.LEATHER_HELMET, "Helmet");
    private static final ItemStack CHEST_PLATE = createIcon(ItemType.LEATHER_CHESTPLATE, "Chest-plate");
    private static final ItemStack LEGGINGS = createIcon(ItemType.LEATHER_LEGGINGS, "Leggings");
    private static final ItemStack BOOTS = createIcon(ItemType.LEATHER_BOOTS, "Boots");
    private static final ItemStack RIGHT_HAND = createIcon(ItemType.WOODEN_SWORD, "Right hand");
    private static final ItemStack LEFT_HAND = createIcon(ItemType.SHIELD, "Left hand");
    private static final ItemStack DISABLED = createIcon(ItemType.GRAY_STAINED_GLASS_PANE, "");

    private final Inventory inventory;
    private final UUID armorStandUuid;

    EquipmentMenu(@NotNull UUID armorStandUuid) {
        this.inventory = Bukkit.createInventory(this, 18, Components.EQUIPMENT_MENU_TITLE);
        this.armorStandUuid = armorStandUuid;
        initMenu(this.inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public @NotNull UUID getArmorStandUuid() {
        return this.armorStandUuid;
    }

    boolean open(@NotNull ArmorStand armorStand, @NotNull Player viewer) {
        if (!this.isFor(armorStand.getUniqueId())) {
            return false;
        }

        this.renderItems(armorStand.getEquipment());
        return viewer.openInventory(this.inventory) != null;
    }

    boolean isFor(@NotNull UUID armorStandUuid) {
        return this.armorStandUuid.equals(armorStandUuid);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        var viewer = event.getWhoClicked();
        if (!this.isAuthorized(viewer)) {
            event.setCancelled(true);
            this.close(viewer);
            return;
        }

        var clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }

        if (!this.inventory.equals(clickedInventory)) {
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
                event.setCancelled(true);
            }
            return;
        }

        event.setCancelled(true);

        var slot = toEquipmentSlot(event.getSlot());
        if (slot != null) {
            this.processEquipmentClick(event, slot);
        }
    }

    public void onDrag(@NotNull InventoryDragEvent event) {
        var viewer = event.getWhoClicked();
        if (!this.isAuthorized(viewer)) {
            event.setCancelled(true);
            this.close(viewer);
            return;
        }

        for (var rawSlot : event.getNewItems().keySet()) {
            if (this.inventory.equals(event.getView().getInventory(rawSlot))) {
                event.setCancelled(true);
                return;
            }
        }
    }

    void renderItems(@NotNull EntityEquipment equipment) {
        for (var slot : EQUIPMENT_SLOTS) {
            this.inventory.setItem(toMenuIndex(slot), equipment.getItem(slot).clone());
        }
    }

    private void processEquipmentClick(@NotNull InventoryClickEvent event, @NotNull EquipmentSlot slot) {
        var viewer = event.getWhoClicked();
        var entity = Bukkit.getEntity(this.armorStandUuid);

        if (!(entity instanceof ArmorStand armorStand)) {
            this.close(viewer);
            return;
        }

        if (!Bukkit.isOwnedByCurrentRegion(viewer) || !Bukkit.isOwnedByCurrentRegion(armorStand)) {
            this.close(viewer);
            return;
        }

        if (armorStand.isDead()) {
            this.close(viewer);
            return;
        }

        var equipment = armorStand.getEquipment();
        var equipmentItem = equipment.getItem(slot);

        if (!sameItem(event.getCurrentItem(), equipmentItem)) {
            this.renderItems(equipment);
            return;
        }

        if (viewer.getGameMode() == GameMode.SPECTATOR) {
            this.renderItems(equipment);
            return;
        }

        if (viewer.getGameMode() == GameMode.CREATIVE) {
            this.handleCreativeClick(event, viewer, equipment, slot);
        } else {
            this.handleTransactionalClick(event, viewer, equipment, slot);
        }

        this.renderItems(equipment);
    }

    private void handleTransactionalClick(
        @NotNull InventoryClickEvent event,
        @NotNull HumanEntity viewer,
        @NotNull EntityEquipment equipment,
        @NotNull EquipmentSlot slot
    ) {
        if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) {
            swapCursorAndEquipment(viewer, equipment, slot);
        }
    }

    private void handleCreativeClick(
        @NotNull InventoryClickEvent event,
        @NotNull HumanEntity viewer,
        @NotNull EntityEquipment equipment,
        @NotNull EquipmentSlot slot
    ) {
        switch (event.getAction()) {
            case HOTBAR_SWAP -> swapHotbarAndEquipment(event, viewer, equipment, slot);
            case CLONE_STACK -> cloneEquipmentToCursor(viewer, equipment, slot);
            default -> this.handleTransactionalClick(event, viewer, equipment, slot);
        }
    }

    private void close(@NotNull HumanEntity viewer) {
        viewer.getScheduler().run(
            ArmorStandEditorPlugin.plugin(),
            ignored -> {
                if (this.inventory.equals(viewer.getOpenInventory().getTopInventory())) {
                    viewer.closeInventory();
                }
            },
            null
        );
    }

    private boolean isAuthorized(@NotNull HumanEntity viewer) {
        boolean commandAccess = viewer.hasPermission(Permissions.COMMAND) &&
            viewer.hasPermission(Permissions.COMMAND_EQUIPMENT);
        boolean editModeAccess = viewer.hasPermission(Permissions.ARMOR_STAND_EDIT) &&
            viewer.hasPermission(EditMode.EQUIPMENT.getPermission());
        return commandAccess || editModeAccess;
    }

    private static void swapCursorAndEquipment(
        @NotNull HumanEntity viewer,
        @NotNull EntityEquipment equipment,
        @NotNull EquipmentSlot slot
    ) {
        var cursorItem = viewer.getItemOnCursor().clone();
        var equipmentItem = equipment.getItem(slot).clone();

        equipment.setItem(slot, cursorItem);
        viewer.setItemOnCursor(equipmentItem);
    }

    private static void swapHotbarAndEquipment(
        @NotNull InventoryClickEvent event,
        @NotNull HumanEntity viewer,
        @NotNull EntityEquipment equipment,
        @NotNull EquipmentSlot slot
    ) {
        var playerInventory = viewer.getInventory();
        var equipmentItem = equipment.getItem(slot).clone();
        int hotbarButton = event.getHotbarButton();

        if (hotbarButton >= 0 && hotbarButton <= 8) {
            var hotbarItem = playerInventory.getItem(hotbarButton);
            equipment.setItem(slot, hotbarItem != null ? hotbarItem.clone() : AIR);
            playerInventory.setItem(hotbarButton, equipmentItem);
            return;
        }

        if (event.getClick() == ClickType.SWAP_OFFHAND) {
            var offHandItem = playerInventory.getItemInOffHand().clone();
            equipment.setItem(slot, offHandItem);
            playerInventory.setItemInOffHand(equipmentItem);
        }
    }

    private static void cloneEquipmentToCursor(
        @NotNull HumanEntity viewer,
        @NotNull EntityEquipment equipment,
        @NotNull EquipmentSlot slot
    ) {
        var item = equipment.getItem(slot).clone();
        if (!item.getType().isAir()) {
            item.setAmount(item.getMaxStackSize());
            viewer.setItemOnCursor(item);
        }
    }

    private static boolean sameItem(@Nullable ItemStack first, @NotNull ItemStack second) {
        if (first == null || first.getType().isAir()) {
            return second.getType().isAir();
        }
        return first.equals(second);
    }

    private static @Nullable EquipmentSlot toEquipmentSlot(int menuIndex) {
        return switch (menuIndex) {
            case 9 -> EquipmentSlot.HEAD;
            case 10 -> EquipmentSlot.CHEST;
            case 11 -> EquipmentSlot.LEGS;
            case 12 -> EquipmentSlot.FEET;
            case 15 -> EquipmentSlot.HAND;
            case 16 -> EquipmentSlot.OFF_HAND;
            default -> null;
        };
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

    private static ItemStack createIcon(@NotNull ItemType type, @NotNull String name) {
        ItemStack item = type.createItemStack();
        item.setData(
            DataComponentTypes.CUSTOM_NAME,
            name.isEmpty() ?
                Component.empty() :
                Component.text(name).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
        );
        return item;
    }
}
