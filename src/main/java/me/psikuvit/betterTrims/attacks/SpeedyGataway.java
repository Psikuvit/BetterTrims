package me.psikuvit.betterTrims.attacks;

import me.psikuvit.betterTrims.BTPlugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedyGataway extends IAttack {

    public SpeedyGataway(BTPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getKey() {
        return "speedy_getaway";
    }

    @Override
    public int getCooldown() {
        return plugin.getCooldownManager().getSpeedyGatawayCooldown();
    }

    @Override
    public void onEquip(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
    }

    @Override
    public void playerIsHitter(EntityDamageByEntityEvent event, Player player) {
        if (event.getEntity() instanceof LivingEntity livingEntity) livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
    }
    @Override
    public void playerIsHit(EntityDamageByEntityEvent event, Player player) {}
}
