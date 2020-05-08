package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.shop.ShopHandler;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import host.kuro.kurobase.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final KuroBase plugin;

    public PayCommand(KuroBase plugin) {
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

        if (args.length != 2) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        try {
            Player target = plugin.getServer().getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.pay.notfound"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            if (player.getName().toLowerCase().equals(target.getName().toLowerCase())) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.pay.notfound"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            int price = 0;
            try {
                price = Integer.parseInt(args[1]);
            } catch (Exception ex) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.pay.numerror"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            if (price < 0) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.pay.numerror"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            int money = PlayerUtils.GetMoney(KuroBase.getDB(), player);
            if (price > money) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.pay.monerror"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            // update
            int ret = PlayerUtils.PayMoney(KuroBase.getDB(), player, price);
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.pay.fail"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.YELLOW);
                sb.append("<支払> ");
                sb.append(String.format("[ %s ] さんに [ %s p] を支払いました", target.getDisplayName(), StringUtils.numFmt.format(price)));
                player.sendMessage(new String(sb));
                SoundUtils.PlaySound(player,"amount-display1", false);
            }

            ret = PlayerUtils.AddMoney(KuroBase.getDB(), target, price);
            if (ret != 1) {
                // rollback
                PlayerUtils.AddMoney(KuroBase.getDB(), player, price);
                // message
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.pay.fail"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.BLUE);
                sb.append("<支払> ");
                sb.append(String.format("[ %s ] さんから [ %s p] が支払われました", player.getDisplayName(), StringUtils.numFmt.format(price)));
                player.sendMessage(new String(sb));
                SoundUtils.PlaySound(player,"amount-display1", false);
            }

            // pay log
            PlayerUtils.AddLogPlayerPay(player, "PAY", price, target);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            return false;
        }
        return true;
    }
}
