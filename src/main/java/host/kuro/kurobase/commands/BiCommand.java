package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BiCommand implements CommandExecutor {

    private final KuroBase plugin;

    public BiCommand(KuroBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            // console check
            plugin.getLogger().warning(Language.translate("plugin.console.error"));
            return false;
        }
        Player player = (Player)sender;
        if (args.length != 0) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        // change mode
        if (plugin.GetClickMode().containsKey(player)) {
            String click_mode = plugin.GetClickMode().get(player);
            if (click_mode.equals("blockid")) {
                plugin.GetClickMode().remove(player);
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.blockid.modeoff"));
                SoundUtils.PlaySound(player,"switch1", false);
            } else {
                plugin.GetClickMode().remove(player);
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.blockid.modeon"));
                SoundUtils.PlaySound(player,"switch1", false);
                plugin.GetClickMode().put(player, "blockid");
            }
        } else {
            plugin.GetClickMode().remove(player);
            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.blockid.modeon"));
            SoundUtils.PlaySound(player,"switch1", false);
            plugin.GetClickMode().put(player, "blockid");
        }
        return true;
    }
}
