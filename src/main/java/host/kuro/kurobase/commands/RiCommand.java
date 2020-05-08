package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RiCommand implements CommandExecutor {

    private final KuroBase plugin;

    public RiCommand(KuroBase plugin) {
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
        // check creative
        if (player.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.creative"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        if (args.length != 0) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        PlayerUtils.RemoveAllItems(player);

        player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.ri.success"));
        SoundUtils.PlaySound(player,"switch1", false);
        return true;
    }
}
