package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE;

public class EntityListener implements Listener {

    private KuroBase plugin = null;

    public EntityListener(KuroBase plugin) {
        this.plugin = plugin;
    }
    private static HashMap<Player, Long> sound_battle = new HashMap<Player, Long>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        try {
            Entity entity = e.getEntity();
            if (entity instanceof Player) {
                EntityDamageEvent.DamageCause cause = e.getCause();
                if (cause != null) {
                    if (cause == ENTITY_ATTACK || cause == PROJECTILE) {
                        Player player = (Player)entity;
                        if (!sound_battle.containsKey(player)) {
                            SoundUtils.PlaySound(player, "battle");
                            sound_battle.put(player, System.currentTimeMillis());
                        } else {
                            long before = sound_battle.get(player);
                            long after = System.currentTimeMillis();
                            long elapse = after - before;
                            elapse = elapse / 1000;
                            if (elapse >= 240) {
                                SoundUtils.PlaySound(player, "battle");
                                sound_battle.put(player, System.currentTimeMillis());
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        try {
            Entity entity = e.getEntity();
            if (!(entity instanceof Monster)) return;
            Player player = e.getEntity().getKiller();
            if (player == null) return;

            // UPDATE
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.MOBKILL.UPDATE.PLAYER"), args);
            args.clear();
            args = null;

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }
}