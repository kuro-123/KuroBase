package host.kuro.kurobase.tasks;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import host.kuro.kurodiscord.DiscordMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ShutdownTask extends BukkitRunnable {

    private final KuroBase plugin;
    private int seq = 0;
    private String message = "";
    private DiscordMessage dm;

    public ShutdownTask(KuroBase plugin) {
        this.plugin = plugin;
        dm = KuroBase.getDiscord().getDiscordMessage();
    }

    @Override
    public void run() {
        try {
            switch (seq) {
                case 0:
                    message = "30秒後に鯖のシャットダウンを開始します";
                    break;
                case 1:
                    message = "シャットダウンまで20秒前です！ご注意ください！";
                    break;
                case 2:
                    message = "シャットダウンまで10秒前です！ご注意ください！";
                    break;
                case 3:
                    message = "";
                    plugin.getServer().shutdown();
                    break;
            }
            if (message.length() > 0) {
                PlayerUtils.BroadcastMessage(ChatColor.RED + message);
                SoundUtils.BroadcastSound("shock5", true);
                if (plugin.IsLinux()) {
                    dm.SendDiscordRedMessage(message);
                }
            }

        } catch (Exception ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
        }
        seq++;
    }
}
