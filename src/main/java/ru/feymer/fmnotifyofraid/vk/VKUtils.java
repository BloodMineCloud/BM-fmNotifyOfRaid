package ru.feymer.fmnotifyofraid.vk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.feymer.fmnotifyofraid.utils.DataConfig;
import ru.feymer.fmnotifyofraid.utils.Utils;

import java.util.Random;

public class VKUtils {

    public static void sendMessage(Integer userId, String message) throws ClientException, ApiException {
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        GroupActor actor = new GroupActor(Utils.getInt("vk.settings.groupId"), Utils.getString("vk.settings.accessToken"));
        Random random = new Random();
        vk.messages().send(actor).message(message).userId(userId).randomId(random.nextInt(10000)).execute();
    }

    public static int getUserId(Player player) {
        DataConfig dataConfig = new DataConfig(player.getName());
        if (dataConfig.contains("vk")) {
            return dataConfig.getInt("vk");
        }
        return 0;
    }

    public static int getUserId(OfflinePlayer player) {
        DataConfig dataConfig = new DataConfig(player.getName());
        if (dataConfig.contains("vk")) {
            return dataConfig.getInt("vk");
        }
        return 0;
    }
}
