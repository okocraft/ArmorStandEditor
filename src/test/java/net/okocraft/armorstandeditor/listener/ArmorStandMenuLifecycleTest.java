package net.okocraft.armorstandeditor.listener;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import net.okocraft.armorstandeditor.ArmorStandEditorPlugin;
import net.okocraft.armorstandeditor.menu.EquipmentMenuProvider;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ArmorStandMenuLifecycleTest {

    @Test
    void testManipulationClosesEquipmentMenu() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        PlayerArmorStandManipulateEvent event = Mockito.mock(PlayerArmorStandManipulateEvent.class);
        Mockito.when(event.getRightClicked()).thenReturn(armorStand);

        try (MockedStatic<EquipmentMenuProvider> provider = Mockito.mockStatic(EquipmentMenuProvider.class)) {
            listener().onManipulate(event);
            provider.verify(() -> EquipmentMenuProvider.closeMenu(armorStand));
        }
    }

    @Test
    void testArmorDispenseClosesEquipmentMenu() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        BlockDispenseArmorEvent event = Mockito.mock(BlockDispenseArmorEvent.class);
        Mockito.when(event.getTargetEntity()).thenReturn(armorStand);

        try (MockedStatic<EquipmentMenuProvider> provider = Mockito.mockStatic(EquipmentMenuProvider.class)) {
            listener().onDispenseArmor(event);
            provider.verify(() -> EquipmentMenuProvider.closeMenu(armorStand));
        }
    }

    @Test
    void testArmorDispenseForOtherEntityIsIgnored() {
        BlockDispenseArmorEvent event = Mockito.mock(BlockDispenseArmorEvent.class);
        Mockito.when(event.getTargetEntity()).thenReturn(Mockito.mock(Entity.class));

        try (MockedStatic<EquipmentMenuProvider> provider = Mockito.mockStatic(EquipmentMenuProvider.class)) {
            listener().onDispenseArmor(event);
            provider.verifyNoInteractions();
        }
    }

    @Test
    void testArmorStandRemovalClosesEquipmentMenu() {
        ArmorStand armorStand = Mockito.mock(ArmorStand.class);
        EntityRemoveFromWorldEvent event = Mockito.mock(EntityRemoveFromWorldEvent.class);
        Mockito.when(event.getEntity()).thenReturn(armorStand);

        try (MockedStatic<EquipmentMenuProvider> provider = Mockito.mockStatic(EquipmentMenuProvider.class)) {
            listener().onRemove(event);
            provider.verify(() -> EquipmentMenuProvider.closeMenu(armorStand));
        }
    }

    @Test
    void testOtherEntityRemovalIsIgnored() {
        EntityRemoveFromWorldEvent event = Mockito.mock(EntityRemoveFromWorldEvent.class);
        Mockito.when(event.getEntity()).thenReturn(Mockito.mock(Entity.class));

        try (MockedStatic<EquipmentMenuProvider> provider = Mockito.mockStatic(EquipmentMenuProvider.class)) {
            listener().onRemove(event);
            provider.verifyNoInteractions();
        }
    }

    private static ArmorStandListener listener() {
        return new ArmorStandListener(Mockito.mock(ArmorStandEditorPlugin.class));
    }
}
