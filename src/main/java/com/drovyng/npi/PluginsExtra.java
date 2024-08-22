package com.drovyng.npi;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;

public class PluginsExtra {
    public static CoreProtectAPI MyCoreProtect;
    static {
        if (Bukkit.getPluginManager().getPlugin("CoreProtect") != null){
            MyCoreProtect = CoreProtect.getInstance().getAPI();
        }
    }
}
