package me.cageydinosaur.duels;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import me.cageydinosaur.duels.*;

public class Duel {

	// expiration time for the duel, in milliseconds
	// 180,000 milliseconds = 3 minutes
	public static final long EXPIRE_TIME = 10;

	// basic information about the duel
	private long timeCreated;
	private static Player callingPlayer;
	private static Player challengedPlayer;
	private static double duelTimeCreated;
	public static Player victor = null;
	public static Player loser = null;
	private boolean accepted = false;

	static Main plugin;

	public Duel(Main plugin) {
		Duel.plugin = plugin;
	}

	// information about both players before they were teleported into the duel
	private static PlayerState callingPlayerState;
	private static PlayerState challengedPlayerState;

	/*
	 * Duels have two players involved A SharedData object is also passed in so that
	 * the duel object can keep track of information more easily
	 */
	public Duel(Player callingPlayer, Player challengedPlayer) {
		Duel.callingPlayer = callingPlayer;
		Duel.challengedPlayer = challengedPlayer;
	}

	// fancy getters and setters
	public boolean isAccepted() {
		return accepted;
	}

	public void accept() {
		accepted = true;
	}

	public Player getCallingPlayer() {
		return callingPlayer;
	}

	public Player getChallengedPlayer() {
		return challengedPlayer;
	}

	public boolean isExpired() {
		return ((System.currentTimeMillis() - timeCreated) >= EXPIRE_TIME);
	}

	public static void beforeDuel() {
		//callingPlayerState = new PlayerState(callingPlayer);
		//challengedPlayerState = new PlayerState(challengedPlayer);
		// send both players a cool title message
		callingPlayer.sendTitle(ChatColor.DARK_RED + "Prepare to fight!", "", 10, 40, 10);
		challengedPlayer.sendTitle(ChatColor.DARK_RED + "Prepare to fight!", "", 10, 40, 10);

	}

	// handles beginning the duel
	public static void beginDuel() {
		// play a cool sound ;)
		callingPlayer.playEffect(callingPlayer.getLocation(), Effect.ENDERDRAGON_GROWL, null);
		challengedPlayer.playEffect(challengedPlayer.getLocation(), Effect.ENDERDRAGON_GROWL, null);
		callingPlayer.sendTitle(ChatColor.DARK_RED + "START!", "", 10, 40, 10);
		challengedPlayer.sendTitle(ChatColor.DARK_RED + "START!", "", 10, 10, 10);
	}

	/*
	 * Ends a duel
	 */
	public static void endDuel(Player loser, Player victor) {
		// restore player states
		if (loser != null && victor != null) {
			//callingPlayerState.restore();
			//challengedPlayerState.restore();
			// move this duel to resolvedDuels
			
			
		}

	}

	// utility method for removing expired duels
	public static void removeExpiredDuels() {
		ArrayList<Duel> expired = new ArrayList<Duel>();
		for (Duel du : plugin.queuedDuels) {
			expired.add(du);
		}
		for (int i = 0; i < expired.size(); i++) {
			Duel d = expired.get(1);
			if (d.isExpired()) {
				d.getCallingPlayer().sendMessage(ChatColor.RED + "Your duel offer to "
						+ d.getChallengedPlayer().getDisplayName() + " has expired");
				plugin.queuedDuels.remove(i);
			}
			expired.remove(1);
		}
	}

	public static Duel getOtherPlayer(ArrayList<Duel> duels, Player playerA) {
		for (Duel d : duels) {
			if (d.getCallingPlayer() == playerA) {
				return (Duel) d.getChallengedPlayer();
			} else if (d.getChallengedPlayer() == playerA) {
				return (Duel) d.getCallingPlayer();
			}
		}
		return null;
	}

	// utility method for getting a duel with specific players
	public static Duel getDuel(ArrayList<Duel> duels, Player callingPlayer, Player challengedPlayer) {
		for (Duel d : duels) {
			if (d.getCallingPlayer() == callingPlayer && d.getChallengedPlayer() == challengedPlayer) {
				return d;
			}
		}
		return null;
	}

	// utility method for deleting all where two players are involved
	public static void deleteDuelsBetween(ArrayList<Duel> duels, Player playerA, Player playerB) {
		for (int i = 0; i < duels.size(); i++) {
			Duel d = duels.get(i);
			if ((d.getCallingPlayer() == playerA && d.getChallengedPlayer() == playerB)
					|| (d.getCallingPlayer() == playerB && d.getChallengedPlayer() == playerA)) {
				duels.remove(i);
			}
		}
	}

	// utility method for deleting all duels where a certain player is involved
	public static void deleteDuelsWhereInvolved(ArrayList<Duel> duels, Player player) {
		for (int i = 0; i < duels.size(); i++) {
			Duel d = duels.get(i);
			if (d.getCallingPlayer() == player || d.getChallengedPlayer() == player) {
				duels.remove(i);
			}
		}
	}

	// utility method for checking whether a player is involved in a duel
	public static boolean involvedInDuel(ArrayList<Duel> duels, Player player) {
		for (Duel d : duels) {
			if (d.getCallingPlayer() == player || d.getChallengedPlayer() == player) {
				return true;
			}
		}
		return false;
	}

	// utility method for checking whether two players are in some way involved via
	// the duel system
	// returns true if the players are involved in a duel object, returns false
	// otherwise
	public static boolean bothInvolved(ArrayList<Duel> duels, Player playerA, Player playerB) {
		for (int i = 0; i < duels.size(); i++) {
			Duel d = duels.get(i);
			if ((d.getCallingPlayer() == playerA && d.getChallengedPlayer() == playerB)
					|| (d.getCallingPlayer() == playerB && d.getChallengedPlayer() == playerA)) {
				return true;
			}
		}
		return false;
	}

	private static double time() {
		double time = 0;
		time = System.currentTimeMillis();
		return time;
	}
}
