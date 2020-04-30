package host.kuro.kurobase.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SoundUtils {

    public static void BroadcastSound(String sound) {
        BroadcastSound(sound, 500.0F, 1.0F);
    }
    public static void BroadcastSound(String sound, float volume, float pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public static void PlaySound(Player player, String sound) {
        PlaySound(player, sound, 500.0F, 1.0F);
    }
    public static void PlaySound(Player player, String sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
