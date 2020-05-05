package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.tasks.SkinTask;
import host.kuro.kurobase.utils.*;
import host.kuro.kurodiscord.DiscordMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class PlayerListener implements Listener {

	KuroBase plugin = null;
	private static HashMap<Player, Long> play_time = new HashMap<Player, Long>();
	public PlayerListener(KuroBase plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		try {
			Player player = e.getPlayer();

			// knock sound
			SoundUtils.BroadcastSound("door-wood-knock1", false);

			plugin.GetAfkStatus().put(player, System.currentTimeMillis()); // afk

			// minarai check update
			PlayerUtils.UpdateJyumin(plugin, KuroBase.getDB(), player);

			// setting rank
			player.setOp(true);
			int rank = PlayerUtils.GetRank(KuroBase.getDB(), player);
			if (rank >= PlayerUtils.RANK_KANRI) {
				if (rank == PlayerUtils.RANK_KANRI) {
					SoundUtils.BroadcastSound("kanri", true);
				}
				else if (rank == PlayerUtils.RANK_NUSHI) {
					player.setGameMode(GameMode.CREATIVE);
					SoundUtils.BroadcastSound("kuro", true);
				}
			} else {
				if (rank == PlayerUtils.RANK_MINARAI) {
					player.setOp(false);
				}
				player.setGameMode(GameMode.SURVIVAL);
			}

			// check skin make
			int days = plugin.getConfig().getInt("Skin.cooldays", 3);
			if (PlayerUtils.GetElapsedDays(KuroBase.getDB(), player) >= days) {
				SkinTask task = new SkinTask(plugin, player);
				task.runTaskLater(plugin, 20);
			}

			// display name setting
			String disp_name = PlayerUtils.GetDisplayName(plugin.getDB(), player);
			player.setDisplayName(disp_name);

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

			player.sendMessage(ChatColor.GOLD + Language.translate("plugin.information"));

			// time start
			play_time.put(player, System.currentTimeMillis());

		} catch (Exception ex) {
			ErrorUtils.GetErrorMessage(ex);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		try {
			Player player = e.getPlayer();

			SoundUtils.BroadcastSound("door-close2", false);

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

			// memory clear
			plugin.GetClickMode().remove(player);
			plugin.GetAfkStatus().remove(player);

			// 計測終了
			int elapse = 0;
			if (play_time.containsKey(player)) {
				long ptime = play_time.get(player);
				long ntime = System.currentTimeMillis();
				elapse = (int)(ntime - ptime);
				elapse = elapse / 1000;
				play_time.remove(player);
			}
			ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
			args.add(new DatabaseArgs("c", ""+player.getLocation().getWorld().getName())); // world
			args.add(new DatabaseArgs("d", ""+player.getLocation().getX())); // x
			args.add(new DatabaseArgs("d", ""+player.getLocation().getY())); // y
			args.add(new DatabaseArgs("d", ""+player.getLocation().getZ())); // z
			args.add(new DatabaseArgs("i", ""+elapse)); // playtime
			args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
			int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.QUIT.UPDATE.PLAYER"), args);
			args.clear();
			args = null;

		} catch (Exception ex) {
			ErrorUtils.GetErrorMessage(ex);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		try {
			Player player = e.getEntity();

			int value = plugin.getConfig().getInt("Game.death", 200);
			int newExp = (player.getTotalExperience()-value);
			if (newExp < 0) newExp = 0;
			player.setExp(newExp);
			e.setKeepLevel(true);

			SoundUtils.BroadcastSound("don-1", false);

			plugin.GetAfkStatus().put(player, System.currentTimeMillis()); // afk

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
				Entity damager = null;
				if (damage instanceof EntityDamageByBlockEvent) {
					if (killer == null) {
						killername = damager.getType().toString();
					}
				}
				else if (damage instanceof EntityDamageByEntityEvent) {
					damager = ((EntityDamageByEntityEvent) damage).getDamager();
					if (killer == null) {
						killername = damager.getName();
					}
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

		} catch (Exception ex) {
			ErrorUtils.GetErrorMessage(ex);
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		try {
			Player player = e.getPlayer();

			e.setLeaveMessage(String.format("[ %sさんはキックされました 理由: %s]", player.getDisplayName(), e.getReason()));
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

		} catch (Exception ex) {
			ErrorUtils.GetErrorMessage(ex);
		}
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		try {
			Player player = e.getPlayer();
			// INSERT
			ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
			args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
			args.add(new DatabaseArgs("c", player.getName())); // name
			args.add(new DatabaseArgs("i", ""+0)); // rank
			args.add(new DatabaseArgs("i", ""+0)); // money
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
			args.add(new DatabaseArgs("i", ""+0)); // play_time
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
			uargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
			ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.LOGIN.UPDATE.PLAYER"), uargs);
			uargs.clear();
			uargs = null;

		} catch (Exception ex) {
			ErrorUtils.GetErrorMessage(ex);
		}
	}

	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
		try {
			Player player = e.getPlayer();

			plugin.GetAfkStatus().put(player, System.currentTimeMillis()); // afk

			// UPDATE
			ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
			args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
			int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.CMD.UPDATE.PLAYER"), args);
			args.clear();
			args = null;

			String command = e.getMessage();
			String[] array = command.split(" ");
			if (array.length <= 0) return;
			String cmd = "";
			String arg1 = "";
			String arg2 = "";
			String arg3 = "";
			String arg4 = "";
			String arg5 = "";
			String arg6 = "";
			String arg7 = "";
			for (int i=0; i<=7; i++) {
				try {
					switch (i) {
						case 0: cmd = array[i]; break;
						case 1: arg1 = array[i]; break;
						case 2: arg2 = array[i]; break;
						case 3: arg3 = array[i]; break;
						case 4: arg4 = array[i]; break;
						case 5: arg5 = array[i]; break;
						case 6: arg6 = array[i]; break;
						case 7: arg7 = array[i]; break;
					}
				} catch(Exception err) {
					break;
				}
			}

			// check exec rank
			if (!PlayerUtils.CheckCommandRank(plugin.getDB(), player, cmd)) {
				e.setCancelled(true);
				return;
			}

			// log cmd
			ArrayList<DatabaseArgs> cargs = new ArrayList<DatabaseArgs>();
			cargs.add(new DatabaseArgs("c", player.getLocation().getWorld().getName())); // world
			cargs.add(new DatabaseArgs("i", ""+player.getLocation().getBlockX())); // x
			cargs.add(new DatabaseArgs("i", ""+player.getLocation().getBlockY())); // y
			cargs.add(new DatabaseArgs("i", ""+player.getLocation().getBlockZ())); // z
			cargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // uuid
			cargs.add(new DatabaseArgs("c", cmd)); // cmd
			cargs.add(new DatabaseArgs("c", arg1)); // arg1
			cargs.add(new DatabaseArgs("c", arg2)); // arg2
			cargs.add(new DatabaseArgs("c", arg3)); // arg3
			cargs.add(new DatabaseArgs("c", arg4)); // arg4
			cargs.add(new DatabaseArgs("c", arg5)); // arg5
			cargs.add(new DatabaseArgs("c", arg6)); // arg6
			cargs.add(new DatabaseArgs("c", arg7)); // arg7
			cargs.add(new DatabaseArgs("c", "")); // action
			cargs.add(new DatabaseArgs("c", "")); // result
			ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.COMMAND.INSERT"), cargs);
			cargs.clear();
			cargs = null;

		} catch (Exception ex) {
			ErrorUtils.GetErrorMessage(ex);
		}
	}

	@EventHandler
	public void onBedEnter(final PlayerBedEnterEvent e) {
		plugin.GetAfkStatus().put(e.getPlayer(), System.currentTimeMillis()); // afk
		SoundUtils.PlaySound(e.getPlayer(), "goodnight", true);
	}

	@EventHandler
	public void onBedLeave(final PlayerBedLeaveEvent e) {
		plugin.GetAfkStatus().put(e.getPlayer(), System.currentTimeMillis()); // afk
		SoundUtils.PlaySound(e.getPlayer(), "goodmorning", true);
	}

	@EventHandler
	public void onExpChange(final PlayerExpChangeEvent e) {
		Player player = e.getPlayer();
		int totalexp = player.getTotalExperience();

		// UPDATE
		ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
		args.add(new DatabaseArgs("i", ""+totalexp)); // totalexp
		args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
		int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.EXP.UPDATE.PLAYER"), args);
		args.clear();
		args = null;
	}

	@EventHandler
	public void onLevelChange(final PlayerLevelChangeEvent e) {
		Player player = e.getPlayer();
		int newlevel = e.getNewLevel();
		int oldlevel = e.getOldLevel();

		ChatColor color;
		String kbn;
		String sound;
		if (newlevel > oldlevel) {
			kbn = "レベルアップ";
			color = ChatColor.GOLD;
			sound = "shine3";
		} else {
			kbn = "レベルダウン";
			color = ChatColor.DARK_PURPLE;
			sound = "sceneswitch2";
		}

		// UPDATE
		ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
		args.add(new DatabaseArgs("i", ""+newlevel)); // level
		args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
		int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.PLAYER.UPDATE.LEVEL"), args);
		args.clear();
		args = null;

		StringBuilder sb = new StringBuilder();

		sb.append(color);
		sb.append("[" + kbn + "] ");
		sb.append(ChatColor.WHITE);
		sb.append("<");
		sb.append(player.getDisplayName());
		sb.append("さん> ");
		sb.append("[ Lv");
		sb.append(oldlevel);
		sb.append(" → ");
		sb.append(color);
		sb.append("Lv");
		sb.append(newlevel);
		sb.append(ChatColor.WHITE);
		sb.append(" ]");
		String message = new String(sb);
		// broadcast chat
		PlayerUtils.BroadcastActionBar(message);
		// broadcast sound
		SoundUtils.BroadcastSound(sound, false);
		// discord
		//DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
		//if (dm != null) {
		//	if (newlevel > oldlevel) {
		//		dm.SendDiscordYellowMessage(message);
		//	} else {
		//		dm.SendDiscordGrayMessage(message);
		//	}
		//}
	}

	@EventHandler
	public void onMove(final PlayerMoveEvent e) {
		Player player = e.getPlayer();
		plugin.GetAfkStatus().put(player, System.currentTimeMillis()); // afk
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent e) {
		Player player = e.getPlayer();
		plugin.GetAfkStatus().put(player, System.currentTimeMillis()); // afk

		// click mode
		if (plugin.GetClickMode().containsKey(player)) {
			String click_mode = plugin.GetClickMode().get(player);
			if (click_mode.equals("blockid")) {
				Block block = e.getClickedBlock();
				if (block != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(ChatColor.GREEN);
					sb.append(block.getBlockData().getMaterial().toString() + "  ");
					sb.append(ChatColor.YELLOW);
					sb.append(block.getLocation().getWorld().getName());
					sb.append(" (");
					sb.append(block.getLocation().getBlockX());
					sb.append(", ");
					sb.append(block.getLocation().getBlockY());
					sb.append(", ");
					sb.append(block.getLocation().getBlockZ());
					sb.append(")");
					PlayerUtils.SendActionBar(player, new String(sb));
					e.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onChat(final AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		String message = e.getMessage();

		plugin.GetAfkStatus().put(player, System.currentTimeMillis()); // afk

		StringBuilder sb = new StringBuilder();
		sb.append(message);
		String buff = StringUtils.GetJapanese(message);
		if (buff.length() > 0) {
			sb.append(" (");
			sb.append(buff);
			sb.append(")");
		}
		message = new String(sb);
		e.setMessage(message);

		if(e.isAsynchronous()) {
			plugin.getServer().getScheduler().callSyncMethod(plugin, new CallableOnChat(plugin, player, message));
		} else {
			try {
				// discord
				DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
				if (dm != null) {
					dm.SendDiscordMessage(player, message);
				}
				// UPDATE
				ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
				args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
				int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.CHAT.UPDATE.PLAYER"), args);
				args.clear();
				args = null;

			} catch (Exception ex) {
				ErrorUtils.GetErrorMessage(ex);
			}
		}
	}
}

class CallableOnChat implements Callable<Object>
{
	private KuroBase plugin;
	private Player player;
	private String message;

	CallableOnChat(KuroBase plugin, Player player, String message) {
		this.plugin = plugin;
		this.player = player;
		this.message = message;
	}

	@Override
	public Object call() throws Exception {
		try {
			// discord
			DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
			if (dm != null) {
				dm.SendDiscordMessage(player, message);
			}

			// UPDATE
			ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
			args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
			int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.CHAT.UPDATE.PLAYER"), args);
			args.clear();
			args = null;

		} catch (Exception ex) {
			ErrorUtils.GetErrorMessage(ex);
		}
		return null;
	}
}