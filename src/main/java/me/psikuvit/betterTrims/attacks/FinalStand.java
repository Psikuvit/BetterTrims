package me.psikuvit.betterTrims.attacks;

import me.psikuvit.betterTrims.BTPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FinalStand extends IAttack {

    public FinalStand(BTPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getKey() {
        return "final_stand";
    }

    @Override
    public int getCooldown() {
        return plugin.getCooldownManager().getFinalStandCooldown();
    }

    @Override
    public void onEquip(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 1));
    }

    @Override
    public void playerIsHitter(EntityDamageByEntityEvent event, Player player) {
        double i = event.getDamage() * 2;
        event.setDamage(i);
        Bukkit.getLogger().info("Final Stand: " + i);
    }

    @Override
    public void playerIsHit(EntityDamageByEntityEvent event, Player player) {}
}
