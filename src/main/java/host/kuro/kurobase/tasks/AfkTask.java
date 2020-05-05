package host.kuro.kurobase.tasks;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurodiscord.DiscordMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AfkTask extends BukkitRunnable {

    private final KuroBase plugin;
    private final int max_time;

    public AfkTask(KuroBase plugin) {
        this.plugin = plugin;
        max_time = plugin.getConfig().getInt("Game.afk", 600000);
    }

    @Override
    public void run() {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (plugin.GetAfkStatus().containsKey(player)) {
                    long lastTime = plugin.GetAfkStatus().get(player);
                    long nowTime = System.currentTimeMillis();
                    long elapse = nowTime - lastTime;
                    if (elapse >= max_time) {
                        // afk kick
                        String message = Language.translate("plugin.kick.afk");
                        player.kickPlayer(message);
                        PlayerUtils.BroadcastMessage(ChatColor.YELLOW + message);
                        DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
                        if (dm != null) {
                            dm.SendDiscordYellowMessage(message);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
        }
    }
}
