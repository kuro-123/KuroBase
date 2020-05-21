package host.kuro.kurobase.trait;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class CreativeMasterTrait extends Trait {
    private int tick = 0;
    private int away_tick = 0;
    private int respawn_tick = 0;
    private Entity attack_target = null;
    private Entity before_target = null;

    private Navigator navi = null;
    private Player npcplayer = null;

    // status
    @Persist private Location location = null;
    @Persist private double max_health = 100.0D;
    @Persist private float range = 8.0F;
    @Persist private double attack_range = 8.0D;
    @Persist private int attack_delay_tick = 2;
    @Persist private int update_path_rate = 4;
    @Persist private float base_speed = 1.0F;

    public CreativeMasterTrait() {
        super("CreativeMasterTrait");
    }
    // location
    public void setLocation(Location location) { this.location = location; }

    public void UpdateStatus() {
        if (navi == null) return;
        // hp
        AttributeInstance healthAttribute = npcplayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        healthAttribute.setBaseValue(max_health);
        npcplayer.setHealth(max_health);
        // status
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
        navi = npc.getNavigator();
        if (!(npc.getEntity() instanceof Player)) return;
        npcplayer = ((Player) npc.getEntity());
        // gamemode
        npcplayer.setGameMode(GameMode.SURVIVAL);
        // status
        UpdateStatus();
    }

    @Override
    public void run() {
        respawn_tick++;
        if (!npc.isSpawned()) return;
        // check guard
        CheckGuard();
        // check location
        CheckLocation();
    }

    private void CheckGuard() {
        double max_dis = range;
        for (Entity entity : npc.getEntity().getWorld().getEntities()) {
            if (!(entity instanceof Monster)) continue;
            if (entity.getEntityId() == npc.getEntity().getEntityId()) continue;
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

    private void CheckLocation() {
        if (attack_target != null) return;
        navi.setTarget(location);

        double distance = location.distance(npc.getEntity().getLocation());
        if (distance >= 5) {
            away_tick++;
            if (away_tick >= 600) {
                npcplayer.teleport(location);
                away_tick = 0;
            }
        } else {
            away_tick = 0;
        }
    }
}