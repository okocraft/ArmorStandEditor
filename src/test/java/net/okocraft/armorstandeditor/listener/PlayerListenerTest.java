package net.okocraft.armorstandeditor.listener;

import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.editor.EditMode;
import net.okocraft.armorstandeditor.editor.PlayerEditor;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.item.EditToolItem;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Axis;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static net.okocraft.armorstandeditor.testsupport.TestIds.PLAYER_UUID;

class PlayerListenerTest {

    private ArmorStandEditorPlugin plugin;
    private EditToolItem editToolItem;
    private PlayerListener listener;
    private Player player;
    private PlayerInventory inventory;

    @BeforeEach
    void setUp() {
        this.plugin = Mockito.mock(ArmorStandEditorPlugin.class);
        this.editToolItem = Mockito.mock(EditToolItem.class);
        this.listener = new PlayerListener(this.plugin);
        this.player = Mockito.mock(Player.class);
        this.inventory = Mockito.mock(PlayerInventory.class);

        Mockito.when(this.plugin.getEditToolItem()).thenReturn(this.editToolItem);
        Mockito.when(this.player.getUniqueId()).thenReturn(PLAYER_UUID);
        Mockito.when(this.player.getInventory()).thenReturn(this.inventory);
    }

    @AfterEach
    void tearDown() {
        PlayerEditorProvider.unloadAll();
    }

    @Test
    void testQuitUnloadsPlayerEditor() {
        PlayerEditor first = PlayerEditorProvider.getEditor(this.player);
        PlayerQuitEvent event = Mockito.mock(PlayerQuitEvent.class);
        Mockito.when(event.getPlayer()).thenReturn(this.player);

        this.listener.onQuit(event);

        Assertions.assertNotSame(first, PlayerEditorProvider.getEditor(this.player));
    }

    @Test
    void testOffHandClickIsIgnored() {
        ItemStack item = Mockito.mock(ItemStack.class);
        PlayerInteractEvent event = Mockito.mock(PlayerInteractEvent.class);
        Mockito.when(event.getHand()).thenReturn(EquipmentSlot.OFF_HAND);
        Mockito.when(event.getAction()).thenReturn(Action.RIGHT_CLICK_AIR);
        Mockito.when(event.getItem()).thenReturn(item);
        Mockito.when(event.getPlayer()).thenReturn(this.player);
        Mockito.when(this.editToolItem.check(item)).thenReturn(true);
        Mockito.when(this.player.hasPermission(Permissions.ARMOR_STAND_EDIT)).thenReturn(true);

        this.listener.onClick(event);

        Mockito.verify(event, Mockito.never()).setCancelled(true);
        Mockito.verify(this.player, Mockito.never()).openInventory(Mockito.any(Inventory.class));
    }

    @Test
    void testNonClickActionIsIgnored() {
        ItemStack item = Mockito.mock(ItemStack.class);
        PlayerInteractEvent event = Mockito.mock(PlayerInteractEvent.class);
        Mockito.when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        Mockito.when(event.getAction()).thenReturn(Action.PHYSICAL);
        Mockito.when(event.getItem()).thenReturn(item);
        Mockito.when(event.getPlayer()).thenReturn(this.player);
        Mockito.when(this.editToolItem.check(item)).thenReturn(true);
        Mockito.when(this.player.hasPermission(Permissions.ARMOR_STAND_EDIT)).thenReturn(true);

        this.listener.onClick(event);

        Mockito.verify(event, Mockito.never()).setCancelled(true);
        Mockito.verify(this.player, Mockito.never()).openInventory(Mockito.any(Inventory.class));
    }

