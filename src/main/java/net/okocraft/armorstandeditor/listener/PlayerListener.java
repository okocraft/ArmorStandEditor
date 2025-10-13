package net.okocraft.armorstandeditor.listener;

import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.editor.EditMode;
import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.menu.SelectionMenu;
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
    private final ArmorStandEditorPlugin plugin;

    public PlayerListener(@NotNull ArmorStandEditorPlugin plugin) {
        this.plugin = plugin;
    }

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

        if (CLICK_ACTIONS.contains(action) && this.plugin.getEditToolItem().check(event.getItem())) {
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
        if (!e.getPlayer().isSneaking() ||
            !this.plugin.getEditToolItem().check(e.getPlayer().getInventory().getItemInMainHand())) {
            return;
        }

        var player = e.getPlayer();
        var editor = PlayerEditorProvider.getEditor(player);

        if (editor.getMode() != EditMode.MOVEMENT &&
            (editor.getMode() == EditMode.RESET_POSE || !editor.getMode().getName().endsWith("pose"))) {
            return;
        }

        e.setCancelled(true);

        var newSlot = e.getNewSlot();
        var previousSlot = e.getPreviousSlot();

        if (newSlot == previousSlot + 1 || (newSlot == 0 && previousSlot == 8)) {
            switch (editor.getAxis()) {
                case X -> editor.setAxis(PlayerEditor.Axis.Y);
                case Y -> editor.setAxis(PlayerEditor.Axis.Z);
                case Z -> editor.setAxis(PlayerEditor.Axis.X);
            }
            player.sendActionBar(Messages.MENU_CHANGE_AXIS.apply(editor.getAxis()));
            return;
        }

        if (e.getNewSlot() == e.getPreviousSlot() - 1 || (newSlot == 8 && previousSlot == 0)) {
            switch (editor.getAxis()) {
                case X -> editor.setAxis(PlayerEditor.Axis.Z);
                case Y -> editor.setAxis(PlayerEditor.Axis.X);
                case Z -> editor.setAxis(PlayerEditor.Axis.Y);
            }
            player.sendActionBar(Messages.MENU_CHANGE_AXIS.apply(editor.getAxis()));
        }
    }
}
