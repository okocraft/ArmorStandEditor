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

import static net.okocraft.armorstandeditor.testsupport.TestIds.OTHER_PLAYER_UUID;
import static net.okocraft.armorstandeditor.testsupport.TestIds.PLAYER_UUID;

class PlayerEditorProviderTest {

    @AfterEach
    void clearEditors() {
        PlayerEditorProvider.unloadAll();
    }

    @Test
    void testSamePlayerReturnsSameEditor() {
        Player player = player(PLAYER_UUID);

        PlayerEditor first = PlayerEditorProvider.getEditor(player);
        PlayerEditor second = PlayerEditorProvider.getEditor(player);

        Assertions.assertSame(first, second);
        Assertions.assertSame(player, first.getPlayer());
    }

    @Test
    void testSameUuidKeepsOriginalPlayerInstance() {
        Player firstPlayer = player(PLAYER_UUID);
        Player secondPlayer = player(PLAYER_UUID);

        PlayerEditor first = PlayerEditorProvider.getEditor(firstPlayer);
        PlayerEditor second = PlayerEditorProvider.getEditor(secondPlayer);

        Assertions.assertSame(first, second);
        Assertions.assertSame(firstPlayer, second.getPlayer());
    }

    @Test
    void testHumanEntityPlayerUsesPlayerPath() {
        Player player = player(PLAYER_UUID);

        PlayerEditor fromPlayer = PlayerEditorProvider.getEditor(player);
        PlayerEditor fromHumanEntity = PlayerEditorProvider.getEditor((HumanEntity) player);

        Assertions.assertSame(fromPlayer, fromHumanEntity);
    }

    @Test
    void testNonPlayerHumanEntityResolvesPlayerByUuid() {
        HumanEntity humanEntity = Mockito.mock(HumanEntity.class);
        Player player = player(PLAYER_UUID);
        Mockito.when(humanEntity.getUniqueId()).thenReturn(PLAYER_UUID);

        try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer(PLAYER_UUID)).thenReturn(player);

            PlayerEditor editor = PlayerEditorProvider.getEditor(humanEntity);

            Assertions.assertSame(player, editor.getPlayer());
            bukkit.verify(() -> Bukkit.getPlayer(PLAYER_UUID));
        }
    }

    @Test
    void testUnloadRemovesPlayerEditor() {
        Player player = player(PLAYER_UUID);
        PlayerEditor first = PlayerEditorProvider.getEditor(player);

        PlayerEditorProvider.unload(player);
        PlayerEditor second = PlayerEditorProvider.getEditor(player);

        Assertions.assertNotSame(first, second);
    }

    @Test
    void testUnloadAllRemovesAllEditors() {
        Player firstPlayer = player(PLAYER_UUID);
        Player secondPlayer = player(OTHER_PLAYER_UUID);
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
