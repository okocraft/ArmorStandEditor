package net.okocraft.armorstandeditor.listener;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.menu.EquipmentMenuProvider;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArmorStandListener implements Listener {

    private final ArmorStandEditorPlugin plugin;

    public ArmorStandListener(@NotNull ArmorStandEditorPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onManipulate(@NotNull PlayerArmorStandManipulateEvent event) {
        var armorStand = event.getRightClicked();
        var menu = EquipmentMenuProvider.getMenuOrNull(armorStand);

        if (menu != null) {
            menu.handleManipulateEvent(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDispenseArmor(@NotNull BlockDispenseArmorEvent event){
        if (event.getTargetEntity() instanceof ArmorStand armorStand) {
            var menu = EquipmentMenuProvider.getMenuOrNull(armorStand);

            if (menu != null) {
                menu.handleDispenseArmorEvent(event);
            }
        }
    }

    @EventHandler
    public void onRemove(@NotNull EntityRemoveFromWorldEvent event) {
        if (event.getEntity() instanceof ArmorStand armorStand) {
            var menu = EquipmentMenuProvider.removeMenu(armorStand);

            if (menu != null) {
                menu.closeMenu();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeftClick(@NotNull EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var damager = event.getDamager();

        if (!(damager instanceof Player player)) {
            return;
        }

        if (!plugin.getEditToolItem().check(player.getInventory().getItemInMainHand())) {
            return;
        }

        if (!player.hasPermission(Permissions.ARMOR_STAND_EDIT)) {
            player.sendMessage(Messages.EDIT_NO_PERMISSION);
            return;
        }

        event.setCancelled(true);
        var entity = event.getEntity();

        if (entity instanceof ArmorStand) {
            var editor = PlayerEditorProvider.getEditor(player);
            editor.getMode().edit(editor, (ArmorStand) entity, player.isSneaking());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(@NotNull PlayerInteractAtEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var entity = event.getRightClicked();

        if (!(entity instanceof ArmorStand)) {
            return;
        }

        var player = event.getPlayer();

        var itemInMainHand = player.getInventory().getItemInMainHand();

        if (plugin.getEditToolItem().check(itemInMainHand)) {
            if (!player.hasPermission(Permissions.ARMOR_STAND_EDIT)) {
                player.sendMessage(Messages.EDIT_NO_PERMISSION);
                return;
            }

            event.setCancelled(true);

            var editor = PlayerEditorProvider.getEditor(player);
            editor.getMode().edit(editor, (ArmorStand) entity, !player.isSneaking());
            return;
        }

        if (itemInMainHand.getType().equals(Material.NAME_TAG)) {
            if (!player.hasPermission(Permissions.ARMOR_STAND_RENAME)) {
                player.sendMessage(Messages.RENAME_NO_PERMISSION);
                return;
            }

            event.setCancelled(true);

            var meta = itemInMainHand.getItemMeta();

            if (meta == null) {
                return;
            }

            rename(player, entity, meta.displayName());
        }
    }

    private void rename(@NotNull Player player, @NotNull Entity entity, @Nullable Component name) {
        if (name != null) {
            entity.customName(name);
            entity.setCustomNameVisible(true);
        } else {
            entity.customName(null);
            entity.setCustomNameVisible(false);
        }
    }
}
