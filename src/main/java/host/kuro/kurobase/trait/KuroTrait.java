package host.kuro.kurobase.trait;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.BuddyUtils;
import host.kuro.kurobase.utils.ErrorUtils;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
    private int away_tick = 0;
    private int follow_tick = 0;
    private long spawn_time = 0;
    // flag
    private boolean closing = false;

    private Player npcplayer = null;
    private Entity attack_target = null;
    private Entity before_target = null;
    private Navigator navi = null;
    private String movemode = "auto";

    private String sword = "";
    private String helmet = "";
    private String chestplate = "";
    private String leggins = "";
    private String boots = "";

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
    @Persist private double mphealth = 1.0D;
    @Persist private double max_mphealth = 1.0D;
    @Persist private int level = 0;
    @Persist private int exp = 0;
    @Persist private float range = 8.0F;
    @Persist private double attack_range = 8.0D;
    @Persist private double attack_option = 0.0D;
    @Persist private double deffence_option = 0.0D;
    @Persist private int attack_delay_tick = 20;
    @Persist private int update_path_rate = 4;
    @Persist private float base_speed = 0.8F;

    public KuroTrait() {
        super("KuroTrait");
    }
    // owner
    public Player getOwner() { return this.owner; } public void setOwner(Player player) { this.owner = player; }
    // sword
    public String getSword() { return this.sword; } public void setSword(String sword) { this.sword = sword; }
    // helmet
    public String getHelmet() { return this.helmet; } public void setHelmet(String helmet) { this.helmet = helmet; }
    // chestplate
    public String getChestplate() { return this.chestplate; } public void setChestplate(String chestplate) { this.chestplate = chestplate; }
    // leggins
    public String getLeggins() { return this.leggins; } public void setLeggins(String leggins) { this.leggins = leggins; }
    // boots
    public String getBoots() { return this.boots; } public void setBoots(String boots) { this.boots = boots; }
    // name
    public void setName(String name) { this.name = name; }
    // movemode
    public void setMoveMode(String movemode) { this.movemode = movemode; }
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
    // maxmphealth
    public double getMaxMpHealth() { return this.max_mphealth; }
    public void setMaxMpHealth(double max_mphealth) {
        this.max_mphealth = max_mphealth;
    }
    // mphealth
    public double getMpHealth() { return this.mphealth; }
    public void setMpHealth(double mphealth) {
        this.mphealth = mphealth;
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
    // attack_option
    public double getAttackOption() { return this.attack_option; } public void setAttackOption(double value) { this.attack_option = value; }
    // deffence_option
    public double getDefenceOption() { return this.deffence_option; } public void setDefenceOption(double value) { this.deffence_option = value; }
    // attack_delay_tick
    public int getAttackDelayTick() { return this.attack_delay_tick; } public void setAttackDelayTick(int value) { this.attack_delay_tick = value; }
    // update_path_rate
    public int getUpdatePathRate() { return this.update_path_rate; } public void setUpdatePathRate(int value) { this.update_path_rate = value; }
    // base_speed
    public float getBaseSpeed() { return this.base_speed; }  public void setBaseSpeed(float value) { this.base_speed = value; }

    // status value
    private boolean setStatus(int level, String type, String mode) {
        double max_health = 20.0D;
        double max_mphealth = 20.0D;
        float range = 8.0F;
        double attack_range = 8.0D;
        double attack_option = 0.0D;
        double deffence_option = 0.0D;
        int attack_delay_tick = 20;
        float base_speed = 0.8F;

        max_health   = 20.0D +((double)(level*level) / 7000.00D);
        max_mphealth = 20.0D +((double)(level*level) / 7000.00D);
        range        = 8.0F + ((float)(level*level) / 8000.00F);
        attack_range = 1.0D + ((double)(level*level) / 8000.00D) / 4.00D;
        attack_delay_tick = 27 - ((level*level) / 5000);
        base_speed = 1.0F + (level * 0.00375F);
        attack_option   = 1.0D + ((double)(level*level) / 8000.00D) / 8.20D;
        deffence_option = 1.0D + ((double)(level*level) / 8000.00D) / 8.20D;

        // upper check
        if (max_health >= 50.0D) max_health = 50.0D;
        if (max_mphealth >= 50.0D) max_mphealth = 50.0D;
        if (range >= 30.0F) range = 30.0F;
        if (attack_range >= 16.0D) attack_range = 16.0D;
        if (attack_delay_tick <= 2) attack_delay_tick = 2;
        if (base_speed >= 2.5F) max_health = 2.5F;
        if (attack_option >= 2) attack_option = 2;
        if (deffence_option >= 2) deffence_option = 2;

        setMaxHealth(max_health);
        setMaxMpHealth(max_mphealth);
        setRange(range);
        setAttackRange(attack_range);
        setAttackOption(attack_option);
        setDefenceOption(deffence_option);
        setAttackDelayTick(attack_delay_tick);
        setBaseSpeed(base_speed);
        return true;
    }

    // owner
    public boolean isOwner() {
        if (owner == null) return false;
        if (owner.isDead()) return false;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equals(owner.getName())) {
                if (!player.getWorld().getName().equals(owner.getWorld().getName())) {
                    return false;
                }
                return true;
            }
        }
        owner = null;
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
        // status
        npcplayer.setHealth(health);
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
        // check follow
        CheckLocation();
    }

    private void CheckGuard() {
        try {
            if (owner == null) return;
            if (owner.isDead()) return;

            if (!this.guard) return;
            if ((System.currentTimeMillis() - spawn_time) <= 3000) return;
            if (movemode.equals("follow")) {
                attack_target = null;
                return;
            }
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
        } catch (Exception ex) {
        }
    }

    private void CheckFollow() {
        try {
            if (owner == null) return;
            if (owner.isDead()) return;

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
            if (follow_tick > 200) {
                if (health < max_health) {
                    health += 1.0D;
                    if (health > max_health) {
                        health = max_health;
                    }
                    npcplayer.setHealth(health);
                }
                if (mphealth < max_mphealth) {
                    mphealth += 1.0D;
                    if (mphealth > max_mphealth) {
                        mphealth = max_mphealth;
                    }
                }
                follow_tick = 0;
            }
        } catch (Exception ex) {
        }
    }

    private void CheckHealth() {
        try {
            if (owner == null) return;
            if (owner.isDead()) return;
            health = npcplayer.getHealth();
            max_health = npcplayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        } catch (Exception ex) {
        }
    }

    private void UpdateDulabity() {
        try {
            if (owner == null) return;
            if (owner.isDead()) return;
            sword = BuddyUtils.GetEquipName(npcplayer.getInventory().getItemInMainHand(), sword);
            helmet = BuddyUtils.GetEquipName(npcplayer.getInventory().getHelmet(), helmet);
            chestplate = BuddyUtils.GetEquipName(npcplayer.getInventory().getChestplate(), chestplate);
            leggins = BuddyUtils.GetEquipName(npcplayer.getInventory().getLeggings(), leggins);
            boots = BuddyUtils.GetEquipName(npcplayer.getInventory().getBoots(), boots);

        } catch (Exception ex) {
        }
    }

    private void CheckLocation() {
        try {
            if (owner == null) return;
            if (owner.isDead()) return;

            Location npc_loc = npc.getEntity().getLocation();
            if (npc_loc == null) return;
            double distance = owner.getLocation().distance(npc_loc);
            if (distance >= 20) {
                away_tick++;
                if (away_tick >= 600) {
                    owner.sendMessage(ChatColor.YELLOW + "ﾊﾞﾃﾞｨｰは離れすぎたため退出しました");
                    Close();
                    away_tick = 0;
                }
            } else {
                away_tick = 0;
            }
        } catch (Exception ex) {
        }
    }

    public void Close() {
        if (closing) return;
        closing = true;
        if (npc != null) {
            // UPDATE
            UpdateDulabity();
            ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
            eargs.add(new DatabaseArgs("d", ""+health)); // hp
            eargs.add(new DatabaseArgs("d", ""+mphealth)); // mp
            eargs.add(new DatabaseArgs("c", ""+sword)); // sword
            eargs.add(new DatabaseArgs("c", ""+helmet)); // helmet
            eargs.add(new DatabaseArgs("c", ""+chestplate)); // chestplate
            eargs.add(new DatabaseArgs("c", ""+leggins)); // leggins
            eargs.add(new DatabaseArgs("c", ""+boots)); // boots
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
            owner = null;
        }
    }
}