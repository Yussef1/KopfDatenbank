package de.yussef.headdatabase.listener;

import de.yussef.headdatabase.HeadDatabase;
import de.yussef.headdatabase.commands.HDBCommand;
import de.yussef.headdatabase.fetcher.Fetcher;
import de.yussef.headdatabase.utils.ConfigMessages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CategoryClickListener implements Listener {

    private final HDBInventoryListener hdbInventoryListener = new HDBInventoryListener();
    private final HDBCommand hdbCommand = new HDBCommand();
    private final Fetcher fetcher = new Fetcher();

    private final boolean closeInventoryAfterClick = HeadDatabase.getHeadDatabase().getConfig().getBoolean("HeadDatabase.AutoCloseInventory"),
                          showChatMessage = HeadDatabase.getHeadDatabase().getConfig().getBoolean("HeadDatabase.ShowChatMessage");

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null) {
            return;
        }

        for(int i = 0; i < hdbInventoryListener.categorieNames.length; i++) {
            if(event.getView().getTitle().equalsIgnoreCase(hdbInventoryListener.categorieNames[i])) {
                final Player player = (Player) event.getWhoClicked();
                final Material material = event.getCurrentItem().getType();
                final ItemStack head = event.getCurrentItem();
                final String headName = event.getCurrentItem().getItemMeta().getDisplayName();
                int clickedSlot = event.getSlot();

                switch (material) {
                    default:
                        event.setCancelled(true);
                        break;
                    case PLAYER_HEAD:
                        event.setCancelled(true);

                        if(clickedSlot == fetcher.pageSlots[0] || clickedSlot == fetcher.pageSlots[1]) {
                            int nextPage = Integer.parseInt(ChatColor.stripColor(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getDisplayName()).replace("Seite ", ""));
                            fetcher.readCategory(player, nextPage, ChatColor.stripColor(HDBInventoryListener.categories[i]), hdbInventoryListener.categorieNames[i]);
                            return;
                        }

                        if(closeInventoryAfterClick) {
                            player.closeInventory();
                        }

                        if (player.getInventory().firstEmpty() == -1) {
                            player.sendMessage(ConfigMessages.HDB_PREFIX + "§cDein Invenntar ist voll, bitte halte mindestens einen Platz Frei.");
                        } else {
                            player.getInventory().addItem(head);

                            if(showChatMessage) {
                                player.sendMessage(ConfigMessages.HDB_PREFIX + "Du hast den Kopf §8'§c" + headName + "§8' §7erfolgreich erhalten.");
                            }
                        }
                        break;
                    case LECTERN:
                        event.setCancelled(true);
                        hdbCommand.openHDB(player);
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onClickOnSearch(InventoryClickEvent event) {
        if(event.getCurrentItem() == null) {
            return;
        }

        if(event.getView().getTitle().startsWith("§c§lSuche:")) {
            final Player player = (Player) event.getWhoClicked();
            final Material material = event.getCurrentItem().getType();
            final ItemStack head = event.getCurrentItem();
            final String headName = event.getCurrentItem().getItemMeta().getDisplayName();
            int clickedSlot = event.getSlot();

            switch (material) {
                default:
                    event.setCancelled(true);
                    break;
                case PLAYER_HEAD:
                    event.setCancelled(true);

                    if(clickedSlot == fetcher.pageSlots[0] || clickedSlot == fetcher.pageSlots[1]) {
                        int nextPage = Integer.parseInt(ChatColor.stripColor(event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getDisplayName()).replace("Seite ", ""));

                        if(HDBInventoryListener.searchHeadName == null) {
                            player.closeInventory();
                            player.sendMessage(ConfigMessages.HDB_PREFIX + "§cEs ist ein Fehler aufgetreten. Bitte suche den Kopf erneut.");
                            return;
                        }

                        hdbInventoryListener.searchForHead(player, nextPage, HDBInventoryListener.searchHeadName, 0, '0', false);
                        return;
                    }

                    if(closeInventoryAfterClick) {
                        player.closeInventory();
                    }

                    if(player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(ConfigMessages.HDB_PREFIX + "§cDein Invenntar ist voll, bitte halte mindestens einen Platz Frei.");
                    } else {
                        player.getInventory().addItem(head);

                        if(showChatMessage) {
                            player.sendMessage(ConfigMessages.HDB_PREFIX + "Du hast den Kopf §8'§c" + headName + "§8' §7erfolgreich erhalten.");
                        }
                    }
                    break;
                case LECTERN:
                    event.setCancelled(true);
                    hdbCommand.openHDB(player);
                    break;
            }
        }
    }
}