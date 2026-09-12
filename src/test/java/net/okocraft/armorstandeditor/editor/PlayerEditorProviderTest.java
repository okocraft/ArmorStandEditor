package net.okocraft.armorstandeditor.editor;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

class PlayerEditorProviderTest {

    @AfterEach
    void clearEditors() {
        PlayerEditorProvider.unloadAll();
    }

    @Test
    void testSamePlayerReturnsSameEditor() {
        UUID uuid = UUID.randomUUID();
        Player player = player(uuid);

        PlayerEditor first = PlayerEditorProvider.getEditor(player);
        PlayerEditor second = PlayerEditorProvider.getEditor(player);

        Assertions.assertSame(first, second);
        Assertions.assertSame(player, first.getPlayer());
    }

    @Test
    void testSameUuidKeepsOriginalPlayerInstance() {
        UUID uuid = UUID.randomUUID();
        Player firstPlayer = player(uuid);
        Player secondPlayer = player(uuid);

        PlayerEditor first = PlayerEditorProvider.getEditor(firstPlayer);
        PlayerEditor second = PlayerEditorProvider.getEditor(secondPlayer);

        Assertions.assertSame(first, second);
        Assertions.assertSame(firstPlayer, second.getPlayer());
    }

    @Test
    void testHumanEntityPlayerUsesPlayerPath() {
        Player player = player(UUID.randomUUID());

        PlayerEditor fromPlayer = PlayerEditorProvider.getEditor(player);
        PlayerEditor fromHumanEntity = PlayerEditorProvider.getEditor((HumanEntity) player);

        Assertions.assertSame(fromPlayer, fromHumanEntity);
    }

    @Test
    void testNonPlayerHumanEntityResolvesPlayerByUuid() {
        UUID uuid = UUID.randomUUID();
        HumanEntity humanEntity = Mockito.mock(HumanEntity.class);
        Player player = player(uuid);
        Mockito.when(humanEntity.getUniqueId()).thenReturn(uuid);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer(uuid)).thenReturn(player);

            PlayerEditor editor = PlayerEditorProvider.getEditor(humanEntity);

            Assertions.assertSame(player, editor.getPlayer());
            bukkit.verify(() -> Bukkit.getPlayer(uuid));
        }
    }

    @Test
    void testUnloadRemovesPlayerEditor() {
        Player player = player(UUID.randomUUID());
        PlayerEditor first = PlayerEditorProvider.getEditor(player);

        PlayerEditorProvider.unload(player);
        PlayerEditor second = PlayerEditorProvider.getEditor(player);

        Assertions.assertNotSame(first, second);
    }

    @Test
    void testUnloadAllRemovesAllEditors() {
        Player firstPlayer = player(UUID.randomUUID());
        Player secondPlayer = player(UUID.randomUUID());
        PlayerEditor first = PlayerEditorProvider.getEditor(firstPlayer);
        PlayerEditor second = PlayerEditorProvider.getEditor(secondPlayer);

        PlayerEditorProvider.unloadAll();

        Assertions.assertNotSame(first, PlayerEditorProvider.getEditor(firstPlayer));
        Assertions.assertNotSame(second, PlayerEditorProvider.getEditor(secondPlayer));
    }

    private static Player player(UUID uuid) {
        Player player = Mockito.mock(Player.class);
        Mockito.when(player.getUniqueId()).thenReturn(uuid);
        return player;
    }
}
