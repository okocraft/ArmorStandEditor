package net.okocraft.armorstandeditor.menu;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EquipmentMenuProvider {

    private static final Map<UUID, EquipmentMenu> MENU_MAP = Collections.synchronizedMap(new HashMap<>());

    private EquipmentMenuProvider() {
        throw new UnsupportedOperationException();
    }

    public static boolean openMenu(@NotNull ArmorStand armorStand, @NotNull Player viewer) {
        return MENU_MAP.computeIfAbsent(armorStand.getUniqueId(), u -> new EquipmentMenu(armorStand)).open(armorStand, viewer);
    }

    public static @Nullable EquipmentMenu getMenuOrNull(@NotNull ArmorStand armorStand) {
        return MENU_MAP.get(armorStand.getUniqueId());
    }

    public static @Nullable EquipmentMenu removeMenu(@NotNull ArmorStand armorStand) {
        return MENU_MAP.remove(armorStand.getUniqueId());
    }
}
