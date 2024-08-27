package com.drovyng.npi;

import com.drovyng.npi.commands.NPICommand;
import com.drovyng.npi.events.CustomCraftEvent;
import com.drovyng.npi.events.InspectBrushEvent;
import com.drovyng.npi.events.NewClimbing;
import com.drovyng.npi.panel.NPIManager;
import com.drovyng.npi.panel.NPIStaticPanels;
import net.coreprotect.utility.Color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class NPI extends JavaPlugin {

    public static NPI Instance;

    @Override
    public void onEnable() {
        Instance = this;

        NPIStorage.Instance.Load();

        NPIStaticPanels.CreateStaticPanels();

        CustomItems.register();

        NPIStaticPanels.ShouldStatic.clear();

        var npi_command = new NPICommand();
        getCommand("npi").setExecutor(npi_command);
        getCommand("npi").setTabCompleter(npi_command);

        registerEvents();

        Bukkit.getScheduler().runTaskTimer(this, NPIManager::UpdatePanels, 0, 20);
        Bukkit.getScheduler().runTaskTimer(this, NPIStorage.Instance::Save, 6000, 12000);
    }
    public static void Send(Player player, String text){
        player.sendMessage("["+TitleShort+"] " + text);
    }
    public static void Error(Player player, String text) {
        Error(player, text, false);
    }
    public static final String TitleShort = Color.RED + Color.BOLD + "N" +
            Color.YELLOW + Color.BOLD + "P" +
            Color.WHITE + Color.BOLD + "I" + Color.RESET;

    public static void Error(Player player, String text, Boolean isLazy){
        text = "[" + TitleShort + "] " + Color.RED + "Ошибка: " + text;
        if (!isLazy){
            Instance.getLogger().severe("[NPI] Ошибка: " + text);
            player.sendMessage(text + ". Пожалуйста сообщите администрации!");
            return;
        }
        player.sendMessage(text + ".");
    }
    private void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
    public void registerEvents() {
        registerEvent(new InspectBrushEvent());
        registerEvent(new CustomCraftEvent());
        registerEvent(new NewClimbing());

        registerEvent(new NPIManager());
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
