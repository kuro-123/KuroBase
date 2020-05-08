package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.ParticleUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
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

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        try {
            Entity entity = e.getEntity();
            if (entity instanceof Player) {
                EntityDamageEvent.DamageCause cause = e.getCause();
                if (cause != null) {
                    if (cause == ENTITY_ATTACK || cause == PROJECTILE) {
                        Player player = (Player)entity;
                        // check world
                        if (!PlayerUtils.IsSurvivalWorld(plugin, player)) {
                            GameMode mode = player.getGameMode();
                            if (mode != GameMode.CREATIVE) {
                                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
                                SoundUtils.PlaySound(player,"cancel5", false);
                                e.setCancelled(true);
                                return;
                            }
                        }
                        if (!plugin.GetSoundBattle().containsKey(player)) {
                            SoundUtils.PlaySound(player, "battle", true);
                            plugin.GetSoundBattle().put(player, System.currentTimeMillis());
                        } else {
                            long before = plugin.GetSoundBattle().get(player);
                            long after = System.currentTimeMillis();
                            long elapse = after - before;
                            elapse = elapse / 1000;
                            if (elapse >= 240) {
                                SoundUtils.PlaySound(player, "battle", true);
                                plugin.GetSoundBattle().put(player, System.currentTimeMillis());
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
            if (!(entity instanceof Monster || entity instanceof Animals)) return;

            Player player = e.getEntity().getKiller();
            if (player == null) return;

            int minxp = 0;
            int maxxp = 0;
            boolean baby = false;
            if(entity instanceof Ageable) {
                Ageable age = (Ageable) entity;
                baby = !age.isAdult();
            }
            if (baby) {
                minxp=1;
                maxxp=1;
            } else {
                switch (entity.getType()) {
                    case BAT:
                        minxp = 1;
                        maxxp = 2;
                        break;
                    case BEE:
                        minxp = 1;
                        maxxp = 2;
                        break;
                    case CAT:
                        minxp = 1;
                        maxxp = 2;
                        break;
                    case PARROT:
                        minxp = 1;
                        maxxp = 2;
                        break;
                    case RABBIT:
                        minxp = 1;
                        maxxp = 2;
                        break;
                    case COD:
                        minxp = 1;
                        maxxp = 2;
                        break;
                    case SALMON:
                        minxp = 1;
                        maxxp = 2;
                        break;
                    case PUFFERFISH:
                        minxp = 1;
                        maxxp = 2;
                        break;
                    case TROPICAL_FISH:
                        minxp = 1;
                        maxxp = 2;
                        break;

                    case CHICKEN:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case COW:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case MUSHROOM_COW:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case OCELOT:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case PIG:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case SHEEP:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case SQUID:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case WOLF:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case FOX:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case MULE:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case HORSE:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case LLAMA:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case PANDA:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case DONKEY:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case LLAMA_SPIT:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case POLAR_BEAR:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case ZOMBIE_HORSE:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case TRADER_LLAMA:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case SKELETON_HORSE:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case TURTLE:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case DOLPHIN:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case GUARDIAN:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case ENDERMITE:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case ELDER_GUARDIAN:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case SLIME:
                        minxp = 2;
                        maxxp = 3;
                        break;
                    case MAGMA_CUBE:
                        minxp = 2;
                        maxxp = 3;
                        break;

                    case ZOMBIE:
                        minxp = 5;
                        maxxp = 8;
                        break;
                    case SKELETON:
                        minxp = 5;
                        maxxp = 8;
                        break;
                    case SILVERFISH:
                        minxp = 5;
                        maxxp = 8;
                        break;
                    case SPIDER:
                        minxp = 5;
                        maxxp = 8;
                        break;
                    case CAVE_SPIDER:
                        minxp = 5;
                        maxxp = 8;
                        break;
                    case CREEPER:
                        minxp = 5;
                        maxxp = 8;
                        break;

                    case ENDERMAN:
                        minxp = 6;
                        maxxp = 8;
                        break;
                    case GHAST:
                        minxp = 6;
                        maxxp = 8;
                        break;
                    case WITHER_SKELETON:
                        minxp = 6;
                        maxxp = 8;
                        break;
                    case PIG_ZOMBIE:
                        minxp = 6;
                        maxxp = 8;
                        break;
                    case SHULKER:
                        minxp = 6;
                        maxxp = 8;
                        break;
                    case DROWNED:
                        minxp = 6;
                        maxxp = 8;
                        break;

                    case PHANTOM:
                        minxp = 7;
                        maxxp = 10;
                        break;
                    case HUSK:
                        minxp = 7;
                        maxxp = 10;
                        break;
                    case STRAY:
                        minxp = 7;
                        maxxp = 10;
                        break;
                    case EVOKER:
                        minxp = 7;
                        maxxp = 10;
                        break;

                    case RAVAGER:
                        minxp = 10;
                        maxxp = 20;
                        break;
                    case BLAZE:
                        minxp = 10;
                        maxxp = 20;
                        break;

                    case WITHER:
                        minxp = 50;
                        maxxp = 100;
                        break;
                    case ENDER_DRAGON:
                        minxp = 500;
                        maxxp = 800;
                        break;

                    case GIANT:
                        minxp = 0;
                        maxxp = 0;
                        break;
                    case SNOWMAN:
                        minxp = 0;
                        maxxp = 0;
                        break;
                    case IRON_GOLEM:
                        minxp = 0;
                        maxxp = 0;
                        break;
                    case WITCH:
                        minxp = 0;
                        maxxp = 0;
                        break;
                    case VILLAGER:
                        minxp = 0;
                        maxxp = 0;
                        break;
                    case PILLAGER:
                        minxp = 0;
                        maxxp = 0;
                        break;
                    case ILLUSIONER:
                        minxp = 0;
                        maxxp = 0;
                        break;
                    case VINDICATOR:
                        minxp = 0;
                        maxxp = 0;
                        break;
                    case ZOMBIE_VILLAGER:
                        minxp = 0;
                        maxxp = 0;
                        break;
                    case WANDERING_TRADER:
                        minxp = 0;
                        maxxp = 0;
                        break;
                    case VEX:
                        minxp = 0;
                        maxxp = 0;
                        break;
                    default:
                        minxp = 0;
                        maxxp = 0;
                        break;
                }
            }
            if (minxp == 0) return;

            int xp = 0;
            if (minxp == maxxp) {
                xp = minxp;
            } else {
                xp = plugin.GetRand().Next(minxp, maxxp);
            }
            player.giveExp(xp);

            // UPDATE
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.MOBKILL.UPDATE.PLAYER"), args);
            args.clear();
            args = null;

            // log insert
            ArrayList<DatabaseArgs> margs = new ArrayList<DatabaseArgs>();
            margs.add(new DatabaseArgs("c", player.getName())); // src
            margs.add(new DatabaseArgs("c", entity.getName())); // dst
            margs.add(new DatabaseArgs("i", ""+xp)); // xp
            ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.INSERT.LOG.MOB"), margs);
            margs.clear();
            margs = null;

            // battle sound stop check
            if (plugin.GetSoundBattle().containsKey(player)) {
                plugin.GetSoundBattle().remove(player);
                SoundUtils.StopSoundAll(player);
                SoundUtils.PlaySound(player, "complete", true);
            }

            SoundUtils.PlaySound(player,"sceneswitch2", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }
}