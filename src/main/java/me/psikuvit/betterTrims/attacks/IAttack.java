package me.psikuvit.betterTrims.attacks;

import me.psikuvit.betterTrims.BTPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class IAttack {

    protected BTPlugin plugin;

    public IAttack(BTPlugin plugin) {
        this.plugin = plugin;
    }

    abstract public String getKey();
    abstract public  int getCooldown();
    abstract public void onEquip(Player player);
    abstract public void playerIsHitter(EntityDamageByEntityEvent event, Player player);
    abstract public void playerIsHit(EntityDamageByEntityEvent event, Player player);
}
