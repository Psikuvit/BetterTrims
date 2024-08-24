package me.psikuvit.betterTrims.utils;

import me.psikuvit.betterTrims.BTPlugin;
import me.psikuvit.betterTrims.Trim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;

import java.util.Set;

public class Utils {

    private static final Set<Material> TOOLS = Set.of(
            Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
            Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE,
            Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
            Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,
            Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            Material.SHEARS, Material.FLINT_AND_STEEL, Material.FISHING_ROD, Material.CARROT_ON_A_STICK, Material.WARPED_FUNGUS_ON_A_STICK, Material.TRIDENT
    );

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static boolean isTool(ItemStack item) {
        return item != null && TOOLS.contains(item.getType());
    }

    public static void addArmorTrim(Player player, Trim trim) {
        Bukkit.getScheduler().runTask(BTPlugin.getPlugin(BTPlugin.class), () -> { // add armor trims
            for (ItemStack armor : player.getInventory().getArmorContents()) {
                if (armor == null) continue;

                ArmorMeta armorMeta = (ArmorMeta) armor.getItemMeta();
                assert armorMeta != null;
                if (trim == null) armorMeta.setTrim(null);
                else armorMeta.setTrim(trim.getArmorTrim());
                armor.setItemMeta(armorMeta);
            }
        });
    }
}
