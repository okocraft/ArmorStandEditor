package net.okocraft.armorstandeditor;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.okocraft.armorstandeditor.item.EditToolItem;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

final class EditToolItemFactory {

    private EditToolItemFactory() {
        throw new UnsupportedOperationException();
    }

    static @NotNull EditToolItem create(@NotNull ConfigurationSection config) {
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
}
