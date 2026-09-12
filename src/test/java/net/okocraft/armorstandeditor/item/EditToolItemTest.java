package net.okocraft.armorstandeditor.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EditToolItemTest {

    private static final NamespacedKey EDIT_ITEM_KEY = new NamespacedKey("armorstandeditor", "edititem");

    private MockedStatic<ItemStack> itemStacks;
    private ItemStack generatedItem;
    private PersistentDataContainer generatedData;

    @BeforeEach
    void setUp() {
        this.itemStacks = Mockito.mockStatic(ItemStack.class);
        this.generatedItem = Mockito.mock(ItemStack.class);
        this.generatedData = Mockito.mock(PersistentDataContainer.class);
        this.itemStacks.when(() -> ItemStack.of(Material.FLINT)).thenReturn(this.generatedItem);
        Mockito.doAnswer(invocation -> {
            Consumer<PersistentDataContainer> consumer = invocation.getArgument(0);
            consumer.accept(this.generatedData);
            return true;
        }).when(this.generatedItem).editPersistentDataContainer(Mockito.any());
    }

    @AfterEach
    void tearDown() {
        this.itemStacks.close();
    }

    @Test
    void testGeneratedItemIsMarkedAsEditTool() {
        new EditToolItem(false, null, List.of());

        Mockito.verify(this.generatedData).set(EDIT_ITEM_KEY, PersistentDataType.BYTE, (byte) 1);
    }

    @Test
    void testCreateFromConfigAppliesPresentationAndAllowsNormalFlint() {
        ConfigurationSection config = Mockito.mock(ConfigurationSection.class);
        Mockito.when(config.getBoolean("tool.allow-normal-flint")).thenReturn(true);
        Mockito.when(config.getString("tool.display-name")).thenReturn("&aEdit Tool");
        Mockito.when(config.getStringList("tool.lore")).thenReturn(List.of("&7First line", "&bSecond line"));

        EditToolItem editToolItem = EditToolItem.createFromConfig(config);

        Mockito.verify(this.generatedItem).setData(
            DataComponentTypes.CUSTOM_NAME,
            Component.text("Edit Tool", NamedTextColor.GREEN)
        );
        Mockito.verify(this.generatedItem).setData(
            DataComponentTypes.LORE,
            ItemLore.lore(List.of(
                Component.text("First line", NamedTextColor.GRAY),
                Component.text("Second line", NamedTextColor.AQUA)
            ))
        );

        ItemStack normalFlint = Mockito.mock(ItemStack.class);
        Mockito.when(normalFlint.getType()).thenReturn(Material.FLINT);
        assertTrue(editToolItem.check(normalFlint));
    }

    @Test
    void testCreateFromConfigWithoutPresentationRequiresMarkedFlint() {
        ConfigurationSection config = Mockito.mock(ConfigurationSection.class);
        Mockito.when(config.getBoolean("tool.allow-normal-flint")).thenReturn(false);
        Mockito.when(config.getString("tool.display-name")).thenReturn(null);
        Mockito.when(config.getStringList("tool.lore")).thenReturn(List.of());

        EditToolItem editToolItem = EditToolItem.createFromConfig(config);

        Mockito.verify(this.generatedItem, Mockito.never()).setData(
            Mockito.eq(DataComponentTypes.CUSTOM_NAME),
            Mockito.any(Component.class)
        );
        Mockito.verify(this.generatedItem, Mockito.never()).setData(
            Mockito.eq(DataComponentTypes.LORE),
            Mockito.any(ItemLore.class)
        );

        ItemStack normalFlint = Mockito.mock(ItemStack.class);
        PersistentDataContainer data = Mockito.mock(PersistentDataContainer.class);
        Mockito.when(normalFlint.getType()).thenReturn(Material.FLINT);
        Mockito.when(normalFlint.getPersistentDataContainer()).thenReturn(data);
        assertFalse(editToolItem.check(normalFlint));
    }

    @Test
    void testNormalFlintIsAcceptedWhenEnabled() {
        EditToolItem editToolItem = new EditToolItem(true, null, List.of());
        ItemStack item = Mockito.mock(ItemStack.class);
        Mockito.when(item.getType()).thenReturn(Material.FLINT);

        assertTrue(editToolItem.check(item));
    }

    @Test
    void testNonFlintIsRejectedWhenNormalFlintIsEnabled() {
        EditToolItem editToolItem = new EditToolItem(true, null, List.of());
        ItemStack item = Mockito.mock(ItemStack.class);
        Mockito.when(item.getType()).thenReturn(Material.STICK);

        assertFalse(editToolItem.check(item));
    }

    @Test
    void testMarkedFlintIsAcceptedWhenNormalFlintIsDisabled() {
        EditToolItem editToolItem = new EditToolItem(false, null, List.of());
        ItemStack item = Mockito.mock(ItemStack.class);
        PersistentDataContainer data = Mockito.mock(PersistentDataContainer.class);
        Mockito.when(item.getType()).thenReturn(Material.FLINT);
        Mockito.when(item.getPersistentDataContainer()).thenReturn(data);
        Mockito.when(data.get(EDIT_ITEM_KEY, PersistentDataType.BYTE)).thenReturn((byte) 1);

        assertTrue(editToolItem.check(item));
    }

    @Test
    void testUnmarkedFlintIsRejectedWhenNormalFlintIsDisabled() {
        EditToolItem editToolItem = new EditToolItem(false, null, List.of());
        ItemStack item = Mockito.mock(ItemStack.class);
        PersistentDataContainer data = Mockito.mock(PersistentDataContainer.class);
        Mockito.when(item.getType()).thenReturn(Material.FLINT);
        Mockito.when(item.getPersistentDataContainer()).thenReturn(data);

        assertFalse(editToolItem.check(item));
    }

    @Test
    void testDifferentMarkerValueIsRejected() {
        EditToolItem editToolItem = new EditToolItem(false, null, List.of());
        ItemStack item = Mockito.mock(ItemStack.class);
        PersistentDataContainer data = Mockito.mock(PersistentDataContainer.class);
        Mockito.when(item.getType()).thenReturn(Material.FLINT);
        Mockito.when(item.getPersistentDataContainer()).thenReturn(data);
        Mockito.when(data.get(EDIT_ITEM_KEY, PersistentDataType.BYTE)).thenReturn((byte) 2);

        assertFalse(editToolItem.check(item));
    }

    @Test
    void testNullItemIsRejected() {
        EditToolItem editToolItem = new EditToolItem(true, null, List.of());

        assertFalse(editToolItem.check(null));
    }

    @Test
    void testGetItemReturnsClone() {
        EditToolItem editToolItem = new EditToolItem(false, null, List.of());
        ItemStack clone = Mockito.mock(ItemStack.class);
        Mockito.when(this.generatedItem.clone()).thenReturn(clone);

        assertSame(clone, editToolItem.getItem());
        Mockito.verify(this.generatedItem).clone();
    }
}
