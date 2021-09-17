package me.cageydinosaur.duels;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class WorkerSlow implements Runnable {

	int EXPIRE_TIME = 5;
	public static double timeCreated;
	double currentTime;
	double timeTill;
	double checkSeconds = 0;
	double checkSecond = 0;

	Main plugin;

	public WorkerSlow(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {

		Player callingPlayer = null;
		Player challengedPlayer = null;

		if (!plugin.aboutToStart.isEmpty()) {
			for (int i = 0; i < plugin.aboutToStart.size(); i++) {
				Duel d = plugin.aboutToStart.get(i);
				callingPlayer = d.getCallingPlayer();
				challengedPlayer = d.getChallengedPlayer();
				currentTime = System.currentTimeMillis();
				timeCreated = System.currentTimeMillis();
				if (plugin.distanceMsg()) {
					if (Duel.bothInvolved(plugin.aboutToStart, callingPlayer, challengedPlayer)) {
						Double challengedPlayerLocX = callingPlayer.getLocation().getX();
						Double challengedPlayerLocZ = callingPlayer.getLocation().getZ();
						Double callingPlayerLocX = challengedPlayer.getLocation().getX();
						Double callingPlayerLocZ = challengedPlayer.getLocation().getZ();
						Double triangle = Math.sqrt(
								(callingPlayerLocX - challengedPlayerLocX) * (callingPlayerLocX - challengedPlayerLocX)
										+ (callingPlayerLocZ - challengedPlayerLocZ)
												* (callingPlayerLocZ - challengedPlayerLocZ));

			

						if (triangle > plugin.autoConfDis() || triangle < -(plugin.autoConfDis())) {
							Duel.beforeDuel();
							Duel duel = Duel.getDuel(plugin.aboutToStart, callingPlayer, challengedPlayer);
							plugin.aboutToStartTwo.add(duel);
							plugin.aboutToStart.remove(duel);

						} else {
							// callingPlayer.sendMessage(ChatColor.GREEN + "Move " +
							// ((Math.abs(Math.rint(triangle - 20)))) + " more blocks");
							// challengedPlayer.sendMessage(ChatColor.GREEN + "Move " +
							// ((Math.abs(Math.rint(triangle - 20)))) + " more blocks");=
							callingPlayer.sendTitle(plugin.chat(plugin.customDisMsg().replace("{distance}", Double.toString(Math.abs(Math.rint(triangle - plugin.autoConfDis()))))), "", 5, 10, 5);
							challengedPlayer.sendTitle(plugin.chat(plugin.customDisMsg().replace("{distance}", Double.toString(Math.abs(Math.rint(triangle - plugin.autoConfDis()))))), "", 5, 10, 5);
						}
					}
				}
			}
		}
		if (!(plugin.aboutToStartTwo.isEmpty())) {
			for (int i = 0; i < plugin.aboutToStartTwo.size(); i++) {
				Duel d = plugin.aboutToStartTwo.get(i);
				callingPlayer = d.getCallingPlayer();
				challengedPlayer = d.getChallengedPlayer();
				if (Duel.bothInvolved(plugin.aboutToStartTwo, callingPlayer, challengedPlayer)) {
					Duel duel = Duel.getDuel(plugin.aboutToStartTwo, callingPlayer, challengedPlayer);
					currentTime = System.currentTimeMillis();
					timeTill = ((currentTime - timeCreated) / 1000);

					/*
					 * Debugging callingPlayer.sendMessage("timecreated " +
					 * Double.toString(Math.floor(timeCreated)));
					 * callingPlayer.sendMessage("CurrentTime " +
					 * Double.toString(Math.floor(currentTime)));
					 * callingPlayer.sendMessage("Subtracted " + Double.toString(timeTill));
					 */
					if (timeTill < plugin.countDown()) {
						callingPlayer.sendMessage(plugin.chat(plugin.CusCountDownMsg().replace("{time}", Double.toString((plugin.countDown() - Math.round(timeTill))))));
						challengedPlayer.sendMessage(plugin.chat(plugin.CusCountDownMsg().replace("{time}", Double.toString((plugin.countDown() - Math.round(timeTill))))));

					} else if (timeTill < (plugin.countDown() + 5) && timeTill > plugin.countDown()) {

						Duel.beginDuel();
						plugin.ongoingDuels.add(duel);
						plugin.aboutToStartTwo.remove(duel);

					}
				}

			}

		}
	}

}
