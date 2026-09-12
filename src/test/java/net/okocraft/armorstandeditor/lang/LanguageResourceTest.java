package net.okocraft.armorstandeditor.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.okocraft.armorstandeditor.editor.EditMode;
import org.bukkit.Axis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

class LanguageResourceTest {

    private static final List<String> LOCALES = List.of("en", "ja");

    @Test
    void testBundledLocalesHaveSameKeys() throws IOException {
        Set<String> englishKeys = loadKeys("en");
        Set<String> japaneseKeys = loadKeys("ja");

        Assertions.assertEquals(englishKeys, japaneseKeys);
    }

    @Test
    void testGeneratedTranslationKeysExistInEveryBundledLocale() throws IOException {
        Set<String> requiredKeys = new HashSet<>();

        requiredKeys.add(translationKey(Components.EQUIPMENT_MENU_TITLE));
        requiredKeys.add(translationKey(Components.SELECTION_MENU_TITLE));
        requiredKeys.add(translationKey(Components.ADJUSTMENT_MODE_FINE));
        requiredKeys.add(translationKey(Components.ADJUSTMENT_MODE_COARSE));

        for (Axis axis : Axis.values()) {
            requiredKeys.add("armorstandeditor.axis." + axis.name().toLowerCase(Locale.ENGLISH));
        }

        EditMode.names().forEach(name -> requiredKeys.add("armorstandeditor.mode." + name));

        Set<String> singleLineIcons = new HashSet<>();
        EditMode.names().filter(name -> !name.equals("none") && !name.equals("lock") && !name.equals("removal"))
            .forEach(singleLineIcons::add);
        singleLineIcons.addAll(List.of(
            "x-axis", "y-axis", "z-axis", "coarse", "fine",
            "copy-slot-1", "copy-slot-2", "copy-slot-3", "copy-slot-4", "help"
        ));

        for (String icon : singleLineIcons) {
            requiredKeys.add("armorstandeditor.menu.selection.icon." + icon + ".name");
            requiredKeys.add("armorstandeditor.menu.selection.icon." + icon + ".description");
        }

        for (String icon : List.of("lock", "removal")) {
            requiredKeys.add("armorstandeditor.menu.selection.icon." + icon + ".name");
            requiredKeys.add("armorstandeditor.menu.selection.icon." + icon + ".description-1");
            requiredKeys.add("armorstandeditor.menu.selection.icon." + icon + ".description-2");
        }

        for (String icon : List.of("helmet", "chest-plate", "leggings", "boots", "right-hand", "left-hand")) {
            requiredKeys.add("armorstandeditor.menu.equipment.icon." + icon);
        }

        for (String locale : LOCALES) {
            Set<String> localeKeys = loadKeys(locale);
            Assertions.assertTrue(
                localeKeys.containsAll(requiredKeys),
                () -> locale + " is missing translation keys: " + difference(requiredKeys, localeKeys)
            );
        }
    }

    private static String translationKey(Component component) {
        Assertions.assertInstanceOf(TranslatableComponent.class, component);
        return ((TranslatableComponent) component).key();
    }

    private static Set<String> loadKeys(String locale) throws IOException {
        Properties properties = new Properties();
        String path = "languages/" + locale + ".properties";

        try (var input = LanguageResourceTest.class.getClassLoader().getResourceAsStream(path)) {
            Assertions.assertNotNull(input, "Missing language resource: " + path);
            try (var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
        }

        return properties.stringPropertyNames();
    }

    private static Set<String> difference(Set<String> required, Set<String> actual) {
        Set<String> missing = new HashSet<>(required);
        missing.removeAll(actual);
        return missing;
    }
}
