package net.okocraft.armorstandeditor.command.subcommand;

import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import com.github.siroshun09.mccommand.common.filter.StringFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.lang.Components;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AxisCommand extends AbstractCommand {

    public AxisCommand() {
        super("axis", Permissions.COMMAND_PREFIX + "axis", Set.of("a"));
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
                    Messages.COMMAND_AXIS_INVALID_ARGUMENT.append(Component.text(secondArgument, NamedTextColor.AQUA))
            );
            return CommandResult.INVALID_ARGUMENTS;
        }

        var editor = PlayerEditorProvider.getEditor(player);
        editor.setAxis(axis);

        sender.sendMessage(
                Messages.COMMAND_AXIS_CHANGE.append(Components.AXIS_NAME.apply(axis))
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

        var arguments = context.getArguments();

        if (arguments.size() == 2) {
            var argumentFilter = StringFilter.startsWithIgnoreCase(arguments.get(1).get());

            return Arrays.stream(PlayerEditor.Axis.values())
                    .map(PlayerEditor.Axis::getName)
                    .filter(argumentFilter)
                    .toList();
        }

        return Collections.emptyList();
    }
}
