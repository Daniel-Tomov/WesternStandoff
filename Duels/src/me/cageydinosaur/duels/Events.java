package me.cageydinosaur.duels;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;


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

			if (Duel.involvedInDuel(plugin.ongoingDuels, loser) || Duel.involvedInDuel(plugin.aboutToStart, loser) || Duel.involvedInDuel(plugin.aboutToStartTwo, loser)) {
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
				plugin.getServer().broadcastMessage(plugin.chat((plugin.killMsg()).replace("{victor}", victor.getName()).replace("{loser}", loser.getName())));
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

	 @EventHandler
	    public void onPlayerQuit(PlayerQuitEvent event) {
	        // get the player
	        Player player = event.getPlayer();
	        Player victor = null;
	        Player loser = null;
	        // remove all queued duels this player is a part of
	        Duel.deleteDuelsWhereInvolved(plugin.queuedDuels, player);
	        // if they are in an ongoing duel, we do more stuff
	        if (Duel.involvedInDuel(plugin.ongoingDuels, player)) {
	            // get the specific duel
	            for (int i = 0; i < plugin.ongoingDuels.size(); i++) {
	                Duel d = plugin.ongoingDuels.get(i);
	                if (player == d.getCallingPlayer()) {
	                    victor = d.getChallengedPlayer();
	                    loser = player;
	                    Duel.endDuel(loser, victor);
	                }
	                if (player == d.getChallengedPlayer()) {
	                    victor = d.getCallingPlayer();
	                    loser = player;
	                    plugin.ongoingDuels.remove(d);
	                    Duel.endDuel(loser, victor);
	                }
	            }
	            plugin.getServer().broadcastMessage(plugin.chat((plugin.killMsg()).replace("{victor}", victor.getName()).replace("{loser}", loser.getName())));
	            return;
	        }
	        if (Duel.involvedInDuel(plugin.aboutToStart, player)) {
	            // get the specific duel
	            for (int i = 0; i < plugin.aboutToStart.size(); i++) {
	                Duel d = plugin.aboutToStart.get(i);
	                if (player == d.getCallingPlayer()) {
	                    victor = d.getChallengedPlayer();
	                    loser = player;
	                    Duel.endDuel(loser, victor);
	                }
	                if (player == d.getChallengedPlayer()) {
	                    victor = d.getCallingPlayer();
	                    loser = player;
	                    plugin.aboutToStart.remove(d);
	                    Duel.endDuel(loser, victor);
	                }
	            }
	            plugin.getServer().broadcastMessage(plugin.chat((plugin.killMsg()).replace("{victor}", victor.getName()).replace("{loser}", loser.getName())));
	            return;
	        }
	        if (Duel.involvedInDuel(plugin.aboutToStartTwo, player)) {
	            // get the specific duel
	            for (int i = 0; i < plugin.aboutToStartTwo.size(); i++) {
	                Duel d = plugin.aboutToStartTwo.get(i);
	                if (player == d.getCallingPlayer()) {
	                    victor = d.getChallengedPlayer();
	                    loser = player;
	                    Duel.endDuel(loser, victor);
	                }
	                if (player == d.getChallengedPlayer()) {
	                    victor = d.getCallingPlayer();
	                    loser = player;
	                    plugin.aboutToStartTwo.remove(d);
	                    Duel.endDuel(loser, victor);
	                }
	            }
	            plugin.getServer().broadcastMessage(plugin.chat((plugin.killMsg()).replace("{victor}", victor.getName()).replace("{loser}", loser.getName())));
	            return;
	        }
	        
	    }
}