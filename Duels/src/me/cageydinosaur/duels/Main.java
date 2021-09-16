package me.cageydinosaur.duels;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public ArrayList<Duel> queuedDuels = new ArrayList<Duel>();
	// duels that are about to start
	public ArrayList<Duel> aboutToStart = new ArrayList<Duel>();
	public ArrayList<Duel> aboutToStartTwo = new ArrayList<Duel>();
	// duels that are being fought at the moment
	public ArrayList<Duel> ongoingDuels = new ArrayList<Duel>();
	// players that are muting duel offers
	public ArrayList<Player> mutingPlayers = new ArrayList<Player>();

	public ArrayList<Player> player1 = new ArrayList<Player>();
	public ArrayList<Player> player2 = new ArrayList<Player>();

	public void onEnable() {

		this.saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(new Events(this), this);
		getCommand("duel").setExecutor(new Commands(this));
		getCommand("duel").setTabCompleter(new TabCompletion(this));
		// getServer().getScheduler().scheduleSyncRepeatingTask(this, new
		// WorkerFast(this), 1, 1);
		getServer().getScheduler().runTaskTimerAsynchronously(this, new WorkerSlow(this), 1, 20);
	}

	public void reloadTheConfig() {
		this.reloadConfig();
	}
	
	public boolean distanceMsg() {
		if (this.getConfig().getString("use_distance_to_autoconfirm") == "true"){
			return true;
		}
		return false;
	}
}
