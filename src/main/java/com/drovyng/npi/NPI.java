package com.drovyng.npi;

import com.drovyng.npi.commands.NPIPanelCommand;
import com.drovyng.npi.events.NPIPanelEvent;
import com.drovyng.npi.events.CustomCraftEvent;
import com.drovyng.npi.events.InspectBrushEvent;
import com.drovyng.npi.events.NewClimbing;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class NPI extends JavaPlugin {

    public static NPI Instance;
    private static BukkitTask panelTask;

    @Override
    public void onEnable() {
        Instance = this;

        registerEvents();

        CustomItems.register();

        var npi_command = new NPIPanelCommand();
        getCommand("npi").setExecutor(npi_command);
        getCommand("npi").setTabCompleter(npi_command);

        Bukkit.getScheduler().runTaskTimer(this, NPIPanelEvent::UpdatePanels, 0, 20);
    }
    private void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
    public void registerEvents() {
        registerEvent(new InspectBrushEvent());
        registerEvent(new CustomCraftEvent());
        registerEvent(new NewClimbing());

        registerEvent(new NPIPanelEvent());
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
