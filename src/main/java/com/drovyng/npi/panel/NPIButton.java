package com.drovyng.npi.panel;

import org.bukkit.Material;

import java.util.List;


public class NPIButton {
    public enum NPIButtonAction {
        NONE,
        OPEN_PANEL
    }
    public List<Material> item;
    public String lore;
    public String name;
    public int customModelData;
    public NPIButtonAction action;
    public String action2;

    public NPIButton(List<Material> item, String name, String lore, NPIButtonAction action, String action2, int customModelData){
        this.item = item;
        this.name = name;
        this.lore = lore;
        this.customModelData = customModelData;
        this.action = action;
        this.action2 = action2;
    }
}
