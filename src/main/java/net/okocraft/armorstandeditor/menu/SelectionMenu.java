package net.okocraft.armorstandeditor.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.translation.GlobalTranslator;
import net.okocraft.armorstandeditor.editor.EditMode;
import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.lang.Components;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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

        map.put(0, new Icon(Material.RED_WOOL, "x-axis", editor -> changeAxis(editor, PlayerEditor.Axis.X)));
        map.put(1, new Icon(Material.GREEN_WOOL, "y-axis", editor -> changeAxis(editor, PlayerEditor.Axis.Y)));
        map.put(2, new Icon(Material.BLUE_WOOL, "z-axis", editor -> changeAxis(editor, PlayerEditor.Axis.Z)));

        map.put(4,
            new Icon(
                Material.COARSE_DIRT,
                "coarse",
                e -> {
                    e.setAngleChangeQuantity(20);
                    e.setMovingDistance(1);
                    e.getPlayer().sendActionBar(
                        Messages.MENU_CHANGE_ADJUSTMENT_MODE.args(Components.ADJUSTMENT_MODE_COARSE)
                    );
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
                    e.getPlayer().sendActionBar(
                        Messages.MENU_CHANGE_ADJUSTMENT_MODE.args(Components.ADJUSTMENT_MODE_FINE)
                    );
                }
            )
        );

        map.put(7, new Icon(Material.COMPASS, "rotation", editor -> changeMode(editor, EditMode.ROTATION)));
        map.put(8, new Icon(Material.MINECART, "movement", editor -> changeMode(editor, EditMode.MOVEMENT)));
        map.put(10, new Icon(Material.LEATHER_HELMET, "head-pose", editor -> changeMode(editor, EditMode.HEAD_POSE)));
        map.put(18, new Icon(Material.STICK, "right-arm-pose", editor -> changeMode(editor, EditMode.RIGHT_ARM_POSE)));
        map.put(19, new Icon(Material.LEATHER_CHESTPLATE, "body-pose", editor -> changeMode(editor, EditMode.BODY_POSE)));
        map.put(20, new Icon(Material.STICK, "left-arm-pose", editor -> changeMode(editor, EditMode.LEFT_ARM_POSE)));
        map.put(22, new Icon(Material.LEVER, "reset-pose", editor -> changeMode(editor, EditMode.RESET_POSE)));
        map.put(24, new Icon(Material.STICK, "show-arms", editor -> changeMode(editor, EditMode.SHOW_ARMS)));
        map.put(25, new Icon(Material.POTION, "visible", editor -> changeMode(editor, EditMode.VISIBLE)));
        map.put(26, new Icon(Material.PUFFERFISH, "size", editor -> changeMode(editor, EditMode.SIZE)));
        map.put(27, new Icon(Material.STICK, "right-leg-pose", editor -> changeMode(editor, EditMode.RIGHT_LEG_POSE)));
        map.put(28, new Icon(Material.CHEST, "equipment", editor -> changeMode(editor, EditMode.EQUIPMENT)));
        map.put(29, new Icon(Material.STICK, "left-leg-pose", editor -> changeMode(editor, EditMode.LEFT_LEG_POSE)));
        map.put(33, new Icon(Material.NAME_TAG, "custom-name-visible", editor -> changeMode(editor, EditMode.CUSTOM_NAME_VISIBLE)));
        map.put(34, new Icon(Material.SAND, "gravity", editor -> changeMode(editor, EditMode.GRAVITY)));
        map.put(35, new Icon(Material.SMOOTH_STONE_SLAB, "base-plate", editor -> changeMode(editor, EditMode.BASE_PLATE)));
        map.put(37, new Icon(Material.WRITABLE_BOOK, "copy", editor -> changeMode(editor, EditMode.COPY)));
        map.put(38, new Icon(Material.ENCHANTED_BOOK, "paste", editor -> changeMode(editor, EditMode.PASTE)));
        map.put(45, new Icon(Material.DANDELION, "copy-slot-1", editor -> changeCopySlot(editor, 1)));
        map.put(46, new Icon(Material.AZURE_BLUET, "copy-slot-2", editor -> changeCopySlot(editor, 2)));
        map.put(47, new Icon(Material.BLUE_ORCHID, "copy-slot-3", editor -> changeCopySlot(editor, 3)));
        map.put(48, new Icon(Material.PEONY, "copy-slot-4", editor -> changeCopySlot(editor, 4)));
        map.put(51, new Icon(Material.TRIPWIRE_HOOK, "lock", editor -> changeMode(editor, EditMode.LOCK), true));
        map.put(52, new Icon(Material.LAVA_BUCKET, "removal", editor -> changeMode(editor, EditMode.REMOVAL), true));

        map.put(53,
            new Icon(
                Material.NETHER_STAR,
                "help",
                editor -> editor.getPlayer().sendMessage(Messages.MENU_HELP.args(Components.WIKI_LINK))
            )
        );

        MENU_MAP = Map.copyOf(map);
    }

    private final Player player;
    private final Inventory inventory;

    public SelectionMenu(@NotNull Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 54, Components.SELECTION_MENU_TITLE);

        this.setItems();
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getClickedInventory() != this.inventory) {
            return;
        }

        var icon = MENU_MAP.get(event.getSlot());

        if (icon != null) {
            var editor = PlayerEditorProvider.getEditor(event.getWhoClicked());
            icon.onClick(editor);
            this.player.closeInventory();
        }
    }

    private void setItems() {
        var size = this.inventory.getSize();

        for (int i = 0; i < size; i++) {
            var icon = MENU_MAP.get(i);

            if (icon != null) {
                var item = icon.getIcon(this.player);
                this.inventory.setItem(i, item);
            }
        }
    }

    private static void changeAxis(@NotNull PlayerEditor editor, @NotNull PlayerEditor.Axis axis) {
        editor.setAxis(axis);
        editor.getPlayer().sendActionBar(
            Messages.MENU_CHANGE_AXIS.args(Components.AXIS_NAME.apply(axis))
        );
    }

    private static void changeMode(@NotNull PlayerEditor editor, @NotNull EditMode mode) {
        editor.setMode(mode);
        editor.getPlayer().sendActionBar(
            Messages.MENU_CHANGE_MODE.args(Components.MODE_NAME.apply(mode))
        );
    }

    private static void changeCopySlot(@NotNull PlayerEditor editor, int slot) {
        editor.setSelectedCopySlot(slot);
        editor.getPlayer().sendActionBar(
            Messages.MENU_CHANGE_COPY_SLOT.args(Component.text(String.valueOf(slot), NamedTextColor.AQUA))
        );
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    private static class Icon {

        private static final String KEY_PREFIX = "armorstandeditor.menu.selection.icon.";
        private static final String DISPLAY_NAME_SUFFIX = ".name";
        private static final String LORE_SUFFIX = ".description";
        // private static final String VISIBLE = "visible";
        private static final ItemStack AIR = new ItemStack(Material.AIR);

        private final String permission;
        private final Material material;
        private final Consumer<PlayerEditor> onClick;
        private final TranslatableComponent name;
        private final List<TranslatableComponent> lore;

        private Icon(@NotNull Material material, @NotNull String name, Consumer<PlayerEditor> onClick) {
            this(material, name, onClick, false);
        }

        private Icon(@NotNull Material material, @NotNull String name, @NotNull Consumer<PlayerEditor> onClick, boolean twoLines) {
            this.permission = Permissions.ICON_PREFIX + name;
            this.material = material;
            this.onClick = onClick;
            this.name = Component.translatable(KEY_PREFIX + name + DISPLAY_NAME_SUFFIX, NamedTextColor.GOLD);

            if (twoLines) {
                this.lore = List.of(
                    Component.translatable(KEY_PREFIX + name + LORE_SUFFIX + "-1", NamedTextColor.GRAY),
                    Component.translatable(KEY_PREFIX + name + LORE_SUFFIX + "-2", NamedTextColor.GRAY)
                );
            } else {
                this.lore = List.of(Component.translatable(KEY_PREFIX + name + LORE_SUFFIX, NamedTextColor.GRAY));
            }
        }

        private @NotNull ItemStack getIcon(@NotNull Player player) {
            if (!player.hasPermission(this.permission)) {
                return AIR;
            }

            var item = new ItemStack(this.material);
            var meta = item.getItemMeta();

            if (meta != null) {
                var translatedName = GlobalTranslator.render(this.name, player.locale()).decoration(TextDecoration.ITALIC, false);


                meta.displayName(translatedName);

                if (this.lore.size() == 1) {
                    var translatedLore = GlobalTranslator.render(this.lore.getFirst(), player.locale()).decoration(TextDecoration.ITALIC, false);
                    meta.lore(List.of(translatedLore));
                } else {
                    meta.lore(this.lore.stream().map(line -> GlobalTranslator.render(line, player.locale()).decoration(TextDecoration.ITALIC, false)).toList());
                }

                if (meta instanceof PotionMeta potionMeta) {
                    potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1, 0), false);
                }
            }

            item.setItemMeta(meta);

            return item;
        }

        private void onClick(@NotNull PlayerEditor editor) {
            if (editor.getPlayer().hasPermission(this.permission)) {
                this.onClick.accept(editor);
            }
        }
    }
}
