package net.okocraft.armorstandeditor.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EditToolItem {

    private static final NamespacedKey EDIT_ITEM_KEY = new NamespacedKey("armorstandeditor", "edititem");
    private static final byte EDIT_ITEM_VALUE = 1;

    private final boolean allowNormalFlint;
    private final ItemStack item;

    public EditToolItem(boolean allowNormalFlint, @Nullable Component displayName, @NotNull List<Component> lore) {
        this.allowNormalFlint = allowNormalFlint;

        ItemStack item = ItemStack.of(Material.FLINT);
        item.editMeta(meta -> {
            meta.getPersistentDataContainer().set(EDIT_ITEM_KEY, PersistentDataType.BYTE, EDIT_ITEM_VALUE);

            if (displayName != null) {
                meta.displayName(displayName);
            }

            if (!lore.isEmpty()) {
                meta.lore(lore);
            }
        });
        this.item = item;
    }

    public boolean check(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }

        if (this.allowNormalFlint) {
            return item.getType() == Material.FLINT;
        }

        if (item.getType() != Material.FLINT) {
            return false;
        }

        Byte value = item.getPersistentDataContainer().get(EDIT_ITEM_KEY, PersistentDataType.BYTE);
        return value != null && value == EDIT_ITEM_VALUE;
    }

    public @NotNull ItemStack getItem() {
        return this.item.clone();
    }
}
