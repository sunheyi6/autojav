package com.autojav.core;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class ConfigManager {

    private static final String CONFIG_FILE_NAME = ".autojav.properties";
    private static final String GLOBAL_CONFIG_DIR = System.getProperty("user.home") + File.separator + ".autojav";
    private static final String LOCAL_CONFIG_DIR = ".";

    private Properties properties;

    public ConfigManager() {
        properties = new Properties();
        loadConfig(false);
    }

    public ConfigManager(boolean global) {
        properties = new Properties();
        loadConfig(global);
    }

    private void loadConfig(boolean global) {
        String configDir = global ? GLOBAL_CONFIG_DIR : LOCAL_CONFIG_DIR;
        File configFile = new File(configDir, CONFIG_FILE_NAME);

        try {
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    properties.load(fis);
                    log.debug("配置文件加载成功: {}", configFile.getAbsolutePath());
                }
            } else {
                log.debug("配置文件不存在: {}", configFile.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("加载配置文件失败", e);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public void save(boolean global) throws IOException {
        String configDir = global ? GLOBAL_CONFIG_DIR : LOCAL_CONFIG_DIR;
        File configDirFile = new File(configDir);
        if (!configDirFile.exists()) {
            configDirFile.mkdirs();
        }

        File configFile = new File(configDir, CONFIG_FILE_NAME);
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "AutoJava CLI Configuration");
            log.debug("配置文件保存成功: {}", configFile.getAbsolutePath());
        }
    }

    public void list() {
        properties.forEach((k, v) -> System.out.println(k + " = " + v));
    }

    public static void main(String[] args) {
        ConfigManager configManager = new ConfigManager();
        configManager.list();
    }
}
