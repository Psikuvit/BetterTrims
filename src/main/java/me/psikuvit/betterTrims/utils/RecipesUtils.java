package me.psikuvit.betterTrims.utils;

import me.psikuvit.betterTrims.BTPlugin;
import me.psikuvit.betterTrims.Trim;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipesUtils {

    private final BTPlugin plugin;
    private final List<NamespacedKey> recipes;

    public RecipesUtils(BTPlugin plugin) {
        this.plugin = plugin;
        this.recipes = new ArrayList<>();
    }

    public void registerRecipe() {
        FileConfiguration config = plugin.getConfig();

        for (Map<?, ?> recipeData : config.getMapList("recipes")) {
            String resultName = (String) recipeData.get("result");
            String attackName = (String) recipeData.get("attack");

            Map<String, String> ingredients = (Map<String, String>) recipeData.get("ingredients");
            List<String> shape = (java.util.List<String>) recipeData.get("shape");

            ItemStack result = new ItemStack(Material.valueOf(resultName));

            plugin.getAttacksManager().addAttack(result, plugin.getAttacksManager().getAttackByKey(attackName.toLowerCase()));

            NamespacedKey key = new NamespacedKey(plugin, attackName.toLowerCase());

            ShapedRecipe recipe = new ShapedRecipe(key, result);
            recipe.shape(shape.toArray(new String[0]));

            for (Map.Entry<String, String> entry : ingredients.entrySet()) {
                char ingredientChar = entry.getKey().charAt(0);
                Material material = Material.valueOf(entry.getValue());
                recipe.setIngredient(ingredientChar, material);
            }
            Bukkit.addRecipe(recipe);
            recipes.add(key);
        }

        for (Map<?, ?> recipeData : config.getMapList("repair")) {
            String resultName = (String) recipeData.get("result");

            Map<String, String> ingredients = (Map<String, String>) recipeData.get("ingredients");
            List<String> shape = (java.util.List<String>) recipeData.get("shape");

            ItemStack result = new ItemStack(Material.valueOf(resultName));

            ItemMeta itemMeta = result.getItemMeta();

            itemMeta.getPersistentDataContainer().set(Trim.repairKey, PersistentDataType.STRING, "repair");
            result.setItemMeta(itemMeta);

            ShapedRecipe recipe = new ShapedRecipe(Trim.repairKey, result);
            recipe.shape(shape.toArray(new String[0]));

            for (Map.Entry<String, String> entry : ingredients.entrySet()) {
                char ingredientChar = entry.getKey().charAt(0);
                Material material = Material.valueOf(entry.getValue());
                recipe.setIngredient(ingredientChar, material);
            }
            Bukkit.addRecipe(recipe);
            recipes.add(Trim.repairKey);
        }
    }

    public void removeRecipes() {
        recipes.forEach(Bukkit::removeRecipe);
    }
}