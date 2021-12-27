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
			if (sender.hasPermission("westernstandoff")) {
				if (sender.hasPermission("westernstandoff.accept")) {
					commands.add("accept");
				}
				if (sender.hasPermission("westernstandoff.deny")) {
					commands.add("deny");
				}
				if (sender.hasPermission("westernstandoff.confirm")) {
					commands.add("confirm");
				}
				if (sender.hasPermission("westernstandoff.reload")) {
					commands.add("reload");
				}

				return commands;
			}
		}

		if (args.length == 2)

		{
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