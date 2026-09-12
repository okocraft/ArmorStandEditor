package net.okocraft.armorstandeditor.data;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ArmorStandDataTest {

    @Test
    void testCreateCopiesArmorStandStateAndClonesEquipment() {
        ArmorStand source = Mockito.mock(ArmorStand.class);

        EulerAngle head = new EulerAngle(1, 2, 3);
        EulerAngle body = new EulerAngle(4, 5, 6);
        EulerAngle rightArm = new EulerAngle(7, 8, 9);
        EulerAngle leftArm = new EulerAngle(10, 11, 12);
        EulerAngle rightLeg = new EulerAngle(13, 14, 15);
        EulerAngle leftLeg = new EulerAngle(16, 17, 18);
        Component name = Component.text("armor stand");

        Mockito.when(source.getHeadPose()).thenReturn(head);
        Mockito.when(source.getBodyPose()).thenReturn(body);
        Mockito.when(source.getRightArmPose()).thenReturn(rightArm);
        Mockito.when(source.getLeftArmPose()).thenReturn(leftArm);
        Mockito.when(source.getRightLegPose()).thenReturn(rightLeg);
        Mockito.when(source.getLeftLegPose()).thenReturn(leftLeg);
        Mockito.when(source.isSmall()).thenReturn(true);
        Mockito.when(source.isVisible()).thenReturn(false);
        Mockito.when(source.isGlowing()).thenReturn(true);
        Mockito.when(source.hasGravity()).thenReturn(false);
        Mockito.when(source.hasArms()).thenReturn(true);
        Mockito.when(source.hasBasePlate()).thenReturn(false);
        Mockito.when(source.isCustomNameVisible()).thenReturn(true);
        Mockito.when(source.customName()).thenReturn(name);

        ItemStack helmet = stubItem(source, EquipmentSlot.HEAD, Material.DIAMOND_HELMET);
        ItemStack chestplate = stubItem(source, EquipmentSlot.CHEST, Material.DIAMOND_CHESTPLATE);
        ItemStack leggings = stubItem(source, EquipmentSlot.LEGS, Material.DIAMOND_LEGGINGS);
        ItemStack boots = stubItem(source, EquipmentSlot.FEET, Material.DIAMOND_BOOTS);
        ItemStack mainHand = stubItem(source, EquipmentSlot.HAND, Material.DIAMOND_SWORD);
        ItemStack offHand = stubItem(source, EquipmentSlot.OFF_HAND, Material.SHIELD);

        ArmorStandData data = ArmorStandData.create(source);

        Assertions.assertSame(head, data.headPose());
        Assertions.assertSame(body, data.bodyPose());
        Assertions.assertSame(rightArm, data.rightArmPose());
        Assertions.assertSame(leftArm, data.leftArmPose());
        Assertions.assertSame(rightLeg, data.rightLegPose());
        Assertions.assertSame(leftLeg, data.leftLegPose());
        Assertions.assertTrue(data.small());
        Assertions.assertFalse(data.visible());
        Assertions.assertTrue(data.glowing());
        Assertions.assertFalse(data.hasGravity());
        Assertions.assertTrue(data.hasArms());
        Assertions.assertFalse(data.hasBasePlate());
        Assertions.assertTrue(data.customNameVisible());
        Assertions.assertSame(name, data.customName());
        assertCopiedItem(helmet, data.helmet());
        assertCopiedItem(chestplate, data.chestplate());
        assertCopiedItem(leggings, data.leggings());
        assertCopiedItem(boots, data.boots());
        assertCopiedItem(mainHand, data.itemInMainHand());
        assertCopiedItem(offHand, data.itemInOffHand());
    }

    @Test
    void testApplyUpdatesEditableStateWithoutCreativeOnlyData() {
        ArmorStand target = Mockito.mock(ArmorStand.class);
        ArmorStandData data = createData();

        data.apply(target, false);

        verifyCommonState(target, data);
        Mockito.verify(target, Mockito.never()).setCustomNameVisible(Mockito.anyBoolean());
        Mockito.verify(target, Mockito.never()).customName(Mockito.any());
        Mockito.verify(target, Mockito.never()).setItem(Mockito.any(EquipmentSlot.class), Mockito.any(ItemStack.class));
    }

    @Test
    void testApplyInCreativeAlsoUpdatesNameAndEquipment() {
        ArmorStand target = Mockito.mock(ArmorStand.class);
        ArmorStandData data = createData();

        data.apply(target, true);

        verifyCommonState(target, data);
        Mockito.verify(target).setCustomNameVisible(data.customNameVisible());
        Mockito.verify(target).customName(data.customName());
        Mockito.verify(target).setItem(EquipmentSlot.HEAD, data.helmet());
        Mockito.verify(target).setItem(EquipmentSlot.CHEST, data.chestplate());
        Mockito.verify(target).setItem(EquipmentSlot.LEGS, data.leggings());
        Mockito.verify(target).setItem(EquipmentSlot.FEET, data.boots());
        Mockito.verify(target).setItem(EquipmentSlot.HAND, data.itemInMainHand());
        Mockito.verify(target).setItem(EquipmentSlot.OFF_HAND, data.itemInOffHand());
    }

    private static ItemStack stubItem(ArmorStand source, EquipmentSlot slot, Material material) {
        ItemStack item = ItemStack.of(material);
        Mockito.when(source.getItem(slot)).thenReturn(item);
        return item;
    }

    private static void assertCopiedItem(ItemStack source, ItemStack copied) {
        Assertions.assertEquals(source, copied);
        Assertions.assertNotSame(source, copied);
    }

    private static ArmorStandData createData() {
        return new ArmorStandData(
            new EulerAngle(1, 2, 3),
            new EulerAngle(4, 5, 6),
            new EulerAngle(7, 8, 9),
            new EulerAngle(10, 11, 12),
            new EulerAngle(13, 14, 15),
            new EulerAngle(16, 17, 18),
            true,
            false,
            true,
            false,
            true,
            false,
            true,
            Component.text("armor stand"),
            ItemStack.of(Material.DIAMOND_HELMET),
            ItemStack.of(Material.DIAMOND_CHESTPLATE),
            ItemStack.of(Material.DIAMOND_LEGGINGS),
            ItemStack.of(Material.DIAMOND_BOOTS),
            ItemStack.of(Material.DIAMOND_SWORD),
            ItemStack.of(Material.SHIELD)
        );
    }

    private static void verifyCommonState(ArmorStand target, ArmorStandData data) {
        Mockito.verify(target).setHeadPose(data.headPose());
        Mockito.verify(target).setBodyPose(data.bodyPose());
        Mockito.verify(target).setRightArmPose(data.rightArmPose());
        Mockito.verify(target).setLeftArmPose(data.leftArmPose());
        Mockito.verify(target).setRightLegPose(data.rightLegPose());
        Mockito.verify(target).setLeftLegPose(data.leftLegPose());
        Mockito.verify(target).setSmall(data.small());
        Mockito.verify(target).setVisible(data.visible());
        Mockito.verify(target).setGlowing(data.glowing());
        Mockito.verify(target).setGravity(data.hasGravity());
        Mockito.verify(target).setArms(data.hasArms());
        Mockito.verify(target).setBasePlate(data.hasBasePlate());
    }
}
