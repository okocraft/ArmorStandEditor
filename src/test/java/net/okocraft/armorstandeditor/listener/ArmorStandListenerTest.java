package net.okocraft.armorstandeditor.listener;

import net.kyori.adventure.text.Component;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.editor.EditMode;
import net.okocraft.armorstandeditor.editor.PlayerEditorProvider;
import net.okocraft.armorstandeditor.item.EditToolItem;
import net.okocraft.armorstandeditor.lang.Messages;
import net.okocraft.armorstandeditor.permission.Permissions;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

class ArmorStandListenerTest {

    private ArmorStandEditorPlugin plugin;
    private EditToolItem editToolItem;
    private ArmorStandListener listener;
    private Player player;
    private PlayerInventory inventory;

    @BeforeEach
    void setUp() {
        this.plugin = Mockito.mock(ArmorStandEditorPlugin.class);
        this.editToolItem = Mockito.mock(EditToolItem.class);
        this.listener = new ArmorStandListener(this.plugin);
        this.player = Mockito.mock(Player.class);
        this.inventory = Mockito.mock(PlayerInventory.class);

        Mockito.when(this.plugin.getEditToolItem()).thenReturn(this.editToolItem);
        Mockito.when(this.player.getUniqueId()).thenReturn(UUID.randomUUID());
        Mockito.when(this.player.getInventory()).thenReturn(this.inventory);
    }

    @AfterEach
    void tearDown() {
        PlayerEditorProvider.unloadAll();
    }

    @Test
    void testCancelledDamageIsIgnored() {
        EntityDamageByEntityEvent event = Mockito.mock(EntityDamageByEntityEvent.class);
        Mockito.when(event.isCancelled()).thenReturn(true);

        this.listener.onLeftClick(event);

        Mockito.verify(event, Mockito.never()).getEntity();
        Mockito.verify(event, Mockito.never()).setCancelled(true);
    }

    @Test
    void testDamageToNonArmorStandIsIgnored() {
        EntityDamageByEntityEvent event = Mockito.mock(EntityDamageByEntityEvent.class);
        Mockito.when(event.getEntity()).thenReturn(Mockito.mock(Entity.class));

        this.listener.onLeftClick(event);

        Mockito.verify(event, Mockito.never()).getDamager();
        Mockito.verify(event, Mockito.never()).setCancelled(true);
    }

    @Test
    void testDamageByNonPlayerIsIgnored() {
        EntityDamageByEntityEvent event = Mockito.mock(EntityDamageByEntityEvent.class);
        Mockito.when(event.getEntity()).thenReturn(Mockito.mock(ArmorStand.class));
        Mockito.when(event.getDamager()).thenReturn(Mockito.mock(Entity.class));

        this.listener.onLeftClick(event);

        Mockito.verify(event, Mockito.never()).setCancelled(true);
    }

    @Test
    void testDamageWithoutEditToolIsIgnored() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        ItemStack item = Mockito.mock(ItemStack.class);
        EntityDamageByEntityEvent event = Mockito.mock(EntityDamageByEntityEvent.class);
        Mockito.when(event.getEntity()).thenReturn(armorStand);
        Mockito.when(event.getDamager()).thenReturn(this.player);
        Mockito.when(this.inventory.getItemInMainHand()).thenReturn(item);

        this.listener.onLeftClick(event);

