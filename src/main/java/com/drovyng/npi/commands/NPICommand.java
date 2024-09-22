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
                                    plr.showTitle(Title.title(Component.text(Color.GOLD+"Перезагрузка текстур: "+Color.GREEN+Color.BOLD+time+Color.GREEN+" сек"), Component.text(Color.RED + "Не выходите с сервера!")));
                                    player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                                }
                                if (time > 10){
                                    Bukkit.getScheduler().runTaskLater(NPI.Instance, () ->
                                    {
                                        for (var plr : Bukkit.getServer().getOnlinePlayers()) {
                                            plr.showTitle(Title.title(Component.text(Color.GOLD+"Перезагрузка текстур: "+Color.GREEN+Color.BOLD+"10"+Color.GREEN+" сек"), Component.text(Color.RED + "Не выходите с сервера!")));
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
                                NPI.Error(player, "Невозможно преобразовать указанное время в число", true);
                            }
                            break;
                        case 2:
                            if (args.length > 1 && args[1].equalsIgnoreCase("help")){
                                var p = "";
                                if (args.length > 2 && EditorArgs.contains(args[2].toLowerCase()) && !args[2].equalsIgnoreCase("help")){
                                    p = " " + args[2].toLowerCase();
                                }
                                NPI.Send(player, "----Editor /help" + p + "----");
                                if ("change".contains(p)){
                                    NPI.Send(player, "/npi editor change [id] [param] [value]");
                                }
                                if ("clone".contains(p)){
                                    NPI.Send(player, "/npi editor clone [id] [new-id]");
                                }
                                if ("create".contains(p)){
                                    NPI.Send(player, "/npi editor create [new-id]");
                                }
                                if ("edit".contains(p)){
                                    NPI.Send(player, "/npi editor edit [id]");
                                }
                                if ("help".contains(p)){
                                    NPI.Send(player, "/npi editor help [*command]");
                                }
                                if ("open".contains(p)){
                                    NPI.Send(player, "/npi editor open [id]");
                                }
                                if ("remove".contains(p)){
                                    NPI.Send(player, "/npi editor remove [id]");
                                }
                                return true;
                            }
                            if (args.length < 3){
                                NPI.Error(player, "мало аргументов", true);
                                return true;
                            }
                            args[1] = args[1].toLowerCase();        // Function
                            args[2] = args[2].toLowerCase();        // Id
                            var lol = EditorArgs.indexOf(args[1]);
                            if (lol < 0 || (NPIStaticPanels.StaticPanels.contains(args[2]) && lol != 0) || (!NPIManager.Panels.containsKey(args[2]) && lol != 2)){
                                NPI.Error(player, "Неизвестные/запрещённые аргументы", true);
                                return true;
                            }
                            switch (lol){
                                case 0:
                                    NPIManager.OpenPanel(player, args[2]);
                                    NPI.Send(player, "Открыта панель \"" + args[2] + "\"");
                                    break;
                                case 1:
                                    if (args.length < 4){
                                        NPI.Error(player, "В удалении отклонено", true);
                                        return true;
                                    }
                                    NPIManager.Panels.remove(args[2]);
                                    NPI.Send(player, "Удалена панель \"" + args[2] + "\"");
                                    break;
                                case 2:
                                    NPIManager.Panels.put(args[2], new NPIPanel("Unnamed"));
                                    NPI.Send(player, "Создана панель \"" + args[2] + "\"");
                                    break;
                                case 3:
                                    NPIManager.OpenPanel(player, args[2], true);
                                    NPI.Send(player, "Открыт редактор панели \"" + args[2] + "\"");
                                    break;
                                case 4:
                                    if (args.length < 5){
                                        NPI.Error(player, "Мало аргументов", true);
                                        return true;
                                    }
                                    var f = args[3].toLowerCase();    // Subfunction
                                    var c = args[4];    // Change To ...
                                    var c2 = "";
                                    for (int i = 4; i < args.length; i++) {
                                        c2 += " " + args[i];
                                    }
                                    c2 = c2.substring(1);

                                    if (f.equals("id")){
                                        var getted = NPIManager.Panels.get(args[1]);
                                        NPIManager.Panels.remove(args[1]);
                                        NPIManager.Panels.put(c.toLowerCase(), getted);
                                        NPI.Send(player, "У панели \"" + args[2] + "\" изменён идентификатор на \"" + c + "\"");
                                        return true;
                                    }
                                    if (f.equals("title")){
                                        NPIManager.Panels.get(args[2]).name = c2;
                                        NPI.Send(player, "У панели \"" + args[2] + "\" изменено название на  \"" + c2 + "\"");
                                        return true;
                                    }
                                    if (f.startsWith("name") || f.startsWith("text")){
                                        try {
                                            var i = Integer.parseInt(f.replace("lore", "").replace("name", ""));
                                            if (NPIManager.Panels.get(args[2]).buttons.containsKey(i)){
                                                var slot = NPIManager.Panels.get(args[2]).buttons.get(i);
                                                if (f.startsWith("lore")){
                                                    slot.lore = c2;
                                                    NPI.Send(player, "У предмета панели \"" + args[2] + "\" в слоте " + i + " изменён лор на  \"" + c2 + "\"");
                                                }
                                                else{
                                                    slot.name = c2;
                                                    NPI.Send(player, "У предмета панели \"" + args[2] + "\" в слоте " + i + " изменено название на  \"" + c2 + "\"");
                                                }
                                                NPIManager.Panels.get(args[2]).buttons.put(i, slot);
                                                return true;
                                            }
                                        } catch (NumberFormatException e) {
                                            NPI.Error(player, "Неверный формат слота", true);
                                            return true;
                                        }
                                        return true;
                                    }
                                    if (f.startsWith("data")){
                                        try {
                                            var i = Integer.parseInt(f.replace("data", ""));
                                            if (NPIManager.Panels.get(args[2]).buttons.containsKey(i)){
                                                var slot = NPIManager.Panels.get(args[2]).buttons.get(i);
                                                slot.customModelData = Integer.parseInt(c);
                                                NPIManager.Panels.get(args[2]).buttons.put(i, slot);
                                                NPI.Send(player, "У предмета панели \"" + args[2] + "\" в слоте " + i + " изменена моделька на  " + slot.customModelData);
                                                return true;
                                            }
                                        } catch (NumberFormatException e) {
                                            NPI.Error(player, "Неверный формат слота", true);
                                            return true;
                                        }
                                        return true;
                                    }
                                    if (f.startsWith("action")){
                                        try {
                                            var i = Integer.parseInt(f.replace("action", ""));
                                            if (NPIManager.Panels.get(args[2]).buttons.containsKey(i)){
                                                var slot = NPIManager.Panels.get(args[2]).buttons.get(i);
                                                slot.action = NPIButton.NPIButtonAction.valueOf(c.toUpperCase());
                                                NPIManager.Panels.get(args[2]).buttons.put(i, slot);
                                                NPI.Send(player, "У предмета панели \"" + args[2] + "\" в слоте " + i + " изменено действие на " + c.toUpperCase());
                                                return true;
                                            }
                                        } catch (NumberFormatException e) {
                                            NPI.Error(player, "Неверный формат слота", true);
                                            return true;
                                        } catch (Exception e) {
                                            NPI.Error(player, "Неверный формат действия", true);
                                            return true;
                                        }
                                        return true;
                                    }
                                    if (f.startsWith("subaction")){
                                        try {
                                            var i = Integer.parseInt(f.replace("subaction", ""));
                                            if (NPIManager.Panels.get(args[2]).buttons.containsKey(i)){
                                                var slot = NPIManager.Panels.get(args[2]).buttons.get(i);
                                                slot.action2 = c;
                                                NPIManager.Panels.get(args[2]).buttons.put(i, slot);
                                                NPI.Send(player, "У предмета панели \"" + args[2] + "\" в слоте " + i + " изменено поддействие на " + c);
                                                return true;
                                            }
                                        } catch (NumberFormatException e) {
                                            NPI.Error(player, "Неверный формат слота", true);
                                            return true;
                                        }
                                        return true;
                                    }
                                    NPI.Error(player, "Указанный параметр не найден", true);
                                    return false;
                                    /*
                                                   0      1     2     3
                                            /npi editor clone from create
                                     */
                                case 5:
                                    if (args.length < 4){
                                        NPI.Error(player, "Недостаточно аргументов", true);
                                        return true;
                                    }
                                    args[3] = args[3].toLowerCase();
                                    if (!NPIManager.Panels.containsKey(args[2])){
                                        NPI.Error(player, "Источника не существует", true);
                                        return true;
                                    }
                                    if (NPIManager.Panels.containsKey(args[3])){
                                        NPI.Error(player, "Идентификатор результата уже существует", true);
                                        return true;
                                    }
                                    NPI.Send(player, "Склонирована панель \"" + args[2] + "\" в новую панель \"" + args[3] + "\"");
                                    return true;
                            }
                            return true;
                        case 3:
                            NPIStorage.Instance.Save();
                            NPI.Send(player, "Конфиг сохранён");
                            break;
                        case 4:
                            NPIStorage.Instance.Load();
                            NPI.Send(player, "Конфиг перезагружен");
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
    public static final List<String> PagesAdmin = List.of("admin", "resourcepack-restart", "editor", "save-cfg", "reload-cfg", "info", "added");
    public static final List<String> EditorArgs = List.of("open", "remove", "create", "edit", "change", "clone", "help");

    public static void SendPlayerInfo(Player player){
        player.sendMessage(
                Component.text(
                        Color.RED + Color.BOLD + "Nexus" +
                                Color.YELLOW + Color.BOLD + "Poins" +
                                Color.GREEN + Color.BOLD + "Ideas " +
                                Color.RESET + "v" + NPI.Instance.getPluginMeta().getVersion() +
                                " by " + Color.AQUA + Color.BOLD + "Drovyng",
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
                    case 5:
                        if (args.length == 3) {
                            return NPIManager.Panels.keySet().stream().toList();
                        }
                        return List.of();
                    case 4:
                        if (args.length < 3) {
                            return List.of();
                        }
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
                    case 6:
                        if (args.length == 3) {
                            return EditorArgs;
                        }
                        return List.of();
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
