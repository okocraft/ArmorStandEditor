package net.okocraft.armorstandeditor.command;

import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.Command;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.SubCommandHolder;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import com.github.siroshun09.mccommand.common.filter.StringFilter;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.command.subcommand.AxisCommand;
import net.okocraft.armorstandeditor.command.subcommand.EquipmentCommand;
import net.okocraft.armorstandeditor.command.subcommand.ItemCommand;
import net.okocraft.armorstandeditor.command.subcommand.ModeCommand;
import net.okocraft.armorstandeditor.command.subcommand.ReloadCommand;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ArmorStandEditorCommand extends AbstractCommand {

    private final SubCommandHolder holder;

    public ArmorStandEditorCommand(@NotNull ArmorStandEditorPlugin plugin) {
        super("armorstandeditor", Permissions.COMMAND, Set.of("ase", "aseditor"));

        holder = SubCommandHolder.of(
                new AxisCommand(),
                new EquipmentCommand(),
                new ItemCommand(plugin),
                new ModeCommand(),
                new ReloadCommand(plugin)
        );
    }

    @Override
    public @NotNull CommandResult onExecution(@NotNull CommandContext context) {
        var sender = context.getSender();

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Messages.COMMAND_NO_PERMISSION);
            return CommandResult.NO_PERMISSION;
        }

        var arguments = context.getArguments();

        if (arguments.isEmpty()) {
            sender.sendMessage(Messages.COMMAND_ARGUMENT_NOT_ENOUGH);
            return CommandResult.NO_ARGUMENT;
        }

        var sub = holder.search(arguments.get(0));

        if (sub != null) {
            return sub.onExecution(context);
        } else {
            sender.sendMessage(Messages.COMMAND_SUB_COMMAND_NOT_FOUND);
            return CommandResult.INVALID_ARGUMENTS;
        }
    }

    @Override
    public @NotNull List<String> onTabCompletion(@NotNull CommandContext context) {
        if (!context.getSender().hasPermission(getPermission())) {
            return Collections.emptyList();
        }

        var arguments = context.getArguments();

        if (arguments.isEmpty()) {
            return Collections.emptyList();
        }

        if (arguments.size() == 1) {
            var argumentFilter = StringFilter.startsWithIgnoreCase(arguments.get(0).get());

            return holder.getSubCommands()
                    .stream()
                    .map(Command::getName)
                    .filter(argumentFilter)
                    .toList();
        }

        var sub = holder.search(arguments.get(0));

        if (sub != null) {
            return sub.onTabCompletion(context);
        } else {
            return Collections.emptyList();
        }
    }
}
