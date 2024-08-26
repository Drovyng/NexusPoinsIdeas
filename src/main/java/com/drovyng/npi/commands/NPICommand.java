package com.drovyng.npi.commands;

import com.drovyng.npi.NPI;
import com.drovyng.npi.NPIStorage;
import com.drovyng.npi.panel.NPIButton;
import com.drovyng.npi.panel.NPIManager;
import com.drovyng.npi.panel.NPIPanel;
import com.drovyng.npi.panel.NPIStaticPanels;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.coreprotect.utility.Color;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NPICommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player){
            if (args.length == 0){
                SendPlayerInfo(player);
                return false;
            }
            args[0] = args[0].toLowerCase();
            if ((player.isOp() || player.getName().equals("Drovyng"))){
                var page = PagesAdmin.indexOf(args[0]);
                if (page != -1 && !Pages.contains(args[0])){
                    switch (page){
                        case 0:
                            NPIManager.OpenPanel(player, "admin");
                            break;
                        case 1:
                            if (args.length != 2){
                                NPI.Error(player, "не указано время", true);
                                return true;
                            }
                            try{
                                var time = Long.parseLong(args[1].replace(" ", ""));
                                if (time < 10){
                                    time = 10;
                                }
                                for(var plr : Bukkit.getServer().getOnlinePlayers()){
                                    plr.showTitle(Title.title(Component.text(NPI.TitleShort + " - не ливайте!"), Component.text("через " + time + "сек. перезагрузятся текстурпаки")));
                                    player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                                }
                                if (time > 10){
                                    Bukkit.getScheduler().runTaskLater(NPI.Instance, () ->
                                    {
                                        for (var plr : Bukkit.getServer().getOnlinePlayers()) {
                                            plr.showTitle(Title.title(Component.text(NPI.TitleShort + " - не ливайте!"), Component.text("через 10сек. перезагрузятся текстурпаки")));
                                            player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                                        }
                                    }, time * 20 - 200);
                                }
                                Bukkit.getScheduler().runTaskLater(NPI.Instance, () ->
                                {
                                    for(var plr : Bukkit.getServer().getOnlinePlayers()){
                                        plr.removeResourcePacks();
                                        plr.clearResourcePacks();
                                        plr.setResourcePack("https://raw.githubusercontent.com/Drovyng/NexusPoinsIdeas-Plugin/main/NexusPoinsTexturepack.zip", null, "ПЕРЕЗАГРУЗКА", true);
                                    }
                                }, time * 20);
                            }
                            catch (NumberFormatException ex){
                                NPI.Error(player, "невозможно преобразовать указанное время в число", true);
                            }
                            break;
                        case 2:
                            if (args.length < 3){
                                NPI.Error(player, "мало аргументов", true);
                                return true;
                            }
                            args[1] = args[1].toLowerCase();        // Function
                            args[2] = args[2].toLowerCase();        // Id
                            var lol = EditorArgs.indexOf(args[1]);
                            if (lol < 0 || (NPIStaticPanels.StaticPanels.contains(args[2]) && lol != 0) || (!NPIManager.Panels.containsKey(args[2]) && lol != 2)){
                                NPI.Error(player, "неизвестные/запрещённые аргументы", true);
                                return true;
                            }
                            switch (lol){
                                case 0:
                                    NPIManager.OpenPanel(player, args[2]);
                                    break;
                                case 1:
                                    if (args.length < 4){
                                        NPI.Error(player, "в удалении отклонено", true);
                                        return true;
                                    }
                                    NPIManager.Panels.remove(args[2]);
                                    break;
                                case 2:
                                    NPIManager.Panels.put(args[2], new NPIPanel("Unnamed"));
                                    break;
                                case 3:
                                    NPIManager.OpenPanel(player, args[2], true);
                                    break;
                                case 4:
                                    if (args.length < 5){
                                        NPI.Error(player, "мало аргументов", true);
                                        return true;
                                    }
                                    var f = args[3].toLowerCase();    // Subfunction
                                    var c = args[4];    // Change To ...

                                    if (f.equals("id")){
                                        var getted = NPIManager.Panels.get(args[1]);
                                        NPIManager.Panels.remove(args[1]);
                                        NPIManager.Panels.put(c.toLowerCase(), getted);
                                        return true;
                                    }
                                    if (f.equals("title")){
                                        NPIManager.Panels.get(args[1]).name = c;
                                        return true;
                                    }
                                    if (f.startsWith("name") || f.startsWith("text")){
                                        try {
                                            var i = Integer.parseInt(f.replace("lore", "").replace("name", ""));
                                            if (NPIManager.Panels.get(args[1]).buttons.containsKey(i)){
                                                var slot = NPIManager.Panels.get(args[1]).buttons.get(i);
                                                if (f.startsWith("lore")){
                                                    slot.lore = c;
                                                }
                                                else{
                                                    slot.name = c;
                                                }
                                                NPIManager.Panels.get(args[1]).buttons.put(i, slot);
                                                return true;
                                            }
                                        } catch (NumberFormatException e) {
                                            NPI.Error(player, "неверный формат слота", true);
                                            return true;
                                        }
                                        return true;
                                    }
                                    if (f.startsWith("data")){
                                        try {
                                            var i = Integer.parseInt(f.replace("data", ""));
                                            if (NPIManager.Panels.get(args[1]).buttons.containsKey(i)){
                                                var slot = NPIManager.Panels.get(args[1]).buttons.get(i);
                                                slot.customModelData = Integer.parseInt(c);
                                                NPIManager.Panels.get(args[1]).buttons.put(i, slot);
                                                return true;
                                            }
                                        } catch (NumberFormatException e) {
                                            NPI.Error(player, "неверный формат слота", true);
                                            return true;
                                        }
                                        return true;
                                    }
                                    if (f.startsWith("action")){
                                        try {
                                            var i = Integer.parseInt(f.replace("action", ""));
                                            if (NPIManager.Panels.get(args[1]).buttons.containsKey(i)){
                                                var slot = NPIManager.Panels.get(args[1]).buttons.get(i);
                                                slot.action = NPIButton.NPIButtonAction.valueOf(c.toUpperCase());
                                                NPIManager.Panels.get(args[1]).buttons.put(i, slot);
                                                return true;
                                            }
                                        } catch (NumberFormatException e) {
                                            NPI.Error(player, "неверный формат слота", true);
                                            return true;
                                        } catch (Exception e) {
                                            NPI.Error(player, "неверный формат действия", true);
                                            return true;
                                        }
                                        return true;
                                    }
                                    if (f.startsWith("subaction")){
                                        try {
                                            var i = Integer.parseInt(f.replace("subaction", ""));
                                            if (NPIManager.Panels.get(args[1]).buttons.containsKey(i)){
                                                var slot = NPIManager.Panels.get(args[1]).buttons.get(i);
                                                slot.action2 = c;
                                                NPIManager.Panels.get(args[1]).buttons.put(i, slot);
                                                return true;
                                            }
                                        } catch (NumberFormatException e) {
                                            NPI.Error(player, "неверный формат слота", true);
                                            return true;
                                        }
                                        return true;
                                    }
                                    break;
                                case 5:
                                    NPIStorage.Instance.Save();
                                case 6:
                                    NPIStorage.Instance.Load();
                            }
                            break;
                    }
                    return true;
                }
            }
            if (!Pages.contains(args[0])){
                return false;
            }
            switch (Pages.indexOf(args[0])){
                case 1:
                    NPIManager.OpenPanel(player, "added");
                    break;
                default:
                    SendPlayerInfo(player);
                    break;
            }
            return true;
        }
        sender.sendMessage("Зайди на серв сначала!");
        return true;
    }
    public static final List<String> Pages = List.of("info", "added");
    public static final List<String> PagesAdmin = List.of("admin", "resourcepack-restart", "editor", "info", "added");
    public static final List<String> EditorArgs = List.of("open", "remove", "create", "edit", "change", "save-cfg", "reload-cfg");

    public static void SendPlayerInfo(Player player){
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
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        var isAdmin = sender.isOp() || sender.getName().equals("Drovyng");
        if (args.length == 1){
            if (isAdmin){
                return PagesAdmin;
            }
            return Pages;
        }
        if (args.length == 2 && isAdmin && PagesAdmin.indexOf(args[0].toLowerCase()) == 1){
            return List.of("10", "20", "30", "40", "50", "60");
        }
        if (args.length >= 2 && isAdmin && PagesAdmin.indexOf(args[0].toLowerCase()) == 2){
            if (EditorArgs.contains(args[1].toLowerCase())) {
                switch (EditorArgs.indexOf(args[1].toLowerCase())) {
                    case 0:
                    case 1:
                    case 3:
                        if (args.length == 2) {
                            return NPIManager.Panels.keySet().stream().toList();
                        }
                        return List.of();
                    case 4:
                        if (args.length == 3) {
                            return NPIManager.Panels.keySet().stream().toList();
                        }
                        if (args.length == 4) {
                            return List.of("data*", "name*", "lore*", "title", "id", "action*", "subaction*");
                        }
                        var f = args[3].toLowerCase();    // Subfunction
                        if (f.startsWith("action")){
                            return List.of("NONE", "OPEN_PANEL");
                        }
                        break;
                }
            }
            if (args.length == 2){
                return EditorArgs;
            }
            return List.of();
        }
        return List.of();
    }
}
