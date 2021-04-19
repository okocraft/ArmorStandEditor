package net.okocraft.armorstandeditor.command.subcommand;

import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ReloadCommand extends AbstractCommand {

    private final ArmorStandEditorPlugin plugin;

    public ReloadCommand(@NotNull ArmorStandEditorPlugin plugin) {
        super("reload", Permissions.COMMAND_PREFIX + "reload", Collections.emptySet());
        this.plugin = plugin;
    }

    @Override
    public @NotNull CommandResult onExecution(@NotNull CommandContext context) {
        var sender = context.getSender();

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(Messages.COMMAND_NO_PERMISSION);
            return CommandResult.NO_PERMISSION;
        }

        try {
            plugin.getLanguageLoader().reload();
        } catch (IOException e) {
            sender.sendMessage(Messages.COMMAND_RELOAD_FAILURE);
            return CommandResult.EXCEPTION_OCCURRED;
        }

        sender.sendMessage(Messages.COMMAND_RELOAD_SUCCESS);
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull List<String> onTabCompletion(@NotNull CommandContext context) {
        return Collections.emptyList();
    }
}
