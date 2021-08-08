package net.okocraft.armorstandeditor.command.subcommand;

import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ItemCommand extends AbstractCommand {

    private final ArmorStandEditorPlugin plugin;

    public ItemCommand(@NotNull ArmorStandEditorPlugin plugin) {
        super("item", Permissions.COMMAND_PREFIX + "item", Set.of("i"));
        this.plugin = plugin;
    }

    @Override
    public @NotNull CommandResult onExecution(@NotNull CommandContext context) {
        var sender = context.getSender();

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Messages.COMMAND_NO_PERMISSION);
            return CommandResult.NO_PERMISSION;
       }

        var object = sender.getOriginalSender();

        if (!(object instanceof Player)) {
            sender.sendMessage(Messages.COMMAND_ONLY_PLAYER);
            return CommandResult.NOT_PLAYER;
        }

        var player = (Player) object;
        var item = plugin.getEditToolItem().createEditTool();

        var success = player.getInventory().addItem(item).isEmpty();

        if (success) {
            sender.sendMessage(Messages.COMMAND_ITEM_SUCCESS);
            return CommandResult.SUCCESS;
        } else {
            sender.sendMessage(Messages.COMMAND_ITEM_FAILURE);
            return CommandResult.STATE_ERROR;
        }
    }

    @Override
    public @NotNull List<String> onTabCompletion(@NotNull CommandContext context) {
        return Collections.emptyList();
    }
}
