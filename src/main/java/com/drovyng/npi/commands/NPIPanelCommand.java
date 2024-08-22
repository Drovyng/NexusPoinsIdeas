package com.drovyng.npi.commands;

import com.drovyng.npi.NPI;
import com.drovyng.npi.events.NPIPanelEvent;
import net.coreprotect.utility.Color;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NPIPanelCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player){
            if (args.length != 1){
                return false;
            }
            args[0] = args[0].toLowerCase();
            if (!Pages.contains(args[0])){
                return false;
            }
            switch (Pages.indexOf(args[0])){
                case 1:
                    NPIPanelEvent.OpenPage1(player);
                    NPIPanelEvent.Players.add(player);
                    break;
                default:
                    player.sendMessage(
                        Component.text(
                        Color.RED + Color.BOLD + "Nexus" +
                                Color.YELLOW + Color.BOLD + "Poins" +
                                Color.WHITE + Color.BOLD + "Ideas " +
                                Color.RESET + "v" + NPI.Instance.getPluginMeta().getVersion() +
                                " by " + Color.GREEN + "Drovyng",
                            TextColor.fromHexString("#D8D8D8")
                        )
                    );
                    break;
            }
            return true;
        }
        sender.sendMessage("Зайди на серв сначала!");
        return false;
    }
    public static final List<String> Pages = List.of("info", "added");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1){
            return Pages;
        }
        return List.of();
    }
}
