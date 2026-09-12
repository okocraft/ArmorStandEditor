package net.okocraft.armorstandeditor.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EditToolItemTest {

    private static final NamespacedKey EDIT_ITEM_KEY = new NamespacedKey("armorstandeditor", "edititem");

    @Test
    void testGeneratedItemIsMarkedAsEditTool() {
        ItemStack item = new EditToolItem(false, null, List.of()).getItem();

        assertEquals((byte) 1, item.getPersistentDataContainer().get(EDIT_ITEM_KEY, PersistentDataType.BYTE));
    }

    @Test
    void testCreateFromConfigAppliesPresentationAndAllowsNormalFlint() {
        ConfigurationSection config = Mockito.mock(ConfigurationSection.class);
        Mockito.when(config.getBoolean("tool.allow-normal-flint")).thenReturn(true);
        Mockito.when(config.getString("tool.display-name")).thenReturn("&aEdit Tool");
        Mockito.when(config.getStringList("tool.lore")).thenReturn(List.of("&7First line", "&bSecond line"));

        EditToolItem editToolItem = EditToolItem.createFromConfig(config);
        ItemStack generatedItem = editToolItem.getItem();

        assertEquals(Component.text("Edit Tool", NamedTextColor.GREEN), generatedItem.getData(DataComponentTypes.CUSTOM_NAME));
        assertEquals(
            List.of(
                Component.text("First line", NamedTextColor.GRAY),
                Component.text("Second line", NamedTextColor.AQUA)
            ),
            generatedItem.getData(DataComponentTypes.LORE).lines()
        );
        assertTrue(editToolItem.check(ItemStack.of(Material.FLINT)));
    }

    @Test
    void testCreateFromConfigWithoutPresentationRequiresMarkedFlint() {
        ConfigurationSection config = Mockito.mock(ConfigurationSection.class);
        Mockito.when(config.getBoolean("tool.allow-normal-flint")).thenReturn(false);
        Mockito.when(config.getString("tool.display-name")).thenReturn(null);
        Mockito.when(config.getStringList("tool.lore")).thenReturn(List.of());

        EditToolItem editToolItem = EditToolItem.createFromConfig(config);
        ItemStack generatedItem = editToolItem.getItem();

        assertFalse(generatedItem.hasData(DataComponentTypes.CUSTOM_NAME));
        assertFalse(generatedItem.hasData(DataComponentTypes.LORE));
        assertFalse(editToolItem.check(ItemStack.of(Material.FLINT)));
    }

    @Test
    void testNormalFlintIsAcceptedWhenEnabled() {
        EditToolItem editToolItem = new EditToolItem(true, null, List.of());

        assertTrue(editToolItem.check(ItemStack.of(Material.FLINT)));
    }

    @Test
    void testNonFlintIsRejectedWhenNormalFlintIsEnabled() {
        EditToolItem editToolItem = new EditToolItem(true, null, List.of());

        assertFalse(editToolItem.check(ItemStack.of(Material.STICK)));
    }

    @Test
    void testMarkedFlintIsAcceptedWhenNormalFlintIsDisabled() {
        EditToolItem editToolItem = new EditToolItem(false, null, List.of());

        assertTrue(editToolItem.check(editToolItem.getItem()));
    }

    @Test
    void testUnmarkedFlintIsRejectedWhenNormalFlintIsDisabled() {
        EditToolItem editToolItem = new EditToolItem(false, null, List.of());

        assertFalse(editToolItem.check(ItemStack.of(Material.FLINT)));
    }

    @Test
    void testDifferentMarkerValueIsRejected() {
        EditToolItem editToolItem = new EditToolItem(false, null, List.of());
        ItemStack item = ItemStack.of(Material.FLINT);
        item.editPersistentDataContainer(container -> container.set(EDIT_ITEM_KEY, PersistentDataType.BYTE, (byte) 2));

        assertFalse(editToolItem.check(item));
    }

    @Test
    void testNullItemIsRejected() {
        EditToolItem editToolItem = new EditToolItem(true, null, List.of());

        assertFalse(editToolItem.check(null));
    }

    @Test
    void testGetItemReturnsIndependentClone() {
        EditToolItem editToolItem = new EditToolItem(false, null, List.of());
        ItemStack first = editToolItem.getItem();
        ItemStack second = editToolItem.getItem();

        assertNotSame(first, second);
        assertEquals(first, second);

        first.setAmount(2);
        assertEquals(1, second.getAmount());
    }
}
