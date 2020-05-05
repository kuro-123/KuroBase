package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;

public class TagCommand implements CommandExecutor {

    private final KuroBase plugin;

    public TagCommand(KuroBase plugin) {
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
            return DeleteTag(player);
        }
        final String target = args[0];
        if (!IsLength(target)) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.len"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                // UPDATE
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", target)); // tag
                args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
                int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.TAG.UPDATE.PLAYER"), args);
                args.clear();
                args = null;
                if (ret != 1) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.update"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return;
                }

                StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.DARK_GREEN);
                sb.append(Language.translate("commands.tag.msg.success"));
                sb.append(ChatColor.YELLOW);
                sb.append(String.format(" [ %s ]", target));
                player.sendMessage(new String(sb));
                SoundUtils.PlaySound(player,"switch1", false);

            }
        }.runTaskAsynchronously(plugin);
        return true;
    }

    private boolean DeleteTag(Player player) {
        new BukkitRunnable(){
            @Override
            public void run() {
                // UPDATE
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
                int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.TAG.UPNULL.PLAYER"), args);
                args.clear();
                args = null;
                if (ret != 1) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.update"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return;
                }

                StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.DARK_GREEN);
                sb.append(Language.translate("commands.tag.msg.deleted"));
                player.sendMessage(new String(sb));
                SoundUtils.PlaySound(player,"switch1", false);

            }
        }.runTaskAsynchronously(plugin);
        return true;
    }

    private boolean IsLength(String target) {
        int len =target.length();
        if (len < 2 || len > 16) {
            return false;
        }
        return true;
    }
}
