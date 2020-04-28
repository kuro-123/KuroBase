package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurodiscord.DiscordMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import java.util.ArrayList;

public class PlayerLister implements Listener {

	KuroBase plugin = null;

	public PlayerLister(KuroBase plugin) {
		this.plugin = plugin;
	}

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

		// UPDATE
		ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
		args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
		int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.QUIT.UPDATE.PLAYER"), args);
		args.clear();
		args = null;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();

		// UPDATE
		ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
		args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
		int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.DEATH.UPDATE.PLAYER"), args);
		args.clear();
		args = null;

		String killername = "";
		String killitem = "";
		String cause_name = "";

		Player killer = e.getEntity().getKiller();
		if (killer != null) {
			// UPDATE
			ArrayList<DatabaseArgs> kargs = new ArrayList<DatabaseArgs>();
			kargs.add(new DatabaseArgs("c", killer.getUniqueId().toString())); // UUID
			ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.KILL.UPDATE.PLAYER"), kargs);
			kargs.clear();
			kargs = null;

			killername = ((Player)killer).getDisplayName();
			PlayerInventory inv = ((Player)killer).getInventory();
			if (inv != null) {
				ItemStack item = inv.getItemInMainHand();
				if (item != null) {
					killitem = item.getItemMeta().getDisplayName();
				}
			}
		}

		EntityDamageEvent damage = e.getEntity().getLastDamageCause();
		if (damage != null) {
			EntityDamageEvent.DamageCause cause = damage.getCause();
			if (cause != null) {
				switch (cause) {
					case BLOCK_EXPLOSION	: cause_name = Language.translate("death.BLOCK_EXPLOSION"); break;
					case CONTACT			: cause_name = Language.translate("death.CONTACT"); break;
					case CRAMMING			: cause_name = Language.translate("death.CRAMMING"); break;
					case CUSTOM				: cause_name = Language.translate("death.CUSTOM"); break;
					case DRAGON_BREATH		: cause_name = Language.translate("death.DRAGON_BREATH"); break;
					case DROWNING			: cause_name = Language.translate("death.DROWNING"); break;
					case DRYOUT				: cause_name = Language.translate("death.DRYOUT"); break;
					case ENTITY_ATTACK		: cause_name = Language.translate("death.ENTITY_ATTACK"); break;
					case ENTITY_EXPLOSION	: cause_name = Language.translate("death.ENTITY_EXPLOSION"); break;
					case ENTITY_SWEEP_ATTACK: cause_name = Language.translate("death.ENTITY_SWEEP_ATTACK"); break;
					case FALL				: cause_name = Language.translate("death.FALL"); break;
					case FALLING_BLOCK		: cause_name = Language.translate("death.FALLING_BLOCK"); break;
					case FIRE				: cause_name = Language.translate("death.FIRE"); break;
					case FIRE_TICK			: cause_name = Language.translate("death.FIRE_TICK"); break;
					case FLY_INTO_WALL		: cause_name = Language.translate("death.FLY_INTO_WALL"); break;
					case HOT_FLOOR			: cause_name = Language.translate("death.HOT_FLOOR"); break;
					case LAVA				: cause_name = Language.translate("death.LAVA"); break;
					case LIGHTNING			: cause_name = Language.translate("death.LIGHTNING"); break;
					case MAGIC				: cause_name = Language.translate("death.MAGIC"); break;
					case MELTING			: cause_name = Language.translate("death.MELTING"); break;
					case POISON				: cause_name = Language.translate("death.POISON"); break;
					case PROJECTILE			: cause_name = Language.translate("death.PROJECTILE"); break;
					case STARVATION			: cause_name = Language.translate("death.STARVATION"); break;
					case SUFFOCATION		: cause_name = Language.translate("death.SUFFOCATION"); break;
					case SUICIDE			: cause_name = Language.translate("death.SUICIDE"); break;
					case THORNS				: cause_name = Language.translate("death.THORNS"); break;
					case VOID				: cause_name = Language.translate("death.VOID"); break;
					case WITHER				: cause_name = Language.translate("death.WITHER"); break;
				}
			}
			Entity damager = ((EntityDamageByEntityEvent) damage).getDamager();
			if (damager != null && killer == null) {
				killername = damager.getName();
			}
		}

