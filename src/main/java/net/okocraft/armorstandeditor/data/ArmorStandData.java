package net.okocraft.armorstandeditor.data;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ArmorStandData(@NotNull EulerAngle headPose, @NotNull EulerAngle bodyPose,
                             @NotNull EulerAngle rightArmPose, @NotNull EulerAngle leftArmPose,
                             @NotNull EulerAngle rightLegPose, @NotNull EulerAngle leftLegPose,
                             boolean small, boolean visible, boolean glowing,
                             boolean hasGravity, boolean hasArms, boolean hasBasePlate,
                             boolean customNameVisible, @Nullable Component customName,
                             @NotNull ItemStack helmet, @NotNull ItemStack chestplate,
                             @NotNull ItemStack leggings, @NotNull ItemStack boots,
                             @NotNull ItemStack itemInMainHand, @NotNull ItemStack itemInOffHand) {

    public static @NotNull ArmorStandData create(@NotNull ArmorStand source) {
        return new ArmorStandData(
                source.getHeadPose(), source.getBodyPose(),
                source.getRightArmPose(), source.getLeftArmPose(),
                source.getRightLegPose(), source.getLeftLegPose(),
                source.isSmall(), source.isVisible(), source.isGlowing(),
                source.hasGravity(), source.hasArms(), source.hasBasePlate(),
                source.isCustomNameVisible(), source.customName(),
                source.getItem(EquipmentSlot.HEAD).clone(),
                source.getItem(EquipmentSlot.CHEST).clone(),
                source.getItem(EquipmentSlot.LEGS).clone(),
                source.getItem(EquipmentSlot.FEET).clone(),
                source.getItem(EquipmentSlot.HAND).clone(),
                source.getItem(EquipmentSlot.OFF_HAND).clone()
        );
    }

    public void apply(@NotNull ArmorStand target, boolean creative) {
        target.setHeadPose(this.headPose());
        target.setBodyPose(this.bodyPose());
        target.setRightArmPose(this.rightArmPose());
        target.setLeftArmPose(this.leftArmPose());
        target.setRightLegPose(this.rightLegPose());
        target.setLeftLegPose(this.leftLegPose());
        target.setSmall(this.small());
        target.setVisible(this.visible());
        target.setGlowing(this.glowing());
        target.setGravity(this.hasGravity());
        target.setArms(this.hasArms());
        target.setBasePlate(this.hasBasePlate());

        if (creative) {
            target.setCustomNameVisible(this.customNameVisible());
            target.customName(this.customName());

            target.setItem(EquipmentSlot.HEAD, this.helmet());
            target.setItem(EquipmentSlot.CHEST, this.chestplate());
            target.setItem(EquipmentSlot.LEGS, this.leggings());
            target.setItem(EquipmentSlot.FEET, this.boots());
            target.setItem(EquipmentSlot.HAND, this.itemInMainHand());
            target.setItem(EquipmentSlot.OFF_HAND, this.itemInOffHand());
        }
    }
}
