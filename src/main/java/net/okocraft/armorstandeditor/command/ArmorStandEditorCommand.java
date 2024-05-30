package net.okocraft.armorstandeditor.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.editor.EditMode;
import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.item.EditToolItem;
import net.okocraft.armorstandeditor.lang.Components;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.menu.EquipmentMenuProvider;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("UnstableApiUsage")
public final class ArmorStandEditorCommand {

    public static void register(Commands commands, ArmorStandEditorPlugin plugin) {
        commands.register(
                plugin.getPluginMeta(),
                Commands.literal("armorstandeditor")
                        .requires(source -> source.getSender().hasPermission(Permissions.COMMAND))
                        .executes(ctx -> {
                            ctx.getSource().getSender().sendMessage(Messages.COMMAND_ARGUMENT_NOT_ENOUGH);
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(axis())
                        .then(equipment())
                        .then(item(plugin.getEditToolItem()))
                        .then(mode())
                        .then(reload(plugin))
                        .build(),
                "The root command of ArmorStandEditor",
                List.of("ase", "aseditor")
        );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> axis() {
        return Commands.literal("axis")
                .requires(source ->
                        source.getSender().hasPermission(Permissions.COMMAND_AXIS) &&
                        source.getSender() instanceof Player
                )
                .then(
                        Commands.argument(
                                "axis",
                                new ConstantsArgumentType<>(
                                        Arrays.stream(PlayerEditor.Axis.values()).map(Enum::name).map(name -> name.toLowerCase(Locale.ENGLISH)).toList(),
                                        name -> {
                                            try {
                                                return PlayerEditor.Axis.valueOf(name.toUpperCase(Locale.ENGLISH));
                                            } catch (IllegalArgumentException ignored) {
                                                return null;
                                            }
                                        },
                                        argument -> Messages.COMMAND_AXIS_INVALID_ARGUMENT.append(Component.text(argument, NamedTextColor.AQUA)),
                                        Messages.COMMAND_AXIS_TOOLTIP
                                )
                        ).executes(context -> {
                            var axis = context.getArgument("axis", PlayerEditor.Axis.class);
                            PlayerEditorProvider.getEditor((Player) context.getSource().getSender()).setAxis(axis);

                            context.getSource().getSender().sendMessage(
                                    Messages.COMMAND_AXIS_CHANGE.append(Components.AXIS_NAME.apply(axis))
                            );

                            return Command.SINGLE_SUCCESS;
                        })
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> equipment() {
        return Commands.literal("equipment")
                .requires(source ->
                        source.getSender().hasPermission(Permissions.COMMAND_EQUIPMENT) &&
                        source.getSender() instanceof Player
                )
                .executes(context -> {
                    var player = (Player) context.getSource().getSender();
                    var target = player.getTargetEntity(3);

                    if (target instanceof ArmorStand armorStand) {
                        player.openInventory(EquipmentMenuProvider.getMenu(armorStand).getInventory());
                    } else {
                        player.sendMessage(Messages.COMMAND_EQUIPMENT_ARMOR_STAND_NOT_FOUND);
                    }

                    return Command.SINGLE_SUCCESS;
                });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> item(@NotNull EditToolItem editToolItem) {
        return Commands.literal("item")
                .requires(source ->
                        source.getSender().hasPermission(Permissions.COMMAND_ITEM) &&
                        source.getSender() instanceof Player
                )
                .executes(context -> {
                    var player = (Player) context.getSource().getSender();

                    if (player.getInventory().addItem(editToolItem.createEditTool()).isEmpty()) {
                        player.sendMessage(Messages.COMMAND_ITEM_SUCCESS);
                    } else {
                        player.sendMessage(Messages.COMMAND_ITEM_FAILURE);
                    }

                    return Command.SINGLE_SUCCESS;
                });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> mode() {
        return Commands.literal("mode")
                .requires(source ->
                        source.getSender().hasPermission(Permissions.COMMAND_MODE) &&
                        source.getSender() instanceof Player
                )
                .then(
                        Commands.argument(
                                "mode",
                                new ConstantsArgumentType<>(
                                        EditMode.names().toList(),
                                        EditMode::byName,
                                        argument -> Messages.COMMAND_MODE_INVALID_ARGUMENT.append(Component.text(argument, NamedTextColor.AQUA)),
                                        Messages.COMMAND_MODE_TOOLTIP
                                )
                        ).executes(context -> {
                            var player = (Player) context.getSource().getSender();
                            var mode = context.getArgument("mode", EditMode.class);

                            if (player.hasPermission(mode.getPermission())) {
                                PlayerEditorProvider.getEditor(player).setMode(mode);
                                player.sendMessage(Messages.COMMAND_MODE_CHANGE.append(Components.MODE_NAME.apply(mode)));
                            } else {
                                player.sendMessage(Messages.COMMAND_MODE_NO_PERMISSION);
                            }

                            return Command.SINGLE_SUCCESS;
                        })
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> reload(@NotNull ArmorStandEditorPlugin plugin) {
        return Commands.literal("reload")
                .requires(source -> source.getSender().hasPermission(Permissions.COMMAND_RELOAD))
                .executes(context -> {
                    try {
                        plugin.getTranslationDirectory().load();
                    } catch (IOException e) {
                        context.getSource().getSender().sendMessage(Messages.COMMAND_RELOAD_FAILURE);
                        return Command.SINGLE_SUCCESS;
                    }

                    context.getSource().getSender().sendMessage(Messages.COMMAND_RELOAD_SUCCESS);
                    return Command.SINGLE_SUCCESS;
                });
    }

    private ArmorStandEditorCommand() {
        throw new UnsupportedOperationException();
    }
}
