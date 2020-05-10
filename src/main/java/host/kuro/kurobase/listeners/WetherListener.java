package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WetherListener {

    KuroBase plugin = null;

    public WetherListener(KuroBase plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onWeatherChange(WeatherChangeEvent event) {
        boolean rain = event.toWeatherState();
        int value = plugin.getConfig().getInt("Game.rain", 30);
        if(rain) {
            if (plugin.GetRand().Next(0, 100) <= value) {
                event.setCancelled(true);
            }
        }
    }
}
