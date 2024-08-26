package com.drovyng.npi.panel;

import org.bukkit.Material;

import java.util.List;

public class NPIStaticPanels {
    public static final List<String> StaticPanels = List.of("admin");
    public static void CreateStaticPanels(){
        {
            var panel = new NPIPanel("Амдинистрирование");

            panel.buttons.put(
                    13,
                    new NPIButton(List.of(Material.CRAFTING_TABLE), "Редактор панелей", "/npi editor [open/edit/create/remove] [id] [*edit-arg0] [*edit-arg1]", NPIButton.NPIButtonAction.NONE, "", 0)
            );

            NPIManager.Panels.put("admin", panel);
        }
    }
}
