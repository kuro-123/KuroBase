package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChestCommand implements CommandExecutor {

    private KuroBase plugin;
    private Player player;

    public ChestCommand(KuroBase plugin) {
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
        // check survival world
        if (!PlayerUtils.IsSurvivalWorld(plugin, player)) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.creative"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        if (args.length < 1) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        if (args.length == 1) {
            return ActionArgOne(args);
        }
        return true;
    }

    private boolean ActionArgOne(String[] args) {
        final String action = args[0];
        if (action.toLowerCase().equals("lock")) {
            // chest lock
            return ActionLock(args);
        }
        return true;
    }

    private boolean ActionLock(String[] args) {
        try {
            int rank = PlayerUtils.GetRank(KuroBase.getDB(), player);
            if (rank < PlayerUtils.RANK_KANRI) {
                // check perm count
                int perm = plugin.getConfig().getInt("Perm.chestlock", 5000);
                int cnt = PlayerUtils.GetBreakPlace(KuroBase.getDB(), player);
                if (cnt < perm) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ChatColor.DARK_RED);
                    sb.append(Language.translate("commands.chest.lock.perm"));
                    sb.append(String.format(Language.translate("commands.chest.lock.perm.rank"), perm));
                    player.sendMessage(new String(sb));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return false;
                }
            }
            // change mode
            if (plugin.GetClickMode().containsKey(player)) {
                String click_mode = plugin.GetClickMode().get(player);
                if (click_mode.equals("chestlock")) {
                    plugin.GetClickMode().remove(player);
                    player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.chest.lock.modeoff"));
                    SoundUtils.PlaySound(player,"switch1", false);
                } else {
                    plugin.GetClickMode().remove(player);
                    player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.chest.lock.modeon"));
                    SoundUtils.PlaySound(player,"switch1", false);
                    plugin.GetClickMode().put(player, "chestlock");
                }
            } else {
                plugin.GetClickMode().remove(player);
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.chest.lock.modeon"));
                SoundUtils.PlaySound(player,"switch1", false);
                plugin.GetClickMode().put(player, "chestlock");
            }

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return true;
    }
}
