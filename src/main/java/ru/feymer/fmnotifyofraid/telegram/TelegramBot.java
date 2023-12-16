package ru.feymer.fmnotifyofraid.telegram;

import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.RandomStringUtils;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.feymer.fmnotifyofraid.utils.DataConfig;
import ru.feymer.fmnotifyofraid.utils.Utils;
import ru.feymer.fmnotifyofraid.vk.VKBot;
import ru.feymer.fmnotifyofraid.vk.VKUtils;

import java.util.HashMap;
import java.util.UUID;

public class TelegramBot extends TelegramLongPollingBot {

    private final HashMap<Long, String> codes = new HashMap<>();
    private final HashMap<Long, String> playerNames = new HashMap<>();

    private PlayerPointsAPI ppAPI;
    private static Economy econ = null;

    @Override
    public void onUpdateReceived(Update update) {
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
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player != null) {
                            if (Utils.getBoolean("currency.enable")) {
                                if (Utils.getString("currency.type").equalsIgnoreCase("PLAYERPOINTS")) {
                                    this.ppAPI = PlayerPoints.getInstance().getAPI();
                                    if (ppAPI.look(player.getUniqueId()) >= Utils.getInt("currency.count")) {
                                        DataConfig dataConfig = new DataConfig("");
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
                                        this.sendMessage(chatId, Utils.getString("telegram.messages.not-playerpoints"));
                                    }
                                }
                                else if (Utils.getString("currency.type").equalsIgnoreCase("VAULT")) {
                                    setupEconomy();
                                    if (econ.has(player.getName(), Utils.getInt("currency.count"))) {
                                        DataConfig dataConfig = new DataConfig("");
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
                                        this.sendMessage(chatId, Utils.getString("telegram.messages.not-vault"));
                                    }
                                }
                            } else {
                                DataConfig dataConfig = new DataConfig("");
                                if (dataConfig.contains(player.getName() + ".telegram")) {
                                    this.sendMessage(chatId, Utils.getString("telegram.messages.already-tied-up"));
                                } else {
                                    String randomNumber = RandomStringUtils.randomNumeric(5);
                                    this.sendMessage(chatId, Utils.getString("telegram.messages.telegram-tied-up"));
                                    Utils.sendMessage(player, Utils.getString("messages.game-tied-up").replace("%code%", randomNumber));
                                    codes.put(chatId, randomNumber);
                                    playerNames.put(chatId, player.getName());
                                }
                            }
                        } else {
                            this.sendMessage(chatId, Utils.getString("telegram.messages.player-null"));
                        }
                    } else if (args[1].equalsIgnoreCase("отвязать")) {
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player != null) {
                            DataConfig dataConfig = new DataConfig("");
                            if (dataConfig.contains(player.getName() + ".telegram")) {
                                TelegramUtils telegramUtils = new TelegramUtils();
                                if (telegramUtils.getChatId(player) == chatId) {
                                    dataConfig.set(player.getName() + ".telegram", null);
                                    DataConfig.saveData();
                                    this.sendMessage(chatId, Utils.getString("vk.messages.unlink-account"));
                                } else {
                                    this.sendMessage(chatId, Utils.getString("telegram.messages.account-is-not-your"));
                                }
                            } else {
                                this.sendMessage(chatId, Utils.getString("telegram.messages.not-tied-up"));
                            }
                        } else {
                            this.sendMessage(chatId, Utils.getString("telegram.messages.player-null"));
                        }
                    } else if (args[1].equalsIgnoreCase("код")) {
                        String code = args[2];

                        if (codes.containsKey(chatId)) {
                            if (code.equals(codes.get(chatId))) {
                                if (Utils.getBoolean("currency.enable")) {
                                    if (Utils.getString("currency.type").equalsIgnoreCase("PLAYERPOINTS")) {
                                        this.sendMessage(chatId, Utils.getString("telegram.messages.success-tied-up"));
                                        DataConfig dataConfig = new DataConfig("");
                                        dataConfig.set(playerNames.get(chatId) + ".telegram", chatId);
                                        DataConfig.saveData();
                                        String playerName = playerNames.get(chatId);
                                        Player player = Bukkit.getPlayer(playerName);
                                        ppAPI.take(player.getUniqueId(), Utils.getInt("currency.count"));
                                        playerNames.remove(chatId);
                                        codes.remove(chatId);
                                    } else if (Utils.getString("currency.type").equalsIgnoreCase("VAULT")) {
                                        this.sendMessage(chatId, Utils.getString("telegram.messages.success-tied-up"));
                                        DataConfig dataConfig = new DataConfig("");
                                        dataConfig.set(playerNames.get(chatId) + ".telegram", chatId);
                                        DataConfig.saveData();
                                        econ.withdrawPlayer(playerNames.get(chatId), Utils.getInt("currency.count"));
                                        playerNames.remove(chatId);
                                        codes.remove(chatId);
                                    }
                                } else {
                                    this.sendMessage(chatId, Utils.getString("telegram.messages.success-tied-up"));
                                    DataConfig dataConfig = new DataConfig("");
                                    dataConfig.set(playerNames.get(chatId) + ".telegram", chatId);
                                    DataConfig.saveData();
                                    playerNames.remove(chatId);
                                    codes.remove(chatId);
                                }
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

    private boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
