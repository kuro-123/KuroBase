package host.kuro.kurobase.trait;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.utils.BuddyUtils;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SendTextTrait extends Trait {

    @Persist private final Set<String> send_text = new HashSet<String>();
    @Persist private int range = 8;
    @Persist private int cool = 60000;
    @Persist private int percent = 50;

    public HashMap<Player, Long> send_player = new HashMap<Player, Long>();

    public SendTextTrait() {
        super("SendTextTrait");
    }

    public void setTextPercent(int percent) {
        this.percent = percent;
    }
    public void setRange(int range) {
        this.range = range;
    }
    public void setCool(int cool) {
        this.cool = cool;
    }
    public void setText(String text) {
        send_text.add(text);
    }

    @Override
    public void run() {
        if (!npc.isSpawned()) return;
        double max_dis = range;
        for (Entity entity : npc.getEntity().getWorld().getEntities()) {
            if (!(entity instanceof Player)) continue;
            if (entity.getEntityId() == npc.getEntity().getEntityId()) continue;
            if (BuddyUtils.IsBuddy(entity)) continue;

            int entity_y = entity.getLocation().getBlockY();
            int own_y = npc.getEntity().getLocation().getBlockY();
            double dis = entity.getLocation().distance(npc.getEntity().getLocation());
            if (dis < max_dis) {
                if (!((own_y-2) <= entity_y && entity_y <= (own_y+2))) {
                    continue;
                }
                Player player = (Player)entity;
                if (!send_player.containsKey(player)) {
                    SendText(player);
                    send_player.put(player, System.currentTimeMillis());
                } else {
                    long before = send_player.get(player);
                    long after = System.currentTimeMillis();
                    if ((after - before) >= cool) {
                        SendText(player);
                        send_player.put(player, System.currentTimeMillis());
                    }
                }
            }
        }
    }
    private void SendText(Player player) {
        if (percent < 100) {
            if (KuroBase.GetRand().Next(1, 100) > percent) {
                return;
            }
        }
        int min = 1;
        int max = send_text.size();
        if (max <= 0) return;
        int value = KuroBase.GetRand().Next(min, max);
        int i=0;
        for (String text : send_text) {
            i++;
            if (value != i) continue;
            text = text.replace("@t", player.getDisplayName());
            player.sendMessage(ChatColor.LIGHT_PURPLE + "[" + npc.getName() + "] " + text);
        }
    }
}