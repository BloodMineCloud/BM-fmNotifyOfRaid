package ru.feymer.fmnotifyofraid.vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.RandomStringUtils;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.feymer.fmnotifyofraid.telegram.TelegramUtils;
import ru.feymer.fmnotifyofraid.utils.DataConfig;
import ru.feymer.fmnotifyofraid.utils.Utils;

import java.util.HashMap;
import java.util.List;

public class VKBot {

    private final HashMap<Integer, String> codes = new HashMap<>();
    private final HashMap<Integer, String> playerNames = new HashMap<>();

    private PlayerPointsAPI ppAPI;
    private static Economy econ = null;

    public void bot() throws ClientException, ApiException, InterruptedException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        GroupActor actor = new GroupActor(Utils.getInt("vk.settings.groupId"), Utils.getString("vk.settings.accessToken"));
        Integer ts = vk.messages().getLongPollServer(actor).execute().getTs();
        while (true) {
            MessagesGetLongPollHistoryQuery historyQuery =  vk.messages().getLongPollHistory(actor).ts(ts);
            List<Message> messages = historyQuery.execute().getMessages().getItems();
            if (!messages.isEmpty()){
                messages.forEach(message -> {
                    try {
                        String msg = message.getText();
                        int userId = message.getFromId();
                        String[] args = msg.split(" ");
                        if (msg.startsWith("/start") || msg.startsWith("Начать")) {
                            return;
                        }
                        if (userId <= 0) {
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
                                                    if (dataConfig.contains(player.getName() + ".vk")) {
                                                        VKUtils.sendMessage(userId, Utils.getString("vk.messages.already-tied-up"));
                                                    } else {
                                                        String randomNumber = RandomStringUtils.randomNumeric(5);
                                                        VKUtils.sendMessage(userId, Utils.getString("vk.messages.vk-tied-up"));
                                                        Utils.sendMessage(player, Utils.getString("messages.game-tied-up").replace("%code%", randomNumber));
                                                        codes.put(userId, randomNumber);
                                                        playerNames.put(userId, player.getName());
                                                    }
                                                } else {
                                                    VKUtils.sendMessage(userId, Utils.getString("vk.messages.not-playerpoints"));
                                                }
                                            } else if (Utils.getString("currency.type").equalsIgnoreCase("VAULT")) {
                                                setupEconomy();
                                                if (econ.has(player.getName(), Utils.getInt("currency.count"))) {
                                                    DataConfig dataConfig = new DataConfig("");
                                                    if (dataConfig.contains(player.getName() + ".vk")) {
                                                        VKUtils.sendMessage(userId, Utils.getString("vk.messages.already-tied-up"));
                                                    } else {
                                                        String randomNumber = RandomStringUtils.randomNumeric(5);
                                                        VKUtils.sendMessage(userId, Utils.getString("vk.messages.vk-tied-up"));
                                                        Utils.sendMessage(player, Utils.getString("messages.game-tied-up").replace("%code%", randomNumber));
                                                        codes.put(userId, randomNumber);
                                                        playerNames.put(userId, player.getName());
                                                    }
                                                } else {
                                                    VKUtils.sendMessage(userId, Utils.getString("vk.messages.not-vault"));
                                                }
                                            } else {
                                                DataConfig dataConfig = new DataConfig("");
                                                if (dataConfig.contains(player.getName() + ".telegram")) {
                                                    VKUtils.sendMessage(userId, Utils.getString("telegram.messages.already-tied-up"));
                                                } else {
                                                    String randomNumber = RandomStringUtils.randomNumeric(5);
                                                    VKUtils.sendMessage(userId, Utils.getString("telegram.messages.telegram-tied-up"));
                                                    Utils.sendMessage(player, Utils.getString("messages.game-tied-up").replace("%code%", randomNumber));
                                                    codes.put(userId, randomNumber);
                                                    playerNames.put(userId, player.getName());
                                                }
                                            }
                                        } else {
                                            DataConfig dataConfig = new DataConfig("");
                                            if (dataConfig.contains(player.getName() + ".vk")) {
                                                VKUtils.sendMessage(userId, Utils.getString("vk.messages.already-tied-up"));
                                            } else {
                                                String randomNumber = RandomStringUtils.randomNumeric(5);
                                                VKUtils.sendMessage(userId, Utils.getString("vk.messages.vk-tied-up"));
                                                Utils.sendMessage(player, Utils.getString("messages.game-tied-up").replace("%code%", randomNumber));
                                                codes.put(userId, randomNumber);
                                                playerNames.put(userId, player.getName());
                                            }
                                        }
                                    } else {
                                        VKUtils.sendMessage(userId, Utils.getString("vk.messages.player-null"));
                                    }
                                } else if (args[1].equalsIgnoreCase("отвязать")) {
                                    Player player = Bukkit.getPlayer(args[2]);
                                    if (player != null) {
                                        DataConfig dataConfig = new DataConfig("");
                                        if (dataConfig.contains(player.getName() + ".vk")) {
                                            if (VKUtils.getUserId(player) == userId) {
                                                dataConfig.set(player.getName() + ".vk", null);
                                                DataConfig.saveData();
                                                VKUtils.sendMessage(userId, Utils.getString("vk.messages.unlink-account"));
                                            } else {
                                                VKUtils.sendMessage(userId, Utils.getString("vk.messages.account-is-not-your"));
                                            }
                                        } else {
                                            VKUtils.sendMessage(userId, Utils.getString("vk.messages.not-tied-up"));
                                        }
                                    } else {
                                        VKUtils.sendMessage(userId, Utils.getString("vk.messages.player-null"));
                                    }
                                } else if (args[1].equalsIgnoreCase("код")) {
                                    String code = args[2];

                                    if (codes.containsKey(userId)) {
                                        if (code.equals(codes.get(userId))) {
                                            if (Utils.getBoolean("currency.enable")) {
                                                if (Utils.getString("currency.type").equalsIgnoreCase("PLAYERPOINTS")) {
                                                    VKUtils.sendMessage(userId, Utils.getString("vk.messages.success-tied-up"));
                                                    DataConfig dataConfig = new DataConfig("");
                                                    dataConfig.set(playerNames.get(userId) + ".vk", userId);
                                                    DataConfig.saveData();
                                                    String playerName = playerNames.get(userId);
                                                    Player player = Bukkit.getPlayer(playerName);
                                                    ppAPI.take(player.getUniqueId(), Utils.getInt("currency.count"));
                                                    playerNames.remove(userId);
                                                    codes.remove(userId);
                                                }
                                                else if (Utils.getString("currency.type").equalsIgnoreCase("VAULT")) {
                                                    VKUtils.sendMessage(userId, Utils.getString("vk.messages.success-tied-up"));
                                                    DataConfig dataConfig = new DataConfig("");
                                                    dataConfig.set(playerNames.get(userId) + ".vk", userId);
                                                    DataConfig.saveData();
                                                    econ.withdrawPlayer(playerNames.get(userId), Utils.getInt("currency.count"));
                                                    playerNames.remove(userId);
                                                    codes.remove(userId);
                                                }
                                            } else {
                                                VKUtils.sendMessage(userId, Utils.getString("vk.messages.success-tied-up"));
                                                DataConfig dataConfig = new DataConfig("");
                                                dataConfig.set(playerNames.get(userId) + ".vk", userId);
                                                DataConfig.saveData();
                                                playerNames.remove(userId);
                                                codes.remove(userId);
                                            }
                                        } else {
                                            VKUtils.sendMessage(userId, Utils.getString("vk.messages.wrong-code"));
                                        }
                                    } else {
                                        VKUtils.sendMessage(userId, Utils.getString("vk.messages.request-not-found"));
                                    }
                                } else {
                                    VKUtils.sendMessage(userId, Utils.getString("vk.messages.command-not-found"));
                                }
                            } else {
                                VKUtils.sendMessage(userId, Utils.getString("vk.messages.command-not-found"));
                            }
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (String stringList : Utils.getStringList("vk.messages.help")) {
                                stringBuilder.append(stringList).append("\n");
                            }
                            String messageHelp = stringBuilder.toString();
                            VKUtils.sendMessage(userId, messageHelp);
                        }
                    }
                    catch (ApiException | ClientException e) {
                        e.printStackTrace();
                    }
                });
            }
            ts = vk.messages().getLongPollServer(actor).execute().getTs();
            Thread.sleep(500);
        }
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
