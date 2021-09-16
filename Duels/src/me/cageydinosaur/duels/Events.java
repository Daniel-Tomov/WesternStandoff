package me.cageydinosaur.duels;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;

import net.md_5.bungee.api.ChatColor;

public class Events implements Listener {

	int EXPIRE_TIME = 5;
	double timeCreated;
	double currentTime;
	double timeTill;
	double checkSeconds = 0;
	double checkSecond = 0;
	double tpAmount = 0.1;

	Main plugin;

	public Events(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player loser = (Player) e.getEntity();
			Player callingPlayer = null;
			Player challengedPlayer = null;
			Player victor = null;

			if (Duel.involvedInDuel(plugin.ongoingDuels, loser)) {
				if (e.getCause() == DamageCause.FALL) {
					e.setCancelled(true);
					return;
				}
				
				for (Duel d : plugin.ongoingDuels) {
					if (d.getCallingPlayer() == loser) {
						callingPlayer = loser;
						challengedPlayer = d.getChallengedPlayer();
						victor = d.getChallengedPlayer();
					} else if (d.getChallengedPlayer() == loser) {
						challengedPlayer = loser;
						callingPlayer = d.getCallingPlayer();
						victor = d.getCallingPlayer();
					}
				}
				
				if (e.getDamager() != victor) {
					e.setCancelled(true);
					return;
				}
				Duel.endDuel(callingPlayer, challengedPlayer);
				plugin.getServer().broadcastMessage(ChatColor.GREEN + victor.getDisplayName() + " has defeated "
						+ loser.getDisplayName() + " in a duel!");
				Duel duel = Duel.getDuel(plugin.ongoingDuels, callingPlayer, challengedPlayer);
				plugin.ongoingDuels.remove(duel);
				return;
			}

			if (Duel.involvedInDuel(plugin.aboutToStart, victor) || Duel.involvedInDuel(plugin.aboutToStartTwo, victor)) {
				e.setCancelled(true);
				return;
			}
			
			if (plugin.player1.contains(callingPlayer) || plugin.player2.contains(challengedPlayer)) {
				e.setCancelled(true);
			}
		}
		return;

	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (Duel.involvedInDuel(plugin.aboutToStartTwo, p)) {
			e.setCancelled(true);
			return;
		}
		if (plugin.player1.contains(p)) {
			e.setCancelled(true);
			return;
		}
		
		if (plugin.player2.contains(p)){
			e.setCancelled(true);
			return;
		}
	}

	private static double time() {
		double time = 0;
		time = System.currentTimeMillis();
		return time;
	}
}