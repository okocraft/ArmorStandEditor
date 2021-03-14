package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.lang.Messages;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

class Removal extends AbstractEditMode {

    private static final ItemStack AIR = new ItemStack(Material.AIR);
    private static final ItemStack ARMOR_STAND = new ItemStack(Material.ARMOR_STAND);

    Removal() {
        super("removal");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        Player player = editor.getPlayer();
        PlayerInventory pi = player.getInventory();
        Map<Integer, ItemStack> returned = pi.addItem(ARMOR_STAND);

        EntityEquipment e = armorStand.getEquipment();
        if (e != null) {
            returned.putAll(
                    pi.addItem(
                            getOrAir(e.getHelmet()),
                            getOrAir(e.getChestplate()),
                            getOrAir(e.getLeggings()),
                            getOrAir(e.getBoots()),
                            e.getItemInMainHand(),
                            e.getItemInOffHand()
                    )
            );
        }

        if (!returned.isEmpty()) {
            for (ItemStack item : returned.values()) {
                if (!item.getType().isAir()) {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
            }
        }

        editor.getPlayer().sendActionBar(Messages.EDIT_REMOVAL);
    }

    private static @NotNull ItemStack getOrAir(@Nullable ItemStack item) {
        return item != null ? item : AIR;
    }
}
