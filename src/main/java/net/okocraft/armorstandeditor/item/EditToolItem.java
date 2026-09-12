package net.okocraft.armorstandeditor.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class EditToolItem {

    private static final NamespacedKey EDIT_ITEM_KEY = new NamespacedKey("armorstandeditor", "edititem");
    private static final byte EDIT_ITEM_VALUE = 1;

    public static @NotNull EditToolItem createFromConfig(@NotNull ConfigurationSection config) {
        boolean allowNormalFlint = config.getBoolean("tool.allow-normal-flint");
        String displayName = config.getString("tool.display-name");

        var serializer = LegacyComponentSerializer.legacyAmpersand();
        var lore = config.getStringList("tool.lore").stream()
            .map(serializer::deserialize)
            .collect(Collectors.toUnmodifiableList());

        return new EditToolItem(
            allowNormalFlint,
            displayName != null ? serializer.deserialize(displayName) : null,
            lore
        );
    }

    private final boolean allowNormalFlint;
    private final ItemStack item;

    public EditToolItem(boolean allowNormalFlint, @Nullable Component displayName, @NotNull List<Component> lore) {
        this.allowNormalFlint = allowNormalFlint;

        ItemStack item = ItemStack.of(Material.FLINT);
        item.editPersistentDataContainer(container -> container.set(EDIT_ITEM_KEY, PersistentDataType.BYTE, EDIT_ITEM_VALUE));

        if (displayName != null) {
            item.setData(DataComponentTypes.CUSTOM_NAME, displayName);
        }

        if (!lore.isEmpty()) {
            item.setData(DataComponentTypes.LORE, ItemLore.lore(lore));
        }

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
