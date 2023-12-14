package ru.feymer.fmnotifyofraid;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.feymer.fmnotifyofraid.commands.FmNotifyOfRaidCommand;
import ru.feymer.fmnotifyofraid.listeners.Listeners;
import ru.feymer.fmnotifyofraid.telegram.TelegramBot;
import ru.feymer.fmnotifyofraid.utils.*;
import ru.feymer.fmnotifyofraid.vk.VKBot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class FmNotifyOfRaid extends JavaPlugin {

    public static FmNotifyOfRaid instance;

    @Override
    public void onEnable() {
        Configurator.setLevel("com.vk.api.sdk.httpclient.HttpTransportClient", Level.OFF);
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

        if (Utils.getBoolean("telegram.settings.enable")) {
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

        ExecutorService ex = Executors.newSingleThreadExecutor();
        ex.execute(() -> {
            if (Utils.getBoolean("vk.settings.enable")) {
                VKBot vkBot = new VKBot();
                try {
                    vkBot.bot();
                } catch (ClientException | ApiException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

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
