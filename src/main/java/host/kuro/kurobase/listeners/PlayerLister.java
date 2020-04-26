package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurodiscord.DiscordMessage;
import host.kuro.kurodiscord.KuroDiscord;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.raid.RaidTriggerEvent;

public class PlayerLister implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		String message = e.getJoinMessage();
		DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
		if (dm != null) {
			dm.SendDiscordBlueMessage(message);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		String message = e.getQuitMessage();
		DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
		if (dm != null) {
			dm.SendDiscordGrayMessage(message);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		String message = e.getDeathMessage();
		DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
		if (dm != null) {
			dm.SendDiscordYellowMessage(message);
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		String message = e.getLeaveMessage();
		DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
		if (dm != null) {
			dm.SendDiscordYellowMessage(message);
		}
	}
}