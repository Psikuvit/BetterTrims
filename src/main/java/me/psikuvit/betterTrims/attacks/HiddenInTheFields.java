package me.psikuvit.betterTrims.attacks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.psikuvit.betterTrims.BTPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collection;
import java.util.Collections;

public class HiddenInTheFields extends IAttack {

    public HiddenInTheFields(BTPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getKey() {
        return "hidden_in_the_fields";
    }

    @Override
    public int getCooldown() {
        return plugin.getCooldownManager().getHiddenInTheFieldsCooldown();
    }

    @Override
    public void onEquip(Player player) {
        hideNameTag(player);
    }

    @Override
    public void playerIsHitter(EntityDamageByEntityEvent event, Player player) {}

    @Override
    public void playerIsHit(EntityDamageByEntityEvent event, Player player) {}

    private void hideNameTag(Player player) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getStrings().write(0, player.getName());
        packet.getIntegers().write(0, 1); // 1 = create, 0 = remove

        // Hide name tag
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(" "));
        packet.getSpecificModifier(Collection.class).write(0, Collections.singletonList(player.getName()));

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(onlinePlayer, packet);
        }

        // Schedule task to show the name tag again after 30 seconds
        Bukkit.getScheduler().runTaskLater(BTPlugin.getPlugin(BTPlugin.class), () -> showNameTag(player), 600L);
    }

    private void showNameTag(Player player) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(com.comphenix.protocol.PacketType.Play.Server.SCOREBOARD_TEAM);
        packet.getStrings().write(0, player.getName());
        packet.getIntegers().write(0, 0); // 1 = create, 0 = remove

        // Show name tag
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(player.getName()));
        packet.getSpecificModifier(Collection.class).write(0, Collections.singletonList(player.getName()));

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(onlinePlayer, packet);
        }
    }
}
