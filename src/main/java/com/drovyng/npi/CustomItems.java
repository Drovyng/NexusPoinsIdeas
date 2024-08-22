package com.drovyng.npi;

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
            if (mat.name().toLowerCase().contains("template")){
                Recipe_TemplateLaminate(mat);
            }
        }
        {
            Recipe_WoodAxe(Material.OAK_LOG, Material.STRIPPED_OAK_LOG);
            Recipe_WoodAxe(Material.SPRUCE_LOG, Material.STRIPPED_SPRUCE_LOG);
            Recipe_WoodAxe(Material.BIRCH_LOG, Material.STRIPPED_BIRCH_LOG);
            Recipe_WoodAxe(Material.DARK_OAK_LOG, Material.STRIPPED_DARK_OAK_LOG);
            Recipe_WoodAxe(Material.ACACIA_LOG, Material.STRIPPED_ACACIA_LOG);
            Recipe_WoodAxe(Material.JUNGLE_LOG, Material.STRIPPED_JUNGLE_LOG);
            Recipe_WoodAxe(Material.MANGROVE_LOG, Material.STRIPPED_MANGROVE_LOG);
            Recipe_WoodAxe(Material.CHERRY_LOG, Material.STRIPPED_CHERRY_LOG);
            Recipe_WoodAxe(Material.BAMBOO_BLOCK, Material.STRIPPED_BAMBOO_BLOCK);
            Recipe_WoodAxe(Material.CRIMSON_STEM, Material.STRIPPED_CRIMSON_STEM);
            Recipe_WoodAxe(Material.WARPED_STEM, Material.STRIPPED_WARPED_STEM);

            Recipe_WoodAxe(Material.OAK_WOOD, Material.STRIPPED_OAK_WOOD);
            Recipe_WoodAxe(Material.SPRUCE_WOOD, Material.STRIPPED_SPRUCE_WOOD);
            Recipe_WoodAxe(Material.BIRCH_WOOD, Material.STRIPPED_BIRCH_WOOD);
            Recipe_WoodAxe(Material.DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_WOOD);
            Recipe_WoodAxe(Material.ACACIA_WOOD, Material.STRIPPED_ACACIA_WOOD);
            Recipe_WoodAxe(Material.JUNGLE_WOOD, Material.STRIPPED_JUNGLE_WOOD);
            Recipe_WoodAxe(Material.MANGROVE_WOOD, Material.STRIPPED_MANGROVE_WOOD);
            Recipe_WoodAxe(Material.CHERRY_WOOD, Material.STRIPPED_CHERRY_WOOD);
            Recipe_WoodAxe(Material.CRIMSON_HYPHAE, Material.STRIPPED_CRIMSON_HYPHAE);
            Recipe_WoodAxe(Material.WARPED_HYPHAE, Material.STRIPPED_WARPED_HYPHAE);
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

        Bukkit.addRecipe(recipe);
    }
    private static void Recipe_WoodAxe(Material source, Material result){

        // Recipe

        ShapelessRecipe recipe = new ShapelessRecipe(Other_CraftKey("WoodAxe" + source.name()), new ItemStack(result));
        recipe.addIngredient(source);
        recipe.addIngredient(new RecipeChoice.MaterialChoice(
                Material.WOODEN_AXE,
                Material.STONE_AXE,
                Material.IRON_AXE,
                Material.GOLDEN_AXE,
                Material.DIAMOND_AXE,
                Material.NETHERITE_AXE
        ));

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
