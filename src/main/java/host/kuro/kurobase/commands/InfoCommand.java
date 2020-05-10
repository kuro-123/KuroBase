package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import host.kuro.kurobase.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand implements CommandExecutor {

    private final KuroBase plugin;

    public InfoCommand(KuroBase plugin) {
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

        // money info
        String money = StringUtils.numFmt.format(PlayerUtils.GetMoney(KuroBase.getDB(), player));
        String money_message = String.format(Language.translate("plugin.money.info")+": %s p", money);
        player.sendMessage(ChatColor.DARK_GREEN + money_message);
        // web info
        player.sendMessage(ChatColor.DARK_GREEN + plugin.getConfig().getString("Url.info"));
        player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.info.message"));
        SoundUtils.PlaySound(player,"switch1", false);
        return true;
    }
}
