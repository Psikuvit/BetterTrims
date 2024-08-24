package me.psikuvit.betterTrims.utils;

import me.psikuvit.betterTrims.BTPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Long> cooldowns;
    private final FileConfiguration config;

    public CooldownManager(BTPlugin plugin) {
        this.cooldowns = new HashMap<>();
        this.config = plugin.getConfig();
    }

    public boolean isOnCooldown(UUID uuid) {
        return cooldowns.containsKey(uuid) && cooldowns.get(uuid) > System.currentTimeMillis();
    }

    public long getRemainingCooldown(UUID uuid) {
        return cooldowns.get(uuid) - System.currentTimeMillis();
    }

    public void setCooldown(UUID uuid, int seconds) {
        cooldowns.put(uuid, System.currentTimeMillis() + seconds * 1000L);
    }

    public int getBomberCooldown() {
        return config.getInt("cooldowns.bomber");
    }

    public int getDamageCooldown() {
        return config.getInt("cooldowns.damage");
    }

    public int getDuelerCooldown() {
        return config.getInt("cooldowns.dueler");
    }

    public int getFinalStandCooldown() {
        return config.getInt("cooldowns.final_stand");
    }

    public int getFlightRiskCooldown() {
        return config.getInt("cooldowns.flight_risk");
    }

    public int getHiddenInTheFieldsCooldown() {
        return config.getInt("cooldowns.hidden_in_the_fields");
    }

    public int getMedicCooldown() {
        return config.getInt("cooldowns.medic");
    }

    public int getSneakCooldown() {
        return config.getInt("cooldowns.sneak");
    }

    public int getSpeedyGatawayCooldown() {
        return config.getInt("cooldowns.speedy_gataway");
    }
}
