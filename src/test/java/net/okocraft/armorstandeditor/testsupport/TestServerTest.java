package net.okocraft.armorstandeditor.testsupport;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestServerTest {

    @Test
    void testRealItemStackSupportsPersistentData() {
        ItemStack item = ItemStack.of(Material.FLINT);
        NamespacedKey key = new NamespacedKey("armorstandeditor", "test");
        item.editPersistentDataContainer(container -> container.set(key, PersistentDataType.BYTE, (byte) 1));

        Assertions.assertEquals(Material.FLINT, item.getType());
        Assertions.assertEquals((byte) 1, item.getPersistentDataContainer().get(key, PersistentDataType.BYTE));
    }
}
