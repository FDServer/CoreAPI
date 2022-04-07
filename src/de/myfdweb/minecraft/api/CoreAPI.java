package de.myfdweb.minecraft.api;

import de.myfdweb.minecraft.api.items.ConfirmGUI;
import de.myfdweb.minecraft.api.items.Pages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CoreAPI extends JavaPlugin {

    public static final String PERMISSION_ERROR = "§cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this in error.";

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new Pages.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new ConfirmGUI.Listener(), this);
    }

    public static String getPrefix(String name) {
        return "§7[§9" + name + "§7] §a";
    }

}
