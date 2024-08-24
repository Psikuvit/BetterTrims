package me.psikuvit.betterTrims.attacks;

import me.psikuvit.betterTrims.BTPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class Sneak extends IAttack {

    public Sneak(BTPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getKey() {
        return "sneak";
    }

    @Override
    public int getCooldown() {
        return plugin.getCooldownManager().getSneakCooldown();
    }

    @Override
    public void onEquip(Player player) {
        Entity nearestOpponent = getNearestOpponent(player);
        if (nearestOpponent == null) {
            player.sendMessage("No opponents nearby to teleport behind.");
            return;
        }

        double y = nearestOpponent.getLocation().getY();

        Vector direction = nearestOpponent.getLocation().getDirection();
        direction.multiply(-10); // Move 10 blocks behind
        Location newLoc = nearestOpponent.getLocation().add(direction);
        newLoc.setY(y);
        player.teleport(newLoc);

        placeCobwebs(nearestOpponent);
    }


    @Override
    public void playerIsHitter(EntityDamageByEntityEvent event, Player player) {}

    @Override
    public void playerIsHit(EntityDamageByEntityEvent event, Player player) {}

    private Entity getNearestOpponent(Player player) {
        double nearestDistance = 20;
        Entity nearestOpponent = null;

        for (Entity entity : player.getNearbyEntities(nearestDistance, nearestDistance, nearestDistance)) {
            if (!entity.equals(player)) {
                double distance = entity.getLocation().distance(player.getLocation());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestOpponent = entity;
                }
            }
        }

        return nearestOpponent;
    }

    private void placeCobwebs(Entity entity) {
        entity.getLocation().getBlock().setType(Material.COBWEB);
        entity.getLocation().add(1, 0, 0).getBlock().setType(Material.COBWEB);
        entity.getLocation().add(-1, 0, 0).getBlock().setType(Material.COBWEB);
        entity.getLocation().add(0, 0, 1).getBlock().setType(Material.COBWEB);
        entity.getLocation().add(0, 0, -1).getBlock().setType(Material.COBWEB);
        entity.getLocation().add(0, 1, 0).getBlock().setType(Material.COBWEB);
        entity.getLocation().add(0, -1, 0).getBlock().setType(Material.COBWEB);
    }
}
