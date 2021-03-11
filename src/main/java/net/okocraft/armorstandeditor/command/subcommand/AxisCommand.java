package net.okocraft.armorstandeditor.command.subcommand;

import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.lang.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AxisCommand extends AbstractCommand {

    public AxisCommand() {
        super("axis", "armorstandeditor.command.axis", Set.of("e"));
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
        PlayerEditor.Axis axis;

        try {
            axis = PlayerEditor.Axis.valueOf(secondArgument.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(
                    Messages.COMMAND_AXIS_INVALID_ARGUMENT
                            .replaceText(Placeholders.AXIS.apply(secondArgument))
            );
            return CommandResult.INVALID_ARGUMENTS;
        }

        var editor = PlayerEditorProvider.getEditor(player);
        editor.setAxis(axis);

        sender.sendMessage(
                Messages.COMMAND_AXIS_CHANGE
                        .replaceText(Placeholders.AXIS.apply(axis.getName()))
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
            return Arrays.stream(PlayerEditor.Axis.values())
                    .map(PlayerEditor.Axis::getName)
                    .collect(Collectors.toUnmodifiableList());
        }

        return Collections.emptyList();
    }
}