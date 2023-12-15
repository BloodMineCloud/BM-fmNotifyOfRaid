package ru.feymer.fmnotifyofraid.telegram;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.feymer.fmnotifyofraid.utils.DataConfig;

public class TelegramUtils extends TelegramLongPollingBot {

    public long getChatId(Player player) {
        DataConfig dataConfig = new DataConfig(player.getName());
        if (dataConfig.contains("telegram")) {
            return dataConfig.getLong("telegram");
        }
        return 0;
    }

    public long getChatId(OfflinePlayer player) {
        DataConfig dataConfig = new DataConfig(player.getName());
        if (dataConfig.contains("telegram")) {
            return dataConfig.getLong("telegram");
        }
        return 0;
    }

    @Override
    public void onUpdateReceived(Update update) {

    }

    @Override
    public String getBotUsername() {
        return null;
    }
}
