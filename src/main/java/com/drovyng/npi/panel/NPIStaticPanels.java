package com.drovyng.npi.panel;

import net.coreprotect.utility.Color;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class NPIStaticPanels {
    public static final List<String> StaticPanels = List.of("admin");
    public static List<String> ShouldStatic = new ArrayList<>(2);
    public static void CreateStaticPanels(){
        {
            var panel = new NPIPanel("Амдинистрирование");

            panel.buttons.put(
                    13,
                    new NPIButton(List.of(Material.CRAFTING_TABLE), "Редактор панелей", "/npi editor [open/edit/create/remove] [id] [*edit-arg0] [*edit-arg1]", NPIButton.NPIButtonAction.NONE, "", 0)
            );

            NPIManager.Panels.put("admin", panel);
        }
        if (!NPIManager.Panels.containsKey("added")){
            var panel = new NPIPanel("Добавления");
            panel.buttons.put(
                    9,
                    new NPIButton(List.of(Material.BRUSH), "Экспертизная кисть", "*клик для просмотра крафта*\nпозволяет посмотреть историю\nизменений блока с\nуказанным лимитом", NPIButton.NPIButtonAction.OPEN_PANEL, "craft-1", 100)
            );
            panel.buttons.put(
                    10,
                    new NPIButton(new ArrayList<>(5), "Ламинирование шаблонов", "*клик для просмотра крафта*\nламинированные шаблоны\nнельзя дублировать", NPIButton.NPIButtonAction.OPEN_PANEL, "craft-2", 27)
            );
            panel.buttons.put(
                    11,
                    new NPIButton(List.of(Material.LADDER, Material.SCAFFOLDING), "Быстрый подъём", "При подъёме по чему-либо\nпосмотрите вверх, и вы\nускоритесь. Также слегка\nускорится потребление голода.", NPIButton.NPIButtonAction.NONE, "", 0)
            );
            NPIManager.Panels.put("added", panel);
            ShouldStatic.add("added");
        }
        if (!NPIManager.Panels.containsKey("craft-example")){
            var p = new NPIButton(List.of(Material.WHITE_STAINED_GLASS_PANE), "", "null", NPIButton.NPIButtonAction.NONE, "", 0);
            var panel = new NPIPanel("Крафт");
            panel.buttons.put(0, p.Clone());
            panel.buttons.put(1, p.Clone());
            panel.buttons.put(2, p.Clone());
            panel.buttons.put(6, p.Clone());
            panel.buttons.put(7, p.Clone());
            panel.buttons.put(8, p.Clone());
            panel.buttons.put(9, p.Clone());
            panel.buttons.put(10, p.Clone());
            panel.buttons.put(11, p.Clone());
            panel.buttons.put(15, p.Clone());
            panel.buttons.put(17, p.Clone());
            panel.buttons.put(18, new NPIButton(List.of(Material.RED_DYE), "Назад", "", NPIButton.NPIButtonAction.OPEN_PANEL, "added", 17));
            panel.buttons.put(19, p.Clone());
            panel.buttons.put(20, p.Clone());
            panel.buttons.put(24, p.Clone());
            panel.buttons.put(25, p.Clone());
            panel.buttons.put(26, p.Clone());
            NPIManager.Panels.put("craft-example", panel);
        }
        if (!NPIManager.Panels.containsKey("craft-1")){
            var panel = NPIManager.Panels.get("craft-example").Clone("Крафт 1");
            var f = new NPIButton(List.of(Material.FEATHER), "null", "null", NPIButton.NPIButtonAction.NONE, "", 0);
            panel.buttons.put(4, f);
            panel.buttons.put(5, f);
            panel.buttons.put(14, f);
            panel.buttons.put(21, new NPIButton(List.of(Material.DIAMOND), "null", "null", NPIButton.NPIButtonAction.NONE, "", 0));

            panel.buttons.put(13, new NPIButton(List.of(Material.BRUSH), "null", "null", NPIButton.NPIButtonAction.NONE, "", 0));
            panel.buttons.put(16, new NPIButton(List.of(Material.BRUSH), "Экспертизная кисть", Color.YELLOW + "Лимит: " + Color.GREEN + "1", NPIButton.NPIButtonAction.NONE, "", 100));

            NPIManager.Panels.put("craft-1", panel);
        }
        if (!NPIManager.Panels.containsKey("craft-2")){
            var panel = NPIManager.Panels.get("craft-example").Clone("Крафт 2");
            var p = new NPIButton(List.of(Material.GLASS_PANE), "null", "null", NPIButton.NPIButtonAction.NONE, "", 0);
            panel.buttons.put(12, p.Clone());
            panel.buttons.put(14, p.Clone());
            panel.buttons.put(4, p.Clone());
            panel.buttons.put(22, p.Clone());
            panel.buttons.put(13, new NPIButton(new ArrayList<>(5), "null", "null", NPIButton.NPIButtonAction.NONE, "", 0));
            panel.buttons.put(16, new NPIButton(new ArrayList<>(5), "null", Color.AQUA + "Ламинировано", NPIButton.NPIButtonAction.NONE, "", 27));
            NPIManager.Panels.put("craft-2", panel);
            ShouldStatic.add("craft-2");
        }
    }
}
