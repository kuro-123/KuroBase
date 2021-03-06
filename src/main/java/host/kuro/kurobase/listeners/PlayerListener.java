package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.AreaData;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.tasks.SkinTask;
import host.kuro.kurobase.utils.*;
import host.kuro.kurodiscord.DiscordMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
			if (BuddyUtils.IsNpc(player)) return;

			// knock sound
			SoundUtils.BroadcastSound("door-wood-knock1", false);

			plugin.GetAfkStatus().put(player, System.currentTimeMillis()); // afk

			// minarai check update
			PlayerUtils.UpdateJyumin(plugin, KuroBase.getDB(), player);

			// init buddy
			BuddyUtils.InitBuddy(player);

			// pvp off
			plugin.GetPvp().put(player, false);

			// setting rank
			player.setOp(true);
			int rank = PlayerUtils.GetRank(plugin, player);
			if (rank >= PlayerUtils.RANK_KANRI) {
				if (rank == PlayerUtils.RANK_KANRI) {
					SoundUtils.BroadcastSound("kanri", true);
				}
				else if (rank == PlayerUtils.RANK_NUSHI) {
					SoundUtils.BroadcastSound("kuro", true);
				}
				// force survival
			} else {
				//if (rank == PlayerUtils.RANK_MINARAI) {
					//player.setOp(false);
				//}
			}
			// force survival
			GameMode mode = player.getGameMode();
			if (mode != GameMode.SURVIVAL) {
				PlayerUtils.ForceSurvival(player);
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
			if (BuddyUtils.IsNpc(player)) return;

			SoundUtils.BroadcastSound("door-close2", false);

			StringBuilder sb = new StringBuilder();
			sb.append(ChatColor.BLUE);
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
			plugin.GetSoundBattle().remove(player);
			plugin.GetMoveMessage().remove(player);
			plugin.GetFrame().remove(player);
			plugin.GetRank().remove(player);
			plugin.GetSelectDataOne().remove(player);
			plugin.GetSelectDataTwo().remove(player);
			plugin.GetSelectStatus().remove(player);
			plugin.GetInteractWait().remove(player);
			plugin.GetExecWE().remove(player);
			plugin.GetPvp().remove(player);

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
			if (BuddyUtils.IsNpc(player)) return;
			if (!plugin.GetRank().containsKey(player)) return;

			int drop = e.getDroppedExp();
			int value = plugin.getConfig().getInt("Game.death", 200);
			value = value * plugin.GetRand().Next(1, 3);
			if (drop>value) {
				drop = value;
			}
			e.setDroppedExp(drop);
			e.setKeepLevel(true);

			ParticleUtils.CrownParticle(player, Particle.LAVA, 50); // particle
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

			Entity entity = e.getEntity();
			if (entity != null) {
				EntityDamageEvent damage = entity.getLastDamageCause();
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
					if (damage instanceof EntityDamageByEntityEvent) {
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
			}

		} catch (Exception ex) {
			ErrorUtils.GetErrorMessage(ex);
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		try {
			Player player = e.getPlayer();
			if (BuddyUtils.IsNpc(player)) return;

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
			if (BuddyUtils.IsNpc(player)) return;

			// INSERT
			ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
			args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
			args.add(new DatabaseArgs("c", player.getName())); // name
			args.add(new DatabaseArgs("i", ""+0)); // rank
			args.add(new DatabaseArgs("i", ""+0)); // money
			args.add(new DatabaseArgs("c", "")); // ip
			args.add(new DatabaseArgs("c", "")); // host
			args.add(new DatabaseArgs("c", e.getRealAddress().toString())); // rip
			args.add(new DatabaseArgs("c", e.getRealAddress().getHostName())); // rhost
			args.add(new DatabaseArgs("i", "0")); // totalexp
			args.add(new DatabaseArgs("d", "0.0")); // exp
			args.add(new DatabaseArgs("i", "0")); // exptolevel
			args.add(new DatabaseArgs("i", "0")); // level
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
			uargs.add(new DatabaseArgs("c", "")); // ip
			uargs.add(new DatabaseArgs("c", "")); // host
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
			if (BuddyUtils.IsNpc(player)) return;

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
			if (!PlayerUtils.CheckCommandRank(plugin, player, cmd)) {
				e.setCancelled(true);
				return;
			}

			// check gamemode
			if (cmd.toLowerCase().equals("/gamemode")) {
				int rank = PlayerUtils.GetRank(plugin, player);
				if (rank < PlayerUtils.RANK_NUSHI) {
					player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
					SoundUtils.PlaySound(player, "cancel5", false);
					e.setCancelled(true);
					return;
				}
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
		Player player = e.getPlayer();
		if (BuddyUtils.IsNpc(player)) return;
		plugin.GetAfkStatus().put(player, System.currentTimeMillis()); // afk
		SoundUtils.PlaySound(player, "goodnight", true);
	}

	@EventHandler
	public void onBedLeave(final PlayerBedLeaveEvent e) {
		Player player = e.getPlayer();
		if (BuddyUtils.IsNpc(player)) return;
		plugin.GetAfkStatus().put(player, System.currentTimeMillis()); // afk
		SoundUtils.PlaySound(player, "goodmorning", true);
	}

	@EventHandler
	public void onExpChange(final PlayerExpChangeEvent e) {
		Player player = e.getPlayer();

		if (BuddyUtils.IsNpc(player)) {
			return;
		}

		// UPDATE
		int totalexp = player.getTotalExperience();
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

		if (BuddyUtils.IsNpc(player)) {
			return;
		}
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
	}

	@EventHandler
	public void onMove(final PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (BuddyUtils.IsNpc(player)) return;

		plugin.GetAfkStatus().put(player, System.currentTimeMillis()); // afk

		if (!plugin.GetMoveMessage().containsKey(player)) {
			SendMoveMessage(player);
			plugin.GetMoveMessage().put(player, System.currentTimeMillis());
		} else {
			long before = plugin.GetMoveMessage().get(player);
			long after = System.currentTimeMillis();
			if ((after-before) > 3000) {
				SendMoveMessage(player);
				plugin.GetMoveMessage().put(player, System.currentTimeMillis());
			}
		}
	}
	private void SendMoveMessage(Player player) {
		if (BuddyUtils.IsNpc(player)) return;

		AreaData area = AreaUtils.CheckInsideProtect(null, player.getLocation().getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		if (area != null) {
			if (player.getGameMode() == GameMode.SURVIVAL) {
				if (area.owner.equals(player.getName())) {
					if (!player.getAllowFlight()) {
						player.sendMessage(ChatColor.YELLOW + "自分のエリア内では飛行可能です");
						player.setAllowFlight(true);
						player.setFlying(true);
						SoundUtils.PlaySound(player, "correct2", false);
					}
				}
			}
			PlayerUtils.SendActionBar(player, ChatColor.YELLOW + String.format("[ 敷地:%s <by %s> ]", area.name, area.owner));
		} else {
			if (player.getGameMode() == GameMode.SURVIVAL) {
				if (player.getAllowFlight()) {
					player.setAllowFlight(false);
					player.setFlying(false);
				}
			}
		}
	}

	@EventHandler
	public void onBucketEmpty(final PlayerBucketEmptyEvent e) {
		Player player = e.getPlayer();
		if (BuddyUtils.IsNpc(player)) return;

		Block block = e.getBlock();
		Material bucket = e.getBucket();
		if (bucket.toString().contains("LAVA") || bucket.toString().contains("WATER")) {
			// check area
			AreaData area = AreaUtils.CheckInsideProtect(null, player.getLocation().getWorld().getName(), block.getX(), block.getY(), block.getZ());
			if (area != null) {
				if (area.owner.length() > 0) {
					if (!area.owner.equals(player.getName())) {
						player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", area.owner, area.name));
						SoundUtils.PlaySound(player,"cancel5", false);
						e.setCancelled(true);
						return;
					} else {
						// owner area is ok
						return;
					}
				}
			}
			player.sendMessage(ChatColor.YELLOW + Language.translate("plugin.bucket.error"));
			SoundUtils.PlaySound(player, "cancel5", false);
			e.setCancelled(true);

		} else {
			int rank = PlayerUtils.GetRank(plugin, player);
			if (rank < PlayerUtils.RANK_JYUMIN) {
				player.sendMessage(ChatColor.YELLOW + Language.translate("plugin.bucket.error"));
				SoundUtils.PlaySound(player, "cancel5", false);
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onBucketFill(final PlayerBucketFillEvent e) {
		Player player = e.getPlayer();
		if (BuddyUtils.IsNpc(player)) return;

		Block block = e.getBlock();
		Material bucket = e.getBucket();
		if (bucket.toString().contains("LAVA") || bucket.toString().contains("WATER")) {
			// check area
			AreaData area = AreaUtils.CheckInsideProtect(null, player.getLocation().getWorld().getName(), block.getX(), block.getY(), block.getZ());
			if (area != null) {
				if (area.owner.length() > 0) {
					if (!area.owner.equals(player.getName())) {
						player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", area.owner, area.name));
						SoundUtils.PlaySound(player,"cancel5", false);
						e.setCancelled(true);
						return;
					} else {
						// owner area is ok
						return;
					}
				}
			}
			player.sendMessage(ChatColor.YELLOW + Language.translate("plugin.bucket.error"));
			SoundUtils.PlaySound(player, "cancel5", false);
			e.setCancelled(true);

		} else {
			int rank = PlayerUtils.GetRank(plugin, player);
			if (rank < PlayerUtils.RANK_JYUMIN) {
				player.sendMessage(ChatColor.YELLOW + Language.translate("plugin.bucket.error"));
				SoundUtils.PlaySound(player, "cancel5", false);
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInteract(final PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (BuddyUtils.IsNpc(player)) return;

		plugin.GetAfkStatus().put(player, System.currentTimeMillis()); // afk

		Block block = e.getClickedBlock();

		// click mode
		if (plugin.GetClickMode().containsKey(player)) {
			String click_mode = plugin.GetClickMode().get(player);
			Action action = e.getAction();
			if (action == Action.RIGHT_CLICK_BLOCK) {
				if (!plugin.GetInteractWait().containsKey(player)) {
					plugin.GetInteractWait().put(player, System.currentTimeMillis());
				} else {
					long before = plugin.GetInteractWait().get(player);
					long after = System.currentTimeMillis();
					if ((after - before) < 500) {
						e.setCancelled(true);
						return;
					}
					plugin.GetInteractWait().put(player, System.currentTimeMillis());
				}
				if (click_mode.equals("blockid")) {
					InteractUtils.ClickBlockId(plugin, e, player, block);
				}
				else if (click_mode.equals("select")) {
					InteractUtils.ClickSelect(plugin, e, player, block);
				}
				else if (click_mode.equals("paste")) {
					InteractUtils.ClickPaste(plugin, e, player, block);
				}

			} else if (action == Action.LEFT_CLICK_BLOCK) {
				if (click_mode.equals("select")) {
					InteractUtils.ClickBlockId(plugin, e, player, block);
				}
			}
		}
	}

	@EventHandler
	public void onDropItem(final PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		if (BuddyUtils.IsNpc(player)) return;
		if (player.getGameMode() == GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onRespawn(final PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		if (BuddyUtils.IsNpc(player)) return;

		e.setRespawnLocation(Bukkit.getWorld("home").getSpawnLocation());

		// force survival
		PlayerUtils.ForceSurvival(player);
	}

	@EventHandler
	public void onTeleport(final PlayerTeleportEvent e) {
		Player player = e.getPlayer();
		if (BuddyUtils.IsNpc(player)) return;
		// force survival
		PlayerUtils.ForceSurvival(player);
	}

	@EventHandler
	public void onGameModeChange(final PlayerGameModeChangeEvent e) {
		// mode change is empty
		Player player = e.getPlayer();
		if (BuddyUtils.IsNpc(player)) return;
		PlayerUtils.RemoveAllItems(player);
	}

	@EventHandler
	public void onChat(final AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		if (BuddyUtils.IsNpc(player)) return;

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