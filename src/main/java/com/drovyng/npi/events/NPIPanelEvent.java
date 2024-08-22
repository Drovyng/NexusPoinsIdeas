package com.drovyng.npi.events;

import com.drovyng.npi.CustomItems;
import com.drovyng.npi.NPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.coreprotect.utility.Color;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class NPIPanelEvent implements Listener {
    public static final String Title = Color.DARK_GREY + Color.BOLD + "NPI - ";
    public static final List<String> Categories = List.of("Меню", "Добавления", "Предпросмотр ");
    public static List<Player> Players = new ArrayList<>(2);

    @EventHandler(priority = EventPriority.MONITOR)
    private void command(AsyncChatEvent event){
        if (!event.message().toString().toLowerCase().contains("npi-panel")) {
            return;
        }
        final Player player = event.getPlayer();
        if (!player.getName().equals("Drovyng")) {
            return;
        }
        event.setCancelled(true);
        Bukkit.getScheduler().runTask(NPI.Instance, () -> {
            OpenPage0(player);
            Players.add(player);
        });
    }
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPanelClick(InventoryClickEvent event){
        var inv = event.getWhoClicked().getOpenInventory();
        if (inv == null || !inv.getTitle().startsWith(Title)){
            return;
        }
        var pageName = inv.getTitle().replace(Title, "");

        var curPage = Categories.indexOf(pageName);
        if (pageName.contains(Categories.get(2))){
            curPage = 2;
        }
        var slot = event.getSlot();

        event.setCancelled(true);

        var plr = (Player)event.getWhoClicked();


        switch (curPage){
            case 1:
                if (slot == 9){
                    OpenPage2(plr, 0);
                }
                if (slot == 10){
                    OpenPage2(plr, 1);
                }
            case 2:
                if (slot == 18){
                    OpenPage1(plr);
                }
        }
    }

    private static Inventory NewInv(Player player, int page) {
        return NewInv(player, page, "");
    }
    private static Inventory NewInv(Player player, int page, String add){
        return Bukkit.createInventory(player, 27, Title + Categories.get(page) + add);
    }
    private static int Slot(int x, int y){
        return x + y * 9;
    }
    private static ItemStack Button(String name, Material material){
        return Button(name, material, 0);
    }
    private static ItemStack Button(String name, Material material, int customModelData){
        var item = new ItemStack(material);
        var meta = item.getItemMeta();
        meta.displayName(Component.text(name, Style.style(TextDecoration.ITALIC.withState(false), TextColor.color(0x88FF88))));
        if (customModelData != 0) meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
        return item;
    }

    public static void OpenPage0(Player player){
        var inv = NewInv(player, 0);
        player.openInventory(inv);
    }
    public static void OpenPage1(Player player){
        var inv = NewInv(player, 1);
        player.openInventory(inv);
        UpdatePage1(player.getOpenInventory());
    }
    private static final List<Integer> CraftPageBackPanels = List.of(0, 1, 2, 6, 7, 8, 9, 10, 11, 15, 17, 19, 20, 24, 25, 26);
    private static final ItemStack CraftPageBackPanel = Button("", Material.WHITE_STAINED_GLASS_PANE, 10);
    public static void OpenPage2(Player player, int subPage){
        var inv = NewInv(player, 2, "  " + subPage);

        for (var i : CraftPageBackPanels){
            inv.setItem(i, CraftPageBackPanel);
        }
        inv.setItem(18, Button("Назад", Material.RED_DYE, 10));

        player.openInventory(inv);
        UpdatePage2(player.getOpenInventory(), "  " + subPage);
    }
    public static int UpdateCount = 0;
    public static void UpdatePanels() {
        UpdateCount++;
        if (UpdateCount >= 2000000000){
            UpdateCount = 0;
        }
        for (int i = 0; i < Players.size(); i++) {
            var player = Players.get(i);
            var inv = player.getOpenInventory();
            if (inv == null || !inv.getTitle().startsWith(Title)){
                Players.remove(player);
                i--;
                continue;
            }
            var paneName = inv.getTitle();

            if (paneName.contains(Categories.get(1))){
                UpdatePage1(inv);
            }
            else if (paneName.contains(Categories.get(2))){
                UpdatePage2(inv, paneName.substring(paneName.length()-3));
            }
        }
    }
    public static void UpdatePage1(InventoryView inv){
        var brush = new ItemStack(Material.BRUSH);
        {
            var meta = brush.getItemMeta();
            meta.setCustomModelData(100);
            meta.lore(ItemLore("клик для просмотра крафта", "Позволяет на ЛКМ/ПКМ просмотреть\nсписок изменений блока с\nуказанным вами лимитом."));
            meta.displayName(Component.text("Экспертизная кисть", Style.style(TextDecoration.ITALIC.withState(false))));
            brush.setItemMeta(meta);
        }
        var template = new ItemStack(CustomItems.Recipe_TemplateLaminate_Item.get(UpdateCount % CustomItems.Recipe_TemplateLaminate_Item.size()));
        {
            var meta = template.getItemMeta();
            meta.setCustomModelData(100);
            meta.lore(ItemLore("клик для просмотра крафта", "Ламинированные шаблоны нельзя\nдублировать. Будет полезно\nдля магазинов."));
            meta.displayName(Component.text("Ламинирование шаблонов", Style.style(TextDecoration.ITALIC.withState(false))));
            template.setItemMeta(meta);
        }
        var climb = new ItemStack(UpdateCount % 2 == 0 ? Material.LADDER : Material.SCAFFOLDING);
        {
            var meta = climb.getItemMeta();
            meta.setCustomModelData(100);
            meta.lore(ItemLore(null, "Быстрый подъём по\nлестнице/подмосткам, если\nсмотреть вверх.\nСлегка уменьшает голод."));
            meta.displayName(Component.text("Быстрый подъём", Style.style(TextDecoration.ITALIC.withState(false))));
            climb.setItemMeta(meta);
        }

        inv.setItem(Slot(0, 1), brush);
        inv.setItem(Slot(1, 1), template);
        inv.setItem(Slot(2, 1), climb);

    }
    public static void UpdatePage2(InventoryView inv, String number){
        var i = Integer.parseInt(number.replace(" ", ""));
        switch (i){
            case 0:
                var f = new ItemStack(Material.FEATHER);
                inv.setItem(Slot(4, 0), f);
                inv.setItem(Slot(5, 0), f);
                inv.setItem(Slot(5, 1), f);
                inv.setItem(Slot(4, 1), new ItemStack(Material.BRUSH));
                inv.setItem(Slot(7, 1), CustomItems.Recipe_InspectBrush_Item);
                inv.setItem(Slot(3, 2), new ItemStack(Material.DIAMOND));
                break;
            case 1:
                var template = CustomItems.Recipe_TemplateLaminate_Item.get(UpdateCount % CustomItems.Recipe_TemplateLaminate_Item.size());

                var g = new ItemStack(Material.GLASS_PANE);
                inv.setItem(Slot(4, 0), g);
                inv.setItem(Slot(5, 1), g);
                inv.setItem(Slot(4, 2), g);
                inv.setItem(Slot(3, 1), g);
                inv.setItem(Slot(4, 1), new ItemStack(template));

                var fuck = new ItemStack(template);
                var meta = fuck.getItemMeta();
                meta.lore(List.of(
                        Component.text("Ламинировано", Style.style(TextColor.color(0x00FFFF), TextDecoration.ITALIC.withState(false)))
                ));
                fuck.setItemMeta(meta);
                inv.setItem(Slot(7, 1), fuck);
                break;
        }
    }
    private static List<Component> ItemLore(String prefix, String yourLore){
        var splitted = yourLore.split("\n");
        List<Component> result = new ArrayList<>(splitted.length);
        if (prefix != null && !prefix.isEmpty()) result.add(Component.text(Color.AQUA + "*" + prefix + "*", Style.style(TextDecoration.ITALIC)));
        for (var str : splitted){
            result.add(Component.text(str, Style.style(TextDecoration.ITALIC.withState(false), TextColor.fromHexString("#D8D8D8"))));
        }
        return result;
    }
}