        Mockito.verify(this.editToolItem).check(item);
        Mockito.verify(event, Mockito.never()).setCancelled(true);
    }

    @Test
    void testDamageWithoutEditPermissionSendsMessage() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        ItemStack item = Mockito.mock(ItemStack.class);
        EntityDamageByEntityEvent event = Mockito.mock(EntityDamageByEntityEvent.class);
        Mockito.when(event.getEntity()).thenReturn(armorStand);
        Mockito.when(event.getDamager()).thenReturn(this.player);
        Mockito.when(this.inventory.getItemInMainHand()).thenReturn(item);
        Mockito.when(this.editToolItem.check(item)).thenReturn(true);

        this.listener.onLeftClick(event);

        Mockito.verify(this.player).sendMessage(Messages.EDIT_NO_PERMISSION);
        Mockito.verify(event, Mockito.never()).setCancelled(true);
    }

    @Test
    void testDamageWithEditToolExecutesCurrentMode() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        ItemStack item = Mockito.mock(ItemStack.class);
        EntityDamageByEntityEvent event = Mockito.mock(EntityDamageByEntityEvent.class);
        Mockito.when(event.getEntity()).thenReturn(armorStand);
        Mockito.when(event.getDamager()).thenReturn(this.player);
        Mockito.when(this.inventory.getItemInMainHand()).thenReturn(item);
        Mockito.when(this.editToolItem.check(item)).thenReturn(true);
        Mockito.when(this.player.hasPermission(Permissions.ARMOR_STAND_EDIT)).thenReturn(true);
        Mockito.when(this.player.hasPermission(EditMode.BASE_PLATE.getPermission())).thenReturn(true);

        PlayerEditorProvider.getEditor(this.player).setMode(EditMode.BASE_PLATE);

        this.listener.onLeftClick(event);

        Mockito.verify(event).setCancelled(true);
        Mockito.verify(armorStand).setBasePlate(true);
    }

    @Test
    void testCancelledInteractionIsIgnored() {
        PlayerInteractAtEntityEvent event = Mockito.mock(PlayerInteractAtEntityEvent.class);
        Mockito.when(event.isCancelled()).thenReturn(true);

        this.listener.onRightClick(event);

        Mockito.verify(event, Mockito.never()).getRightClicked();
        Mockito.verify(event, Mockito.never()).setCancelled(true);
    }

    @Test
    void testInteractionWithNonArmorStandIsIgnored() {
        PlayerInteractAtEntityEvent event = Mockito.mock(PlayerInteractAtEntityEvent.class);
        Mockito.when(event.getRightClicked()).thenReturn(Mockito.mock(Entity.class));

        this.listener.onRightClick(event);

        Mockito.verify(event, Mockito.never()).getPlayer();
        Mockito.verify(event, Mockito.never()).setCancelled(true);
    }

    @Test
    void testOffHandInteractionUsesOffHandItem() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        ItemStack mainHand = Mockito.mock(ItemStack.class);
        ItemStack offHand = Mockito.mock(ItemStack.class);
        PlayerInteractAtEntityEvent event = Mockito.mock(PlayerInteractAtEntityEvent.class);
        Mockito.when(event.getRightClicked()).thenReturn(armorStand);
        Mockito.when(event.getPlayer()).thenReturn(this.player);
        Mockito.when(event.getHand()).thenReturn(EquipmentSlot.OFF_HAND);
        Mockito.when(this.inventory.getItemInMainHand()).thenReturn(mainHand);
        Mockito.when(this.inventory.getItemInOffHand()).thenReturn(offHand);
        Mockito.when(this.editToolItem.check(offHand)).thenReturn(true);
        Mockito.when(this.player.hasPermission(Permissions.ARMOR_STAND_EDIT)).thenReturn(true);
        Mockito.when(this.player.hasPermission(EditMode.BASE_PLATE.getPermission())).thenReturn(true);

        PlayerEditorProvider.getEditor(this.player).setMode(EditMode.BASE_PLATE);

        this.listener.onRightClick(event);

        Mockito.verify(this.inventory).getItemInOffHand();
        Mockito.verify(this.inventory, Mockito.never()).getItemInMainHand();
        Mockito.verify(this.editToolItem).check(offHand);
        Mockito.verify(event).setCancelled(true);
        Mockito.verify(armorStand).setBasePlate(true);
    }

    @Test
    void testEditToolInteractionWithoutPermissionSendsMessage() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        ItemStack item = Mockito.mock(ItemStack.class);
        PlayerInteractAtEntityEvent event = Mockito.mock(PlayerInteractAtEntityEvent.class);
        Mockito.when(event.getRightClicked()).thenReturn(armorStand);
        Mockito.when(event.getPlayer()).thenReturn(this.player);
        Mockito.when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        Mockito.when(this.inventory.getItemInMainHand()).thenReturn(item);
        Mockito.when(this.editToolItem.check(item)).thenReturn(true);

        this.listener.onRightClick(event);

        Mockito.verify(this.player).sendMessage(Messages.EDIT_NO_PERMISSION);
        Mockito.verify(event, Mockito.never()).setCancelled(true);
    }

    @Test
    void testNameTagRenamesArmorStand() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        ItemStack nameTag = Mockito.mock(ItemStack.class);
        ItemMeta meta = Mockito.mock(ItemMeta.class);
        Component name = Component.text("name");
        PlayerInteractAtEntityEvent event = Mockito.mock(PlayerInteractAtEntityEvent.class);
        Mockito.when(event.getRightClicked()).thenReturn(armorStand);
        Mockito.when(event.getPlayer()).thenReturn(this.player);
        Mockito.when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        Mockito.when(this.inventory.getItemInMainHand()).thenReturn(nameTag);
        Mockito.when(nameTag.getType()).thenReturn(Material.NAME_TAG);
        Mockito.when(nameTag.getItemMeta()).thenReturn(meta);
        Mockito.when(meta.displayName()).thenReturn(name);
        Mockito.when(this.player.hasPermission(Permissions.ARMOR_STAND_RENAME)).thenReturn(true);

        this.listener.onRightClick(event);

        Mockito.verify(event).setCancelled(true);
        Mockito.verify(armorStand).customName(name);
        Mockito.verify(armorStand).setCustomNameVisible(true);
    }

    @Test
    void testUnnamedNameTagClearsArmorStandName() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        ItemStack nameTag = Mockito.mock(ItemStack.class);
        ItemMeta meta = Mockito.mock(ItemMeta.class);
        PlayerInteractAtEntityEvent event = Mockito.mock(PlayerInteractAtEntityEvent.class);
        Mockito.when(event.getRightClicked()).thenReturn(armorStand);
        Mockito.when(event.getPlayer()).thenReturn(this.player);
        Mockito.when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        Mockito.when(this.inventory.getItemInMainHand()).thenReturn(nameTag);
        Mockito.when(nameTag.getType()).thenReturn(Material.NAME_TAG);
        Mockito.when(nameTag.getItemMeta()).thenReturn(meta);
        Mockito.when(this.player.hasPermission(Permissions.ARMOR_STAND_RENAME)).thenReturn(true);

        this.listener.onRightClick(event);

        Mockito.verify(event).setCancelled(true);
        Mockito.verify(armorStand).customName(null);
        Mockito.verify(armorStand).setCustomNameVisible(false);
    }

    @Test
    void testNameTagWithoutRenamePermissionIsRejected() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        ItemStack nameTag = Mockito.mock(ItemStack.class);
        PlayerInteractAtEntityEvent event = Mockito.mock(PlayerInteractAtEntityEvent.class);
        Mockito.when(event.getRightClicked()).thenReturn(armorStand);
        Mockito.when(event.getPlayer()).thenReturn(this.player);
        Mockito.when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        Mockito.when(this.inventory.getItemInMainHand()).thenReturn(nameTag);
        Mockito.when(nameTag.getType()).thenReturn(Material.NAME_TAG);

        this.listener.onRightClick(event);

        Mockito.verify(event).setCancelled(true);
        Mockito.verify(this.player).sendMessage(Messages.RENAME_NO_PERMISSION);
        Mockito.verify(nameTag, Mockito.never()).getItemMeta();
    }
}
