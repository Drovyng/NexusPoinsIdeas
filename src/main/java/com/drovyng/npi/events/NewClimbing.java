package com.drovyng.npi.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class NewClimbing implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerClimb(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;

        if (player.isClimbing() && player.getPitch() <= -85f && event.getFrom().getY() < event.getTo().getY()) {
            var velocity = player.getVelocity();
            
            velocity.setY(0.5f);

            player.setVelocity(velocity);
            if (player.getGameMode() != GameMode.CREATIVE){
                player.setExhaustion(player.getExhaustion() + 0.025f);
                if (player.getExhaustion() >= 4) {
                    player.setExhaustion(0);

                    if (player.getSaturation() > 0) {
                        player.setSaturation(player.getSaturation() - 1.5f);
                    }
                    else {
                        player.setFoodLevel(player.getFoodLevel() - 1);
                    }
                }
            }
        }
    }
}
