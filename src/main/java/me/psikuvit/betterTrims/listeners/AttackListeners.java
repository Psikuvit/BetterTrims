package me.psikuvit.betterTrims.listeners;

import me.psikuvit.betterTrims.BTPlugin;
import me.psikuvit.betterTrims.Trim;
import me.psikuvit.betterTrims.attacks.IAttack;
import me.psikuvit.betterTrims.utils.AttacksManager;
import me.psikuvit.betterTrims.utils.CooldownManager;
import me.psikuvit.betterTrims.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class AttackListeners implements Listener {

    private final CooldownManager cooldownManager;
    private final AttacksManager attacksManager;

    public AttackListeners(BTPlugin plugin) {
        this.cooldownManager = plugin.getCooldownManager();
        this.attacksManager = plugin.getAttacksManager();
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {

            Trim trim = Trim.getPlayerTrim(player);
            IAttack attack = attacksManager.getPlayerAttack(player);

            if (cooldownManager.isOnCooldown(player.getUniqueId()) && attack != null) {
                Bukkit.getLogger().info(attack.getKey());
                attack.playerIsHitter(event, player);
                Bukkit.getLogger().info("attack fired");
                return;
            }

            if (trim == Trim.GUARDIAN) {
                double i = event.getDamage() + 1.5;
                event.setDamage(i);

                Bukkit.getLogger().info("Damage Increased: " + i);

                if (event.getEntity() instanceof Player entity) {
                    for (ItemStack armorItem : entity.getInventory().getArmorContents()) {
                        if (armorItem != null && armorItem.getItemMeta() != null && armorItem.getType().getMaxDurability() > 0) {
                            Damageable damageable = (Damageable) armorItem.getItemMeta();
                            damageable.setDamage(damageable.getDamage() + 2);
                            armorItem.setItemMeta(damageable);
                        }
                    }
                }
            } else if (trim == Trim.RESISTANCE) {
                if (new Random().nextInt(4) + 1 == 1) {
                    event.setDamage(event.getDamage() * 2);
                }
            } else if (trim == Trim.BLITZ) {
                event.setDamage(event.getDamage() * 0.75);

            }


        } else if (event.getEntity() instanceof Player player) {
            Trim trim = Trim.getPlayerTrim(player);
            IAttack attack = attacksManager.getPlayerAttack(player);

            if (!cooldownManager.isOnCooldown(player.getUniqueId()) || attack != null) {
                attack.playerIsHit(event, player);
                return;
            }

            if (trim == Trim.RESISTANCE) {
                if (new Random().nextInt(4) + 1 == 1) {
                    event.setDamage(event.getDamage() * 2);
                }
                for (ItemStack armorItem : player.getInventory().getArmorContents()) {
                    if (armorItem != null && armorItem.getItemMeta() != null && armorItem.getType().getMaxDurability() > 0) {
                        Damageable damageable = (Damageable) armorItem.getItemMeta();
                        damageable.setDamage(damageable.getDamage() + 2);
                        armorItem.setItemMeta(damageable);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (!event.getAction().name().contains("RIGHT")) return;
        if (event.getItem() == null || event.getItem().getItemMeta() == null || !Utils.isTool(event.getItem())) return;

        IAttack attack = attacksManager.getPlayerAttack(player);

        if (attack == null) {
            player.sendMessage(Utils.color("&cYou don't have any attacks"));
            return;
        }

        if (cooldownManager.isOnCooldown(playerUUID)) {
            long remainingTime = cooldownManager.getRemainingCooldown(playerUUID);
            player.sendMessage("You are on getCooldown for " + remainingTime / 1000 + " more seconds.");
            event.setCancelled(true);
        } else {
            cooldownManager.setCooldown(playerUUID, attack.getCooldown() + (Trim.getPlayerCracks(player) * 10));
            attack.onEquip(player);
            player.sendMessage("You have clicked the item and are now on cooldown.");
        }
    }
}
