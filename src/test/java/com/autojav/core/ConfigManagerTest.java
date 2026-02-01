package com.autojav.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {

    private ConfigManager configManager;

    @BeforeEach
    void setUp() {
        configManager = new ConfigManager(false); // 使用本地配置
    }

    @Test
    void testGetNonExistentKey() {
        String value = configManager.get("non.existent.key");
        assertNull(value);
    }

    @Test
    void testGetWithDefault() {
        String value = configManager.get("non.existent.key", "default");
        assertEquals("default", value);
    }

    @Test
    void testSetAndGet() throws IOException {
        String key = "test.key";
        String value = "test.value";
        configManager.set(key, value);
        configManager.save(false);

        // 创建新实例加载配置
        ConfigManager newConfigManager = new ConfigManager(false);
        String retrievedValue = newConfigManager.get(key);
        assertEquals(value, retrievedValue);
    }

    @Test
    void testList() {
        // 测试列表功能，确保不会抛出异常
        configManager.list();
        // 这里不做断言，只确保方法能正常执行
    }
}
