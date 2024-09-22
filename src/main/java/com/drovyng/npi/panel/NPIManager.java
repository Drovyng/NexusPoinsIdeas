package com.drovyng.npi.panel;

import com.drovyng.npi.NPI;
import net.coreprotect.utility.Color;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NPIManager implements Listener {
    public static List<Player> Players = new ArrayList<>(5);
    public static Map<String, NPIPanel> Panels = new HashMap<>(5);
    public static final String Title = Color.DARK_GREY + Color.BOLD + "NPI - ";

    public static void ParseLine(String line){
        var args = line.split(",");
        var panel = new NPIPanel(args[1]);
        System.out.println("ПОХУЙ!!! ЗАБЕЙ НА ЭТО!!!");
        for (int i = 2; i < args.length; i++) {
            try {
                var btn = args[i].split(";");
                var slot = Integer.parseInt(btn[0]);
                var name = btn[2];
                var lore = btn[3].replace("\\n", "\n");
                var action = NPIButton.NPIButtonAction.valueOf(btn[4]);
                var action2 = btn[5];
                var customModelData = Integer.parseInt(btn[6]);
                List<Material> materials = new ArrayList<>(1);

                for (int j = 7; j < btn.length; j++) {
                    materials.add(Material.getMaterial(btn[j]));
                }
                panel.buttons.put(slot, new NPIButton(materials, name, lore, action, action2, customModelData));
            }
            catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
        System.out.println("ПОХУЙ!!! ЗАБЕЙ НА ЭТО!!!");
        Panels.put(args[0], panel);
    }
    public static String SaveString(){
        var save = "";
        for (var paneId : Panels.keySet()){
            if (NPIStaticPanels.StaticPanels.contains(paneId)){
                continue;
            }
            var panel = Panels.get(paneId);

            save += "\n";
            save += paneId + ",";
            save += panel.name + ",";
            var saveButtons = "";
            for (var slot : panel.buttons.keySet()){
                var btn = panel.buttons.get(slot);
                saveButtons += ",";
                saveButtons += slot + ";";
                saveButtons += btn.name + ";";
                saveButtons += btn.lore.replace("\n", "\\n").replace(",", "").replace(";", "") + ";";
                saveButtons += btn.action.toString() + ";";
                saveButtons += btn.action2 + ";";
                saveButtons += btn.customModelData;
                for (var mat : btn.item){
                    saveButtons += ";" + mat.toString();
                }
            }
            if (saveButtons.length() > 0)
                save += saveButtons.substring(1);
        }
        return save.substring(1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPanelClick(InventoryClickEvent event){
        if (event.getWhoClicked() instanceof Player player) {

            if (!Players.contains(player)){
                return;
            }

            var slot = event.getSlot();
            var panel = Panels.get(player.getMetadata("NPIOpenedPanel").getFirst().asString());


            if (player.getOpenInventory().getTopInventory().getSize() < 30){
                event.setCancelled(true);
                if (panel.buttons.containsKey(slot)){
                    var btn = panel.buttons.get(event.getSlot());
                    switch (btn.action){
                        case OPEN_PANEL:
                            OpenPanel(player, btn.action2);
                            break;
                        default:
                            break;
                    }
                }
            }
            else {
                var type = event.getCursor().getType();
                if (slot > 26) return;
                event.setCancelled(true);

                if (event.isRightClick()) {
                    if (type.equals(Material.AIR)){
                        panel.buttons.remove(slot);
                    }
                    else if (panel.buttons.containsKey(slot)){
                        var btn = panel.buttons.get(slot);
                        btn.item.add(type);
                        panel.buttons.put(slot, btn);
                    }
                    else {
                        panel.buttons.put(slot, new NPIButton(List.of(type), "Unnamed", "", NPIButton.NPIButtonAction.NONE, "", 0));
                    }
                }
                else if (event.isLeftClick() && panel.buttons.containsKey(slot)){
                    var mats = "";

                    for (var mat : panel.buttons.get(slot).item){
                        mats += ", " + mat.toString();
                    }
                    if (mats.length() < 2){
                        mats = ", ";
                    }
                    NPI.Send(player, "Материалы предмета: ["+mats.substring(2)+"]");
                }
            }
        }
    }
    public static void OpenPanel(Player player, String id){
        OpenPanel(player, id, false);
    }
    public static void OpenPanel(Player player, String id, Boolean isEditor){
        if (!Panels.containsKey(id)){
            NPI.Error(player, "Невозможно открыть панель \"" + id + "\"");
            return;
        }
        player.setMetadata("NPIOpenedPanel", new FixedMetadataValue(NPI.Instance, id));
        Players.add(player);

        Panels.get(id).Update(player.openInventory(Bukkit.createInventory(player, 27 * (isEditor ? 2 : 1), Title + Panels.get(id).name)));
    }
    public static void UpdatePanels(){
        NPIPanel.Updates++;
        if (NPIPanel.Updates >= 1000000000){
            NPIPanel.Updates = 0;
        }
        for (int i = 0; i < Players.size(); i++){
            var player = Players.get(i);
            var inv = player.getOpenInventory();

            if (!player.isConnected() || !inv.getTitle().startsWith(Title)) {
                Players.remove(player);
                i--;
                continue;
            }
            Panels.get(player.getMetadata("NPIOpenedPanel").getFirst().asString()).Update(inv);
        }
    }
}
