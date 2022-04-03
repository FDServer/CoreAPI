package de.myfdweb.minecraft.itemsapi;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemsAPI extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new Pages.Listener(), this);
    }
}
