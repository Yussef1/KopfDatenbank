package de.yussef.headdatabase.commands;

import de.yussef.headdatabase.HeadDatabase;
import de.yussef.headdatabase.fetcher.Fetcher;
import de.yussef.headdatabase.listener.HDBInventoryListener;
import de.yussef.headdatabase.utils.ConfigMessages;
import de.yussef.headdatabase.utils.ItemBuilder;
import de.yussef.headdatabase.utils.SkullBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;

public class HDBCommand implements CommandExecutor {

    private final int INVENTORY_SIZE = 9*6;

    public final String INVENTORY_NAME = "§cKopf Datenbank",
                        AUTHOR_NAME = "JavaEnvironment",
                        REQUESTER_NAME = "Lukassch",
                        SERVER_NAME = "KlickMich.net",
                        PLUGIN_VERSION = HeadDatabase.getHeadDatabase().getDescription().getVersion();

    private final Fetcher fetcher = new Fetcher();

    private void sendHelp(Player player) {
        player.sendMessage(ConfigMessages.HDB_PREFIX + "Meintest du §e/kdb§7?");
    }

    private void reloadHDB(Player player) {
        Bukkit.getConsoleSender().sendMessage("§e§l" + player.getName() + " §a§lLädt nun die JSON Dokumente neu...");
        player.sendMessage(ConfigMessages.HDB_PREFIX + "§aAktualisiere die JSON Dokumente...");

        String pluginName = HeadDatabase.getHeadDatabase().getName();
        File folder = new File("plugins/"+pluginName+"/jsonFiles");

        if(folder.exists()) {
            player.sendMessage(ConfigMessages.HDB_PREFIX + "§aSchritt 1: §7Lösche und Erstelle einen neuen Speicher Ordner...");
            folder.delete();
            folder.mkdir();
            player.sendMessage(ConfigMessages.HDB_PREFIX + "Abgeschlossen! der alte Ordner wurde gelöscht und wurde ersetzt.");

            player.sendMessage(ConfigMessages.HDB_PREFIX + "§aSchritt 2: §7Lade alle JSON Dokumente aus dem Internet Herunter...");

            try {
                fetcher.saveCategory(fetcher.alphabetUrl, HDBInventoryListener.categories[0]);
                fetcher.saveCategory(fetcher.animalsUrl, HDBInventoryListener.categories[1]);
                fetcher.saveCategory(fetcher.blocksUrl, HDBInventoryListener.categories[2]);
                fetcher.saveCategory(fetcher.decorationUrl, HDBInventoryListener.categories[3]);
                fetcher.saveCategory(fetcher.food_drinksUrl, HDBInventoryListener.categories[4]);
                fetcher.saveCategory(fetcher.humanoidsUrl, HDBInventoryListener.categories[5]);
                fetcher.saveCategory(fetcher.humansUrl, HDBInventoryListener.categories[6]);
                fetcher.saveCategory(fetcher.miscellaneousUrl, HDBInventoryListener.categories[7]);
                fetcher.saveCategory(fetcher.monstersUrl, HDBInventoryListener.categories[8]);
                fetcher.saveCategory(fetcher.plantsUrl, HDBInventoryListener.categories[9]);
                fetcher.saveCategory(null, HDBInventoryListener.categories[10]);

                player.sendMessage(ConfigMessages.HDB_PREFIX + "Abgeschlossen! Alle JSON Datein wurde erfolgreich heruntergeladen.");
            } catch (IOException ioException) {
                player.sendMessage(ConfigMessages.HDB_PREFIX + "§4ERROR: §7Ein Fehler ist aufgetreten beim Herunterladen der JSON Datein.");
            }

            player.sendMessage(ConfigMessages.HDB_PREFIX + "§aSchritt 3: §7Das Setup wurde erfolgreich Abgeschlossen. Die JSON Dokumente sind nun auf dem neusten Stand.");
        } else {
            player.sendMessage(ConfigMessages.HDB_PREFIX + "§4ERROR: Ein Fehler ist aufgetreten der Ordner ist nicht existent. Bitte Lade das Plugin neu.");
            player.sendMessage(ConfigMessages.HDB_PREFIX + "§4INFO: So ein Fehler hätte eigentlich nie auftreten dürfen. Sei dir sicher das du nicht am Speicher Ordner rum gespielt hast.");
        }
    }

