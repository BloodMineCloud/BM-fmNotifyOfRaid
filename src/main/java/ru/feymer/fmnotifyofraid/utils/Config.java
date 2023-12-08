package ru.feymer.fmnotifyofraid.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Config {

    public static FileConfiguration config;
    public static File file;

    public static void loadYamlFile(Plugin plugin) {

        file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {

            plugin.getDataFolder().mkdirs();
            plugin.saveResource("config.yml", true);

        }

        config = YamlConfiguration.loadConfiguration(file);

    }

    public static void saveConfig() {

        try {
            getConfig().save(file);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static void reloadConfig(Plugin plugin) {

        if (!file.exists()) {

            plugin.getDataFolder().mkdirs();
            plugin.saveResource("config.yml", true);

        }
        try {

            config.load(file);

        } catch (IOException | InvalidConfigurationException e) {

            Bukkit.getConsoleSender().sendMessage("Не удалось перезагрузить конфигурацию!");

        }
    }
}
