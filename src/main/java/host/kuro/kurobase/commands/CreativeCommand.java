package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import host.kuro.kurobase.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CreativeCommand implements CommandExecutor {

    private final KuroBase plugin;

    public CreativeCommand(KuroBase plugin) {
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

        int change = plugin.getConfig().getInt("Game.mode_change", 7200);
        int time = PlayerUtils.GetPlayTime(plugin , player);
        if (time < change) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.creative.time.fail"));
            player.sendMessage(ChatColor.YELLOW + String.format("指定時間: %s秒 (約%d時間)", StringUtils.numFmt.format(change), (int)(change/3600)));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        // check world
        if (!PlayerUtils.IsCityWorld(plugin, player)) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.creative.fail"));
            SoundUtils.PlaySound(player,"cancel5", false);
        } else {
            int rank = PlayerUtils.GetRank(plugin, player);
            if (rank < PlayerUtils.RANK_KANRI) {
                // check item
                ItemStack stack = player.getInventory().getItemInMainHand();
                if (stack != null) {
                    ItemMeta data = stack.getItemMeta();
                    if (data != null) {
                        String display = data.getDisplayName();
                        if (!display.equals(Language.translate("shop.item.creative"))) {
                            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.creative.item.error"));
                            SoundUtils.PlaySound(player,"cancel5", false);
                            return false;
                        }
                        int amount = stack.getAmount();
                        amount--;
                        if (amount <= 0) {
                            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
                        } else {
                            stack.setAmount(amount);
                            player.getInventory().setItemInMainHand(stack);
                        }
                        player.setGameMode(GameMode.CREATIVE);
                        player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.creative.success"));
                        SoundUtils.PlaySound(player,"switch1", false);
                    }
                }
            } else {
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.creative.success"));
                SoundUtils.PlaySound(player,"switch1", false);
            }
        }
        return true;
    }
}
