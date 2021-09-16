package me.cageydinosaur.duels;

import java.util.ArrayList;

import me.cageydinosaur.duels.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Commands implements CommandExecutor {

	boolean isOnline = false;

	Main plugin;

	public Commands(Main plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("duel")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.GREEN + "Usage:");
				sender.sendMessage(ChatColor.GREEN + "/duel <player> - Duel a player");
				sender.sendMessage(ChatColor.GREEN + "/duel accept - Accept another player's duel");
				sender.sendMessage(ChatColor.RED + "/duel deny - Deny another player's duel");
				sender.sendMessage(ChatColor.GREEN + "/duel confirm - Confirm when you are ready to duel");
				sender.sendMessage(ChatColor.RED + "/duel cancel - Cancel the duel");
			} else if (args.length > 0) {
				if (args[0].equalsIgnoreCase("reload")) {
					plugin.reloadConfig();
					sender.sendMessage("Reloaded the config");
					return true;
				} else if (args[0].equalsIgnoreCase("accept")) {
					Player challengedPlayer = (Player) sender;
					Player callingPlayer = null;
					if (!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED + "You are not a player!");
						return true;
					}
					if (Duel.involvedInDuel(plugin.ongoingDuels, challengedPlayer)) {
						challengedPlayer.sendMessage(ChatColor.RED + "You are already in an ongoing Duel!");
						return true;
					}
					if (!challengedPlayer.hasPermission("duel.accept")) {
						challengedPlayer
								.sendMessage(ChatColor.RED + "You do not have permission to accept/deny duels!");
						return true;
					}

					ArrayList<Player> accept = new ArrayList<Player>();
					for (Duel d : plugin.queuedDuels) {
						if (d.getChallengedPlayer() == challengedPlayer)
							accept.add(d.getCallingPlayer());
					}
					if (accept.size() == 0) {
						challengedPlayer.sendMessage(ChatColor.RED + "You do not have any pending duel requests!");
						return true;
					}
					if (accept.size() >= 2) {
						if (args.length == 0) {
							challengedPlayer
									.sendMessage(ChatColor.GREEN + "You have been challenged by multiple people!");
							for (Player p : accept) {
								challengedPlayer.sendMessage(ChatColor.GREEN + " - " + p.getDisplayName());
							}
							challengedPlayer.sendMessage(
									ChatColor.GREEN + "You must specify whose challenged you want to accept");
						}
						callingPlayer = Bukkit.getPlayer(args[0]);
						if (callingPlayer == null && !args[0].equals("")) {
							challengedPlayer
									.sendMessage(ChatColor.RED + args[0] + " has not challenged you to a duel!");
							return true;
						} else if (callingPlayer == null) {
							challengedPlayer.sendMessage(ChatColor.RED + args[0] + " is not online");
							return true;
						}
					} else {
						callingPlayer = accept.get(0);
					}
					Duel duel = Duel.getDuel(plugin.queuedDuels, callingPlayer, challengedPlayer);
					duel.accept();
					callingPlayer.sendMessage(
							ChatColor.GREEN + challengedPlayer.getName() + " has accepted your request  :)");
					challengedPlayer.sendMessage(
							ChatColor.GREEN + "You have accepted " + callingPlayer.getName() + "'s request");

					callingPlayer.sendMessage("");
					challengedPlayer.sendMessage("");

					callingPlayer.sendMessage(ChatColor.GREEN + "Find your stance");
					challengedPlayer.sendMessage(ChatColor.GREEN + "Find your stance");

					plugin.queuedDuels.remove(duel);
					plugin.aboutToStart.add(duel);
					Duel.beforeDuel();

					return true;
				} else if (args[0].equalsIgnoreCase("deny")) {
					Player challengedPlayer = (Player) sender;
					Player callingPlayer = null;
					if (!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED + "You are not a player!");
						return true;
					}
					if (Duel.involvedInDuel(plugin.ongoingDuels, challengedPlayer)) {
						challengedPlayer.sendMessage(ChatColor.RED + "You are already in an ongoing Duel!");
						return true;
					}
					if (!challengedPlayer.hasPermission("duel.accept")) {
						challengedPlayer
								.sendMessage(ChatColor.RED + "You do not have permission to accept/deny duels!");
						return true;
					}

					ArrayList<Player> deny = new ArrayList<Player>();
					for (Duel d : plugin.queuedDuels) {
						if (d.getChallengedPlayer() == challengedPlayer)
							deny.add(d.getCallingPlayer());
					}
					if (deny.size() == 0) {
						challengedPlayer.sendMessage(ChatColor.RED + "You do not have any pending duel requests!");
						return true;
					}
					if (deny.size() >= 2) {
						if (args.length == 0) {
							challengedPlayer
									.sendMessage(ChatColor.GREEN + "You have been challenged by multiple people!");
							for (Player p : deny) {
								challengedPlayer.sendMessage(ChatColor.GREEN + " - " + p.getDisplayName());
							}
							challengedPlayer.sendMessage(
									ChatColor.GREEN + "You must specify whose challenged you want to deny");
						}
						callingPlayer = Bukkit.getPlayer(args[0]);
						if (callingPlayer == null && !args[0].equals("")) {
							challengedPlayer
									.sendMessage(ChatColor.RED + args[0] + " has not challenged you to a duel!");
							return true;
						} else if (callingPlayer == null) {
							challengedPlayer.sendMessage(ChatColor.RED + args[0] + " is not online");
							return true;
						}
					} else {
						callingPlayer = deny.get(0);
					}
					Duel duel = Duel.getDuel(plugin.queuedDuels, callingPlayer, challengedPlayer);
					duel.accept();
					callingPlayer
							.sendMessage(ChatColor.RED + challengedPlayer.getName() + " has denied your request  :(");
					challengedPlayer
							.sendMessage(ChatColor.RED + "You have denied " + callingPlayer.getName() + "'s request");
					plugin.queuedDuels.remove(duel);

					return true;
				} else if (args[0].equalsIgnoreCase("confirm")) {
					Player p = (Player) sender;
					Player callingPlayer = null;
					Player challengedPlayer = null;

					for (Duel d : plugin.aboutToStart) {
						if (d.getCallingPlayer() == p) {
							callingPlayer = p;
							challengedPlayer = d.getChallengedPlayer();
						} else if (d.getChallengedPlayer() == p) {
							callingPlayer = d.getCallingPlayer();
							challengedPlayer = p;
						}
					}

					if (!(Duel.bothInvolved(plugin.aboutToStart, callingPlayer, challengedPlayer))) {
						sender.sendMessage(ChatColor.RED + "You have to accept a duel first!");
						return true;
					}
					if ((Duel.involvedInDuel(plugin.aboutToStart, callingPlayer) && (sender == callingPlayer)
							&& (!(plugin.player1.contains(callingPlayer))))) {
						if (plugin.player2.contains(challengedPlayer)) {
							plugin.player1.add(callingPlayer);
						} else {
							sender.sendMessage(
									ChatColor.GREEN + "Ok, waiting for the other player to confirm their position");
							plugin.player1.add(callingPlayer);
						}
					}
					if ((Duel.involvedInDuel(plugin.aboutToStart, challengedPlayer) && sender == challengedPlayer)
							&& (!(plugin.player2.contains(challengedPlayer)))) {
						if (plugin.player1.contains(callingPlayer)) {
							plugin.player2.add(challengedPlayer);
						} else {
							sender.sendMessage(
									ChatColor.GREEN + "Ok, waiting for the other player to confirm their position");
							plugin.player2.add(challengedPlayer);
						}

					}

					if (plugin.player1.contains(callingPlayer) && plugin.player2.contains(challengedPlayer)) {
						Duel duel = Duel.getDuel(plugin.aboutToStart, callingPlayer, challengedPlayer);
						plugin.player1.remove(callingPlayer);
						plugin.player2.remove(challengedPlayer);
						plugin.aboutToStartTwo.add(duel);
						plugin.aboutToStart.remove(duel);
						callingPlayer.sendMessage(ChatColor.GREEN + "Starting the duel");
						challengedPlayer.sendMessage(ChatColor.GREEN + "Starting the duel");
						return true;

					}
				} else if (args[0].equalsIgnoreCase("cancel")) {
					sender.sendMessage("You canceled the duel with ");
				} else {
					Player callingPlayer = (Player) sender;
					Player challengedPlayer = Bukkit.getPlayer(args[0]);
					if (!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED + "You are not a player!");
						return true;
					}
					if (!(callingPlayer.hasPermission("duel.send"))) {
						callingPlayer
								.sendMessage(ChatColor.RED + "You do not have the permission to send others duels!");
						return true;
					}
					if (challengedPlayer == null) {
						callingPlayer.sendMessage(ChatColor.RED + args[0] + " is not online");
						return true;
					}

					Double challengedPlayerLocX = callingPlayer.getLocation().getX();
					Double challengedPlayerLocZ = callingPlayer.getLocation().getZ();
					Double callingPlayerLocX = challengedPlayer.getLocation().getX();
					Double callingPlayerLocZ = challengedPlayer.getLocation().getZ();
					Double triangle = Math.sqrt((callingPlayerLocX - challengedPlayerLocX)
							* (callingPlayerLocX - challengedPlayerLocX)
							+ (callingPlayerLocZ - challengedPlayerLocZ) * (callingPlayerLocZ - challengedPlayerLocZ));

					if (triangle > 10 || triangle < -10) {
						callingPlayer
								.sendMessage(ChatColor.RED + "You are too far away from the player you want to duel!");
						return true;
					}
					if (Duel.involvedInDuel(plugin.ongoingDuels, callingPlayer)) {
						sender.sendMessage(ChatColor.RED + "You are involed in a duel!");
						return true;
					}
					if (callingPlayer.getName() == challengedPlayer.getName()) {
						callingPlayer.sendMessage(ChatColor.RED + "You can not challenged yourself to a duel!");
						return true;
					}

					if (!challengedPlayer.hasPermission("duels.accept")) {
						callingPlayer.sendMessage(ChatColor.RED + args[0] + " can not accept duel offers!");
						return true;
					}
					if (Duel.bothInvolved(plugin.queuedDuels, callingPlayer, challengedPlayer)) {
						callingPlayer.sendMessage(ChatColor.RED + "You have already sent a request to " + args[0]);
						return true;
					}
					if (!plugin.mutingPlayers.contains(challengedPlayer)) {
						challengedPlayer.sendMessage(
								ChatColor.GREEN + callingPlayer.getDisplayName() + " has challenged to a Duel!");
						challengedPlayer.sendMessage(ChatColor.GREEN + "Accept their request with /duel accept");
					}
					callingPlayer.sendMessage(
							ChatColor.GREEN + "You have challenged " + challengedPlayer.getName() + " to a duel!");

					plugin.queuedDuels.add(new Duel(callingPlayer, challengedPlayer));

					return true;
				}
			}
		}
		return true;

	}

	private static double time() {
		double time = 0;
		time = System.currentTimeMillis();
		return time;
	}

}
