package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RuleCommand implements CommandExecutor {

    private final KuroBase plugin;

    public RuleCommand(KuroBase plugin) {
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
        if (args.length != 2) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        int rank = PlayerUtils.GetRank(plugin, player);
        if (rank < PlayerUtils.RANK_KANRI) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.perm.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        String kind = args[0].toLowerCase();
        String option = args[1].toLowerCase();

        if (kind.equals("stoptime")) {
            if (option.equals("on")) {
                player.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                PlayerUtils.BroadcastMessage(ChatColor.YELLOW + player.getDisplayName() + "さんが、" + Language.translate("commands.rule.stoptime.on") + " [ " + player.getWorld().getName() + " ]", true);
                SoundUtils.PlaySound(player,"switch1", false);
                return true;
            }
            else if (option.equals("off")) {
                player.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                PlayerUtils.BroadcastMessage(ChatColor.YELLOW + player.getDisplayName() + "さんが、" + Language.translate("commands.rule.stoptime.off") + " [ " + player.getWorld().getName() + " ]", true);
                SoundUtils.PlaySound(player,"switch1", false);
                return true;
            }
        }
        else if (kind.equals("firetick")) {
            if (option.equals("on")) {
                player.getWorld().setGameRule(GameRule.DO_FIRE_TICK, false);
                PlayerUtils.BroadcastMessage(ChatColor.YELLOW + player.getDisplayName() + "さんが、" + Language.translate("commands.rule.firetick.on") + " [ " + player.getWorld().getName() + " ]", true);
                SoundUtils.PlaySound(player,"switch1", false);
                return true;
            }
            else if (option.equals("off")) {
                player.getWorld().setGameRule(GameRule.DO_FIRE_TICK, true);
                PlayerUtils.BroadcastMessage(ChatColor.YELLOW + player.getDisplayName() + "さんが、" + Language.translate("commands.rule.firetick.off") + " [ " + player.getWorld().getName() + " ]", true);
                SoundUtils.PlaySound(player,"switch1", false);
                return true;
            }
        }
        player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
        SoundUtils.PlaySound(player,"cancel5", false);
        return false;
    }
}
