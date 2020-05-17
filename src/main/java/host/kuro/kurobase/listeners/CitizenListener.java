package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.npc.KuroTrait;
import host.kuro.kurobase.utils.ParticleUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import host.kuro.kurodiscord.DiscordMessage;
import net.citizensnpcs.api.event.*;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class CitizenListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCRemoved(NPCRemoveEvent event) {
        NPC npc = event.getNPC();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onTraitRemoved(NPCRemoveTraitEvent event) {
        NPC npc = event.getNPC();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCClickEvent event) {
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCRightClick(NPCRightClickEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCDeath(NPCDeathEvent event) {
        NPC npc = event.getNPC();
        KuroTrait trait = npc.getTrait(KuroTrait.class);
        if (trait != null) {
            trait.Close();

            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.LAVA, 50); // particle
            SoundUtils.BroadcastSound("don-1", false);

            // message
            String message = String.format("[ %sさん ] のバディー [ %s ] が死亡しました", npc.getTrait(KuroTrait.class).getOwner().getDisplayName(), npc.getName());
            PlayerUtils.BroadcastMessage(message, false);
            DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
            if (dm != null) {
                dm.SendDiscordRedMessage(message);
            }

            // UPDATE
            ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
            eargs.add(new DatabaseArgs("c", npc.getUniqueId().toString())); // uuid
            int ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.DEATH.ENTITY"), eargs);
            eargs.clear();
            eargs = null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCDamageByEntity(NPCDamageByEntityEvent event) {
        NPC npc = event.getNPC();
    }

/*
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onEntityTargetNPC(EntityTargetNPCEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCCollisionEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCCombustByBlockEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCCombustByEntityEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCCreateEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCDamageByBlockEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCDamageEntityEvent event) {
    }



    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCOpenDoorEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCPushEvent event) {
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCClick(NPCSpeechEvent event) {
    }
*/
}
