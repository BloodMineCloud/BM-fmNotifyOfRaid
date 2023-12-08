package ru.feymer.fmnotifyofraid;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.feymer.fmnotifyofraid.commands.FmNotifyOfRaidCommand;
import ru.feymer.fmnotifyofraid.listeners.Listeners;
import ru.feymer.fmnotifyofraid.telegram.TelegramBot;
import ru.feymer.fmnotifyofraid.utils.Config;
import ru.feymer.fmnotifyofraid.utils.DataConfig;
import ru.feymer.fmnotifyofraid.utils.Hex;
import ru.feymer.fmnotifyofraid.utils.Updater;

public final class FmNotifyOfRaid extends JavaPlugin {

    public static FmNotifyOfRaid instance;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getConsoleSender().sendMessage(Hex.color(""));
        Bukkit.getConsoleSender().sendMessage(Hex.color("&4» &fПлагин &4" + getPlugin(FmNotifyOfRaid.class).getName() + " &fвключился&f!"));
        Bukkit.getConsoleSender().sendMessage(Hex.color("&4» &fВерсия: &4v" + getPlugin(FmNotifyOfRaid.class).getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(Hex.color(""));

        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
        Config.loadYamlFile(this);
        DataConfig.loadYamlFile(this);
        this.getCommand("fmnotifyofraid").setExecutor(new FmNotifyOfRaidCommand());
        Updater updater = new Updater(this);
        updater.start();

        TelegramBotsApi api;

        try {
            api = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        try {
            api.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(Hex.color(""));
        Bukkit.getConsoleSender().sendMessage(Hex.color("&4» &fПлагин &4" + getPlugin(FmNotifyOfRaid.class).getName() + " &fвыключился&f!"));
        Bukkit.getConsoleSender().sendMessage(Hex.color("&4» &fВерсия: &4v" + getPlugin(FmNotifyOfRaid.class).getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage(Hex.color(""));
    }

    public static FmNotifyOfRaid getInstance() {
        return instance;
    }
}
