package de.yussef.headdatabase;

import de.yussef.headdatabase.commands.HDBCommand;
import de.yussef.headdatabase.fetcher.Fetcher;
import de.yussef.headdatabase.listener.CategoryClickListener;
import de.yussef.headdatabase.listener.HDBInventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class HeadDatabase extends JavaPlugin {

    private static HeadDatabase headDatabase;
    private final Fetcher fetcher = new Fetcher();

    @Override
    public void onLoad() {
        Bukkit.getConsoleSender().sendMessage("§eDas Plugin wurde erfolgreich initialisiert.");

        headDatabase = this;
        loadConfig();

        try {
            fetcher.setupJSONFiles();
        } catch (IOException exception) {
            Bukkit.getConsoleSender().sendMessage("§4ERROR: §cEs ist ein Fehler beim Erstellen der Datein aufgetreten.");
        }
    }

    @Override
    public void onEnable() {
        printOutWatermark();
        registerEvents();

        Bukkit.getConsoleSender().sendMessage("§aDas HeadDatabase System wurde vollständig Aktiviert.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§4Das HeadDatabase System wurde Deaktiviert.");
    }

    private void printOutWatermark() {
        Bukkit.getConsoleSender().sendMessage("""
                  _    _ _____  ____ \s
                 | |  | |  __ \\|  _ \\\s
                 | |__| | |  | | |_) |
                 |  __  | |  | |  _ <\s
                 | |  | | |__| | |_) |
                 |_|  |_|_____/|____/\s
                """);
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        Bukkit.getConsoleSender().sendMessage("§eDie Config wurde erfolgreich geladen.");
    }

    private void registerEvents() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        // Listener
        pluginManager.registerEvents(new HDBInventoryListener(), this);
        pluginManager.registerEvents(new CategoryClickListener(), this);
        // Commands
        getCommand("hdb").setExecutor(new HDBCommand());

        Bukkit.getConsoleSender().sendMessage("§eAlle Befehle und Events wurden registriert.");
    }

    public static HeadDatabase getHeadDatabase() {
        return headDatabase;
    }
}