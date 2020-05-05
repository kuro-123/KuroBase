package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import host.kuro.kurodiscord.DiscordMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PriceCommand implements CommandExecutor {

    private KuroBase plugin;

    public PriceCommand(KuroBase plugin) {
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
        if (args.length != 2) {
            // check args
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        try {
            String item_name = args[0].toLowerCase();

            // check price
            int min = plugin.getConfig().getInt("Price.min", 0);
            int max = plugin.getConfig().getInt("Price.max", 1000);
            int price = 0;
            try {
                price = Integer.parseInt(args[1]);
            } catch (Exception ex) {
                price = -1;
            }
            if (!(min<= price && price <= max)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.price.error.price") + ChatColor.YELLOW + "[ " + min + "p～" + max + "p ]");
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            // check hand
            ItemStack main_item = player.getInventory().getItemInMainHand();
            if (main_item == null) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.price.error.itemnone"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            // UPDATE
            if (item_name.equals("air")) {
                // check air
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.price.error.itemnone"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            ArrayList<DatabaseArgs> pargs = new ArrayList<DatabaseArgs>();
            pargs.add(new DatabaseArgs("i", ""+price)); // price
            pargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
            pargs.add(new DatabaseArgs("c", player.getName())); // name
            pargs.add(new DatabaseArgs("c", item_name)); // item_name
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.PRICE.UPDATE"), pargs);
            pargs.clear();
            pargs = null;
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.update"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.GOLD);
            sb.append(Language.translate("commands.price.success"));
            sb.append(" [ ｱｲﾃﾑ: ");
            sb.append(ChatColor.GREEN);
            sb.append(item_name);
            sb.append(ChatColor.GOLD);
            sb.append(" 価格: ");
            sb.append(ChatColor.GREEN);
            sb.append(price);
            sb.append(ChatColor.GOLD);
            sb.append(" ] by ");
            sb.append(ChatColor.GREEN);
            sb.append(player.getDisplayName());
            String message = new String(sb);
            player.sendMessage(message);
            if (plugin.IsLinux()) {
                DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
                if (dm != null) {
                    dm.SendDiscordGreenMessage(message);
                }
            }
            SoundUtils.BroadcastSound("piano-single1", false);
            return true;

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return false;
    }
}

