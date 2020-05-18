package host.kuro.kurobase.trait;

import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class ExplanerTrait extends Trait {
    private Navigator navi = null;
    private Player npcplayer = null;
    // status
    @Persist private double max_health = 100.0D;
    @Persist private float range = 8.0F;
    @Persist private double attack_range = 8.0D;
    @Persist private int attack_delay_tick = 2;
    @Persist private int update_path_rate = 4;
    @Persist private float base_speed = 2.0F;

    public ExplanerTrait() {
        super("ExplanerTrait");
    }

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
        if (!npc.isSpawned()) return;
    }
}