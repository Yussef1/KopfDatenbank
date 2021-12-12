package de.yussef.headdatabase.fetcher;

import de.yussef.headdatabase.HeadDatabase;
import de.yussef.headdatabase.listener.HDBInventoryListener;
import de.yussef.headdatabase.utils.ConfigMessages;
import de.yussef.headdatabase.utils.ItemBuilder;
import de.yussef.headdatabase.utils.SkullBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class Fetcher {

    public final String  alphabetUrl = "https://minecraft-heads.com/scripts/api.php?cat=alphabet",
                         animalsUrl = "https://minecraft-heads.com/scripts/api.php?cat=animals",
                         blocksUrl = "https://minecraft-heads.com/scripts/api.php?cat=blocks",
                         decorationUrl = "https://minecraft-heads.com/scripts/api.php?cat=decoration",
                         food_drinksUrl = "https://minecraft-heads.com/scripts/api.php?cat=food-drinks",
                         humanoidsUrl = "https://minecraft-heads.com/scripts/api.php?cat=humanoids",
                         humansUrl = "https://minecraft-heads.com/scripts/api.php?cat=humans",
                         miscellaneousUrl = "https://minecraft-heads.com/scripts/api.php?cat=miscellaneous",
                         monstersUrl = "https://minecraft-heads.com/scripts/api.php?cat=monsters",
                         plantsUrl = "https://minecraft-heads.com/scripts/api.php?cat=plants";

    private final int INVENTORY_SIZE = 9*6;
    private String textureValue;

    public final int[] pageSlots = {47, 51, 53};

    private String generalDocument;

    private final String[] infoTextFileLines = {"ACHTUNG:,\n",
                                                "Bitte, Lösche, Bearbeite oder Hinzufüge nichts im 'jsonFiles' Ordner.\n",
                                                "Solltest du versehentlich DOCH etwas unbefugt bearbeiten. Nutze das '/kdb reload' Kommando ingame."};

    public final HashMap<Integer, ItemStack> currentHeadInfo = new HashMap<>();

    public void setupJSONFiles() throws IOException {
        String pluginName = HeadDatabase.getHeadDatabase().getName();
        File folder = new File("plugins/"+pluginName+"/jsonFiles");

        if(folder.exists()) {
            Bukkit.getConsoleSender().sendMessage("§aAlle JSON Kategorien wurden erfolgreich geladen.");
            return;
        }

        File infoTextFile = new File("plugins/"+pluginName+"/ReadMe.txt");
        FileWriter infoTextWriter = new FileWriter(infoTextFile);

        for(int i = 0; i < infoTextFileLines.length; i++) {
            infoTextWriter.write(infoTextFileLines[i]);
        }

        Bukkit.getConsoleSender().sendMessage("§aDas §8[§eReadMe§8] §aText File wurde erfolgreich erstellt.");
        infoTextWriter.close();

        folder.mkdir();
        Bukkit.getConsoleSender().sendMessage("§aDer Speicher Ordner der JSON Datein wurde erstellt! §8[§e"+folder.getPath()+"§8]");
        Bukkit.getConsoleSender().sendMessage("§aDownloade alle JSON Datein einmalig...");

        try {
            saveCategory(alphabetUrl, HDBInventoryListener.categories[0]);
            saveCategory(blocksUrl, HDBInventoryListener.categories[1]);
            saveCategory(decorationUrl, HDBInventoryListener.categories[2]);
            saveCategory(food_drinksUrl, HDBInventoryListener.categories[3]);
            saveCategory(humanoidsUrl, HDBInventoryListener.categories[4]);
            saveCategory(humansUrl, HDBInventoryListener.categories[5]);
            saveCategory(monstersUrl, HDBInventoryListener.categories[6]);
            saveCategory(plantsUrl, HDBInventoryListener.categories[7]);
            saveCategory(miscellaneousUrl, HDBInventoryListener.categories[8]);
            saveCategory(animalsUrl, HDBInventoryListener.categories[9]);
            saveCategory(null, HDBInventoryListener.categories[10]);

            Bukkit.getConsoleSender().sendMessage("§aDownload Abgeschlossen! Alle JSON Datein wurde erfolgreich heruntergeladen.");
        } catch (IOException ioException) {
            Bukkit.getConsoleSender().sendMessage("§4Ein Fehler ist aufgetreten beim Herunterladen der JSON Datein.");
        }
    }

    public void saveCategory(String urlAddress, String category) throws IOException {
        String pluginName = HeadDatabase.getHeadDatabase().getName();
        File folder = new File("plugins/"+pluginName+"/jsonFiles");
        File jsonResultFile = new File(folder+"/"+category+".json");
        FileWriter writeToJsonFile = new FileWriter(jsonResultFile);

        if(category.equalsIgnoreCase("GeneralDocument")) {
            while(generalDocument.contains("null")) {
                generalDocument = generalDocument.replace("null", "");
            }

            while(generalDocument.contains("}][{")) {
                generalDocument = generalDocument.replace("}][{", ",");
            }

            writeToJsonFile.write(generalDocument);
            writeToJsonFile.close();
            jsonResultFile.createNewFile();

            Bukkit.getConsoleSender().sendMessage("§aDas wichtigste Dokument §8[§e"+category+"§8] §awurde soeben erstellt.");
            return;
        }

        URL url = new URL(urlAddress);
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        StringBuilder result = new StringBuilder();
        String nextLine;

        while((nextLine = bufferedReader.readLine()) != null) {
            result.append(nextLine);
        }
        bufferedReader.close();

        writeToJsonFile.write(result.toString());
        generalDocument += result.toString();
        writeToJsonFile.close();
        jsonResultFile.createNewFile();

        Bukkit.getConsoleSender().sendMessage("§aDie Kategorie §8[§e"+category+"§8] §awurde soeben erstellt.");
    }

    public void readCategory(Player player, int currentPage, String categoryName, String category) {
        String pluginName = HeadDatabase.getHeadDatabase().getName();
        File folder = new File("plugins/"+pluginName+"/jsonFiles");
        File jsonFile = new File(folder+"/"+categoryName+".json");

        JSONParser jsonParser = new JSONParser();

        final Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, category);

        try(FileReader fileReader = new FileReader(jsonFile)) {
            Object object = jsonParser.parse(fileReader);
            JSONArray jsonArray = (JSONArray) object;

            jsonArray.forEach(list -> parseList(((JSONObject)list)));
        } catch(ParseException | IOException exception) {
            player.sendMessage(ConfigMessages.HDB_PREFIX + "§4ERROR: §cDie Kategorie §4" + categoryName + "§c wurde nicht gefunden oder es ist ein Fehler aufgetreten!");
        }

        int entries = currentHeadInfo.size();
        int entriesPerSite = 45;
        int sites = ((entries - (entries % entriesPerSite)) / entriesPerSite) + (entries % entriesPerSite == 0 ? 0 : 1);

        if((currentPage > sites) || (currentPage < 1)) {
            player.sendMessage(ConfigMessages.HDB_PREFIX + "§cERROR: Es ist ein Fehler aufgetreten.");
            return;
        }

        int firstEntry = (currentPage - 1) * entriesPerSite,
            lastEntry = firstEntry + (entriesPerSite - 1);

        if((entries - 1) < lastEntry) {
            lastEntry = entries - 1;
        }

        while(firstEntry <= lastEntry) {
            firstEntry++;

            inventory.addItem(currentHeadInfo.get(firstEntry));
        }

        for(int i = 45; i < 54; i++) {
            inventory.setItem(i, new ItemBuilder(" ", Material.BLACK_STAINED_GLASS_PANE, 1).build());
        }

        if(currentPage != 1) {
            inventory.setItem(pageSlots[0], new SkullBuilder().createSkull("§4§lSeite " + (currentPage - 1), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JkZjJjMzliYjVjYmEyNDQzMjllMDI4MGMwYjRhNDNlOWMzY2VhMjllMDZhYzIyMjcyMjM4ZmZiM2Q1ZTUzYiJ9fX0=\"},{\"name\":\"Bahamas\",\"uuid\":\"3b52be26-9dd3-4851-99ce-0dfdcae71cf4\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU2MzBlZjM0NTZlZDg1YmMxMzZmZGFkNjVhMTQ4NDIzNGJlYjQ1N2Q1ZTM1OWFlM2NiOGM3MjZiNzJiNWY2YSJ9fX0=\"},{\"name\":\"Bahrain\",\"uuid\":\"904e33a7-446e-4fc4-b55a-719c3aaafd57\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2MzU1YjMwOTg1ZDg1ZDM3ZWNhYTJhZmU3MGI5Y2RkODM5MDA0ZmE0MTE0N2FkNzQzYzdmYzkyYjliYTU0MCJ9fX0=\"},{\"name\":\"Bandera Canaria (Canary Islands)\",\"uuid\":\"9cba7ea0-bb03-4c0f-ac13-28cceb08d380\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ5NzAzM2U4YzBlZjYzMzNjZDAzZGJjODMzMjJlNGNmMDFiMDZlOTg0OTdkODg4Y2UzYzk3ZWY2OTVkNWRlMCJ9fX0=", true, "§8⇾ §7Linksklicke um eine Seite §4zurück §7zu blättern §8⇽"));
        }

        if(!(currentPage >= sites)) {
            inventory.setItem(pageSlots[1], new SkullBuilder().createSkull("§a§lSeite " + (currentPage + 1), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTNiZjJmYzY5M2IxNmNiOTFiOGM4N2E0YjA4OWZkOWUxODI1ZmNhMDFjZWZiMTY1YzYxODdmYzUzOWIxNTJjOSJ9fX0=\"},{\"name\":\"Framed Cube (blue)\",\"uuid\":\"a30cf439-8172-4eb3-92a2-d585f1af3050\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2NjN2M4ZjdhODliNjhlZWNmOWQ0NDE3Yjg1ODlmOWY2ZDExMTVjYmZmYjYzYjFmMzQ5MWU4YzUyMzM4In19fQ==\"},{\"name\":\"Framed Cube (green)\",\"uuid\":\"5f1bf5f7-5910-45d6-8fff-2e6d0730793e\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZjNmVjM2I3NTM1NGI0OTIyMmE4OWM2NjNjNGFjYWQ1MjY0ZmI5NzdjYWUyNmYwYjU0ODNhNTk5YzQ2NCJ9fX0=\"},{\"name\":\"Framed Cube (red)\",\"uuid\":\"b7450a67-add3-4f7a-af5b-26b97c26e4bb\",\"value\":\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ3OGNjMzkxYWZmYjgwYjJiMzVlYjczNjRmZjc2MmQzODQyNGMwN2U3MjRiOTkzOTZkZWU5MjFmYmJjOWNmIn19fQ==", true, "§8⇾ §7Linksklicke um eine Seite §avor §7zu blättern §8⇽"));
        }

        inventory.setItem(49, new ItemBuilder("§e§lAktuelle Seite", Material.PAPER, 1).setLore("§8⇾ §7Du befindest dich zurzeit auf der §e"+ currentPage +" §7Seite §8⇽").build());
        inventory.setItem(pageSlots[2], new ItemBuilder("§9§lZurück", Material.LECTERN, 1).setLore("§8⇾ §7Linksklicke §9hier§7, um zurück zum Main Menü zu gelangen. §8⇽").build());

        currentHeadInfo.clear();
        player.openInventory(inventory);
    }

    public void parseList(JSONObject jsonObject) {
        String headName = (String) jsonObject.get("name");
        textureValue = (String) jsonObject.get("value");

        currentHeadInfo.put(currentHeadInfo.size() + 1, new SkullBuilder().createSkull("§c§l"+headName, textureValue, true, "§8⇾ §aLinksklicke §7um den Kopf zu erhalten §8⇽"));
    }

    public void parseList(String searchName, JSONObject jsonObject) {
        String headName = (String) jsonObject.get("name");
        textureValue = (String) jsonObject.get("value");

        if(!(headName.regionMatches(true, 0, searchName, 0, headName.length()))) {
            return;
        }

        currentHeadInfo.put(currentHeadInfo.size() + 1, new SkullBuilder().createSkull("§c§l"+headName, textureValue, true, "§8⇾ §aLinksklicke §7um den Kopf zu erhalten §8⇽"));
    }
}