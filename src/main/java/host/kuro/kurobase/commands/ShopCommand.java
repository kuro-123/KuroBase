package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.shop.GuiHandler;
import host.kuro.kurobase.shop.GuiShop;
import host.kuro.kurobase.shop.ShopHandler;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    private KuroBase plugin;

    public ShopCommand(KuroBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            // console
            plugin.getLogger().warning(Language.translate("plugin.console.error"));
            return false;
        }
        Player player = (Player)sender;
        if (!(args.length == 0 || args.length == 1)) {
            // check args
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        try {
            int rank = PlayerUtils.GetRank(KuroBase.getDB(), player);
            if (rank < PlayerUtils.RANK_KANRI) {
                // check perm count
                int perm = plugin.getConfig().getInt("Perm.shop", 3000);
                int cnt = PlayerUtils.GetBreakPlace(KuroBase.getDB(), player);
                if (cnt < perm) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ChatColor.DARK_RED);
                    sb.append(Language.translate("shop.perm"));
                    sb.append(String.format(Language.translate("commands.chest.lock.perm.rank"), perm));
                    player.sendMessage(new String(sb));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return false;
                }
            }

            // SHOP MAKE
            String keyword = "";
            if (args.length == 1) {
                keyword = args[0].toLowerCase();
            }
            ShopHandler.loadShop(keyword);
            GuiHandler.open(player, new GuiShop(player, 0));

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            return false;
        }
        return true;
    }
}
