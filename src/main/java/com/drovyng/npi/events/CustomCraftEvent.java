package com.drovyng.npi.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

public class CustomCraftEvent  implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private void onCraftAxeWood(CraftItemEvent evt) {
        if (!evt.getRecipe().getResult().getType().name().toLowerCase().contains("stripped"))
            return;
        CraftingInventory ci = evt.getInventory();
        var matrix = ci.getMatrix();
        int count = 0;
        for (ItemStack item : matrix) {
            if (item != null && !item.getType().name().toLowerCase().contains("axe")) {
                count += item.getAmount();
            }
        }
        for (ItemStack item : matrix) {
            if (item != null && item.getType().name().toLowerCase().contains("axe")) {

                var meta = (Damageable) item.getItemMeta();
                var maxDamage = meta.hasMaxDamage() ? meta.getMaxDamage() : item.getType().getMaxDurability();

                if (!meta.hasDamage()) meta.setDamage(0);

                if (evt.isShiftClick()){
                    count = Math.min(count, maxDamage - meta.getDamage() - 1);
                    meta.setDamage(meta.getDamage() + count);
                    item.setItemMeta(meta);
                    item.setAmount(count + 1);
                    break;
                }
                meta.setDamage(meta.getDamage() + 1); // Set the damage
                item.setItemMeta(meta); // Set the meta back to the itemstack
                item.setAmount(maxDamage - meta.getDamage() > 0 ? 2 : 1); // Чтобы не исчез

                break;
            }
        }
        ci.setMatrix(matrix);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    private void onCraftPrepareLaminated(PrepareItemCraftEvent evt) {
        CraftingInventory ci = evt.getInventory();
        var matrix = ci.getMatrix();
        for (ItemStack item : matrix) {
            if (item != null && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == 27) {
                ci.setResult(null);
                return;
            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    private void onCraftLaminated(CraftItemEvent evt) {
        var result = evt.getRecipe().getResult();
        if (result.hasItemMeta() && result.getItemMeta().hasCustomModelData() && result.getItemMeta().getCustomModelData() == 27){
            var meta = result.getItemMeta();
            meta.lore(List.of(
                    Component.text("Ламинировано (" + evt.getWhoClicked().getName() + ")", Style.style(TextColor.color(0x00FFFF), TextDecoration.ITALIC.withState(false)))
            ));
            result.setItemMeta(meta);
            evt.getInventory().setResult(result);
        }
    }
}
