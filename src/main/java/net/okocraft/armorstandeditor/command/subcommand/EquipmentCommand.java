package net.okocraft.armorstandeditor.command.subcommand;

import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.menu.EquipmentMenuProvider;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EquipmentCommand extends AbstractCommand {

    public EquipmentCommand() {
        super("equipment", Permissions.COMMAND_PREFIX + "equipment", Set.of("eq"));
    }

    @Override
    public @NotNull CommandResult onExecution(@NotNull CommandContext context) {
        var sender = context.getSender();

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Messages.COMMAND_NO_PERMISSION);
            return CommandResult.NO_PERMISSION;
        }

        var object = sender.getOriginalSender();

        if (!(object instanceof Player player)) {
            sender.sendMessage(Messages.COMMAND_ONLY_PLAYER);
            return CommandResult.NOT_PLAYER;
        }

        var target = player.getTargetEntity(3);

        if (target instanceof ArmorStand armorStand) {
            var menu = EquipmentMenuProvider.getMenu(armorStand);
            player.openInventory(menu.getInventory());

            return CommandResult.SUCCESS;
        } else {
            sender.sendMessage(Messages.COMMAND_EQUIPMENT_ARMOR_STAND_NOT_FOUND);

            return CommandResult.STATE_ERROR;
        }
    }

    @Override
    public @NotNull List<String> onTabCompletion(@NotNull CommandContext context) {
        return Collections.emptyList();
    }
}
