package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import host.kuro.kurodiscord.DiscordMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RandCommand implements CommandExecutor {

    private final KuroBase plugin;

    public RandCommand(KuroBase plugin) {
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
        if (args.length != 2) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        int min = 0;
        try {
            min = Integer.parseInt(args[0]);
        } catch (Exception ex) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.rand.numerror"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        int max = 0;
        try {
            max = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.rand.numerror"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        int rand_min = Math.min(min, max);
        int rand_max = Math.max(min, max);
        int result = plugin.GetRand().Next(rand_min, rand_max);

        String kisu_gusu;
        if ((result % 2) == 0) {
            kisu_gusu = "偶数";
        } else {
            kisu_gusu = "奇数";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.WHITE);
        sb.append("[");
        sb.append(ChatColor.GOLD);
        sb.append(player.getDisplayName());
        sb.append(ChatColor.WHITE);
        sb.append("]さんが引いた値 (最小:");
        sb.append(String.valueOf(rand_min));
        sb.append(" 最大:");
        sb.append(String.valueOf(rand_max));
        sb.append(") は -> [ ");
        sb.append(ChatColor.GREEN);
        sb.append(String.valueOf(result));
        sb.append(ChatColor.WHITE);
        sb.append(" ] (");
        sb.append(kisu_gusu);
        sb.append(") です");
        String message = new String(sb);

        PlayerUtils.BroadcastMessage(message);
        SoundUtils.BroadcastSound("hyoushigi1", false);
        DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
        if (dm != null) {
            dm.SendDiscordGreenMessage(message);
        }
        return true;
    }
}
