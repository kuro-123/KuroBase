package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WetherListener implements Listener {

    KuroBase plugin = null;

    public WetherListener(KuroBase plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onWeatherChange(WeatherChangeEvent event) {
        String name = plugin.getConfig().getString("Game.creative", "city").toLowerCase();
        String world = event.getWorld().getName().toLowerCase();
        if (world.equals(name)) {
            event.setCancelled(event.toWeatherState());
        } else {
            boolean rain = event.toWeatherState();
            int value = plugin.getConfig().getInt("Game.rain", 15);
            if(rain) {
                if (plugin.GetRand().Next(0, 100) > value) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onThunderChange(ThunderChangeEvent event) {
        String name = plugin.getConfig().getString("Game.creative", "city").toLowerCase();
        String world = event.getWorld().getName().toLowerCase();
        if (world.equals(name)) {
            event.setCancelled(event.toThunderState());
        } else {
            boolean rain = event.toThunderState();
            int value = plugin.getConfig().getInt("Game.rain", 15);
            if(rain) {
                if (plugin.GetRand().Next(0, 100) > value) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
