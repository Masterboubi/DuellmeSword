package de.deinplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class DuelItemChallenge extends JavaPlugin implements Listener {

    private final Set<Player> inDuel = new HashSet<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("DuelItemChallenge aktiviert!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        giveDuelSword(e.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Bukkit.getScheduler().runTaskLater(this, () -> giveDuelSword(e.getPlayer()), 10L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        inDuel.remove(e.getPlayer());
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player)) return;

        Player challenger = e.getPlayer();
        Player target = (Player) e.getRightClicked();

        if (inDuel.contains(challenger) || inDuel.contains(target)) return;

        ItemStack item = challenger.getInventory().getItemInMainHand();
        if (item != null && item.getType() == Material.STONE_SWORD) {
            challenger.performCommand("duel " + target.getName());
            challenger.sendMessage(ChatColor.GREEN + "Du hast " + target.getName() + " herausgefordert!");
        }
    }

    public void onDuelStart(Player p1, Player p2) {
        inDuel.add(p1);
        inDuel.add(p2);
        removeDuelSword(p1);
        removeDuelSword(p2);
    }

    public void onDuelEnd(Player p1, Player p2) {
        inDuel.remove(p1);
        inDuel.remove(p2);
        giveDuelSword(p1);
        giveDuelSword(p2);
    }

    private void giveDuelSword(Player player) {
        if (player.getInventory().first(Material.STONE_SWORD) == -1) {
            player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
        }
    }

    private void removeDuelSword(Player player) {
        player.getInventory().remove(Material.STONE_SWORD);
    }
}
