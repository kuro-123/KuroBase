package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.AreaData;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class HomeCommand implements CommandExecutor {

    private KuroBase plugin;
    private Player player;

    public HomeCommand(KuroBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            // console
            plugin.getLogger().warning(Language.translate("plugin.console.error"));
            return false;
        }
        player = (Player)sender;
        if (args.length > 1) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        if (args.length == 0) {
            return ActionArgNone(player);
        } else {
            return ActionArgOne(player, args);
        }
    }

    private boolean ActionArgNone(Player player) {
        // teleport
        try {
            Location loc = GetHomePoint(player);
            if (loc == null) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.home.teleport.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            loc.setY(loc.getY() + 0.5D);

            int money = PlayerUtils.GetMoney(KuroBase.getDB(), player);
            if (money < 50) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.home.money.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            PlayerUtils.PayMoney(KuroBase.getDB(), player, 50);
            PlayerUtils.AddLogWarpPay(player, "WARP", 50);

            player.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
            new BukkitRunnable(){
                @Override
                public void run() {
                player.sendMessage(ChatColor.YELLOW + Language.translate("commands.home.teleport"));
                ParticleUtils.CenterParticle(player, Particle.CAMPFIRE_COSY_SMOKE, 50, 4);
                SoundUtils.PlaySound(player, "typewriter-2", false);
                }
            }.runTaskLaterAsynchronously(plugin, 10);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.home.teleport.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }

    private boolean ActionArgOne(Player player, String[] args) {
        if (args[0].toLowerCase().equals("set")) {
            return ActionSet(player);
        }
        else if (args[0].toLowerCase().equals("del")) {
            return ActionDel(player);
        }
        return false;
    }

    private boolean ActionSet(Player player) {
        // update
        try {
            AreaData area = AreaUtils.CheckInsideProtect(player, player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
            if (!(area.owner.toLowerCase().equals(player.getName().toLowerCase()))) {
                // not own area
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.home.error.own"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            String exist = ExistHome(player);
            if (exist != area.name) {
                // already home
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.home.error.already") + " [エリア:" + area.name + "]");
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            // UPDATE
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("i", ""+player.getLocation().getBlockX())); // x
            args.add(new DatabaseArgs("i", ""+player.getLocation().getBlockY())); // y
            args.add(new DatabaseArgs("i", ""+player.getLocation().getBlockZ())); // z
            args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
            args.add(new DatabaseArgs("i", area.name)); // name
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.AREA.UPDATE.HOME"), args);
            args.clear();
            args = null;
            if (ret != 1) {
                // error
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.home.error.update"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            player.sendMessage(ChatColor.GREEN + Language.translate("commands.home.update.success"));
            SoundUtils.PlaySound(player, "switch1", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }

    private boolean ActionDel(Player player) {
        try {
            // delte
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.AREA.DELETE.HOME"), args);
            args.clear();
            args = null;
            if (ret != 1) {
                // error
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.home.error.delete"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            player.sendMessage(ChatColor.GREEN + Language.translate("commands.home.delete.success"));
            SoundUtils.PlaySound(player, "switch1", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }

    private String ExistHome(Player player) {
        String ret = "";
        try {
            PreparedStatement ps = plugin.getDB().getConnection().prepareStatement(Language.translate("SQL.AREA.EXIST.HOME"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = plugin.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    ret = rs.getString("name");
                    break;
                }
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            ret = "ERROR";
        }
        return ret;
    }

    private Location GetHomePoint(Player player) {
        Location ret = null;
        try {
            PreparedStatement ps = plugin.getDB().getConnection().prepareStatement(Language.translate("SQL.AREA.GET.HOME"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = plugin.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    ret = new Location(player.getWorld(), rs.getInt("hx"), rs.getInt("hy"), rs.getInt("hz"));
                    break;
                }
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            ret = null;
        }
        return ret;
    }

}
