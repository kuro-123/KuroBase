package host.kuro.kurobase.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtils {

    public static void BroadcastSound(String sound, boolean clear) {
        BroadcastSound(sound, clear, 500.0F, 1.0F);
    }
    public static void BroadcastSound(String sound, boolean clear, float volume, float pitch) {
        if (clear) {
            BroadcastStopSoundAll();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public static void PlaySound(Player player, String sound, boolean clear) {
        PlaySound(player, sound, clear, 500.0F, 1.0F);
    }
    public static void PlaySound(Player player, String sound, boolean clear, float volume, float pitch) {
        if (clear) {
            StopSoundAll(player);
        }
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void StopSound(Player player, String sound) {
        player.stopSound(sound);
    }
    public static void BroadcastStopSound(String sound) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.stopSound(sound);
        }
    }

    public static void StopSoundAll(Player player) {
        final Sound[] SOUNDS = Sound.values();
        for (Sound snd : SOUNDS) {
            player.stopSound(snd);
        }
        player.stopSound("battle"); // test
    }
    public static void BroadcastStopSoundAll() {
        final Sound[] SOUNDS = Sound.values();
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Sound snd : SOUNDS) {
                player.stopSound(snd);
            }
            player.stopSound("battle"); // test
        }
    }
}
