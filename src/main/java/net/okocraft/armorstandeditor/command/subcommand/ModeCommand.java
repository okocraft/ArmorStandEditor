package net.okocraft.armorstandeditor.command.subcommand;

import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import net.okocraft.armorstandeditor.editmode.Mode;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.lang.Placeholders;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModeCommand extends AbstractCommand {

    public ModeCommand() {
        super("mode", Permissions.COMMAND_PREFIX + "mode", Set.of("m"));
    }

    @Override
    public @NotNull CommandResult onExecution(@NotNull CommandContext context) {
        var sender = context.getSender();

        if (!context.getSender().hasPermission(getPermission())) {
            sender.sendMessage(Messages.COMMAND_NO_PERMISSION);
            return CommandResult.NO_PERMISSION;
        }

        var object = sender.getOriginalSender();

        if (!(object instanceof Player)) {
            sender.sendMessage(Messages.COMMAND_ONLY_PLAYER);
            return CommandResult.NOT_PLAYER;
        }

        var player = (Player) object;
        var arguments = context.getArguments();

        if (arguments.size() < 2) {
            sender.sendMessage(Messages.COMMAND_ARGUMENT_NOT_ENOUGH);
            return CommandResult.NO_ARGUMENT;
        }

        var secondArgument = arguments.get(1).get();
        Mode mode;

        try {
            mode = Mode.valueOf(secondArgument.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(
                    Messages.COMMAND_MODE_INVALID_ARGUMENT
                            .replaceText(Placeholders.MODE_STRING.apply(secondArgument))
            );
            return CommandResult.INVALID_ARGUMENTS;
        }

        if (!player.hasPermission(mode.getPermission())) {
            player.sendMessage(Messages.COMMAND_MODE_NO_PERMISSION);
            return CommandResult.NO_PERMISSION;
        }

        var editor = PlayerEditorProvider.getEditor(player);
        editor.setMode(mode);

        sender.sendMessage(
                Messages.COMMAND_MODE_CHANGE
                        .replaceText(Placeholders.MODE.apply(mode))
        );

        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull List<String> onTabCompletion(@NotNull CommandContext context) {
        var sender = context.getSender();

        if (!(sender.hasPermission(getPermission())) ||
                !(sender.getOriginalSender() instanceof Player)) {
            return Collections.emptyList();
        }

        if (context.getArguments().size() == 2) {
            return Arrays.stream(Mode.values())
                    .map(Mode::getName)
                    .collect(Collectors.toUnmodifiableList());
        }

        return Collections.emptyList();
    }
}
