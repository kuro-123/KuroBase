package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurodiscord.DiscordMessage;
import host.kuro.kurodiscord.KuroDiscord;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerLister implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.BLUE);
		sb.append("[ ");
		sb.append(ChatColor.WHITE);
		sb.append(player.getDisplayName());
		sb.append(ChatColor.BLUE);
		sb.append(" ] さんが参加!!");
		String message = new String(sb);
		e.setJoinMessage(message);

		DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
		if (dm != null) {
			dm.SendDiscordBlueMessage(message);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GRAY);
		sb.append("[ ");
		sb.append(ChatColor.WHITE);
		sb.append(player.getDisplayName());
		sb.append(ChatColor.BLUE);
		sb.append(" ] さんが退場");
		String message = new String(sb);
		e.setQuitMessage(message);

		DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
		if (dm != null) {
			dm.SendDiscordGrayMessage(message);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();

		String killername = "";
		String killitem = "";
		String cause_name = "";

		Player killer = e.getEntity().getKiller();
		if (killer != null) {
			if (killer instanceof Player) {
				killername = ((Player)killer).getDisplayName();
				PlayerInventory inv = ((Player)killer).getInventory();
				if (inv != null) {
					ItemStack item = inv.getItemInMainHand();
					if (item != null) {
						killitem = item.getItemMeta().getDisplayName();
					}
				}
			}
		}

		EntityDamageEvent damage = e.getEntity().getLastDamageCause();
		if (damage != null) {
			EntityDamageEvent.DamageCause cause = damage.getCause();
			if (cause != null) {
				switch (cause) {
					case BLOCK_EXPLOSION	: cause_name = KuroBase.getPlugin().getConfig().getString("death.BLOCK_EXPLOSION", ""); break;
					case CONTACT			: cause_name = KuroBase.getPlugin().getConfig().getString("death.CONTACT", ""); break;
					case CRAMMING			: cause_name = KuroBase.getPlugin().getConfig().getString("death.CRAMMING", ""); break;
					case CUSTOM				: cause_name = KuroBase.getPlugin().getConfig().getString("death.CUSTOM", ""); break;
					case DRAGON_BREATH		: cause_name = KuroBase.getPlugin().getConfig().getString("death.DRAGON_BREATH", ""); break;
					case DROWNING			: cause_name = KuroBase.getPlugin().getConfig().getString("death.DROWNING", ""); break;
					case DRYOUT				: cause_name = KuroBase.getPlugin().getConfig().getString("death.DRYOUT", ""); break;
					case ENTITY_ATTACK		: cause_name = KuroBase.getPlugin().getConfig().getString("death.ENTITY_ATTACK", ""); break;
					case ENTITY_EXPLOSION	: cause_name = KuroBase.getPlugin().getConfig().getString("death.ENTITY_EXPLOSION", ""); break;
					case ENTITY_SWEEP_ATTACK: cause_name = KuroBase.getPlugin().getConfig().getString("death.ENTITY_SWEEP_ATTACK", ""); break;
					case FALL				: cause_name = KuroBase.getPlugin().getConfig().getString("death.FALL", ""); break;
					case FALLING_BLOCK		: cause_name = KuroBase.getPlugin().getConfig().getString("death.FALLING_BLOCK", ""); break;
					case FIRE				: cause_name = KuroBase.getPlugin().getConfig().getString("death.FIRE", ""); break;
					case FIRE_TICK			: cause_name = KuroBase.getPlugin().getConfig().getString("death.FIRE_TICK", ""); break;
					case FLY_INTO_WALL		: cause_name = KuroBase.getPlugin().getConfig().getString("death.FLY_INTO_WALL", ""); break;
					case HOT_FLOOR			: cause_name = KuroBase.getPlugin().getConfig().getString("death.HOT_FLOOR", ""); break;
					case LAVA				: cause_name = KuroBase.getPlugin().getConfig().getString("death.LAVA", ""); break;
					case LIGHTNING			: cause_name = KuroBase.getPlugin().getConfig().getString("death.LIGHTNING", ""); break;
					case MAGIC				: cause_name = KuroBase.getPlugin().getConfig().getString("death.MAGIC", ""); break;
					case MELTING			: cause_name = KuroBase.getPlugin().getConfig().getString("death.MELTING", ""); break;
					case POISON				: cause_name = KuroBase.getPlugin().getConfig().getString("death.POISON", ""); break;
					case PROJECTILE			: cause_name = KuroBase.getPlugin().getConfig().getString("death.PROJECTILE", ""); break;
					case STARVATION			: cause_name = KuroBase.getPlugin().getConfig().getString("death.STARVATION", ""); break;
					case SUFFOCATION		: cause_name = KuroBase.getPlugin().getConfig().getString("death.SUFFOCATION", ""); break;
					case SUICIDE			: cause_name = KuroBase.getPlugin().getConfig().getString("death.SUICIDE", ""); break;
					case THORNS				: cause_name = KuroBase.getPlugin().getConfig().getString("death.THORNS", ""); break;
					case VOID				: cause_name = KuroBase.getPlugin().getConfig().getString("death.VOID", ""); break;
					case WITHER				: cause_name = KuroBase.getPlugin().getConfig().getString("death.WITHER", ""); break;
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.YELLOW);
		if (killername.length() > 0) {
			if (killitem.length() <= 0) {
				killitem = "不明";
			}
			sb.append(String.format(KuroBase.getPlugin().getConfig().getString("death.MESSAGE_ATTACK", ""), player.getDisplayName(), cause_name, killername, killitem));
		} else {
			sb.append(String.format(KuroBase.getPlugin().getConfig().getString("death.MESSAGE_NORMAL", ""), player.getDisplayName(), cause_name));
		}
		String message = new String(sb);
		e.setDeathMessage(message);

		DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
		if (dm != null) {
			dm.SendDiscordRedMessage(message);
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