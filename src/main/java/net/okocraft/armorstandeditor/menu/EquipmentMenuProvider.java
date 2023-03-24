package net.okocraft.armorstandeditor.menu;

import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EquipmentMenuProvider {

    private static final Map<UUID, EquipmentMenu> MENU_MAP = new HashMap<>();

    private EquipmentMenuProvider() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull EquipmentMenu getMenu(@NotNull ArmorStand armorStand) {
        return MENU_MAP.computeIfAbsent(armorStand.getUniqueId(), u -> new EquipmentMenu(armorStand));
    }

    public static @Nullable EquipmentMenu getMenuOrNull(@NotNull ArmorStand armorStand) {
        return MENU_MAP.get(armorStand.getUniqueId());
    }

    public static @Nullable EquipmentMenu removeMenu(@NotNull ArmorStand armorStand) {
        return MENU_MAP.remove(armorStand.getUniqueId());
    }
}
