package net.okocraft.armorstandeditor.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.editor.EditMode;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.item.EditToolItem;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Axis;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static net.okocraft.armorstandeditor.testsupport.TestIds.PLAYER_UUID;

class ArmorStandEditorCommandTest {

    @AfterEach
    void tearDown() {
        PlayerEditorProvider.unloadAll();
    }

    @Test
    void testAxisCommandChangesPlayerAxis() throws CommandSyntaxException {
        Player player = playerWithPermission(Permissions.COMMAND_AXIS);
        CommandSourceStack source = source(player);

        execute(ArmorStandEditorCommand.axis(), source, "axis y");

        Assertions.assertEquals(Axis.Y, PlayerEditorProvider.getEditor(player).getAxis());
        Mockito.verify(player).sendMessage(Messages.COMMAND_AXIS_CHANGE.apply(Axis.Y));
    }

    @Test
    void testAxisCommandIsUnavailableToNonPlayerSender() {
        CommandSender sender = Mockito.mock(CommandSender.class);
        Mockito.when(sender.hasPermission(Permissions.COMMAND_AXIS)).thenReturn(true);

        Assertions.assertFalse(ArmorStandEditorCommand.axis().build().canUse(source(sender)));
    }

    @Test
    void testModeCommandChangesPlayerModeWhenAllowed() throws CommandSyntaxException {
        Player player = playerWithPermission(Permissions.COMMAND_MODE);
        Mockito.when(player.hasPermission(EditMode.HEAD_POSE.getPermission())).thenReturn(true);

        execute(ArmorStandEditorCommand.mode(), source(player), "mode head-pose");

        Assertions.assertSame(EditMode.HEAD_POSE, PlayerEditorProvider.getEditor(player).getMode());
        Mockito.verify(player).sendMessage(Messages.COMMAND_MODE_CHANGE.apply(EditMode.HEAD_POSE));
    }

    @Test
    void testModeCommandKeepsCurrentModeWhenModePermissionIsMissing() throws CommandSyntaxException {
        Player player = playerWithPermission(Permissions.COMMAND_MODE);
        var editor = PlayerEditorProvider.getEditor(player);
        editor.setMode(EditMode.ROTATION);

        execute(ArmorStandEditorCommand.mode(), source(player), "mode head-pose");

        Assertions.assertSame(EditMode.ROTATION, editor.getMode());
        Mockito.verify(player).sendMessage(Messages.COMMAND_MODE_NO_PERMISSION);
    }

    @Test
    void testEquipmentCommandReportsMissingArmorStand() throws CommandSyntaxException {
        Player player = playerWithPermission(Permissions.COMMAND_EQUIPMENT);

        execute(ArmorStandEditorCommand.equipment(), source(player), "equipment");

        Mockito.verify(player).getTargetEntity(3);
        Mockito.verify(player).sendMessage(Messages.COMMAND_EQUIPMENT_ARMOR_STAND_NOT_FOUND);
    }

    @Test
    void testItemCommandReportsSuccessWhenItemFitsInventory() throws CommandSyntaxException {
        Player player = playerWithPermission(Permissions.COMMAND_ITEM);
        PlayerInventory inventory = Mockito.mock(PlayerInventory.class);
        EditToolItem editToolItem = Mockito.mock(EditToolItem.class);
        ItemStack item = Mockito.mock(ItemStack.class);
        Mockito.when(player.getInventory()).thenReturn(inventory);
        Mockito.when(editToolItem.getItem()).thenReturn(item);
        Mockito.when(inventory.addItem(item)).thenReturn(new HashMap<>());

        execute(ArmorStandEditorCommand.item(editToolItem), source(player), "item");

        Mockito.verify(player).sendMessage(Messages.COMMAND_ITEM_SUCCESS);
    }

    @Test
    void testItemCommandReportsFailureWhenItemDoesNotFitInventory() throws CommandSyntaxException {
        Player player = playerWithPermission(Permissions.COMMAND_ITEM);
        PlayerInventory inventory = Mockito.mock(PlayerInventory.class);
        EditToolItem editToolItem = Mockito.mock(EditToolItem.class);
        ItemStack item = Mockito.mock(ItemStack.class);
        Mockito.when(player.getInventory()).thenReturn(inventory);
        Mockito.when(editToolItem.getItem()).thenReturn(item);
        Mockito.when(inventory.addItem(item)).thenReturn(new HashMap<>(Map.of(0, item)));

        execute(ArmorStandEditorCommand.item(editToolItem), source(player), "item");

        Mockito.verify(player).sendMessage(Messages.COMMAND_ITEM_FAILURE);
    }

    @Test
    void testReloadCommandReportsSuccess() throws CommandSyntaxException, IOException {
        CommandSender sender = Mockito.mock(CommandSender.class);
        ArmorStandEditorPlugin plugin = Mockito.mock(ArmorStandEditorPlugin.class);
        Mockito.when(sender.hasPermission(Permissions.COMMAND_RELOAD)).thenReturn(true);

        execute(ArmorStandEditorCommand.reload(plugin), source(sender), "reload");

        Mockito.verify(plugin).loadMessages();
        Mockito.verify(sender).sendMessage(Messages.COMMAND_RELOAD_SUCCESS);
    }

    @Test
    void testReloadCommandReportsFailureWhenMessagesCannotBeLoaded() throws CommandSyntaxException, IOException {
        CommandSender sender = Mockito.mock(CommandSender.class);
        ArmorStandEditorPlugin plugin = Mockito.mock(ArmorStandEditorPlugin.class);
        Mockito.when(sender.hasPermission(Permissions.COMMAND_RELOAD)).thenReturn(true);
        Mockito.doThrow(new IOException()).when(plugin).loadMessages();

        execute(ArmorStandEditorCommand.reload(plugin), source(sender), "reload");

        Mockito.verify(sender).sendMessage(Messages.COMMAND_RELOAD_FAILURE);
    }

    private static Player playerWithPermission(String permission) {
        Player player = Mockito.mock(Player.class);
        Mockito.when(player.getUniqueId()).thenReturn(PLAYER_UUID);
        Mockito.when(player.hasPermission(permission)).thenReturn(true);
        return player;
    }

    private static CommandSourceStack source(CommandSender sender) {
        CommandSourceStack source = Mockito.mock(CommandSourceStack.class);
        Mockito.when(source.getSender()).thenReturn(sender);
        return source;
    }

    private static int execute(LiteralArgumentBuilder<CommandSourceStack> command,
                               CommandSourceStack source,
                               String input) throws CommandSyntaxException {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        dispatcher.getRoot().addChild(command.build());
        return dispatcher.execute(input, source);
    }
}
