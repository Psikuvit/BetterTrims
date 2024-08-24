package me.psikuvit.betterTrims;

import me.psikuvit.betterTrims.armorevents.ArmorListener;
import me.psikuvit.betterTrims.armorevents.DispenserArmorListener;
import me.psikuvit.betterTrims.attacks.IAttack;
import me.psikuvit.betterTrims.listeners.AttackListeners;
import me.psikuvit.betterTrims.listeners.TrimListeners;
import me.psikuvit.betterTrims.utils.AttacksManager;
import me.psikuvit.betterTrims.utils.CooldownManager;
import me.psikuvit.betterTrims.utils.RecipesUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class BTPlugin extends JavaPlugin {

    private Map<UUID, Trim> playerTrim;
    private RecipesUtils recipesUtils;
    private CooldownManager cooldownManager;
    private AttacksManager attacksManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        playerTrim = new HashMap<>();
        saveDefaultConfig();

        cooldownManager = new CooldownManager(this);
        attacksManager = new AttacksManager(this);
        recipesUtils = new RecipesUtils(this);
        recipesUtils.registerRecipe();

        getServer().getPluginManager().registerEvents(new ArmorListener(), this);
        getServer().getPluginManager().registerEvents(new DispenserArmorListener(), this);

        getServer().getPluginManager().registerEvents(new TrimListeners(this), this);
        getServer().getPluginManager().registerEvents(new AttackListeners(this), this);

        getCommand("trim").setExecutor(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        recipesUtils.removeRecipes();
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public AttacksManager getAttacksManager() {
        return attacksManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reset")) {
                    Trim.removePlayerTrim(player);
                    Trim trim = getRandomTrim();
                    player.getPersistentDataContainer().set(Trim.trimKey, PersistentDataType.STRING, trim.getKey());

                } else if (args[0].equalsIgnoreCase("clear")) {
                    player.getPersistentDataContainer().remove(Trim.trimKey);
                    player.getPersistentDataContainer().remove(Trim.attackKey);
                    player.getPersistentDataContainer().remove(Trim.crackKey);

                    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                        player.removePotionEffect(potionEffect.getType());
                    }
                } else if (args[0].equalsIgnoreCase("trim")) {
                    player.sendMessage(Trim.getPlayerTrim(player).getKey());
                } else if (args[0].equalsIgnoreCase("attack")) {
                    player.sendMessage(attacksManager.getItemAttack(player.getInventory().getItemInMainHand()).getKey());
                } else if (args[0].equalsIgnoreCase("cracks")) {
                    player.sendMessage(String.valueOf(Trim.getPlayerCracks(player)));
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("attack")) {
                    IAttack attack = attacksManager.getAttackByKey(args[1]);
                    ItemStack itemStack = Bukkit.getRecipe(new NamespacedKey(this, attack.getKey())).getResult();
                    player.getInventory().addItem(itemStack);

                } else if (args[0].equalsIgnoreCase("trim")) {
                    Trim trim = Trim.getTrimByKey(args[1]);
                    player.getPersistentDataContainer().set(Trim.trimKey, PersistentDataType.STRING, trim.getKey());

                    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                        player.removePotionEffect(potionEffect.getType());
                    }

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
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            List<String> completions = new ArrayList<>();
            if (args.length == 1) {
                List<String> subcommands = List.of("reset", "clear", "trim", "attack", "cracks");
                for (String subcommand : subcommands) {
                    if (subcommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(subcommand);
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("attack")) {
                    List<String> attackKeys = new ArrayList<>();
                    for (IAttack attack : attacksManager.getAttacks()) {
                        attackKeys.add(attack.getKey());
                    }
                    for (String key : attackKeys) {
                        if (key.toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(key);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("trim")) {
                    List<String> trimKeys = new ArrayList<>();
                    for (Trim trim : Trim.TRIMS) {
                        trimKeys.add(trim.getKey());
                    }
                    for (String key : trimKeys) {
                        if (key.toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(key);
                        }
                    }
                }
            }
            return completions;
        }
        return null;
    }

    private Trim getRandomTrim() {
        Random rnd = new Random();
        return Trim.TRIMS.get(rnd.nextInt(Trim.TRIMS.size()));
    }
}
