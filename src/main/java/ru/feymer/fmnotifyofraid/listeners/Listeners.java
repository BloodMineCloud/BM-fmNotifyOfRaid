package ru.feymer.fmnotifyofraid.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.feymer.fmnotifyofraid.telegram.TelegramUtils;
import ru.feymer.fmnotifyofraid.utils.Utils;

import java.util.UUID;

public class Listeners extends TelegramLongPollingBot implements Listener {

    WorldGuard worldGuard = WorldGuard.getInstance();

    @EventHandler
    public void onBreakBlock(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) event.getEntity();
            World world = tnt.getWorld();
            int x = tnt.getLocation().getBlockX();
            int y = tnt.getLocation().getBlockY();
            int z = tnt.getLocation().getBlockZ();

            ApplicableRegionSet region = worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getApplicableRegions(BlockVector3.at(x, y,z));
            for (ProtectedRegion rg : region.getRegions()) {
                DefaultDomain owners = rg.getOwners();
                for (UUID uuid : owners.getUniqueIds()) {
                    Player ownerName = Bukkit.getPlayer(uuid);

                    if (ownerName != null) {
                        if (Utils.getBoolean("settings.message-in-game-of-raid")) {
                            Utils.sendMessage(ownerName, Utils.getString("messages.notify-of-raid-in-game").replace("%region%", rg.getId()).replace("%x%", Integer.toString(x)).replace("%y%", Integer.toString(y)).replace("%z%", Integer.toString(z)));
                        }
                        if (Utils.getBoolean("settings.message-in-telegram-of-raid")) {
                            TelegramUtils telegramUtils = new TelegramUtils();
                            long chatId = telegramUtils.getChatId(ownerName);
                            if (chatId != 0) {
                                this.sendMessage(chatId, Utils.getString("telegram.messages.notify-of-raid-in-telegram").replace("%region%", rg.getId()).replace("%x%", Integer.toString(x)).replace("%y%", Integer.toString(y)).replace("%z%", Integer.toString(z)));
                            }
                        }
                    } else if (Utils.getBoolean("settings.message-in-telegram-of-raid")) {
                        OfflinePlayer ownerNameOffline = (OfflinePlayer) Bukkit.getOfflinePlayer(uuid);
                        TelegramUtils telegramUtils = new TelegramUtils();
                        long chatId = telegramUtils.getChatId(ownerNameOffline);
                        if (chatId != 0) {
                            this.sendMessage(chatId, Utils.getString("telegram.messages.notify-of-raid-in-telegram").replace("%region%", rg.getId()).replace("%x%", Integer.toString(x)).replace("%y%", Integer.toString(y)).replace("%z%", Integer.toString(z)));
                        }
                    }
                }
            }
        } else if (event.getEntity() instanceof WitherSkull) {
            WitherSkull wither = (WitherSkull) event.getEntity();
            World world = wither.getWorld();
            int x = wither.getLocation().getBlockX();
            int y = wither.getLocation().getBlockY();
            int z = wither.getLocation().getBlockZ();

            ApplicableRegionSet region = worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getApplicableRegions(BlockVector3.at(x, y,z));
            for (ProtectedRegion rg : region.getRegions()) {
                DefaultDomain owners = rg.getOwners();
                for (UUID uuid : owners.getUniqueIds()) {
                    Player ownerName = Bukkit.getPlayer(uuid);

                    if (ownerName != null) {
                        if (Utils.getBoolean("settings.message-in-game-of-raid")) {
                            Utils.sendMessage(ownerName, Utils.getString("messages.notify-of-raid-in-game").replace("%region%", rg.getId()).replace("%x%", Integer.toString(x)).replace("%y%", Integer.toString(y)).replace("%z%", Integer.toString(z)));
                        }
                        if (Utils.getBoolean("settings.message-in-telegram-of-raid")) {
                            TelegramUtils telegramUtils = new TelegramUtils();
                            long chatId = telegramUtils.getChatId(ownerName);
                            if (chatId != 0) {
                                this.sendMessage(chatId, Utils.getString("telegram.messages.notify-of-raid-in-telegram").replace("%region%", rg.getId()).replace("%x%", Integer.toString(x)).replace("%y%", Integer.toString(y)).replace("%z%", Integer.toString(z)));
                            }
                        }
                    } else if (Utils.getBoolean("settings.message-in-telegram-of-raid")) {
                        OfflinePlayer ownerNameOffline = (OfflinePlayer) Bukkit.getOfflinePlayer(uuid);
                        TelegramUtils telegramUtils = new TelegramUtils();
                        long chatId = telegramUtils.getChatId(ownerNameOffline);
                        if (chatId != 0) {
                            this.sendMessage(chatId, Utils.getString("telegram.messages.notify-of-raid-in-telegram").replace("%region%", rg.getId()).replace("%x%", Integer.toString(x)).replace("%y%", Integer.toString(y)).replace("%z%", Integer.toString(z)));
                        }
                    }
                }
            }
        } else if (event.getEntity() instanceof Creeper) {
            Creeper creeper = (Creeper) event.getEntity();
            World world = creeper.getWorld();
            int x = creeper.getLocation().getBlockX();
            int y = creeper.getLocation().getBlockY();
            int z = creeper.getLocation().getBlockZ();

            ApplicableRegionSet region = worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getApplicableRegions(BlockVector3.at(x, y,z));
            for (ProtectedRegion rg : region.getRegions()) {
                DefaultDomain owners = rg.getOwners();
                for (UUID uuid : owners.getUniqueIds()) {
                    Player ownerName = Bukkit.getPlayer(uuid);

                    if (ownerName != null) {
                        if (Utils.getBoolean("settings.message-in-game-of-raid")) {
                            Utils.sendMessage(ownerName, Utils.getString("messages.notify-of-raid-in-game").replace("%region%", rg.getId()).replace("%x%", Integer.toString(x)).replace("%y%", Integer.toString(y)).replace("%z%", Integer.toString(z)));
                        }
                        if (Utils.getBoolean("settings.message-in-telegram-of-raid")) {
                            TelegramUtils telegramUtils = new TelegramUtils();
                            long chatId = telegramUtils.getChatId(ownerName);
                            if (chatId != 0) {
                                this.sendMessage(chatId, Utils.getString("telegram.messages.notify-of-raid-in-telegram").replace("%region%", rg.getId()).replace("%x%", Integer.toString(x)).replace("%y%", Integer.toString(y)).replace("%z%", Integer.toString(z)));
                            }
                        }
                    } else if (Utils.getBoolean("settings.message-in-telegram-of-raid")) {
                        OfflinePlayer ownerNameOffline = (OfflinePlayer) Bukkit.getOfflinePlayer(uuid);
                        TelegramUtils telegramUtils = new TelegramUtils();
                        long chatId = telegramUtils.getChatId(ownerNameOffline);
                        if (chatId != 0) {
                            this.sendMessage(chatId, Utils.getString("telegram.messages.notify-of-raid-in-telegram").replace("%region%", rg.getId()).replace("%x%", Integer.toString(x)).replace("%y%", Integer.toString(y)).replace("%z%", Integer.toString(z)));
                        }
                    }
                }
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
    public void onUpdateReceived(Update update) {

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
