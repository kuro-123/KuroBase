package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import host.kuro.kurobase.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class NameCommand implements CommandExecutor {

    private final KuroBase plugin;

    public NameCommand(KuroBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            // console check
            plugin.getLogger().warning(Language.translate("plugin.console.error"));
            return false;
        }
        final Player player = (Player)sender;
        if (args.length > 1) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        if (args.length == 0) {
            return DeleteNickName(player);
        }
        final String target = args[0];
        if (!IsLength(target)) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.len"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        if (!IsHankakuEisu(target)) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.hankaku"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        if (IsDupli(target)) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.name.error.dupli"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                // UPDATE
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", target)); // nickname
                args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
                int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.NAME.UPDATE.PLAYER"), args);
                args.clear();
                args = null;
                if (ret != 1) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.update"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return;
                }

                player.setDisplayName(target);

                StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.DARK_GREEN);
                sb.append(Language.translate("commands.name.msg.success"));
                sb.append(ChatColor.YELLOW);
                sb.append(String.format(" [ %s ]", target));
                player.sendMessage(new String(sb));
                SoundUtils.PlaySound(player,"switch1", false);

            }
        }.runTaskAsynchronously(plugin);
        return true;
    }

    private boolean DeleteNickName(Player player) {
        new BukkitRunnable(){
            @Override
            public void run() {
                // UPDATE
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
                int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.NAME.UPNULL.PLAYER"), args);
                args.clear();
                args = null;
                if (ret != 1) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.update"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return;
                }

                player.setDisplayName(player.getName());

                StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.DARK_GREEN);
                sb.append(Language.translate("commands.name.msg.deleted"));
                player.sendMessage(new String(sb));
                SoundUtils.PlaySound(player,"switch1", false);

            }
        }.runTaskAsynchronously(plugin);
        return true;
    }

    private boolean IsLength(String target) {
        int len =target.length();
        if (len < 3 || len > 16) {
            return false;
        }
        return true;
    }

    private boolean IsHankakuEisu(String target) {
        if (!StringUtils.isHankakuEisu(target)) {
            return false;
        }
        return true;
    }

    private boolean IsDupli(String target) {
        boolean hit = false;
        try {
            PreparedStatement ps = plugin.getDB().getConnection().prepareStatement(Language.translate("SQL.NAME.CHECK.NAME"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", target.toLowerCase()));
            args.add(new DatabaseArgs("c", target.toLowerCase()));
            ResultSet rs = plugin.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    hit = true;
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
            if (hit) {
                return true;
            }
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            return true;
        }
        return false;
    }
}
