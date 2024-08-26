package com.drovyng.npi.events;

import com.drovyng.npi.CustomItems;
import com.drovyng.npi.NPI;
import com.drovyng.npi.TextComponentParser;
import net.coreprotect.bukkit.BukkitAdapter;
import net.coreprotect.database.Database;
import net.coreprotect.database.lookup.BlockLookup;
import net.coreprotect.database.lookup.ChestTransactionLookup;
import net.coreprotect.database.lookup.InteractionLookup;
import net.coreprotect.database.lookup.SignMessageLookup;
import net.coreprotect.language.Phrase;
import net.coreprotect.model.BlockGroup;
import net.coreprotect.utility.Chat;
import net.coreprotect.utility.Color;
import net.coreprotect.utility.Util;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.EquipmentSlot;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static net.coreprotect.listener.player.PlayerInteractListener.lastInspectorEvent;

public class InspectBrushEvent implements Listener {
    public static final int IBrushLimit = 8;

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onPlayerInspectBrushMode(InventoryClickEvent event){
        var item = event.getCurrentItem();

        if (item == null || item.getType() != Material.BRUSH || !event.isRightClick()) return;

        var meta = item.getItemMeta();

        if (meta.getCustomModelData() < 100) return;

        meta.setCustomModelData((meta.getCustomModelData() - 99) % IBrushLimit + 100);

        meta.lore(CustomItems.Other_InspectBrushLore(meta.getCustomModelData() - 99));

        item.setItemMeta(meta);
        event.setCancelled(true);
    }
    private static TextComponent change(String value) {
        return change(value, null);
    }
    private static TextComponent change(String value, String bypass){
        value = changeOld(value);
        /*
        if (value.contains("ago")) {
            var index = value.indexOf("ago") + 3;
            return Component.text(
                    value.substring(0, index).replaceAll(Color.GREY, ""),
                    Style.style(TextColor.fromHexString("#D8D8D8"))
            ).append(Component.text(value.substring(index)));
        }
        if (value.contains("(x")) {
            var index = value.indexOf("(x");
            return Component.text(value.substring(0, index)).append(
                Component.text(
                    value.substring(index).replaceAll(Color.GREY, ""),
                    Style.style(TextColor.fromHexString("#F0F0F0"))
                )
            );
        }
        */
        return TextComponentParser.getComponent(value, bypass);
    }
    private static String changeOld(String value){
        value = value.replaceAll(Color.DARK_AQUA + "CoreProtect", Color.YELLOW + "Inspect" + Color.GOLD + "Brush");

        if (value.contains("-----")){
            value = value.replaceFirst(Color.DARK_AQUA, Color.YELLOW);
            var lol = value.indexOf(" ", 8);
            if (lol != -1)
                value = value.substring(0, lol) + Color.GOLD + value.substring(lol);
        }
        value = value.replaceAll(Color.DARK_AQUA, Color.AQUA);
//        while (value.contains("<COMPONENT>")){
//            var i1 = value.indexOf("<COMPONENT>");
//            if (i1 != -1){
//                var i2 = value.indexOf("|", i1+5);
//                i2 = value.indexOf("|", i2+1);
//                if (i2 != -1) {
//                    value = value.substring(0, i1) + value.substring(i2+1);
//                }
//            }
//            value = value.replaceFirst("</COMPONENT>", "");
//        }
        Chat.sendConsoleMessage(value);
        return value;
    }
    private static boolean skip(String value){
        return value.contains(Color.WHITE + "-----\n") || value.contains("Page");
    }
    @EventHandler(priority = EventPriority.MONITOR)
    protected void onPlayerInspectByBrush(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        var item = event.getItem();

        if (event.getClickedBlock() == null || item == null || item.getType() != Material.BRUSH) return;

        var meta = item.getItemMeta();
        var metaDamagable = (Damageable) meta;


        if (!meta.hasCustomModelData() || meta.getCustomModelData() < 100) return;

        var limit = Math.min(meta.getCustomModelData() - 99, metaDamagable.getMaxDamage() - metaDamagable.getDamage());

        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            BlockState checkBlock = event.getClickedBlock().getState();
            int x = checkBlock.getX();
            int y = checkBlock.getY();
            int z = checkBlock.getZ();

            /* Check if clicking top half of double plant */
            BlockData checkBlockData = checkBlock.getBlockData();
            if (checkBlockData instanceof Bisected && !(checkBlockData instanceof Waterlogged)) {
                if (((Bisected) checkBlockData).getHalf().equals(Bisected.Half.TOP) && y > BukkitAdapter.ADAPTER.getMinHeight(world)) {
                    checkBlock = world.getBlockAt(checkBlock.getX(), checkBlock.getY() - 1, checkBlock.getZ()).getState();
                }
            }
            /* Check if clicking top half of bed */
            if (checkBlockData instanceof Bed bed) {
                if (bed.getPart().equals(org.bukkit.block.data.type.Bed.Part.HEAD)) {
                    checkBlock = event.getClickedBlock().getRelative(bed.getFacing().getOppositeFace()).getState();
                }
            }

            final BlockState blockFinal = checkBlock;
            class BasicThread implements Runnable {
                @Override
                public void run() {
                    int amount = -1;
                    try (Connection connection = Database.getConnection(true)) {
                        if (connection != null) {
                            Statement statement = connection.createStatement();

                            String resultData = BlockLookup.performLookup(null, statement, blockFinal, player, 0, 1, limit);

                            if (resultData.contains("\n")) {
                                for (String b : resultData.split("\n")) {
                                    if (amount >= limit) break;
                                    if (skip(b)) continue;
                                    player.sendMessage(change(b));
                                    amount += 1;
                                }
                            }
                            else if (resultData.length() > 0) {
                                player.sendMessage(change(resultData));
                            }

                            statement.close();

                            if (blockFinal instanceof Sign && player.getGameMode() != GameMode.CREATIVE) {
                                Thread.sleep(1500);
                                Sign sign = (Sign) blockFinal;
                                player.sendSignChange(sign.getLocation(), sign.getLines(), sign.getColor());
                            }
                            final int fAmount = amount;
                            if (amount > 0)
                                Bukkit.getScheduler().runTask(NPI.Instance, () -> player.damageItemStack(item, fAmount));
                        }
                        else {
                            player.sendMessage(Color.YELLOW + "Inspect" + Color.GOLD + "Brush " + Color.WHITE + "- " + Phrase.build(Phrase.DATABASE_BUSY));
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            Runnable runnable = new BasicThread();
            Thread thread = new Thread(runnable);
            thread.start();

            if (checkBlockData instanceof Bisected) {
                int worldMaxHeight = world.getMaxHeight();
                if (y < (worldMaxHeight - 1)) {
                    Block y1 = world.getBlockAt(x, y + 1, z);
                    player.sendBlockChange(y1.getLocation(), y1.getBlockData());
                }

                int worldMinHeight = BukkitAdapter.ADAPTER.getMinHeight(world);
                if (y > worldMinHeight) {
                    Block y2 = world.getBlockAt(x, y - 1, z);
                    player.sendBlockChange(y2.getLocation(), y2.getBlockData());
                }
            }

            Block x1 = world.getBlockAt(x + 1, y, z);
            Block x2 = world.getBlockAt(x - 1, y, z);
            Block z1 = world.getBlockAt(x, y, z + 1);
            Block z2 = world.getBlockAt(x, y, z - 1);
            player.sendBlockChange(x1.getLocation(), x1.getBlockData());
            player.sendBlockChange(x2.getLocation(), x2.getBlockData());
            player.sendBlockChange(z1.getLocation(), z1.getBlockData());
            player.sendBlockChange(z2.getLocation(), z2.getBlockData());
            event.setCancelled(true);
        }
        else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            if (block != null) {
                final Material type = block.getType();
                boolean isInteractBlock = BlockGroup.INTERACT_BLOCKS.contains(type);
                boolean isContainerBlock = BlockGroup.CONTAINERS.contains(type);
                boolean isSignBlock = BukkitAdapter.ADAPTER.isSign(type);

                if (isInteractBlock || isContainerBlock || isSignBlock) {
                    final Block clickedBlock = event.getClickedBlock();

                    if (isSignBlock) {
                        Location location = clickedBlock.getLocation();

                        // sign messages
                        class BasicThread implements Runnable {
                            @Override
                            public void run() {
                                int amount = -1;

                                try (Connection connection = Database.getConnection(true)) {
                                    if (connection != null) {
                                        Statement statement = connection.createStatement();
                                        List<String> signData = SignMessageLookup.performLookup(null, statement, location, player, 1, limit);
                                        for (String signMessage : signData) {
                                            if (amount >= limit) break;
                                            String bypass = null;
                                            if (skip(signMessage)) continue;

                                            if (signMessage.contains("\n")) {
                                                String[] split = signMessage.split("\n");
                                                signMessage = split[0];
                                                bypass = split[1];
                                            }

                                            if (signMessage.length() > 0) {
                                                player.sendMessage(change(signMessage, bypass));
                                            }
                                            amount += 1;
                                        }

                                        statement.close();

                                        final int fAmount = amount;
                                        if (amount > 0)
                                            Bukkit.getScheduler().runTask(NPI.Instance, () -> player.damageItemStack(item, fAmount));
                                    }
                                    else { 
                                        Chat.sendMessage(player, Color.YELLOW + "Inspect" + Color.GOLD + "Brush " + Color.WHITE + "- " + Phrase.build(Phrase.DATABASE_BUSY));
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                             }
                        }

                        Runnable runnable = new BasicThread();
                        Thread thread = new Thread(runnable);
                        thread.start();
                        event.setCancelled(true);
                    }
                    else if (isContainerBlock) {
                        Location location = null;
                        if (type.equals(Material.CHEST) || type.equals(Material.TRAPPED_CHEST)) {
                            Chest chest = (Chest) clickedBlock.getState();
                            InventoryHolder inventoryHolder = chest.getInventory().getHolder();

                            if (inventoryHolder instanceof DoubleChest) {
                                location = ((DoubleChest) inventoryHolder).getLocation();
                            }
                            else {
                                location = chest.getLocation();
                            }
                        }

                        if (location == null) {
                            location = clickedBlock.getLocation();
                        }

                        Location finalLocation = location;

                        // logged chest items
                        class BasicThread implements Runnable {
                            @Override
                            public void run() {
                                int amount = -1;
                                try (Connection connection = Database.getConnection(true)) {
                                    if (connection != null) {
                                        Statement statement = connection.createStatement();
                                        List<String> blockData = ChestTransactionLookup.performLookup(null, statement, finalLocation, player, 1, limit, false);
                                        for (String data : blockData) {
                                            if (amount >= limit) break;
                                            if (skip(data)) continue;
                                            player.sendMessage(change(data));
                                            amount += 1;
                                        }

                                        statement.close();

                                        final int fAmount = amount;
                                        if (amount > 0)
                                            Bukkit.getScheduler().runTask(NPI.Instance, () -> player.damageItemStack(item, fAmount));
                                    }
                                    else {
                                        Chat.sendMessage(player, Color.YELLOW + "Inspect" + Color.GOLD + "Brush " + Color.WHITE + "- " + Phrase.build(Phrase.DATABASE_BUSY));
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        Runnable runnable = new BasicThread();
                        Thread thread = new Thread(runnable);
                        thread.start();
                        event.setCancelled(true);
                    }
                    else if (isInteractBlock) {
                        // standard player interactions
                        Block interactBlock = clickedBlock;
                        if (BlockGroup.DOORS.contains(type)) {
                            int y = interactBlock.getY() - 1;
                            Block blockUnder = interactBlock.getWorld().getBlockAt(interactBlock.getX(), y, interactBlock.getZ());

                            if (blockUnder.getType().equals(type)) {
                                interactBlock = blockUnder;
                            }
                        }

                        final Block finalInteractBlock = interactBlock;
                        class BasicThread implements Runnable {
                            @Override
                            public void run() {
                                int amount = -1;
                                try (Connection connection = Database.getConnection(true)) {
                                    if (connection != null) {
                                        Statement statement = connection.createStatement();
                                        String blockData = InteractionLookup.performLookup(null, statement, finalInteractBlock, player, 0, 1, limit);

                                        if (blockData.contains("\n")) {
                                            for (String splitData : blockData.split("\n")) {
                                                if (amount >= limit) break;
                                                if (skip(splitData)) continue;
                                                player.sendMessage(change(splitData));
                                                amount += 1;
                                            }
                                        }
                                        else {
                                            player.sendMessage(change(blockData));
                                        }

                                        statement.close();
                                        final int fAmount = amount;
                                        if (amount > 0)
                                            Bukkit.getScheduler().runTask(NPI.Instance, () -> player.damageItemStack(item, fAmount));
                                    }
                                    else {
                                        Chat.sendMessage(player, Color.YELLOW + "Inspect" + Color.GOLD + "Brush " + Color.WHITE + "- " + Phrase.build(Phrase.DATABASE_BUSY));
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        Runnable runnable = new BasicThread();
                        Thread thread = new Thread(runnable);
                        thread.start();

                        if (!BlockGroup.SAFE_INTERACT_BLOCKS.contains(type) || player.isSneaking()) {
                            event.setCancelled(true);
                        }
                    }
                }
                else {
                    boolean performLookup = true;
                    EquipmentSlot eventHand = event.getHand();
                    String uuid = event.getPlayer().getUniqueId().toString();
                    long systemTime = System.currentTimeMillis();

                    if (lastInspectorEvent.get(uuid) != null) {
                        Object[] lastEvent = lastInspectorEvent.get(uuid);
                        long lastTime = (long) lastEvent[0];
                        EquipmentSlot lastHand = (EquipmentSlot) lastEvent[1];

                        long timeSince = systemTime - lastTime;
                        if (timeSince < 50 && !eventHand.equals(lastHand)) {
                            performLookup = false;
                        }
                    }

                    if (performLookup) {
                        final Player finalPlayer = player;
                        final BlockState finalBlock = event.getClickedBlock().getRelative(event.getBlockFace()).getState();

                        class BasicThread implements Runnable {
                            @Override
                            public void run() {
                                int amount = -1;
                                try (Connection connection = Database.getConnection(true)) {
                                    if (connection != null) {
                                        Statement statement = connection.createStatement();
                                        if (finalBlock.getType().equals(Material.AIR) || finalBlock.getType().equals(Material.CAVE_AIR)) {
                                            String blockData = BlockLookup.performLookup(null, statement, finalBlock, finalPlayer, 0, 1, limit);

                                            if (blockData.contains("\n")) {
                                                for (String b : blockData.split("\n")) {
                                                    if (amount >= limit) break;
                                                    if (skip(b)) continue;
                                                    finalPlayer.sendMessage(change(b));
                                                    amount += 1;
                                                }
                                            }
                                            else if (blockData.length() > 0) {
                                                finalPlayer.sendMessage(change(blockData));
                                            }
                                        }
                                        else {
                                            String blockData = BlockLookup.performLookup(null, statement, finalBlock, finalPlayer, 0, 1, limit);

                                            if (blockData.contains("\n")) {
                                                for (String splitData : blockData.split("\n")) {
                                                    if (amount >= limit) break;
                                                    if (skip(splitData)) continue;
                                                    finalPlayer.sendMessage(change(splitData));
                                                    amount += 1;
                                                }
                                            }
                                            else if (blockData.length() > 0) {
                                                finalPlayer.sendMessage(change(blockData));
                                            }
                                        }

                                        statement.close();
                                        final int fAmount = amount;
                                        if (amount > 0)
                                            Bukkit.getScheduler().runTask(NPI.Instance, () -> player.damageItemStack(item, fAmount));
                                    }
                                    else {
                                        Chat.sendMessage(finalPlayer, Color.YELLOW + "Inspect" + Color.GOLD + "Brush " + Color.WHITE + "- " + Phrase.build(Phrase.DATABASE_BUSY));
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        Runnable runnable = new BasicThread();
                        Thread thread = new Thread(runnable);
                        thread.start();

                        Util.updateInventory(event.getPlayer());
                        lastInspectorEvent.put(uuid, new Object[] { systemTime, eventHand });

                        if (event.hasItem()) {
                            Material eventItem = event.getItem().getType();
                            if (eventItem.isBlock() && (eventItem.createBlockData() instanceof Bisected)) {
                                int x = finalBlock.getX();
                                int y = finalBlock.getY();
                                int z = finalBlock.getZ();
                                int worldMaxHeight = world.getMaxHeight();
                                if (y < (worldMaxHeight - 1)) {
                                    Block blockBisected = world.getBlockAt(x, y + 1, z);
                                    player.sendBlockChange(blockBisected.getLocation(), blockBisected.getBlockData());
                                }
                                int worldMinHeight = BukkitAdapter.ADAPTER.getMinHeight(world);
                                if (y > worldMinHeight) {
                                    Block blockBisected = world.getBlockAt(x, y - 1, z);
                                    player.sendBlockChange(blockBisected.getLocation(), blockBisected.getBlockData());
                                }
                            }
                        }
                    }

                    event.setCancelled(true);
                }
            }
        }
    }
}
