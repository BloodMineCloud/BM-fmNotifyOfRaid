package ru.feymer.fmnotifyofraid.vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.feymer.fmnotifyofraid.utils.DataConfig;
import ru.feymer.fmnotifyofraid.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class VKBot {

    private final HashMap<Integer, String> codes = new HashMap<>();
    private final HashMap<Integer, String> playerNames = new HashMap<>();

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
                        if (msg.startsWith("/start")) {
                            return;
                        }
                        if (userId <= 0) {
                            return;
                        }
                        if (args.length == 3) {
                            if (msg.startsWith("!анти-рейд")) {
                                if (args[1].equalsIgnoreCase("привязать")) {
                                    Player player = Bukkit.getPlayer(args[2]);
                                    DataConfig dataConfig = new DataConfig("");

                                    if (player != null) {
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
                                        VKUtils.sendMessage(userId, Utils.getString("vk.messages.player-null"));
                                    }
                                } else if (args[1].equalsIgnoreCase("код")) {
                                    String code = args[2];

                                    if (codes.containsKey(userId)) {
                                        if (code.equals(codes.get(userId))) {
                                            VKUtils.sendMessage(userId, Utils.getString("vk.messages.success-tied-up"));
                                            DataConfig dataConfig = new DataConfig("");
                                            dataConfig.set(playerNames.get(userId) + ".vk", userId);
                                            DataConfig.saveData();
                                            playerNames.remove(userId);
                                            codes.remove(userId);
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
 }
