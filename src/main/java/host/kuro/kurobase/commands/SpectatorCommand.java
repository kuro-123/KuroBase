package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectatorCommand implements CommandExecutor {

    private final KuroBase plugin;

    public SpectatorCommand(KuroBase plugin) {
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

        int rank = PlayerUtils.GetRank(plugin, player);
        if (rank < PlayerUtils.RANK_KANRI) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.perm.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.spectator.success"));
        SoundUtils.PlaySound(player,"switch1", false);
        return true;
    }
}
