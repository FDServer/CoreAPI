package de.myfdweb.minecraft.itemsapi;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Pages {

    private static final ItemStack PLACEHOLDER = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build();
    private static HashMap<Player, Pages> pagesView = new HashMap<>();
    private static ArrayList<Player> whitelistClose = new ArrayList<>();
    private String title;
    private ArrayList<Item> contents = new ArrayList<>();
    private final Item[] item = new Item[]{null, null, null};
    private boolean canGrab = false;
    private boolean closeOnClick = true;
    private HashMap<Inventory, Integer> views = new HashMap<>();

    public Pages() {
    }

    public Pages(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(ArrayList<Item> contents) {
        this.contents = contents;
    }

    public void addContent(Item... itemStacks) {
        this.contents.addAll(Arrays.asList(itemStacks));
    }

    public Item getItem1() {
        return item[0];
    }

    public void setItem1(Item item1) {
        this.item[0] = item1;
    }

    public Item getItem2() {
        return item[1];
    }

    public void setItem2(Item item2) {
        this.item[1] = item2;
    }

    public Item getItem3() {
        return item[2];
    }

    public void setItem3(Item item3) {
        this.item[2] = item3;
    }

    public void open(Player p) {
        open(p, 0);
    }

    public boolean canGrab() {
        return canGrab;
    }

    public void setCanGrab(boolean canGrab) {
        this.canGrab = canGrab;
    }

    public boolean isCloseOnClick() {
        return closeOnClick;
    }

    public void setCloseOnClick(boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
    }

    public void open(Player p, int page) {
        if (page < 0 || page >= Math.ceil(contents.size() / (5 * 9f)))
            return;
        Inventory inv = Bukkit.createInventory(null, 6 * 9, "§a§l" + getTitle() + " - Seite " + (page + 1));
        for (int i = page * (5 * 9); i < Math.min(contents.size(), (page + 1) * (5 * 9)); i++)
            inv.setItem(i % (5 * 9), contents.get(i).getItemStack());
        inv.setItem(45, PLACEHOLDER);
        inv.setItem(46, page == 0 ? PLACEHOLDER : new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("MHF_ArrowLeft").setDisplayName("§aVorherige Seite").build());
        inv.setItem(47, PLACEHOLDER);
        for (int i = 0; i < 3; i++)
            inv.setItem(48 + i, item[i] == null ? PLACEHOLDER : item[i].getItemStack());
        inv.setItem(51, PLACEHOLDER);
        inv.setItem(52, page == Math.ceil(contents.size() / (5 * 9f)) - 1 ? PLACEHOLDER : new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("MHF_ArrowRight").setDisplayName("§aNächste Seite").build());
        inv.setItem(53, PLACEHOLDER);
        views.put(inv, page);
        pagesView.put(p, this);
        whitelistClose.add(p);
        p.openInventory(inv);
    }

    public static class Item {

        private ItemStack itemStack;
        private OnClickListener onClickListener;

        public Item(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.onClickListener = (p, item) -> {
            };
        }

        public Item(ItemStack itemStack, OnClickListener onClickListener) {
            this.itemStack = itemStack;
            this.onClickListener = onClickListener;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public OnClickListener getOnClickListener() {
            return onClickListener;
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public void click(Player p) {
            this.onClickListener.onClick(p, this);
        }
    }

    public interface OnClickListener {

        void onClick(Player p, Item item);

    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        public void onClick(InventoryClickEvent e) {
            if (e.getWhoClicked() instanceof Player) {
                Player p = (Player) e.getWhoClicked();
                if (pagesView.containsKey(p)) {
                    Pages pages = pagesView.get(p);
                    int page = -1;
                    for (Inventory inv : pages.views.keySet())
                        if (inv.equals(e.getInventory()))
                            page = pages.views.get(inv);
                    if (page == -1) {
                        p.sendMessage("§cEs ist ein unbekannter Fehler aufgetreten.");
                        p.closeInventory();
                    } else {
                        e.setCancelled(!pages.canGrab());
                        if (e.getCurrentItem() != null && e.getInventory().equals(e.getClickedInventory())) {
                            if (e.getSlot() == 46 && page != 0) {
                                pages.open(p, page - 1);
                                return;
                            } else if (e.getSlot() == 52 && page != Math.ceil(pages.contents.size() / (5 * 9f)) - 1) {
                                pages.open(p, page + 1);
                                return;
                            }
                            Item i = null;
                            if (e.getSlot() > 47 && e.getSlot() < 51)
                                i = pages.item[e.getSlot() - 48];
                            else if (e.getSlot() < 45 && page * (5 * 9) + e.getSlot() < pages.contents.size())
                                i = pages.contents.get(page * (5 * 9) + e.getSlot());
                            if (i != null) {
                                i.click(p);
                                if (pages.isCloseOnClick())
                                    p.closeInventory();
                            }
                        }
                    }
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            if (e.getPlayer() instanceof Player p) {
                if(whitelistClose.contains(p)) {
                    whitelistClose.remove(p);
                    return;
                }
                if (pagesView.containsKey(p)) {
                    pagesView.get(p).views.remove(e.getInventory());
                    pagesView.remove(p);
                }
            }
        }

        public void onQuit(PlayerQuitEvent e) {
            pagesView.remove(e.getPlayer());
        }

    }
}