		String fmt;
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.YELLOW);
		if (killername.length() > 0) {
			if (killitem.length() <= 0) {
				killitem = "不明";
			}
			fmt = Language.translate("death.MESSAGE_ATTACK");
			sb.append(String.format(fmt, player.getDisplayName(), cause_name, killername, killitem));
		} else {
			fmt = Language.translate("death.MESSAGE_NORMAL");
			sb.append(String.format(fmt, player.getDisplayName(), cause_name));
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
		Player player = e.getPlayer();

		String message = e.getLeaveMessage();
		DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
		if (dm != null) {
			dm.SendDiscordYellowMessage(message);
		}

		// UPDATE
		ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
		args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
		int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.KICK.UPDATE.PLAYER"), args);
		args.clear();
		args = null;
	}

	@EventHandler
	public void onChat(PlayerChatEvent e) {
		Player player = e.getPlayer();

		// UPDATE
		ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
		args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
		int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.CHAT.UPDATE.PLAYER"), args);
		args.clear();
		args = null;
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		Player player = e.getPlayer();

		// INSERT
		ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
		args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
		args.add(new DatabaseArgs("c", player.getName())); // name
		args.add(new DatabaseArgs("i", ""+0)); // rank
		args.add(new DatabaseArgs("c", e.getAddress().toString())); // ip
		args.add(new DatabaseArgs("c", e.getHostname())); // host
		args.add(new DatabaseArgs("c", e.getRealAddress().toString())); // rip
		args.add(new DatabaseArgs("c", e.getRealAddress().getHostName())); // rhost
		args.add(new DatabaseArgs("i", ""+player.getTotalExperience())); // totalexp
		args.add(new DatabaseArgs("d", ""+player.getExp())); // exp
		args.add(new DatabaseArgs("i", ""+player.getExpToLevel())); // exptolevel
		args.add(new DatabaseArgs("i", ""+player.getLevel())); // level
		args.add(new DatabaseArgs("c", ""+player.getLocation().getWorld().getName())); // world
		args.add(new DatabaseArgs("d", ""+player.getLocation().getX())); // x
		args.add(new DatabaseArgs("d", ""+player.getLocation().getY())); // y
		args.add(new DatabaseArgs("d", ""+player.getLocation().getZ())); // z
		args.add(new DatabaseArgs("i", ""+0)); // login
		args.add(new DatabaseArgs("i", ""+0)); // break
		args.add(new DatabaseArgs("i", ""+0)); // place
		args.add(new DatabaseArgs("i", ""+0)); // kill
		args.add(new DatabaseArgs("i", ""+0)); // death
		args.add(new DatabaseArgs("i", ""+0)); // kick
		args.add(new DatabaseArgs("i", ""+0)); // chat
		args.add(new DatabaseArgs("i", ""+0)); // cmd
		args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
		int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.LOGIN.INSERT.PLAYER"), args);
		args.clear();
		args = null;

		// UPDATE
		ArrayList<DatabaseArgs> uargs = new ArrayList<DatabaseArgs>();
		uargs.add(new DatabaseArgs("c", e.getAddress().toString())); // ip
		uargs.add(new DatabaseArgs("c", e.getHostname())); // host
		uargs.add(new DatabaseArgs("c", e.getRealAddress().toString())); // rip
		uargs.add(new DatabaseArgs("c", e.getRealAddress().getHostName())); // rhost
		uargs.add(new DatabaseArgs("i", ""+player.getTotalExperience())); // totalexp
		uargs.add(new DatabaseArgs("d", ""+player.getExp())); // exp
		uargs.add(new DatabaseArgs("i", ""+player.getExpToLevel())); // exptolevel
		uargs.add(new DatabaseArgs("i", ""+player.getLevel())); // level
		uargs.add(new DatabaseArgs("c", ""+player.getLocation().getWorld().getName())); // world
		uargs.add(new DatabaseArgs("d", ""+player.getLocation().getX())); // x
		uargs.add(new DatabaseArgs("d", ""+player.getLocation().getY())); // y
		uargs.add(new DatabaseArgs("d", ""+player.getLocation().getZ())); // z
		uargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
		ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.LOGIN.UPDATE.PLAYER"), uargs);
		uargs.clear();
		uargs = null;
	}

	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();

		// UPDATEex
		ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
		args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
		int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.CMD.UPDATE.PLAYER"), args);
		args.clear();
		args = null;
	}
}