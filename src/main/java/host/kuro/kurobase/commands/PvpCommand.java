package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvpCommand implements CommandExecutor {

    private final KuroBase plugin;

    public PvpCommand(KuroBase plugin) {
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
        if (args.length != 1) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        String option = args[0].toLowerCase();
        if (option.equals("on")) {
            plugin.GetPvp().put(player, true);
            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.pvp.modeon"));
            SoundUtils.PlaySound(player,"switch1", false);
            return true;
        }
        else if (option.equals("off")) {
            plugin.GetPvp().put(player, false);
            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.pvp.modeoff"));
            SoundUtils.PlaySound(player,"switch1", false);
            return true;
        }
        player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
        SoundUtils.PlaySound(player,"cancel5", false);
        return false;
    }
}
