package host.kuro.kurobase.npc;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class KuroTrait extends Trait {
    // counter
    private int tick = 0;
    private int follow_tick = 0;
    private long spawn_time = 0;
    // flag
    private boolean closing = false;

    private Player npcplayer = null;
    private Entity attack_target = null;
    private Entity before_target = null;
    private Navigator navi = null;

    // status
    @Persist private boolean protect;
    @Persist private Player owner = null;
    @Persist private String name = "";
    @Persist private String type = "";
    @Persist private String mode = "";
    @Persist private boolean follow = false;
    @Persist private boolean guard = false;
    @Persist private double max_health = 1.0D;
    @Persist private double health = 1.0D;
    @Persist private int level = 0;
    @Persist private int exp = 0;
    @Persist private float range = 8.0F;
    @Persist private double attack_range = 8.0D;
    @Persist private int attack_delay_tick = 20;
    @Persist private int update_path_rate = 4;
    @Persist private float base_speed = 0.8F;

    public KuroTrait() {
        super("KuroTrait");
    }
    // owner
    public Player getOwner() { return this.owner; } public void setOwner(Player player) { this.owner = player; }
    // name
    public void setName(String name) { this.name = name; }
    // name
    public void setType(String type) { this.type = type; }
    // name
    public void setMode(String mode) { this.mode = mode; }
    // maxhealth
    public double getMaxHealth() { return this.max_health; }
    public void setMaxHealth(double health) {
        this.max_health = health;
        if (npcplayer!=null) {
            AttributeInstance healthAttribute = npcplayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            healthAttribute.setBaseValue(max_health);
        }
    }
    // health
    public double getHealth() { return this.health; }
    public void setHealth(double health) {
        this.health = health;
        if (npcplayer!=null) npcplayer.setHealth(health);
    }
    // follow
    public boolean getFollow() { return this.follow; } public void setFollow(boolean value) { this.follow = value; }
    // guard
    public boolean getGuard() { return this.guard; } public void setGuard(boolean value) { this.protect = value; this.guard = value; }
    // level
    public int getLevel() { return this.level; } public void setLevel(int value) { this.level = value; }
    // exp
    public int getExp() { return this.exp; } public void setExp(int value) { this.exp = value; }
    // range
    public float getRange() { return this.range; } public void setRange(float value) { this.range = value; }
    // attack_range
    public double getAttackRange() { return this.attack_range; } public void setAttackRange(double value) { this.attack_range = value; }
    // attack_delay_tick
    public int getAttackDelayTick() { return this.attack_delay_tick; } public void setAttackDelayTick(int value) { this.attack_delay_tick = value; }
    // update_path_rate
    public int getUpdatePathRate() { return this.update_path_rate; } public void setUpdatePathRate(int value) { this.update_path_rate = value; }
    // base_speed
    public float getBaseSpeed() { return this.base_speed; }  public void setBaseSpeed(float value) { this.base_speed = value; }

    // status value
    private boolean setStatus(int level, String type, String mode) {
        double max_health = 20.0D;
        float range = 8.0F;
        double attack_range = 8.0D;
        int attack_delay_tick = 20;
        float base_speed = 0.8F;
        if (mode.equals(Language.translate("buddy.data.normal"))) {
            // LV000 HP:20.00 R: 8.0 AR: 8.00 AD:20.000 BS: 0.7000
            // LV100 HP:24.00 R:18.0 AR:16.00 AD:15.500 BS: 0.9500
            // LV200 HP:28.00 R:28.0 AR:24.00 AD:11.000 BS: 1.2000
            // LV300 HP:32.00 R:38.0 AR:32.00 AD: 6.500 BS: 1.4500
            max_health   = 20.0D + (level * 0.04D);
            range        = 8.0F + ((float)level * 0.1F);
            attack_range = 8.0D + ((double)level * 0.08D);
            attack_delay_tick = 20 - (int)((double)level * 0.045D);
            base_speed = 0.7F + ((float)level * 0.0025F);
        } else if (mode.equals(Language.translate("buddy.data.guard"))) {
            // LV000 HP:24.00 R:12.0 AR:12.00 AD:18.000 BS: 0.8000
            // LV100 HP:31.00 R:24.0 AR:21.00 AD:13.200 BS: 1.0700
            // LV200 HP:38.00 R:36.0 AR:30.00 AD: 8.400 BS: 1.3400
            // LV300 HP:45.00 R:48.0 AR:39.00 AD: 3.600 BS: 1.6100
            max_health   = 24.0D + (level * 0.07D);
            range        = 12.0F + ((float)level * 0.12F);
            attack_range = 12.0D + ((double)level * 0.09D);
            attack_delay_tick = 18 - (int)((double)level * 0.048D);
            base_speed = 0.8F + ((float)level * 0.0027F);
        } else if (mode.equals(Language.translate("buddy.data.battle"))) {
            // LV000 HP:22.00 R:14.0 AR:14.00 AD:14.000 BS: 0.9000
            // LV100 HP:27.00 R:26.0 AR:23.00 AD:10.000 BS: 1.2500
            // LV200 HP:32.00 R:38.0 AR:32.00 AD: 6.000 BS: 1.6000
            // LV300 HP:37.00 R:50.0 AR:41.00 AD: 2.000 BS: 1.9500
            max_health   = 22.0D + (level * 0.05D);
            range        = 14.0F + ((float)level * 0.12F);
            attack_range = 14.0D + ((double)level * 0.09D);
            attack_delay_tick = 14 - (int)((double)level * 0.040D);
            base_speed = 0.9F + ((float)level * 0.0035F);
        } else if (mode.equals(Language.translate("buddy.data.nijya"))) {
            // LV000 HP:24.00 R:20.0 AR:20.00 AD:12.000 BS: 1.0000
            // LV100 HP:31.00 R:36.0 AR:30.00 AD: 7.000 BS: 1.4200
            // LV200 HP:38.00 R:52.0 AR:40.00 AD: 2.000 BS: 1.8400
            // LV300 HP:45.00 R:68.0 AR:50.00 AD: 2.000 BS: 2.2600
            max_health   = 24.0D + (level * 0.07D);
            range        = 20.0F + ((float)level * 0.16F);
            attack_range = 20.0D + ((double)level * 0.1D);
            attack_delay_tick = 12 - (int)((double)level * 0.05D);
            base_speed = 1.0F + ((float)level * 0.0042F);
        }
        // upper check
        if (max_health >= 50.0D) max_health = 50.0D;
        if (range >= 70.0F) range = 70.0F;
        if (attack_range >= 70.0D) attack_range = 70.0D;
        if (attack_delay_tick <= 2) attack_delay_tick = 2;
        if (base_speed >= 2.5F) max_health = 2.5F;

        setMaxHealth(max_health);
        setHealth(max_health);

        setRange(range);
        setAttackRange(attack_range);
        setAttackDelayTick(attack_delay_tick);
        setBaseSpeed(base_speed);
        return true;
    }

    // owner
    public boolean isOwner() {
        if (owner == null) {
            return false;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equals(owner.getName())) {
                if (!player.getWorld().getName().equals(owner.getWorld().getName())) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public void UpdateStatus() {
        if (navi == null) return;
        setStatus(level, type, mode);
        NavigatorParameters param = navi.getLocalParameters();
        param.range(range);
        param.attackRange(attack_range);
        param.attackDelayTicks(attack_delay_tick);
        param.updatePathRate(update_path_rate);
        param.baseSpeed(base_speed);
        param.avoidWater(true);
    }

    @Override
    public void onSpawn() {
        // spawn time
        spawn_time = System.currentTimeMillis();
        // cast
        if (!(npc.getEntity() instanceof Player)) return;
        npcplayer = ((Player) npc.getEntity());
        // navi set
        navi = npc.getNavigator();
        // gamemode
        npcplayer.setGameMode(GameMode.SURVIVAL);
        // display name
        npcplayer.setDisplayName(ChatColor.LIGHT_PURPLE + "[BD] " + name);
        // status
        UpdateStatus();
    }

    @Override
    public void run() {
        if (!npc.isSpawned()) return;
        if (!isOwner()) {
            Close();
            return;
        }
        // check health
        CheckHealth();
        // check guard
        CheckGuard();
        // check follow
        CheckFollow();
    }

    private void CheckGuard() {
        if (!this.guard) return;
        if ((System.currentTimeMillis() - spawn_time) <= 3000) return;

        double max_dis = range;
        for (Entity entity : npc.getEntity().getWorld().getEntities()) {
            if (!((entity instanceof Monster) || (entity instanceof Animals))) continue;
            if (entity.getEntityId() == npc.getEntity().getEntityId()) continue;
            if (entity.getEntityId() == owner.getEntityId()) continue;
            int entity_y = entity.getLocation().getBlockY();
            int own_y = npc.getEntity().getLocation().getBlockY();
            double dis = entity.getLocation().distance(npc.getEntity().getLocation());
            if (dis < max_dis) {
                if (!((own_y-2) <= entity_y && entity_y <= (own_y+2))) {
                    continue;
                }
                max_dis = dis;
                attack_target = entity;
            }
        }
        if (attack_target != null) {
            follow_tick = 0;
            if (!attack_target.isDead()) {
                if (before_target != null) {
                    if (attack_target.getEntityId() != before_target.getEntityId()) {
                        navi.setTarget(attack_target, true);
                        before_target = attack_target;
                    } else {
                        if ((tick % 20) == 0) {
                            before_target = null;
                        }
                    }
                } else {
                    navi.setTarget(attack_target, true);
                    before_target = attack_target;
                }
            } else {
                attack_target = null;
                before_target = null;
            }
        }
        tick++;
    }

    private void CheckFollow() {
        if (attack_target != null) return;
        if (navi.isNavigating()) {
            if (!follow) {
                navi.cancelNavigation();
            } else {
                navi.setTarget(owner, false);
                follow_tick++;
            }
        } else {
            if (follow) {
                navi.setTarget(owner, false);
                follow_tick++;
            }
        }
        if (follow_tick > 100) {
            if (health < max_health) {
                health += 1.0D;
                if (health > max_health) {
                    health = max_health;
                }
                npcplayer.setHealth(health);
            }
            follow_tick = 0;
        }
    }

    private void CheckHealth() {
        health = npcplayer.getHealth();
        max_health = npcplayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
    }

    public void Close() {
        if (closing) return;
        closing = true;
        if (npc != null) {
            // UPDATE
            ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
            eargs.add(new DatabaseArgs("c", npc.getUniqueId().toString())); // uuid
            int ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.QUIT.ENTITY"), eargs);
            eargs.clear();
            eargs = null;

            if (npc.isSpawned()) {
                try {
                    npc.getEntity().remove();
                    KuroBase.GetCitizens().getNPCRegistry().deregister(npc);
                } catch (Exception ex) {
                    ErrorUtils.GetErrorMessageNonDb(ex);
                }
            }
        }
    }
}