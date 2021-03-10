package net.okocraft.armorstandeditor.command;

import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import net.okocraft.armorstandeditor.lang.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class ArmorStandEditorCommand extends AbstractCommand {

    public ArmorStandEditorCommand() {
        super("armorstandeditor", "armorstandeditor.command", Set.of("ase", "aseditor"));
    }

    @Override
    public @NotNull CommandResult onExecution(@NotNull CommandContext context) {
        var sender = context.getSender();

        if (!context.getSender().hasPermission(getPermission())) {
            sender.sendMessage(Messages.COMMAND_NO_PERMISSION);
            return CommandResult.NO_PERMISSION;
        }

        

        return null;
    }

    @Override
    public @NotNull List<String> onTabCompletion(@NotNull CommandContext context) {
        return null;
    }
}
