package net.okocraft.armorstandeditor.util;

import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EditItemChecker {

    private static final byte VALUE = 1;
    private static NamespacedKey KEY = null;

    private EditItemChecker() {
        throw new UnsupportedOperationException();
    }

    public static void setNamespacedKey(@NotNull ArmorStandEditorPlugin plugin) {
        EditItemChecker.KEY = new NamespacedKey(plugin, "armorstandeditor.edititem");
    }

    public static boolean check(@Nullable ItemStack item) {
        if (KEY == null || item == null) {
            return false;
        }

        var meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        var b = meta.getPersistentDataContainer().get(KEY, PersistentDataType.BYTE);

        return b != null && b == VALUE;
    }

    public static void addKey(@NotNull ItemStack item) {
        var meta = item.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(KEY, PersistentDataType.BYTE, VALUE);
        }

        item.setItemMeta(meta);
    }
}
