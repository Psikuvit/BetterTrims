package me.psikuvit.betterTrims.attacks;

import me.psikuvit.betterTrims.BTPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Bomber extends IAttack {

    public Bomber(BTPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getKey() {
        return "bomber";
    }

    @Override
    public int getCooldown() {
        return plugin.getCooldownManager().getBomberCooldown();
    }

    @Override
    public void onEquip(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, getCooldown() * 20, 1));
    }

    @Override
    public void playerIsHitter(EntityDamageByEntityEvent event, Player player) {
        event.setDamage(event.getDamage() * 1.25);
    }

    @Override
    public void playerIsHit(EntityDamageByEntityEvent event, Player player) {
        for (ItemStack armorItem : player.getInventory().getArmorContents()) {
            if (armorItem != null && armorItem.getItemMeta() != null && armorItem.getType().getMaxDurability() > 0) {
                Damageable damageable = (Damageable) armorItem.getItemMeta();
                damageable.setDamage((int) (damageable.getDamage() + 1 * 1.25));
                armorItem.setItemMeta(damageable);
            }
        }
    }
}
