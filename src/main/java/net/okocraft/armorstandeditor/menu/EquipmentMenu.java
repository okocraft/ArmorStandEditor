package net.okocraft.armorstandeditor.menu;

import io.papermc.paper.datacomponent.DataComponentTypes;
import it.unimi.dsi.fastutil.ints.IntSet;
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
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;

public class EquipmentMenu implements ArmorStandEditorMenu {

    private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[]{
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
        EquipmentSlot.HAND, EquipmentSlot.OFF_HAND
    };
    private static final int[] MENU_EQUIPMENT_SLOT_INDEXES = Arrays.stream(EQUIPMENT_SLOTS).mapToInt(EquipmentMenu::toMenuIndex).toArray();
    private static final IntSet MODIFIABLE_SLOTS = IntSet.of(MENU_EQUIPMENT_SLOT_INDEXES);
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
    private final NamespacedKey worldKey;

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
        if (!this.isAuthorized(event.getWhoClicked())) {
            event.setCancelled(true);
            this.closeMenuFor(event.getWhoClicked());
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

        // The menu is only a view of EntityEquipment. Never let a normal inventory
        // transaction move the rendered clones into a player's real inventory.
        event.setCancelled(true);

        if (!MODIFIABLE_SLOTS.contains(event.getSlot())) {
            return;
        }

        this.processEquipmentClick(event);
    }

    public void onDrag(@NotNull InventoryDragEvent event) {
        if (!this.isAuthorized(event.getWhoClicked())) {
            event.setCancelled(true);
            this.closeMenuFor(event.getWhoClicked());
            return;
        }

        for (var rawSlot : event.getNewItems().keySet()) {
            if (this.inventory.equals(event.getView().getInventory(rawSlot))) {
                // Dragging into the ghost inventory would make the rendered copy
                // temporarily authoritative, so keep drag operations player-only.
                event.setCancelled(true);
                return;
            }
        }
    }

    public void handleManipulateEvent(@NotNull PlayerArmorStandManipulateEvent event) {
        this.refreshAfterExternalModification(event.getRightClicked());
    }

    public void handleDispenseArmorEvent(@NotNull BlockDispenseArmorEvent event) {
        var armorStand = this.getArmorStand();
        if (armorStand != null) {
            this.refreshAfterExternalModification(armorStand);
        }
    }

    private void processEquipmentClick(@NotNull InventoryClickEvent event) {
        var armorStand = this.getArmorStand();

        if (armorStand == null || armorStand.isDead()) {
            this.closeMenu();
            return;
        }

        var slot = toEquipmentSlot(event.getSlot());
        if (slot == null) {
            return;
        }

        var equipment = armorStand.getEquipment();
        var viewer = event.getWhoClicked();

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
        if (!event.isShiftClick() && (event.isLeftClick() || event.isRightClick())) {
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
            case CLONE_STACK -> viewer.setItemOnCursor(equipment.getItem(slot).clone());
            case DROP_ALL_SLOT, DROP_ONE_SLOT -> equipment.setItem(slot, AIR);
            default -> this.handleTransactionalClick(event, viewer, equipment, slot);
        }
    }

    private static void swapCursorAndEquipment(
        @NotNull HumanEntity viewer,
        @NotNull EntityEquipment equipment,
        @NotNull EquipmentSlot slot
    ) {
        var cursorItem = viewer.getItemOnCursor().clone();
        var equipmentItem = equipment.getItem(slot).clone();

        equipment.setItem(slot, cursorItem);

        try {
            viewer.setItemOnCursor(equipmentItem);
        } catch (RuntimeException e) {
            equipment.setItem(slot, equipmentItem);
            throw e;
        }
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

        if (hotbarButton >= 0) {
            var hotbarItem = playerInventory.getItem(hotbarButton);
            var replacement = hotbarItem != null ? hotbarItem.clone() : AIR;

            equipment.setItem(slot, replacement);

            try {
                playerInventory.setItem(hotbarButton, equipmentItem);
            } catch (RuntimeException e) {
                equipment.setItem(slot, equipmentItem);
                throw e;
            }
            return;
        }

        if (event.getClick() == ClickType.SWAP_OFFHAND) {
            var offHandItem = playerInventory.getItemInOffHand().clone();
            equipment.setItem(slot, offHandItem);

            try {
                playerInventory.setItemInOffHand(equipmentItem);
            } catch (RuntimeException e) {
                equipment.setItem(slot, equipmentItem);
                throw e;
            }
        }
    }

    public void renderItems(@NotNull EntityEquipment equipment) {
        for (int i = 0; i < EQUIPMENT_SLOTS.length; i++) {
            var slot = EQUIPMENT_SLOTS[i];
            this.inventory.setItem(MENU_EQUIPMENT_SLOT_INDEXES[i], equipment.getItem(slot).clone());
        }
    }

    public void closeMenu() {
        this.inventory.getViewers().forEach(this::closeMenuFor);
    }

    private void closeMenuFor(@NotNull HumanEntity viewer) {
        viewer.getScheduler().run(ArmorStandEditorPlugin.plugin(), ignored -> viewer.closeInventory(), null);
    }

    private boolean isAuthorized(@NotNull HumanEntity viewer) {
        boolean commandAccess = viewer.hasPermission(Permissions.COMMAND) &&
            viewer.hasPermission(Permissions.COMMAND_EQUIPMENT);
        boolean editModeAccess = viewer.hasPermission(Permissions.ARMOR_STAND_EDIT) &&
            viewer.hasPermission(EditMode.EQUIPMENT.getPermission());
        return commandAccess || editModeAccess;
    }

    private void refreshAfterExternalModification(@NotNull ArmorStand armorStand) {
        armorStand.getScheduler().run(
            ArmorStandEditorPlugin.plugin(),
            ignored -> this.renderItemsIfArmorStandExist(armorStand),
            null
        );
    }

    private void renderItemsIfArmorStandExist(@Nullable ArmorStand armorStand) {
        if (armorStand != null && !armorStand.isDead()) {
            this.renderItems(armorStand.getEquipment());
        }
    }

    private @Nullable ArmorStand getArmorStand() {
        var world = Bukkit.getWorld(this.worldKey);
        return world != null && world.getEntity(this.armorStandUuid) instanceof ArmorStand armorStand ? armorStand : null;
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
