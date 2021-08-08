package net.okocraft.armorstandeditor.item;

import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.config.Settings;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditToolItem {

    private static final byte VALUE = 1;

    private final ArmorStandEditorPlugin plugin;
    private final NamespacedKey key;

    public EditToolItem(@NotNull ArmorStandEditorPlugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "armorstandeditor.edititem");
    }

    public boolean check(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }

        if (plugin.getConfiguration().get(Settings.TOOL_ALLOW_NORMAL_FLINT)) {
            return item.getType() == Material.FLINT;
        }

        if (item.getType() != Material.FLINT) {
            return false;
        }

        var meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        var b = meta.getPersistentDataContainer().get(key, PersistentDataType.BYTE);

        return b != null && b == VALUE;
    }

    public @NotNull ItemStack createEditTool() {
        var item = new ItemStack(Material.FLINT);
        var meta = item.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, VALUE);

            var displayName = plugin.getConfiguration().get(Settings.TOOL_DISPLAY_NAME);
            if (!displayName.content().isEmpty()) {
                meta.displayName(displayName);
            }

            var lore = plugin.getConfiguration().get(Settings.TOOL_LORE);
            if (!lore.isEmpty()) {
                meta.lore(lore);
            }
        }

        item.setItemMeta(meta);

        return item;
    }
}
