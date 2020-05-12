package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelCommand implements CommandExecutor {

    private final KuroBase plugin;

    public SelCommand(KuroBase plugin) {
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

        try {
            // clear select type data
            plugin.GetSelectDataOne().remove(player);
            plugin.GetSelectDataTwo().remove(player);
            plugin.GetSelectStatus().remove(player);

            // change mode
            if (plugin.GetClickMode().containsKey(player)) {
                String click_mode = plugin.GetClickMode().get(player);
                if (click_mode.equals("select")) {
                    plugin.GetClickMode().remove(player);
                    player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.sel.modeoff"));
                    SoundUtils.PlaySound(player, "switch1", false);
                } else {
                    player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.sel.modeon"));
                    SoundUtils.PlaySound(player, "switch1", false);
                    plugin.GetClickMode().put(player, "select");
                }
            } else {
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.sel.modeon"));
                SoundUtils.PlaySound(player, "switch1", false);
                plugin.GetClickMode().put(player, "select");
            }

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            return false;
        }
        return true;
    }
}
