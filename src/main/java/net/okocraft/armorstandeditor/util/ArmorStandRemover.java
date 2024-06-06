package net.okocraft.armorstandeditor.util;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ArmorStandRemover {

    private static final ItemStack AIR = new ItemStack(Material.AIR);
    private static final ItemStack ARMOR_STAND = new ItemStack(Material.ARMOR_STAND);

    public static void remove(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean ignored) {
        var player = editor.getPlayer();

        var equipment = armorStand.getEquipment();
        var returned = player.getInventory().addItem(
                ARMOR_STAND,
                getOrAir(equipment.getHelmet()),
                getOrAir(equipment.getChestplate()),
                getOrAir(equipment.getLeggings()),
                getOrAir(equipment.getBoots()),
                equipment.getItemInMainHand(),
                equipment.getItemInOffHand()
        );

        if (!returned.isEmpty()) {
            for (var item : returned.values()) {
                if (!item.getType().isAir()) {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
            }
        }

        armorStand.remove();

        editor.getPlayer().sendActionBar(Messages.EDIT_REMOVAL);
    }

    private static @NotNull ItemStack getOrAir(@Nullable ItemStack item) {
        return item != null ? item : AIR;
    }

    private ArmorStandRemover() {
        throw new UnsupportedOperationException();
    }
}
