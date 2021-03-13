package net.okocraft.armorstandeditor.listener;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.menu.SelectionMenu;
import net.okocraft.armorstandeditor.util.EditItemChecker;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PlayerListener implements Listener {

    private static final Set<Action> CLICK_ACTIONS =
            Set.of(Action.LEFT_CLICK_AIR, Action.RIGHT_CLICK_AIR, Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK);

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        PlayerEditorProvider.unload(event.getPlayer());
    }

    @EventHandler
    public void onClick(@NotNull PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }

        var action = event.getAction();

        if (CLICK_ACTIONS.contains(action) && EditItemChecker.check(event.getItem())) {
            event.setCancelled(true);

            var player = event.getPlayer();
            var target = player.getTargetEntity(5);

            if (!(target instanceof ArmorStand)) {
                var menu = new SelectionMenu(event.getPlayer());
                player.openInventory(menu.getInventory());
            }
        }
    }

    @EventHandler
    public void onScroll(@NotNull PlayerItemHeldEvent e) {
        if (!e.getPlayer().isSneaking() || !EditItemChecker.check(e.getPlayer().getInventory().getItemInMainHand())) {
            return;
        }

        e.setCancelled(true);

        var player = e.getPlayer();
        var newSlot = e.getNewSlot();
        var previousSlot = e.getPreviousSlot();
        var editor = PlayerEditorProvider.getEditor(player);

        if (newSlot == previousSlot + 1 || (newSlot == 0 && previousSlot == 8)) {
            switch (editor.getAxis()) {
                case X:
                    editor.setAxis(PlayerEditor.Axis.Y);
                    break;
                case Y:
                    editor.setAxis(PlayerEditor.Axis.Z);
                    break;
                case Z:
                    editor.setAxis(PlayerEditor.Axis.X);
                    break;
            }
        }

        if (e.getNewSlot() == e.getPreviousSlot() - 1 || (e.getNewSlot() == 8 && e.getPreviousSlot() == 0)) {
            switch (editor.getAxis()) {
                case X:
                    editor.setAxis(PlayerEditor.Axis.Z);
                    break;
                case Y:
                    editor.setAxis(PlayerEditor.Axis.X);
                    break;
                case Z:
                    editor.setAxis(PlayerEditor.Axis.Y);
                    break;
            }
        }
    }
}
