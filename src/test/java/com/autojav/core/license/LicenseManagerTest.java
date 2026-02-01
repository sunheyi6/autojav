package com.autojav.core.license;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LicenseManagerTest {

    private LicenseManager licenseManager;

    @BeforeEach
    void setUp() {
        licenseManager = new LicenseManager();
    }

    @Test
    void testLoadLicense() {
        licenseManager.loadLicense();
        assertNotNull(licenseManager.getCurrentLicense());
        assertEquals(VersionType.FREE, licenseManager.getCurrentLicense().getVersionType());
    }

    @Test
    void testValidateLicense() {
        licenseManager.loadLicense();
        assertTrue(licenseManager.validateLicense());
    }

    @Test
    void testHasPermission() {
        licenseManager.loadLicense();
        // 免费版应该有基础功能权限
        assertTrue(licenseManager.hasPermission("code.audit"));
        assertTrue(licenseManager.hasPermission("doc.generate"));
        // 免费版不应该有高级功能权限
        assertFalse(licenseManager.hasPermission("code.fix"));
        assertFalse(licenseManager.hasPermission("ai.audit"));
    }

    @Test
    void testCheckAndApplyRestriction() {
        licenseManager.loadLicense();
        // 免费版应该通过基础功能检查
        assertTrue(licenseManager.checkAndApplyRestriction("code.audit"));
        // 免费版不应该通过高级功能检查
        assertFalse(licenseManager.checkAndApplyRestriction("code.fix"));
    }

    @Test
    void testActivateLicense() {
        boolean activated = licenseManager.activateLicense("test-license-key");
        assertTrue(activated);
        assertEquals(VersionType.TEAM, licenseManager.getCurrentLicense().getVersionType());
    }
}
