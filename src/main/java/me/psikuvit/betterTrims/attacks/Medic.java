package me.psikuvit.betterTrims.attacks;

import me.psikuvit.betterTrims.BTPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

public class Medic extends IAttack {

    public Medic(BTPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getKey() {
        return "medic";
    }

    @Override
    public int getCooldown() {
        return plugin.getCooldownManager().getMedicCooldown();
    }

    @Override
    public void onEquip(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 0));

        Location currentLocation = player.getLocation();
        Vector direction = currentLocation.getDirection();
        direction.normalize().multiply(5);

        Location newLocation = currentLocation.clone().add(direction);
        newLocation.setY(Math.max(newLocation.getY(), currentLocation.getWorld().getHighestBlockYAt(newLocation)));

        player.teleport(newLocation);
    }

    @Override
    public void playerIsHitter(EntityDamageByEntityEvent event, Player player) {}

    @Override
    public void playerIsHit(EntityDamageByEntityEvent event, Player player) {}
}