    @Test
    void testClickWithoutEditToolIsIgnored() {
        ItemStack item = Mockito.mock(ItemStack.class);
        PlayerInteractEvent event = Mockito.mock(PlayerInteractEvent.class);
        Mockito.when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        Mockito.when(event.getAction()).thenReturn(Action.RIGHT_CLICK_AIR);
        Mockito.when(event.getItem()).thenReturn(item);
        Mockito.when(event.getPlayer()).thenReturn(this.player);
        Mockito.when(this.player.hasPermission(Permissions.ARMOR_STAND_EDIT)).thenReturn(true);

        this.listener.onClick(event);

        Mockito.verify(event, Mockito.never()).setCancelled(true);
        Mockito.verify(this.player, Mockito.never()).openInventory(Mockito.any(Inventory.class));
    }

    @Test
    void testClickWithoutEditPermissionIsIgnored() {
        ItemStack item = Mockito.mock(ItemStack.class);
        PlayerInteractEvent event = Mockito.mock(PlayerInteractEvent.class);
        Mockito.when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        Mockito.when(event.getAction()).thenReturn(Action.RIGHT_CLICK_AIR);
        Mockito.when(event.getItem()).thenReturn(item);
        Mockito.when(event.getPlayer()).thenReturn(this.player);
        Mockito.when(this.editToolItem.check(item)).thenReturn(true);

        this.listener.onClick(event);

        Mockito.verify(event, Mockito.never()).setCancelled(true);
        Mockito.verify(this.player, Mockito.never()).openInventory(Mockito.any(Inventory.class));
    }

    @Test
    void testAuthorizedClickTargetingArmorStandIsConsumed() {
        ItemStack item = Mockito.mock(ItemStack.class);
        PlayerInteractEvent event = Mockito.mock(PlayerInteractEvent.class);
        Mockito.when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        Mockito.when(event.getAction()).thenReturn(Action.LEFT_CLICK_AIR);
        Mockito.when(event.getItem()).thenReturn(item);
        Mockito.when(event.getPlayer()).thenReturn(this.player);
        Mockito.when(this.editToolItem.check(item)).thenReturn(true);
        Mockito.when(this.player.hasPermission(Permissions.ARMOR_STAND_EDIT)).thenReturn(true);
        Mockito.when(this.player.getTargetEntity(5)).thenReturn(Mockito.mock(ArmorStand.class));

        this.listener.onClick(event);

        Mockito.verify(event).setCancelled(true);
        Mockito.verify(this.player, Mockito.never()).openInventory(Mockito.any(Inventory.class));
    }

    @Test
    void testScrollWithoutSneakingIsIgnored() {
        ItemStack item = Mockito.mock(ItemStack.class);
        Mockito.when(this.inventory.getItemInMainHand()).thenReturn(item);
        Mockito.when(this.editToolItem.check(item)).thenReturn(true);
        PlayerEditor editor = PlayerEditorProvider.getEditor(this.player);
        editor.setMode(EditMode.MOVEMENT);
        editor.setAxis(Axis.X);
        PlayerItemHeldEvent event = scrollEvent(0, 1);

        this.listener.onScroll(event);

        Mockito.verify(event, Mockito.never()).setCancelled(true);
        Assertions.assertEquals(Axis.X, editor.getAxis());
    }

    @Test
    void testScrollWithoutEditToolIsIgnored() {
        ItemStack item = Mockito.mock(ItemStack.class);
        Mockito.when(this.player.isSneaking()).thenReturn(true);
        Mockito.when(this.inventory.getItemInMainHand()).thenReturn(item);
        PlayerEditor editor = PlayerEditorProvider.getEditor(this.player);
        editor.setMode(EditMode.MOVEMENT);
        editor.setAxis(Axis.X);
        PlayerItemHeldEvent event = scrollEvent(0, 1);

        this.listener.onScroll(event);

        Mockito.verify(event, Mockito.never()).setCancelled(true);
        Assertions.assertEquals(Axis.X, editor.getAxis());
    }

