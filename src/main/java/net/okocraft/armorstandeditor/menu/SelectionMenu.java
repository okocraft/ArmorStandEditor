package net.okocraft.armorstandeditor.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.translation.GlobalTranslator;
import net.okocraft.armorstandeditor.editmode.Mode;
import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.lang.Components;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SelectionMenu implements ArmorStandEditorMenu {

    private static final Map<Integer, Icon> MENU_MAP;

    static {
        var map = new HashMap<Integer, Icon>();

        map.put(0, new Icon(Material.RED_WOOL, "x-axis", editor -> editor.setAxis(PlayerEditor.Axis.X)));
        map.put(1, new Icon(Material.GREEN_WOOL, "y-axis", editor -> editor.setAxis(PlayerEditor.Axis.Y)));
        map.put(2, new Icon(Material.BLUE_WOOL, "z-axis", editor -> editor.setAxis(PlayerEditor.Axis.Z)));

        map.put(4,
                new Icon(
                        Material.COARSE_DIRT,
                        "coarse",
                        e -> {
                            e.setAngleChangeQuantity(20);
                            e.setMovingDistance(1);
                        }
                )
        );

        map.put(5,
                new Icon(
                        Material.SANDSTONE,
                        "fine",
                        e -> {
                            e.setAngleChangeQuantity(2);
                            e.setMovingDistance(0.1);
                        }
                )
        );

        map.put(7, new Icon(Material.COMPASS, "rotation", editor -> editor.setMode(Mode.ROTATION)));
        map.put(8, new Icon(Material.MINECART, "movement", editor -> editor.setMode(Mode.MOVEMENT)));
        map.put(10, new Icon(Material.LEATHER_HELMET, "head-pose", editor -> editor.setMode(Mode.HEAD_POSE)));
        map.put(18, new Icon(Material.STICK, "right-arm-pose", editor -> editor.setMode(Mode.RIGHT_ARM_POSE)));
        map.put(19, new Icon(Material.LEATHER_CHESTPLATE, "body-pose", editor -> editor.setMode(Mode.BODY_POSE)));
        map.put(20, new Icon(Material.STICK, "left-arm-pose", editor -> editor.setMode(Mode.LEFT_ARM_POSE)));
        map.put(22, new Icon(Material.LEVER, "reset-pose", editor -> editor.setMode(Mode.RESET_POSE)));
        map.put(24, new Icon(Material.STICK, "show-arms", editor -> editor.setMode(Mode.SHOW_ARMS)));
        map.put(25, new Icon(Material.POTION, "visible", editor -> editor.setMode(Mode.VISIBLE)));
        map.put(26, new Icon(Material.PUFFERFISH, "size", editor -> editor.setMode(Mode.SIZE)));
        map.put(27, new Icon(Material.STICK, "right-leg-pose", editor -> editor.setMode(Mode.RIGHT_LEG_POSE)));
        map.put(28, new Icon(Material.CHEST, "equipment", editor -> editor.setMode(Mode.EQUIPMENT)));
        map.put(29, new Icon(Material.STICK, "left-leg-pose", editor -> editor.setMode(Mode.LEFT_LEG_POSE)));
        map.put(34, new Icon(Material.SAND, "gravity", editor -> editor.setMode(Mode.GRAVITY)));
        map.put(35, new Icon(Material.SMOOTH_STONE_SLAB, "base-plate", editor -> editor.setMode(Mode.BASE_PLATE)));
        map.put(37, new Icon(Material.WRITABLE_BOOK, "copy", editor -> editor.setMode(Mode.COPY)));
        map.put(38, new Icon(Material.ENCHANTED_BOOK, "paste", editor -> editor.setMode(Mode.PASTE)));
        map.put(45, new Icon(Material.DANDELION, "copy-slot-1", editor -> editor.setSelectedCopySlot(1)));
        map.put(46, new Icon(Material.AZURE_BLUET, "copy-slot-2", editor -> editor.setSelectedCopySlot(2)));
        map.put(47, new Icon(Material.BLUE_ORCHID, "copy-slot-3", editor -> editor.setSelectedCopySlot(3)));
        map.put(48, new Icon(Material.PEONY, "copy-slot-4", editor -> editor.setSelectedCopySlot(4)));
        map.put(52, new Icon(Material.LAVA_BUCKET, "removal", editor -> editor.setMode(Mode.REMOVAL)));
        map.put(53, new Icon(Material.NETHER_STAR, "help", editor -> editor.getPlayer())); // TODO

        MENU_MAP = Map.copyOf(map);
    }

    private final Player player;
    private final Inventory inventory;

    public SelectionMenu(@NotNull Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, Components.SELECTION_MENU_TITLE);

        setItems();
    }

    @Override
    public void onOpen(@NotNull InventoryOpenEvent event) {

    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        var editor = PlayerEditorProvider.getEditor(event.getWhoClicked());

        event.setCancelled(true);

        var icon = MENU_MAP.get(event.getSlot());

        if (icon != null) {
            icon.onClick(editor);
            player.closeInventory();
        }
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {

    }

    private void setItems() {
        var size = inventory.getSize();

        for (int i = 0; i < size; i++) {
            var icon = MENU_MAP.get(i);

            if (icon != null) {
                var item = icon.getIcon(player);
                inventory.setItem(i, item);
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    private static class Icon {

        private static final String PERMISSION_PREFIX = "armorstandeditor.mode.";
        private static final String KEY_PREFIX = "armorstandeditor.menu.selection.icon.";
        private static final String DISPLAY_NAME_SUFFIX = ".name";
        private static final String LORE_SUFFIX = ".description";
        private static final String VISIBLE = "visible";
        private static final ItemStack AIR = new ItemStack(Material.AIR);

        private final String permission;
        // private final ItemStack icon;
        private final Material material;
        private final Consumer<PlayerEditor> onClick;
        private final TranslatableComponent name;
        private final TranslatableComponent lore;

        private Icon(@NotNull Material material, @NotNull String name, Consumer<PlayerEditor> onClick) {
            this.permission = PERMISSION_PREFIX + name;
            //  this.icon = new ItemStack(material);
            this.material = material;
            this.onClick = onClick;
            this.name = Component.translatable(KEY_PREFIX + name + DISPLAY_NAME_SUFFIX, NamedTextColor.GOLD);
            this.lore = Component.translatable(KEY_PREFIX + name + LORE_SUFFIX, NamedTextColor.GRAY);
        }

        private @NotNull ItemStack getIcon(@NotNull Player player) {
            if (!player.hasPermission(permission)) {
                return AIR;
            }

            var item = new ItemStack(material);
            var meta = item.getItemMeta();

            if (meta != null) {
                var translatedName = GlobalTranslator.render(name, player.locale()).decoration(TextDecoration.ITALIC, false);
                var translatedLore = GlobalTranslator.render(lore, player.locale()).decoration(TextDecoration.ITALIC, false);

                meta.displayName(translatedName);
                meta.lore(List.of(translatedLore));

                if (meta instanceof PotionMeta) {
                    var potionMeta = (PotionMeta) meta;
                    potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1, 0), false);
                }
            }

            item.setItemMeta(meta);

            return item;
        }

        private void onClick(@NotNull PlayerEditor editor) {
            if (editor.getPlayer().hasPermission(permission)) {
                onClick.accept(editor);
            }
        }
    }
}
