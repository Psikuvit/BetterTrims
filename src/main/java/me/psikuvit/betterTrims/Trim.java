package me.psikuvit.betterTrims;

import me.psikuvit.betterTrims.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Set;

public class Trim {

    public static final NamespacedKey trimKey = new NamespacedKey(BTPlugin.getProvidingPlugin(BTPlugin.class), "trim");
    public static final NamespacedKey attackKey = new NamespacedKey(BTPlugin.getProvidingPlugin(BTPlugin.class), "attack");
    public static final NamespacedKey crackKey = new NamespacedKey(BTPlugin.getProvidingPlugin(BTPlugin.class), "cracks");
    public static final NamespacedKey repairKey = new NamespacedKey(BTPlugin.getProvidingPlugin(BTPlugin.class), "repair");

    public static final Trim GUARDIAN = new Trim("guardian", TrimPattern.TIDE, TrimMaterial.DIAMOND, "final_stand", "damage");
    public static final Trim RESISTANCE = new Trim("resistance", TrimPattern.RIB, TrimMaterial.NETHERITE, "bomber", "medic");
    public static final Trim SHADOW = new Trim("shadow", TrimPattern.COAST, TrimMaterial.IRON, "hidden_in_the_fields", "sneak", "flight_risk");
    public static final Trim BLITZ = new Trim("blitz", TrimPattern.WARD, TrimMaterial.GOLD, "dueler", "speedy_getaway");

    public static final List<Trim> TRIMS = List.of(GUARDIAN, RESISTANCE, SHADOW, BLITZ);
    private static final int MAX_CRACKS = 5;

    private final String key;
    private final ArmorTrim armorTrim;
    private final Set<String> supportedAttacks;

    public Trim(String key, TrimPattern trimPattern, TrimMaterial trimMaterial, String... supportedAttacks) {
        this.key = key;
        this.armorTrim = new ArmorTrim(trimMaterial, trimPattern);
        this.supportedAttacks = Set.of(supportedAttacks);
    }

    public ArmorTrim getArmorTrim() {
        return armorTrim;
    }

    public Set<String> getSupportedAttacks() {
        return supportedAttacks;
    }

    public String getKey() {
        return key;
    }

    public static Trim getTrimByKey(String key) {
        for (Trim trim : TRIMS) {
            if (trim.getKey().equalsIgnoreCase(key)) return trim;
        }
        return null;
    }

    public static Trim getPlayerTrim(Player player) {
        String trimString = player.getPersistentDataContainer().get(trimKey, PersistentDataType.STRING);
        return getTrimByKey(trimString);
    }

    public static int getPlayerCracks(Player player) {
        return player.getPersistentDataContainer().getOrDefault(crackKey, PersistentDataType.INTEGER, 0);
    }

    public static void addPlayerCrack(Player player) {
        int cracks = getPlayerCracks(player);
        if (cracks < MAX_CRACKS) cracks++;
        player.getPersistentDataContainer().set(crackKey, PersistentDataType.INTEGER, cracks);

    }

    public static boolean reduceItemCrack(Player player) {
        int cracks = getPlayerCracks(player);
        if (cracks == 0) return false;
        cracks--;
        player.getPersistentDataContainer().set(crackKey, PersistentDataType.INTEGER, cracks);
        return true;
    }

    public static void removePlayerTrim(Player player) {
        player.getPersistentDataContainer().remove(trimKey);
        Utils.addArmorTrim(player, null);
    }


}