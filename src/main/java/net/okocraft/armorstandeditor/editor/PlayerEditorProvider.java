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
        return getOrCreateEditor(player.getUniqueId());
    }

    public static @NotNull PlayerEditor getEditor(@NotNull HumanEntity humanEntity) {
        return getOrCreateEditor(humanEntity.getUniqueId());
    }

    public static void unload(@NotNull Player player) {
        EDITOR_MAP.remove(player.getUniqueId());
    }

    public static void unloadAll() {
        EDITOR_MAP.clear();
    }

    private static @NotNull PlayerEditor getOrCreateEditor(@NotNull UUID uuid) {
        return EDITOR_MAP.computeIfAbsent(uuid, ignored -> {
            var player = Bukkit.getPlayer(uuid);
            return new PlayerEditor(Objects.requireNonNull(player));
        });
    }
}
