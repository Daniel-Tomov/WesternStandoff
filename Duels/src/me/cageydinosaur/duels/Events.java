package me.cageydinosaur.duels;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

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
	public void onLaunch(WeaponDamageEntityEvent e) {
		if (e.getVictim() instanceof Player) {
			Player loser = (Player) e.getVictim();
			Player callingPlayer = null;
			Player challengedPlayer = null;
			Player victor = e.getPlayer();

			if (Duel.bothInvolved(plugin.ongoingDuels, loser, victor)
					|| Duel.bothInvolved(plugin.aboutToStart, loser, victor)
					|| Duel.bothInvolved(plugin.aboutToStartTwo, loser, victor)) {

				for (Duel d : plugin.ongoingDuels) {
					if (d.getCallingPlayer() == loser) {
						callingPlayer = loser;
						challengedPlayer = d.getChallengedPlayer();
					} else if (d.getChallengedPlayer() == loser) {
						challengedPlayer = loser;
						callingPlayer = d.getCallingPlayer();
					}
				}

				if (loser != callingPlayer && loser != challengedPlayer) {
					e.setCancelled(true);
					return;
				}

				Duel.endDuel(callingPlayer, challengedPlayer);
				loser.setHealth(0);
				plugin.getServer().broadcastMessage(plugin.chat("§" + (plugin.killMsg())
						.replace("{victor}", victor.getName()).replace("{loser}", loser.getName())));
				Duel duel = Duel.getDuel(plugin.ongoingDuels, callingPlayer, challengedPlayer);
				plugin.ongoingDuels.remove(duel);
				return;
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player loser = (Player) e.getEntity();
			Player callingPlayer = null;
			Player challengedPlayer = null;
			Player victor = null;

			if (Duel.involvedInDuel(plugin.ongoingDuels, loser) || Duel.involvedInDuel(plugin.aboutToStart, loser)
					|| Duel.involvedInDuel(plugin.aboutToStartTwo, loser)) {

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
				if (Duel.involvedInDuel(plugin.aboutToStart, victor)
						|| Duel.involvedInDuel(plugin.aboutToStartTwo, victor)) {
					e.setCancelled(true);
					return;
				}

				if (plugin.player1.contains(callingPlayer) || plugin.player2.contains(challengedPlayer)) {
					e.setCancelled(true);
				}

			}
		}
		return;

	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			e.setDeathMessage("");
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (Duel.involvedInDuel(plugin.aboutToStartTwo, p) || Duel.involvedInDuel(plugin.ongoingDuels, p)) {
			if (e.getFrom().getZ() != e.getTo().getZ() || e.getFrom().getX() != e.getTo().getX()) {
				e.setCancelled(true);
				return;
			}
		}
		if (plugin.player1.contains(p)) {
			if (e.getFrom().getZ() != e.getTo().getZ() || e.getFrom().getX() != e.getTo().getX()) {
				e.setCancelled(true);
				return;
			}
		}

		if (plugin.player2.contains(p)) {
			if (e.getFrom().getZ() != e.getTo().getZ() || e.getFrom().getX() != e.getTo().getX()) {
				e.setCancelled(true);
				return;
			}
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
			plugin.getServer().broadcastMessage(plugin.chat("§"
					+ (plugin.killMsg()).replace("{victor}", victor.getName()).replace("{loser}", loser.getName())));
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
			plugin.getServer().broadcastMessage(plugin.chat("§"
					+ (plugin.killMsg()).replace("{victor}", victor.getName()).replace("{loser}", loser.getName())));
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
			plugin.getServer().broadcastMessage(plugin.chat(("§" + plugin.killMsg())
					.replace("{victor}", victor.getName()).replace("{loser}", loser.getName())));
			return;
		}

	}
}