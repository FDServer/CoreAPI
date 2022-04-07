package de.myfdweb.minecraft.api.items;

import de.myfdweb.minecraft.api.utils.PlayerRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class ConfirmGUI {

    private static final ArrayList<ConfirmGUI> confirmGUIs = new ArrayList<>();
    private final Inventory inv;
    private final PlayerRunnable yAction, nAction;

    public ConfirmGUI(String title, String message, PlayerRunnable yAction, PlayerRunnable nAction) {
        inv = Bukkit.createInventory(null, 27, title);
        inv.setItem(11, new ItemBuilder(Material.GREEN_WOOL).setDisplayName("§aJa").build());
        inv.setItem(13, new ItemBuilder(Material.PAPER).setDisplayName(message).build());
        inv.setItem(15, new ItemBuilder(Material.RED_WOOL).setDisplayName("§cNein").build());
        this.yAction = yAction;
        this.nAction = nAction;
    }

    public void open(Player p) {
        p.openInventory(inv);
        confirmGUIs.add(this);
    }

    public static class Listener implements org.bukkit.event.Listener {
        @EventHandler
        public void onCLick(InventoryClickEvent e) {
            ConfirmGUI confirmGUI = null;
            for(ConfirmGUI c : confirmGUIs)
                if(c.inv.equals(e.getInventory()))
                    confirmGUI = c;
            if(confirmGUI != null) {
                e.setCancelled(true);
                if(e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null)
                    if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§aJa"))
                        confirmGUI.yAction.run((Player) e.getWhoClicked());
                    else if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§cNein"))
                        confirmGUI.nAction.run((Player) e.getWhoClicked());
                e.getWhoClicked().closeInventory();
                confirmGUIs.remove(confirmGUI);
            }
        }
    }

}
