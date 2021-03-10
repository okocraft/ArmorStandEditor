package net.okocraft.armorstandeditor.editmode;

import net.okocraft.armorstandeditor.editor.PlayerEditor;
import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.jetbrains.annotations.NotNull;

class Paste extends AbstractEditMode {

    Paste() {
        super("paste");
    }

    @Override
    public void edit(@NotNull PlayerEditor editor, @NotNull ArmorStand armorStand, boolean reverse) {
        ArmorStand original = editor.getSelectedArmorStand();

        if (original == null) {
            return;
        }

        armorStand.setHeadPose(original.getHeadPose());
        armorStand.setBodyPose(original.getBodyPose());
        armorStand.setRightArmPose(original.getRightArmPose());
        armorStand.setLeftArmPose(original.getLeftArmPose());
        armorStand.setRightLegPose(original.getRightLegPose());
        armorStand.setLeftLegPose(original.getLeftLegPose());
        armorStand.setSmall(original.isSmall());
        armorStand.setGravity(original.hasGravity());
        armorStand.setVisible(original.isVisible());
        armorStand.setArms(original.hasArms());
        armorStand.setBasePlate(original.hasBasePlate());
        armorStand.setGlowing(original.isGlowing());

        if (editor.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            EntityEquipment armorStandEquipment = armorStand.getEquipment();
            EntityEquipment originalEquipment = original.getEquipment();

            if (armorStandEquipment != null && originalEquipment != null) {
                armorStandEquipment.setHelmet(originalEquipment.getHelmet());
                armorStandEquipment.setChestplate(originalEquipment.getChestplate());
                armorStandEquipment.setLeggings(originalEquipment.getLeggings());
                armorStandEquipment.setBoots(originalEquipment.getBoots());
                armorStandEquipment.setItemInMainHand(originalEquipment.getItemInMainHand());
                armorStandEquipment.setItemInOffHand(originalEquipment.getItemInOffHand());
            }
        }
    }
}