package me.psikuvit.betterTrims.listeners;

import me.psikuvit.betterTrims.BTPlugin;
import me.psikuvit.betterTrims.Trim;
import me.psikuvit.betterTrims.attacks.IAttack;
import me.psikuvit.betterTrims.utils.AttacksManager;
import me.psikuvit.betterTrims.utils.Utils;
import me.psikuvit.betterTrims.armorevents.ArmorEquipEvent;
import me.psikuvit.betterTrims.armorevents.ArmorType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class TrimListeners implements Listener {

    private final BTPlugin plugin;
    private final AttacksManager attacksManager;

    public TrimListeners(BTPlugin plugin) {
        this.plugin = plugin;
        this.attacksManager = plugin.getAttacksManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (!pdc.has(Trim.trimKey)) {
            Trim trim = getRandomTrim();
            pdc.set(Trim.trimKey, PersistentDataType.STRING, trim.getKey());
            player.sendMessage(Utils.color("&7Trim: &b" + trim.getKey() + " &7was added to you!"));

            if (trim == Trim.RESISTANCE) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 0));
            } else if (trim == Trim.BLITZ) {
                AttributeInstance attackSpeed = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                if (attackSpeed != null) {
                    attackSpeed.setBaseValue(4.0);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getCursor() != null && event.getCurrentItem() != null) {
            ItemStack heldItem = event.getCursor();
            ItemStack clickedItem = event.getCurrentItem();

            if (heldItem.getType() == Material.AIR || heldItem.getType().name().contains("CHESTPLATE") || !clickedItem.getType().name().contains("CHESTPLATE")) return;
            if (heldItem.getItemMeta() == null || clickedItem.getItemMeta() == null) return;

            if (isAttack(heldItem) && !isAttack(clickedItem)) { // handles attack adding to chestplate

                IAttack attack = attacksManager.getItemAttack(heldItem);
                Trim trim = Trim.getPlayerTrim(player);

                if (!trim.getSupportedAttacks().contains(attack.getKey())) return;

                event.setCancelled(true);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    attacksManager.addAttack(clickedItem, attack);

                    player.sendMessage(Utils.color("&bAttack: &7" + attack.getKey() + " &bwas equipped."));
                    event.getWhoClicked().setItemOnCursor(null);
                });
            } else if (isRepair(heldItem) && clickedItem.getType().name().contains("CHESTPLATE")) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTask(plugin, () -> {

                    if (Trim.reduceItemCrack(player)) {
                        player.sendMessage(Utils.color("&bTrim: &7repaired successfully."));
                        event.getWhoClicked().setItemOnCursor(null);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        Trim trim = Trim.getPlayerTrim(player);

        if (trim == null) {
            Utils.addArmorTrim(player, null);
            return;
        }

        ItemStack itemStack = event.getNewArmorPiece();
        if (itemStack == null || itemStack.getItemMeta() == null) return;

        if (event.getType().equals(ArmorType.CHESTPLATE)) { // checks if the chestplate attack is compatible with the player trim
            ItemMeta itemMeta = itemStack.getItemMeta();
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

            if (pdc.has(Trim.attackKey)) {
                IAttack attack = attacksManager.getItemAttack(itemStack); // get the chestplate attack

                if (!trim.getSupportedAttacks().contains(attack.getKey())) attacksManager.removeAttack(itemStack); // check if the attack is not supported by trim
            }
        }

        Utils.addArmorTrim(player, trim);
    }

    @EventHandler
    public void onDie(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (Trim.getPlayerTrim(player) == null) return;

        Trim.addPlayerCrack(player);

        if (Trim.getPlayerCracks(player) == 5) {
            Trim.removePlayerTrim(player);
            ItemStack itemStack = player.getInventory().getChestplate();
            if (itemStack != null) attacksManager.removeAttack(itemStack);
        }
    }

    private Trim getRandomTrim() {
        Random rnd = new Random();
        return Trim.TRIMS.get(rnd.nextInt(Trim.TRIMS.size()));
    }

    private boolean isAttack(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        return itemMeta.getPersistentDataContainer().has(Trim.attackKey);
    }

    private boolean isRepair(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        return itemMeta.getPersistentDataContainer().has(Trim.repairKey);
    }
}
