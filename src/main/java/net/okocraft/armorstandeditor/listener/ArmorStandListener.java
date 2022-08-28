package net.okocraft.armorstandeditor.listener;

import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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

    @EventHandler
    public void onManipulate(@NotNull PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(Messages.DISABLE_MANIPULATION_1);
        event.getPlayer().sendMessage(Messages.DISABLE_MANIPULATION_2);
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
