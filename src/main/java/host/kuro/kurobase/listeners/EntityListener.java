package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.BuddyUtils;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.PROJECTILE;

public class EntityListener implements Listener {

    private KuroBase plugin = null;

    public EntityListener(KuroBase plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntity() instanceof Creeper) {
            e.blockList().removeIf(block -> (block.getType() == Material.CHEST || block.getType() == Material.ITEM_FRAME));
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        try {
            Entity entity = e.getEntity();
            if (BuddyUtils.IsBuddy(entity)) {
                return;
            }
            if (BuddyUtils.IsBuddyMaster(e.getDamager())) {
                double damage = e.getDamage();
                e.setDamage(damage * 100.0D);
                return;
            }
            if (entity instanceof Player) {
                EntityDamageEvent.DamageCause cause = e.getCause();
                if (cause != null) {
                    // special attack
                    if (cause == ENTITY_ATTACK || cause == PROJECTILE) {
                        Player player = (Player)entity;
                        Entity damager = e.getDamager();
                        if (damager instanceof Projectile) {
                            Projectile pt = (Projectile)e.getDamager();
                            damager = (Entity)pt.getShooter();
                        }
                        if (damager instanceof Player) {
                            if (!BuddyUtils.IsNpc(damager)) {
                                Player dmger = ((Player)damager);
                                // pvp off
                                if (plugin.GetPvp().containsKey(player)) {
                                    boolean pvp = plugin.GetPvp().get(player);
                                    if (pvp == false) {
                                        e.setCancelled(true);
                                        dmger.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.pvp.target"));
                                        SoundUtils.PlaySound(dmger,"cancel5", false);
                                        return;
                                    } else {
                                        pvp = plugin.GetPvp().get(dmger);
                                        if (pvp == false) {
                                            e.setCancelled(true);
                                            dmger.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.pvp.own"));
                                            SoundUtils.PlaySound(dmger,"cancel5", false);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        // check world
                        if (PlayerUtils.IsCityWorld(plugin, player)) {
                            if (PlayerUtils.GetRank(plugin, player) < PlayerUtils.RANK_KANRI) {
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
                        // special attack
                        if (cause == ENTITY_ATTACK) {
                            if (ActionSpecialAttack(player, e.getDamager())) {
                                double damage = e.getDamage();
                                damage = damage * plugin.GetRand().Next(2, 5);
                                e.setDamage(damage);
                                SoundUtils.PlaySound(player,"buun1", false);
                                player.sendMessage(ChatColor.YELLOW + Language.translate("plugin.attack.special"));
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
    public void onEntitySpawn(EntitySpawnEvent e) {
        try {
            Entity entity = e.getEntity();
            if (!(entity instanceof Monster)) return;
            int val = KuroBase.GetRand().Next(1, 20);
            if (val == 1) {
                entity.setMetadata("SPECIAL", new FixedMetadataValue(KuroBase.GetInstance(), "SPECIAL"));
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

            Player player = null;
            LivingEntity liv = e.getEntity();
            if (liv != null) {
                if (BuddyUtils.IsBuddyMaster(liv.getKiller())) {
                    e.setDroppedExp(0);
                    return;
                }
                player = e.getEntity().getKiller();
            }
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

            if (BuddyUtils.IsBuddy(player)) {
                BuddyUtils.SetNpcExprience(player.getName(), xp);
                e.setDroppedExp(0);

            } else {
                player.giveExp(xp);

                if (entity.hasMetadata("SPECIAL")) {
                    RandomDrop(player);
                }

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
            }

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }

    private void RandomDrop(Player player) {
        if (KuroBase.GetRand().Next(1, 20) != 1) return;
        int amount = KuroBase.GetRand().Next(2, 5);
        ItemStack item = new ItemStack(Material.DIAMOND, amount);
        int x = KuroBase.GetRand().Next(-2, 2);
        int z = KuroBase.GetRand().Next(-2, 2);
        player.getWorld().dropItem(player.getLocation().add(x,1,z), item);
        PlayerUtils.BroadcastMessage(ChatColor.AQUA + String.format("%sさんはスペシャルモンスター討伐時にダイアモンド%d個を発見した！", player.getDisplayName(), amount), true);
        SoundUtils.BroadcastSound("correct2", false);
    }

    private boolean ActionSpecialAttack(Player player, Entity entity) {
        try {
            boolean meta = false;
            if (entity.hasMetadata("SPECIAL")) {
                meta = true;
            }
            int ritu = 0;
            switch (entity.getType()) {
                case ZOMBIE:
                    ritu = 5;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case SKELETON:
                    ritu = 5;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case SPIDER:
                    ritu = 5;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case CAVE_SPIDER:
                    ritu = 5;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case CREEPER:
                    ritu = 7;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case ENDERMAN:
                    ritu = 7;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case GHAST:
                    ritu = 7;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case WITHER_SKELETON:
                    ritu = 8;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case PIG_ZOMBIE:
                    ritu = 8;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case PHANTOM:
                    ritu = 8;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case HUSK:
                    ritu = 8;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case STRAY:
                    ritu = 8;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case EVOKER:
                    ritu = 8;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case RAVAGER:
                    ritu = 8;
                    if (meta) player.setFireTicks(ritu);
                    break;
                case BLAZE:
                    ritu = 10;
                    if (meta) {
                        player.setFireTicks(10);
                    } else {
                        if (plugin.GetRand().Next(1, 100) < 15) {
                            player.setFireTicks(10);
                        }
                    }
                    break;
                case WITHER:
                    ritu = 10;
                    if (meta) {
                        player.setFireTicks(10);
                    } else {
                        if (plugin.GetRand().Next(1, 100) < 20) {
                            player.setFireTicks(10);
                        }
                    }
                    break;
                case ENDER_DRAGON:
                    ritu = 30;
                    if (meta) {
                        player.setFireTicks(20);
                    } else {
                        if (plugin.GetRand().Next(1, 100) < 30) {
                            player.setFireTicks(30);
                        }
                    }
                    break;
            }
            int value = plugin.GetRand().Next(1, 100);
            if (meta) {
                ritu = ritu * 2;
            }
            if (value < ritu) {
                return true;
            }

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            return false;
        }
        return false;
    }
}