    @Test
    void testScrollInUnrelatedModeIsIgnored() {
        PlayerEditor editor = configureScroll(EditMode.BASE_PLATE);
        editor.setAxis(Axis.X);
        PlayerItemHeldEvent event = scrollEvent(0, 1);

        this.listener.onScroll(event);

        Mockito.verify(event, Mockito.never()).setCancelled(true);
        Assertions.assertEquals(Axis.X, editor.getAxis());
    }

    @Test
    void testForwardScrollCyclesAxisForward() {
        PlayerEditor editor = configureScroll(EditMode.MOVEMENT);
        editor.setAxis(Axis.X);
        PlayerItemHeldEvent event = scrollEvent(1, 2);

        this.listener.onScroll(event);

        Mockito.verify(event).setCancelled(true);
        Assertions.assertEquals(Axis.Y, editor.getAxis());
        Mockito.verify(this.player).sendActionBar(Messages.MENU_CHANGE_AXIS.apply(Axis.Y));
    }

    @Test
    void testForwardScrollWrapsFromLastSlot() {
        PlayerEditor editor = configureScroll(EditMode.MOVEMENT);
        editor.setAxis(Axis.Z);
        PlayerItemHeldEvent event = scrollEvent(8, 0);

        this.listener.onScroll(event);

        Mockito.verify(event).setCancelled(true);
        Assertions.assertEquals(Axis.X, editor.getAxis());
        Mockito.verify(this.player).sendActionBar(Messages.MENU_CHANGE_AXIS.apply(Axis.X));
    }

    @Test
    void testBackwardScrollCyclesAxisBackward() {
        PlayerEditor editor = configureScroll(EditMode.HEAD_POSE);
        editor.setAxis(Axis.X);
        PlayerItemHeldEvent event = scrollEvent(2, 1);

        this.listener.onScroll(event);

        Mockito.verify(event).setCancelled(true);
        Assertions.assertEquals(Axis.Z, editor.getAxis());
        Mockito.verify(this.player).sendActionBar(Messages.MENU_CHANGE_AXIS.apply(Axis.Z));
    }

    @Test
    void testBackwardScrollWrapsFromFirstSlot() {
        PlayerEditor editor = configureScroll(EditMode.HEAD_POSE);
        editor.setAxis(Axis.Y);
        PlayerItemHeldEvent event = scrollEvent(0, 8);

        this.listener.onScroll(event);

        Mockito.verify(event).setCancelled(true);
        Assertions.assertEquals(Axis.X, editor.getAxis());
        Mockito.verify(this.player).sendActionBar(Messages.MENU_CHANGE_AXIS.apply(Axis.X));
    }

    @Test
    void testResetPoseDoesNotUseScrollAxisSelection() {
        PlayerEditor editor = configureScroll(EditMode.RESET_POSE);
        editor.setAxis(Axis.X);
        PlayerItemHeldEvent event = scrollEvent(0, 1);

        this.listener.onScroll(event);

        Mockito.verify(event, Mockito.never()).setCancelled(true);
        Assertions.assertEquals(Axis.X, editor.getAxis());
    }

    private PlayerEditor configureScroll(EditMode mode) {
        ItemStack item = Mockito.mock(ItemStack.class);
        Mockito.when(this.player.isSneaking()).thenReturn(true);
        Mockito.when(this.inventory.getItemInMainHand()).thenReturn(item);
        Mockito.when(this.editToolItem.check(item)).thenReturn(true);

        PlayerEditor editor = PlayerEditorProvider.getEditor(this.player);
        editor.setMode(mode);
        return editor;
    }

    private PlayerItemHeldEvent scrollEvent(int previousSlot, int newSlot) {
        PlayerItemHeldEvent event = Mockito.mock(PlayerItemHeldEvent.class);
        Mockito.when(event.getPlayer()).thenReturn(this.player);
        Mockito.when(event.getPreviousSlot()).thenReturn(previousSlot);
        Mockito.when(event.getNewSlot()).thenReturn(newSlot);
        return event;
    }
}
