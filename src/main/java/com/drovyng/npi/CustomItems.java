package com.drovyng.npi;

import com.drovyng.npi.panel.NPIManager;
import com.drovyng.npi.panel.NPIStaticPanels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;

public final class CustomItems {
    public static void register(){

        Recipe_InspectBrush();

        for (var mat : Material.values()) {
            if (mat.toString().toLowerCase().contains("template")){
                Recipe_TemplateLaminate(mat);
            }
            if (mat.toString().startsWith("STRIPPED_")){
                Recipe_WoodAxe(Material.getMaterial(mat.toString().replace("STRIPPED_","")), mat);
            }
        }
    }
    //
    // Recipes
    //
    public static ItemStack Recipe_InspectBrush_Item;
    private static void Recipe_InspectBrush(){
        Recipe_InspectBrush_Item = new ItemStack(Material.BRUSH);

        // Meta

        var meta = (Damageable)Recipe_InspectBrush_Item.getItemMeta();

        meta.setMaxDamage(48);
        meta.setCustomModelData(100);
        meta.itemName(Component.text("Экспертизная кисть", Style.style(TextDecoration.ITALIC.withState(false))));
        meta.lore(Other_InspectBrushLore(1));

        Recipe_InspectBrush_Item.setItemMeta(meta);

        // Recipe

        ShapedRecipe recipe = new ShapedRecipe(Other_CraftKey("InspectBrush"), Recipe_InspectBrush_Item);
        recipe.shape(
                " ff",
                " bf",
                "d  "
        );
        recipe.setIngredient('f', Material.FEATHER);
        recipe.setIngredient('b', Material.BRUSH);
        recipe.setIngredient('d', Material.DIAMOND);

        Bukkit.addRecipe(recipe);
    }
    public static List<Material> Recipe_TemplateLaminate_Item = new ArrayList<>(2);
    private static void Recipe_TemplateLaminate(Material template){
        Recipe_TemplateLaminate_Item.add(template);
        ItemStack result = new ItemStack(template);
        var meta = result.getItemMeta();
        meta.setCustomModelData(27);
        meta.lore(List.of(
                Component.text("Ламинировано", Style.style(TextColor.color(0x00FFFF), TextDecoration.ITALIC.withState(false)))
        ));
        result.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(Other_CraftKey("LaminatedTemplate" + template.name()), result);
        recipe.shape(
                " g ",
                "gtg",
                " g "
        );
        recipe.setIngredient('t', template);
        recipe.setIngredient('g', Material.GLASS_PANE);

        if (NPIStaticPanels.ShouldStatic.contains("added")){
            var panel = NPIManager.Panels.get("added");
            var btn1 = panel.buttons.get(10);
            btn1.item.add(template);
            NPIManager.Panels.get("added").buttons.put(10, btn1);
        }
        if (NPIStaticPanels.ShouldStatic.contains("craft-2")){
            var panel = NPIManager.Panels.get("craft-2");
            var btn1 = panel.buttons.get(13);
            btn1.item.add(template);
            NPIManager.Panels.get("craft-2").buttons.put(13, btn1);

            var btn2 = panel.buttons.get(16);
            btn2.item.add(template);
            NPIManager.Panels.put("craft-2", panel);
        }

        Bukkit.addRecipe(recipe);
    }
    public static final List<Material> Axes = List.of(
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLDEN_AXE,
            Material.DIAMOND_AXE,
            Material.NETHERITE_AXE
    );
    public static List<Material> Woods = new ArrayList<>(10);
    public static List<Material> WoodsStripped = new ArrayList<>(10);
    private static void Recipe_WoodAxe(Material source, Material result){

        // Recipe

        Woods.add(source);
        WoodsStripped.add(result);

        ShapelessRecipe recipe = new ShapelessRecipe(Other_CraftKey("WoodAxe" + source.toString().toLowerCase()), new ItemStack(result));
        recipe.addIngredient(source);
        recipe.addIngredient(new RecipeChoice.MaterialChoice(Axes));

        Bukkit.addRecipe(recipe);
    }
    //
    // Other
    //
    private static NamespacedKey Other_CraftKey(String key){
        return new NamespacedKey(NPI.Instance, key);
    }
    public static List<Component> Other_InspectBrushLore(int limit){
        return List.of(
                Component.textOfChildren(
                        Component.text(
                                "Лимит: ", Style.style(
                                        TextColor.color(1f, 1f, 0f),
                                        TextDecoration.ITALIC.withState(false)
                                )
                        ).append(
                                Component.text(
                                        "" + limit,
                                        Style.style(
                                                TextColor.color(0f, 1f, 0f),
                                                TextDecoration.ITALIC.withState(false)
                                        )
                                )
                        )
                )
        );
    }
}
