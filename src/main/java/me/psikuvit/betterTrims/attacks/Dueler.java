package me.psikuvit.betterTrims.attacks;

import me.psikuvit.betterTrims.BTPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Dueler extends IAttack {

    public Dueler(BTPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getKey() {
        return "dueler";
    }

    @Override
    public int getCooldown() {
        return plugin.getCooldownManager().getDuelerCooldown();
    }

    @Override
    public void onEquip(Player player) {}

    @Override
    public void playerIsHitter(EntityDamageByEntityEvent event, Player player) {
        event.setDamage(event.getDamage() * 1.25);
    }

    @Override
    public void playerIsHit(EntityDamageByEntityEvent event, Player player) {}
}
