package me.cageydinosaur.duels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TabCompletion implements TabCompleter {

	Main plugin;

	public TabCompletion(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length == 1) {
			List<String> commands = new ArrayList<>();
			if (sender.hasPermission("duel")) {
				if (sender.hasPermission("duel.accept")) {
					commands.add("accept");
				}
				if (sender.hasPermission("duel.deny")) {
					commands.add("deny");
				}
				if (sender.hasPermission("duel.cancel")) {
					commands.add("cancel");
				}
				if (sender.hasPermission("duel.confirm")) {
					commands.add("confirm");
				}
				if (sender.hasPermission("duel.reload")) {
					commands.add("reload");
				}
				if (sender.hasPermission("duel.send")) {
					Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
					Bukkit.getServer().getOnlinePlayers().toArray(players);
					for (int i = 0; i < players.length; i++) {
						commands.add(players[i].getName());
					}
				}
				return commands;
			}
		}

		if (args.length == 2) {
			Player callingPlayer = null;
			Player challengedPlayer = (Player) sender;
			ArrayList<String> accept = new ArrayList<String>();
			for (Duel d : plugin.queuedDuels) {
				if (d.getChallengedPlayer() == challengedPlayer)
					accept.add(d.getCallingPlayer().getName());
			}
			return accept;

		}
		return null;
	}

}