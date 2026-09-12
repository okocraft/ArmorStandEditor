package net.okocraft.armorstandeditor.editor;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PlayerEditorProvider {

    private static final Map<UUID, PlayerEditor> EDITOR_MAP = Collections.synchronizedMap(new HashMap<>());

    private PlayerEditorProvider() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull PlayerEditor getEditor(@NotNull Player player) {
        return EDITOR_MAP.computeIfAbsent(player.getUniqueId(), ignored -> new PlayerEditor(player));
    }

    public static @NotNull PlayerEditor getEditor(@NotNull HumanEntity humanEntity) {
        if (humanEntity instanceof Player player) {
            return getEditor(player);
        }

        return EDITOR_MAP.computeIfAbsent(humanEntity.getUniqueId(), ignored -> {
            var player = Bukkit.getPlayer(humanEntity.getUniqueId());
            return new PlayerEditor(Objects.requireNonNull(player));
        });
    }

    public static void unload(@NotNull Player player) {
        EDITOR_MAP.remove(player.getUniqueId());
    }

    public static void unloadAll() {
        EDITOR_MAP.clear();
    }
}
