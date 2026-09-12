package net.okocraft.armorstandeditor.testsupport;

import java.util.UUID;

public final class TestIds {

    public static final UUID PLAYER_UUID = new UUID(0, 1);
    public static final UUID OTHER_PLAYER_UUID = new UUID(0, 2);
    public static final UUID ARMOR_STAND_UUID = new UUID(0, 3);
    public static final UUID OTHER_ARMOR_STAND_UUID = new UUID(0, 4);

    private TestIds() {
        throw new UnsupportedOperationException();
    }
}
