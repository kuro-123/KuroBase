package host.kuro.kurobase.tasks;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MinutesTask extends BukkitRunnable {

    private final KuroBase plugin;
    private final int max_time;

    public MinutesTask(KuroBase plugin) {
        this.plugin = plugin;
        max_time = plugin.getConfig().getInt("Game.afk", 600000);
    }

    @Override
    public void run() {
        CheckAfk();
    }

    private void CheckAfk() {
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
                    }
                }
            }

        } catch (Exception ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
        }
    }
}
