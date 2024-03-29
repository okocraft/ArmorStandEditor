package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class Removal extends AbstractEditMode {

    private static final ItemStack AIR = new ItemStack(Material.AIR);
    private static final ItemStack ARMOR_STAND = new ItemStack(Material.ARMOR_STAND);

    Removal() {
        super("removal");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        var player = editor.getPlayer();
        var pi = player.getInventory();
        var returned = pi.addItem(ARMOR_STAND);

        var equipment = armorStand.getEquipment();
        returned.putAll(
                pi.addItem(
                        getOrAir(equipment.getHelmet()),
                        getOrAir(equipment.getChestplate()),
                        getOrAir(equipment.getLeggings()),
                        getOrAir(equipment.getBoots()),
                        equipment.getItemInMainHand(),
                        equipment.getItemInOffHand()
                )
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
}
