package ru.feymer.fmnotifyofraid.telegram;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.feymer.fmnotifyofraid.utils.DataConfig;
import ru.feymer.fmnotifyofraid.utils.Utils;

import java.util.HashMap;

public class TelegramBot extends TelegramLongPollingBot {

    private final HashMap<Long, String> codes = new HashMap<>();
    private final HashMap<Long, String> playerNames = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        Bukkit.getConsoleSender().sendMessage("0");
        Message message = update.getMessage();
        if (message != null) {
            long chatId = message.getChatId();
            String msg = message.getText();
            String[] args = msg.split(" ");
            if (msg.startsWith("/start")) {
                return;
            }
            if (args.length == 3) {
                if (msg.startsWith("!анти-рейд")) {
                    if (args[1].equalsIgnoreCase("привязать")) {
                        Bukkit.getConsoleSender().sendMessage("1");
                        Player player = Bukkit.getPlayer(args[2]);
                        DataConfig dataConfig = new DataConfig("");

                        if (player != null) {
                            if (dataConfig.contains(player.getName() + ".telegram")) {
                                this.sendMessage(chatId, Utils.getString("telegram.messages.already-tied-up"));
                            } else {
                                String randomNumber = RandomStringUtils.randomNumeric(5);
                                this.sendMessage(chatId, Utils.getString("telegram.messages.telegram-tied-up"));
                                Utils.sendMessage(player, Utils.getString("messages.game-tied-up").replace("%code%", randomNumber));
                                codes.put(chatId, randomNumber);
                                playerNames.put(chatId, player.getName());
                            }
                        } else {
                            this.sendMessage(chatId, Utils.getString("telegram.messages.player-null"));
                        }
                    } else if (args[1].equalsIgnoreCase("код")) {
                        String code = args[2];

                        if (codes.containsKey(chatId)) {
                            if (code.equals(codes.get(chatId))) {
                                this.sendMessage(chatId, Utils.getString("telegram.messages.success-tied-up"));
                                DataConfig dataConfig = new DataConfig("");
                                dataConfig.set(playerNames.get(chatId) + ".telegram", chatId);
                                DataConfig.saveData();
                                playerNames.remove(chatId);
                                codes.remove(chatId);
                            } else {
                                this.sendMessage(chatId, Utils.getString("telegram.messages.wrong-code"));
                            }
                        } else {
                            this.sendMessage(chatId, Utils.getString("telegram.messages.request-not-found"));
                        }
                    } else {
                        this.sendMessage(chatId, Utils.getString("telegram.messages.command-not-found"));
                    }
                } else {
                    this.sendMessage(chatId, Utils.getString("telegram.messages.command-not-found"));
                }
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (String stringList : Utils.getStringList("telegram.messages.help")) {
                    stringBuilder.append(stringList).append("\n");
                }
                String messageHelp = stringBuilder.toString();
                this.sendMessage(chatId, messageHelp);
            }
        }
    }


    public void sendMessage(long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(message).build();
        try {
            this.execute(sendMessage);
        } catch (TelegramApiException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public String getBotUsername() {
        return Utils.getString("telegram.settings.bot-username");
    }

    @Override
    public String getBotToken() {
        return Utils.getString("telegram.settings.bot-token");
    }
}