    private void sendInfo(Player player) {
        player.sendMessage("§8§m--------------------§c§lInfo§8§m--------------------");
        player.sendMessage("");
        player.sendMessage(ConfigMessages.HDB_PREFIX + "Plugin Entwickler: §3" + AUTHOR_NAME);
        player.sendMessage(ConfigMessages.HDB_PREFIX + "Plugin Version: §3" + PLUGIN_VERSION);
        player.sendMessage(" ");
        player.sendMessage(ConfigMessages.HDB_PREFIX + "Dieses Plugin wurde im Auftrag gegeben von §3" + REQUESTER_NAME + "§7 und wurde für §3" + SERVER_NAME + " §7Programmiert.");
        player.sendMessage("");
        player.sendMessage("§8§m--------------------§c§lInfo§8§m--------------------");
    }

    public void openHDB(Player player) {
        final Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, INVENTORY_NAME);

        inventory.setItem(17, new ItemBuilder(" ", Material.BLACK_STAINED_GLASS_PANE, 1).build());
        inventory.setItem(18, new ItemBuilder(" ", Material.BLACK_STAINED_GLASS_PANE, 1).build());
        inventory.setItem(26, new ItemBuilder(" ", Material.BLACK_STAINED_GLASS_PANE, 1).build());
        inventory.setItem(27, new ItemBuilder(" ", Material.BLACK_STAINED_GLASS_PANE, 1).build());
        inventory.setItem(35, new ItemBuilder(" ", Material.BLACK_STAINED_GLASS_PANE, 1).build());
        inventory.setItem(36, new ItemBuilder(" ", Material.BLACK_STAINED_GLASS_PANE, 1).build());
        inventory.setItem(44, new ItemBuilder(" ", Material.BLACK_STAINED_GLASS_PANE, 1).build());

        for(int i = 0; i < 10; i++) {
            inventory.setItem(i, new ItemBuilder(" ", Material.BLACK_STAINED_GLASS_PANE, 1).build());
        }

        for(int i = 45; i < 54; i++) {
            inventory.setItem(i, new ItemBuilder(" ", Material.BLACK_STAINED_GLASS_PANE, 1).build());
        }

        inventory.setItem(42, new ItemBuilder("§3§lExtra Befehle", Material.BOOK, 1).setLores("§8⇾ §3/kdb reload §8- §7Aktualisiert die JSON Dokumente. §8⇽", "§8⇾ §3/kdb info §8- §7Zeigt Infos über das Plugin an §8⇽").build());
        inventory.setItem(43, new ItemBuilder("§e§lSuche Kopf", Material.OAK_SIGN, 1).setLore("§8⇾ §7Linksklicke §ehier§7, um einen bestimmten Kopf zu suchen").build());

