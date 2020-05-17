package host.kuro.kurobase.npc;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.UUID;

public class KuroTrait extends Trait {
    @Persist("KuroSetting") boolean automaticallyPersistedSetting = false;
    @Persist("active") private boolean enabled = false;
    @Persist private UUID followingUUID;
    @Persist private Player player;
    @Persist private boolean protect;

    private boolean closing = false;
    private String name = "";
    private boolean SomeSetting = false;
    private Player owner = null;
    private boolean follow = false;
    private boolean guard = false;
    private GameMode mode = GameMode.SURVIVAL;
    private int max_health = 1;
    private int health = 1;
    private int level = 0;
    private int exp = 0;
    private Entity attack_target = null;
    private Entity before_target = null;
    private Navigator navi = null;
    private long spawn_time = 0;
    private Player npcplayer = null;

    public KuroTrait() {
        super("KuroTrait");
    }

    public void load(DataKey key) {
        SomeSetting = key.getBoolean("SomeSetting", false);
    }

    public void save(DataKey key) {
        key.setBoolean("SomeSetting",SomeSetting);
    }

    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
    }

    @Override
    public void onDespawn() {
    }

    @Override
    public void onSpawn() {
        spawn_time = System.currentTimeMillis();

        if (!(npc.getEntity() instanceof Player)) return;
        npcplayer = ((Player) npc.getEntity());

        // gamemode
        npcplayer.setGameMode(mode);

        // display
        npcplayer.setDisplayName(ChatColor.LIGHT_PURPLE + "[BD] " + name);

        // hp
        AttributeInstance healthAttribute = npcplayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        healthAttribute.setBaseValue(max_health);
        npcplayer.setHealth(health);

        // navi set
        this.navi = npc.getNavigator();
        NavigatorParameters param = navi.getLocalParameters();
        param.range(8);
        param.attackRange(8);
        param.attackDelayTicks(2); // 20
        param.updatePathRate(4);
        param.baseSpeed(0.8F);
        param.avoidWater(true); // 水を避ける
    }

    @Override
    public void onRemove() {
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

    // name
    public void setName(String name) {
        this.name = name;
    }

    // gamemode
    public void setGameMode(GameMode mode) {
        this.mode = mode;
    }

    // maxhealth
    public void setMaxHealth(int health) {
        this.max_health = health;
    }

    // sethealth
    public void setHealth(int health) {
        this.health = health;
    }

    public Player getOwner() {
        return this.owner;
    }
    public void setOwner(Player player) {
        this.owner = player;
    }

    public void setFollow(boolean value) {
        this.follow = value;
    }

    public void setGuard(boolean value) {
        this.protect = value;
        this.guard = value;
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

        if (!(npc.getEntity() instanceof Player)) return;
        Player npcplayer = ((Player) npc.getEntity());
        // check mode change
        CheckGameMode(npcplayer);
    }

    private void CheckGuard() {
        if (!this.guard) return;
        if ((System.currentTimeMillis() - spawn_time) <= 3000) return;

        double max_dis = 8;
        for (Entity entity : owner.getWorld().getEntities()) {
            if (!((entity instanceof Monster) || (entity instanceof Animals))) continue;
            if (entity.getEntityId() == npc.getEntity().getEntityId()) continue;
            if (entity.getEntityId() == owner.getEntityId()) continue;
            int entity_y = entity.getLocation().getBlockY();
            int own_y = npc.getEntity().getLocation().getBlockY();
            double dis = entity.getLocation().distance(npc.getEntity().getLocation());
            if (dis < max_dis) {
                if (!((own_y-3) <= entity_y && entity_y <= (own_y+3))) {
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
    }

    private void CheckFollow() {
        if (attack_target != null) return;
        if (navi.isNavigating()) {
            if (!follow) {
                navi.cancelNavigation();
            } else {
                navi.setTarget(owner, false);
            }
        } else {
            if (follow) {
                navi.setTarget(owner, false);
            }
        }
    }

    private void CheckGameMode(Player npcplayer) {
        if (npcplayer.getGameMode() != mode) {
            npcplayer.setGameMode(mode);
        }
    }

    private void CheckHealth() {
        double maxhp = ((Player) npc.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
        if (maxhp != max_health) {
            AttributeInstance healthAttribute = ((Player) npc.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH);
            healthAttribute.setBaseValue(max_health);
        }
    }

    public void Close() {
        if (closing) return;
        closing = true;
        if (npc != null) {
            if (npc.isSpawned()) {
                // UPDATE
                ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
                eargs.add(new DatabaseArgs("c", npc.getUniqueId().toString())); // uuid
                int ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.QUIT.ENTITY"), eargs);
                eargs.clear();
                eargs = null;

                npc.despawn();
                npc.destroy();
            }
        }
        owner.sendMessage(ChatColor.LIGHT_PURPLE+name+"は退出しました");
    }
}