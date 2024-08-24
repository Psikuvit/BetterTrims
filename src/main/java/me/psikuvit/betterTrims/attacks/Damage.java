package me.psikuvit.betterTrims.attacks;

import me.psikuvit.betterTrims.BTPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class Damage extends IAttack {

    public Damage(BTPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getKey() {
        return "damage";
    }

    @Override
    public int getCooldown() {
        return plugin.getCooldownManager().getDamageCooldown();
    }

    @Override
    public void onEquip(Player player) {}

    @Override
    public void playerIsHitter(EntityDamageByEntityEvent event, Player player) {
        double i = event.getDamage() * 1.25;
        event.setDamage(i);
        Bukkit.getLogger().info("Damage: " + i);
    }

    @Override
    public void playerIsHit(EntityDamageByEntityEvent event, Player player) {
        Bukkit.getLogger().info("Damage armor");
        for (ItemStack armorItem : player.getInventory().getArmorContents()) {
            if (armorItem != null && armorItem.getItemMeta() != null && armorItem.getType().getMaxDurability() > 0) {
                Damageable damageable = (Damageable) armorItem.getItemMeta();
                damageable.setDamage((int) (damageable.getDamage() + 1 * 1.5));
                armorItem.setItemMeta(damageable);
            }
        }
    }
}