        inventory.setItem(10, new SkullBuilder().createSkull("§c§lAlphabet", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNlYTYwZWE2MGMwNWQ3MDIwNTMxNzk0MzM3Nzc2MmI4ZGM1MmRjNjIzZmI3N2ZkY2Y2YjNkYWMyNWYyZTMyIn19fQ==", true, "§8⇾ §7Linksklicke um diese §cKategorie §7zu öffnen §8⇽"));
        inventory.setItem(11, new SkullBuilder().createSkull("§c§lBlöcke", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGRmMjE4NTI1YzE5YTkyMmRmMmQwMDAxNzc4YTFkOTQ1Yzc1ZDJhOTI0YzBlYWEyMGU0YjI3MTlmODQ4NTQyZSJ9fX0=", true, "§8⇾ §7Linksklicke um diese §cKategorie §7zu öffnen §8⇽"));
        inventory.setItem(12, new SkullBuilder().createSkull("§c§lDekoration", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y5NDE4NDFjOTYyZjM2ODliOGJmNjhiM2E4M2Q2NGY3MGU4NTI0ODk0MWJiZTAyOTNjNGI0NjRhZjc5OSJ9fX0=", true, "§8⇾ §7Linksklicke um diese §cKategorie §7zu öffnen §8⇽"));
        inventory.setItem(13, new SkullBuilder().createSkull("§c§lEssen und Getränke", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE5OTdkYTY0MDQzYjI4NDgyMjExNTY0M2E2NTRmZGM0ZThhNzIyNjY2NGI0OGE0ZTFkYmI1NTdiNWMwZmUxNCJ9fX0=", true, "§8⇾ §7Linksklicke um diese §cKategorie §7zu öffnen §8⇽"));
        inventory.setItem(14, new SkullBuilder().createSkull("§c§lHumanoide", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIyZDhlNzUxYzhmMmZkNGM4OTQyYzQ0YmRiMmY1Y2E0ZDhhZThlNTc1ZWQzZWIzNGMxOGE4NmU5M2IifX19", true, "§8⇾ §7Linksklicke um diese §cKategorie §7zu öffnen §8⇽"));
        inventory.setItem(15, new SkullBuilder().createSkull("§c§lMenschen", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUxNDYzMzNiODE2OTY0ZDk2YTY1NDc0NTAyYmE2NTdjYzI4NGE5YjYwODUzOWUxZTZkZjUwMGYwMjA4NzUwOSJ9fX0=", true, "§8⇾ §7Linksklicke um diese §cKategorie §7zu öffnen §8⇽"));
        inventory.setItem(16, new SkullBuilder().createSkull("§c§lMonster", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMzZmY4ZWNlYmZhNGEzMGUwMDg5MWI1M2IyMGU3ODFlMmUxN2NhYWUzOGE1ZmY5YjI1OTE4OWE3NjNkMTgxMCJ9fX0=", true, "§8⇾ §7Linksklicke um diese §cKategorie §7zu öffnen §8⇽"));
        inventory.setItem(19, new SkullBuilder().createSkull("§c§lPflanzen", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjg3Nzk1OThkZTVlOWFmYWFjOWQyNmU4YzY1MTk3MmU5MGQ5MGY2NjI1ZDc4Zjk5NjY1OGEyOWFkZWY0NzJkNyJ9fX0=", true, "§8⇾ §7Linksklicke um diese §cKategorie §7zu öffnen §8⇽"));
        inventory.setItem(20, new SkullBuilder().createSkull("§c§lSonstiges", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTJlOTgxNjVkZWVmNGVkNjIxOTUzOTIxYzFlZjgxN2RjNjM4YWY3MWMxOTM0YTQyODdiNjlkN2EzMWY2YjgifX19", true, "§8⇾ §7Linksklicke um diese §cKategorie §7zu öffnen §8⇽"));
        inventory.setItem(21, new SkullBuilder().createSkull("§c§lTiere", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzVhOWNkNThkNGM2N2JjY2M4ZmIxZjVmNzU2YTJkMzgxYzlmZmFjMjkyNGI3ZjRjYjcxYWE5ZmExM2ZiNWMifX19", true, "§8⇾ §7Linksklicke um diese §cKategorie §7zu öffnen §8⇽"));

        player.openInventory(inventory);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] strings) {
        if(!(commandSender instanceof final Player player)) {
            Bukkit.getConsoleSender().sendMessage(ConfigMessages.SENDER_IS_CONSOLE);
            return true;
        }

        final int arguments = strings.length;

        if(!player.hasPermission(ConfigMessages.COMMAND_PERMISSION)) {
            player.sendMessage(ConfigMessages.NO_PERMISSION_MESSAGE);
            return true;
        }

        switch (arguments) {
            default:
                sendHelp(player);
                break;
            case 0:
                openHDB(player);
                break;
            case 1:
                if(strings[0].equalsIgnoreCase("reload")) {
                    reloadHDB(player);
                } else if(strings[0].equalsIgnoreCase("info")) {
                    sendInfo(player);
                } else {
                    sendHelp(player);
                }
                break;
        }
        return false;
    }
}