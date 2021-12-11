package de.yussef.headdatabase.listener;

import de.yussef.headdatabase.HeadDatabase;
import de.yussef.headdatabase.commands.HDBCommand;
import de.yussef.headdatabase.fetcher.Fetcher;
import de.yussef.headdatabase.utils.ConfigMessages;
import de.yussef.headdatabase.utils.ItemBuilder;
import de.yussef.headdatabase.utils.SkullBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HDBInventoryListener implements Listener {

    private final HDBCommand hdbCommand = new HDBCommand();

    private final int[] headSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21};

    private final ArrayList<Player> searchingHead = new ArrayList<>();

    private final Fetcher fetcher = new Fetcher();

    private final int INVENTORY_SIZE = 9*6;

    public static final String[] categories = {"Alphabet", "Blocks", "Decoration", "Food_Drinks",
                                               "Humanoid", "Humans", "Monsters",
                                               "Plants", "Miscellaneous", "Animals", "GeneralDocument"};

    public final String[] categorieNames = {"§c§lAlphabet", "§c§lBlöcke", "§c§lDekoration", "§c§lEssen und Getränke",
                                           "§c§lHumanoide", "§c§lMenschen", "§c§lMonster",
                                           "§c§lPflanzen", "§c§lSonstiges", "§c§lTiere"};

    private final String GENERAL_DOCUMENT = "GeneralDocument.json";

    public static String searchHeadName;

    private final boolean showInfoSearchMessage = HeadDatabase.getHeadDatabase().getConfig().getBoolean("HeadDatabase.ShowInfoSearchMessage");

    public void searchForHead(Player player, int currentPage, String headName, int headNameLength, char headStartChar, boolean sendInfoMessage) {
        searchHeadName = headName;

        if(showInfoSearchMessage && sendInfoMessage) {
            player.sendMessage("§8§m--------------------§c§lInfo§8§m--------------------");
            player.sendMessage("");
            player.sendMessage(ConfigMessages.HDB_PREFIX + "Kopf Name: §3" + headName);
            player.sendMessage(ConfigMessages.HDB_PREFIX + "Start Buchstabe: §3" + headStartChar);
            player.sendMessage(ConfigMessages.HDB_PREFIX + "Buchstaben Länge: §3" + headNameLength);
            player.sendMessage("");
            player.sendMessage(ConfigMessages.HDB_PREFIX + "§aSuch Vorgang gestartet...");
            player.sendMessage("");
            player.sendMessage("§8§m--------------------§c§lInfo§8§m--------------------");
        }

        JSONParser jsonParser = new JSONParser();
        String pluginName = HeadDatabase.getHeadDatabase().getName();
        File folder = new File("plugins/"+pluginName+"/jsonFiles");

        final Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, "§c§lSuche: "+headName);

        try(FileReader fileReader = new FileReader(folder+"/"+GENERAL_DOCUMENT)) {
            Object object = jsonParser.parse(fileReader);
            JSONArray jsonArray = (JSONArray) object;

            jsonArray.forEach(list -> fetcher.parseList(headName, ((JSONObject)list)));
        } catch(ParseException | IOException exception) {
            player.sendMessage(ConfigMessages.HDB_PREFIX + "§4ERROR: §cDas JSON Document wurde nicht gefunden oder es ist ein Fehler aufgetreten!");
        }

        int entries = fetcher.currentHeadInfo.size();
        int entriesPerSite = 45;
        int sites = ((entries - (entries % entriesPerSite)) / entriesPerSite) + (entries % entriesPerSite == 0 ? 0 : 1);

        if ((currentPage > sites)) {
            return;
        } else if(currentPage < 1) {
            player.sendMessage(ConfigMessages.HDB_PREFIX + "§cEs wurde kein Kopf mit dem Namen §8[§e"+headName+"§8] §cgefunden.");
            return;
        }

        int firstEntry = (currentPage - 1) * entriesPerSite,
                lastEntry = firstEntry + (entriesPerSite - 1);

        if ((entries - 1) < lastEntry) {
            lastEntry = entries - 1;
        }

        while (firstEntry <= lastEntry) {
            firstEntry++;

            inventory.addItem(fetcher.currentHeadInfo.get(firstEntry));
        }

        for(int i = 45; i < 54; i++) {
            inventory.setItem(i, new ItemBuilder(" ", Material.BLACK_STAINED_GLASS_PANE, 1).build());
        }

        if(currentPage != 1) {
            inventory.setItem(fetcher.pageSlots[0], new SkullBuilder().createSkull("§4§lSeite " + (currentPage - 1), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JkZjJjMzliYjVjYmEyNDQzMjllMDI4MGMwYjRhNDNlOWMzY2VhMjllMDZhYzIyMjcyMjM4ZmZiM2Q1ZTUzYiJ9fX0=\"},{\"name\":\"Bahamas\",\"uuid\":\"3b52be26-9dd3-4851-99ce-0dfdcae71cf4\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU2MzBlZjM0NTZlZDg1YmMxMzZmZGFkNjVhMTQ4NDIzNGJlYjQ1N2Q1ZTM1OWFlM2NiOGM3MjZiNzJiNWY2YSJ9fX0=\"},{\"name\":\"Bahrain\",\"uuid\":\"904e33a7-446e-4fc4-b55a-719c3aaafd57\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2MzU1YjMwOTg1ZDg1ZDM3ZWNhYTJhZmU3MGI5Y2RkODM5MDA0ZmE0MTE0N2FkNzQzYzdmYzkyYjliYTU0MCJ9fX0=\"},{\"name\":\"Bandera Canaria (Canary Islands)\",\"uuid\":\"9cba7ea0-bb03-4c0f-ac13-28cceb08d380\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ5NzAzM2U4YzBlZjYzMzNjZDAzZGJjODMzMjJlNGNmMDFiMDZlOTg0OTdkODg4Y2UzYzk3ZWY2OTVkNWRlMCJ9fX0=", true, "§8⇾ §7Linksklicke um eine Seite §4zurück §7zu blättern §8⇽"));
        }

        if(!(currentPage >= sites)) {
            inventory.setItem(fetcher.pageSlots[1], new SkullBuilder().createSkull("§a§lSeite " + (currentPage + 1), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTNiZjJmYzY5M2IxNmNiOTFiOGM4N2E0YjA4OWZkOWUxODI1ZmNhMDFjZWZiMTY1YzYxODdmYzUzOWIxNTJjOSJ9fX0=\"},{\"name\":\"Framed Cube (blue)\",\"uuid\":\"a30cf439-8172-4eb3-92a2-d585f1af3050\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2NjN2M4ZjdhODliNjhlZWNmOWQ0NDE3Yjg1ODlmOWY2ZDExMTVjYmZmYjYzYjFmMzQ5MWU4YzUyMzM4In19fQ==\"},{\"name\":\"Framed Cube (green)\",\"uuid\":\"5f1bf5f7-5910-45d6-8fff-2e6d0730793e\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZjNmVjM2I3NTM1NGI0OTIyMmE4OWM2NjNjNGFjYWQ1MjY0ZmI5NzdjYWUyNmYwYjU0ODNhNTk5YzQ2NCJ9fX0=\"},{\"name\":\"Framed Cube (red)\",\"uuid\":\"b7450a67-add3-4f7a-af5b-26b97c26e4bb\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ3OGNjMzkxYWZmYjgwYjJiMzVlYjczNjRmZjc2MmQzODQyNGMwN2U3MjRiOTkzOTZkZWU5MjFmYmJjOWNmIn19fQ==", true, "§8⇾ §7Linksklicke um eine Seite §avor §7zu blättern §8⇽"));
        }

        inventory.setItem(49, new ItemBuilder("§e§lAktuelle Seite", Material.PAPER, 1).setLore("§8⇾ §7Du befindest dich zurzeit auf der §e"+ currentPage +" §7Seite §8⇽").build());
        inventory.setItem(fetcher.pageSlots[2], new ItemBuilder("§9§lZurück", Material.LECTERN, 1).setLore("§8⇾ §7Linksklicke §9hier§7, um zurück zum Main Menü zu gelangen. §8⇽").build());

        Bukkit.getScheduler().runTaskLater(HeadDatabase.getHeadDatabase(), () -> player.openInventory(inventory), 0);
        fetcher.currentHeadInfo.clear();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(!(event.getView().getTitle().equalsIgnoreCase(hdbCommand.INVENTORY_NAME))) {
            return;
        }

        if(event.getCurrentItem() == null) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();
        final Material material = event.getCurrentItem().getType();
        int clickedSlot = event.getSlot();

        switch (material) {
            default:
                event.setCancelled(true);
                break;
            case PLAYER_HEAD:
                event.setCancelled(true);

                if(!(event.isLeftClick())) {
                    return;
                }

                if(clickedSlot == headSlots[0]) {
                    fetcher.readCategory(player, 1, categories[0], categorieNames[0]);
                } else if(clickedSlot == headSlots[1]) {
                    fetcher.readCategory(player, 1, categories[1], categorieNames[1]);
                } else if(clickedSlot == headSlots[2]) {
                    fetcher.readCategory(player, 1, categories[2], categorieNames[2]);
                } else if(clickedSlot == headSlots[3]) {
                    fetcher.readCategory(player, 1, categories[3], categorieNames[3]);
                } else if(clickedSlot == headSlots[4]) {
                    fetcher.readCategory(player, 1, categories[4], categorieNames[4]);
                } else if(clickedSlot == headSlots[5]) {
                    fetcher.readCategory(player, 1, categories[5], categorieNames[5]);
                } else if(clickedSlot == headSlots[6]) {
                    fetcher.readCategory(player, 1, categories[6], categorieNames[6]);
                } else if(clickedSlot == headSlots[7]) {
                    fetcher.readCategory(player, 1, categories[7], categorieNames[7]);
                } else if(clickedSlot == headSlots[8]) {
                    fetcher.readCategory(player, 1, categories[8], categorieNames[8]);
                } else if(clickedSlot == headSlots[9]) {
                    fetcher.readCategory(player, 1, categories[9], categorieNames[9]);
                }
                break;
            case OAK_SIGN:
                event.setCancelled(true);

                if(!(event.isLeftClick())) {
                    return;
                }

                player.closeInventory();

                if(searchingHead.contains(player)) {
                    searchingHead.remove(player);

                    player.sendMessage(ConfigMessages.HDB_PREFIX + "§cDu hast den Suchvorgang Abgebrochen.");
                } else {
                    searchingHead.add(player);

                    player.sendMessage(ConfigMessages.HDB_PREFIX + "Bitte gebe einen §eKopf Namen §7in den Chat ein:");
                    player.sendMessage(ConfigMessages.HDB_PREFIX + "Um den Suchvorgang abzubrechen schreibe §8'§ccancel§8' §7in den Chat.");

                    if(showInfoSearchMessage) {
                        player.sendMessage(ConfigMessages.HDB_PREFIX + "§eHinweis: §7Achte auf die Groß und klein Schreibung. Zudem auch das du den Kopf Namen richtig schreibst und das die Wörter in Englisch geschrieben werden.");
                    }
                }
                break;
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();

        if(!(searchingHead.contains(player))) {
            return;
        }

        event.setCancelled(true);
        searchingHead.remove(player);

        final String headName = event.getMessage();
        final int headNameLength = headName.length(),
                  maxHeadNameLength = 16;
        final char headStartChar = headName.charAt(0);

        if(headName.equalsIgnoreCase("cancel")) {
            player.sendMessage(ConfigMessages.HDB_PREFIX + "§cDu hast den Suchvorgang Abgebrochen.");
            return;
        }

        if(headNameLength > maxHeadNameLength) {
            player.sendMessage(ConfigMessages.HDB_PREFIX + "§cDer Name eines Kopfes darf nicht über 16 Buchstaben beinhalten.");
            return;
        }

        searchForHead(player, 1, headName, headNameLength, headStartChar, true);
    }
}