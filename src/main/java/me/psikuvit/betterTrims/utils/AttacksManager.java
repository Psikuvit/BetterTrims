package me.psikuvit.betterTrims.utils;

import me.psikuvit.betterTrims.BTPlugin;
import me.psikuvit.betterTrims.Trim;
import me.psikuvit.betterTrims.attacks.Bomber;
import me.psikuvit.betterTrims.attacks.Damage;
import me.psikuvit.betterTrims.attacks.Dueler;
import me.psikuvit.betterTrims.attacks.FinalStand;
import me.psikuvit.betterTrims.attacks.FlightRisk;
import me.psikuvit.betterTrims.attacks.HiddenInTheFields;
import me.psikuvit.betterTrims.attacks.IAttack;
import me.psikuvit.betterTrims.attacks.Medic;
import me.psikuvit.betterTrims.attacks.Sneak;
import me.psikuvit.betterTrims.attacks.SpeedyGataway;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AttacksManager {

    private final List<IAttack> attacks;

    public AttacksManager(BTPlugin plugin) {
        this.attacks = List.of(new Bomber(plugin), new Damage(plugin), new Dueler(plugin), new FinalStand(plugin),
                new FlightRisk(plugin), new HiddenInTheFields(plugin), new Medic(plugin), new Sneak(plugin), new SpeedyGataway(plugin));
    }

    public IAttack getAttackByKey(String key) {
        for (IAttack attack : attacks) {
            if (attack.getKey().equals(key)) return attack;
        }
        return null;
    }

    public IAttack getItemAttack(ItemStack itemStack) {
        String attackString = Objects.requireNonNull(itemStack.getItemMeta()).getPersistentDataContainer().get(Trim.attackKey, PersistentDataType.STRING);
        return getAttackByKey(attackString);
    }

    public IAttack getPlayerAttack(Player player) {
        ItemStack itemStack = player.getInventory().getChestplate();
        if (itemStack == null) return null;
        return getItemAttack(itemStack);
    }

    public void addAttack(ItemStack itemStack, IAttack attack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        itemMeta.setLore(List.of(" ", Utils.color("&7Attack: &b" + attack.getKey())));

        pdc.set(Trim.attackKey, PersistentDataType.STRING, attack.getKey());
        itemStack.setItemMeta(itemMeta);
    }

    public void removeAttack(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        itemMeta.setLore(new ArrayList<>());
        pdc.remove(Trim.attackKey);
        pdc.remove(Trim.crackKey);
        itemStack.setItemMeta(itemMeta);
    }

    public List<IAttack> getAttacks() {
        return attacks;
    }
}
