package net.okocraft.armorstandeditor.testsupport;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Initializes the lightweight Paper test environment before test classes run.
 */
public final class TestServerExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        TestServer.setUp();
    }
}
