package ru.feymer.fmnotifyofraid.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Utils {

    public static FileConfiguration getConfig() {
        return Config.getConfig();
    }

    public static String getString(String path) {
        return Hex.color(getConfig().getString(path));
    }

    public static List<String> getStringList(String path) {
        return Hex.color(getConfig().getStringList(path));
    }

    public static int getInt(String path) {
        return getConfig().getInt(path);
    }

    public static boolean getBoolean(String path) {
        return getConfig().getBoolean(path);
    }

    public void set(String path, Object value) {
        getConfig().set(path, value);
    }

    public static void sendMessage(CommandSender player, String message) {
        player.sendMessage(Hex.color(message));
    }
}
